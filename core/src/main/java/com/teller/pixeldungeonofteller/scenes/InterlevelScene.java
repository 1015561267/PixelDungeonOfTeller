/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015  Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2016 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package com.teller.pixeldungeonofteller.scenes;

import com.teller.pixeldungeonofteller.Assets;
import com.teller.pixeldungeonofteller.Dungeon;
import com.teller.pixeldungeonofteller.GamesInProgress;
import com.teller.pixeldungeonofteller.PDSettings;
import com.teller.pixeldungeonofteller.PixelDungeonOfTeller;
import com.teller.pixeldungeonofteller.Statistics;
import com.teller.pixeldungeonofteller.actors.Actor;
import com.teller.pixeldungeonofteller.items.Generator;
import com.teller.pixeldungeonofteller.levels.Level;
import com.teller.pixeldungeonofteller.levels.RegularLevel;
import com.teller.pixeldungeonofteller.messages.Messages;
import com.teller.pixeldungeonofteller.ui.GameLog;
import com.teller.pixeldungeonofteller.ui.RenderedTextBlock;
import com.teller.pixeldungeonofteller.windows.WndError;
import com.teller.pixeldungeonofteller.windows.WndStory;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.noosa.RenderedText;
import com.watabou.noosa.audio.Music;
import com.watabou.noosa.audio.Sample;

import java.io.FileNotFoundException;
import java.io.IOException;

public class InterlevelScene extends PixelScene {

    private static final float TIME_TO_FADE = 0.3f;
    public static Mode mode;
    public static int returnDepth;
    public static int returnPos;
    public static boolean noStory = false;
    public static boolean fallIntoPit;
    private Phase phase;
    private float timeLeft;
    private RenderedTextBlock message;
    private Thread thread;
    private Exception error = null;
    private float waitingTime;

    @Override
    public void create() {
        super.create();

        String text = Messages.get(Mode.class, mode.name());

        message = PixelScene.renderTextBlock(text, 9);

        message.setPos((Camera.main.width - message.width()) / 2 ,  (Camera.main.height - message.height()) / 2 );

        align(message);
        add(message);

        phase = Phase.FADE_IN;
        timeLeft = TIME_TO_FADE;

        thread = new Thread() {
            @Override
            public void run() {

                try {

                    Generator.reset();

                    switch (mode) {
                        case DESCEND:
                            descend();
                            break;
                        case ASCEND:
                            ascend();
                            break;
                        case CONTINUE:
                            restore();
                            break;
                        case RESURRECT:
                            resurrect();
                            break;
                        case RETURN:
                            returnTo();
                            break;
                        case FALL:
                            fall();
                            break;
                        case RESET:
                            reset();
                            break;
                    }

                    if ((Dungeon.depth % 5) == 0) {
                        Sample.INSTANCE.load(Assets.SND_BOSS);
                    }

                } catch (Exception e) {

                    error = e;

                }

                if (phase == Phase.STATIC && error == null) {
                    phase = Phase.FADE_OUT;
                    timeLeft = TIME_TO_FADE;
                }
            }
        };
        thread.start();
        waitingTime = 0f;
    }

    @Override
    public void update() {
        super.update();

        waitingTime += Game.elapsed;

        float p = timeLeft / TIME_TO_FADE;

        switch (phase) {

            case FADE_IN:
                message.alpha(1 - p);
                if ((timeLeft -= Game.elapsed) <= 0) {
                    if (!thread.isAlive() && error == null) {
                        phase = Phase.FADE_OUT;
                        timeLeft = TIME_TO_FADE;
                    } else {
                        phase = Phase.STATIC;
                    }
                }
                break;

            case FADE_OUT:
                message.alpha(p);

                if (mode == Mode.CONTINUE || (mode == Mode.DESCEND && Dungeon.depth == 1)) {
                    Music.INSTANCE.volume(p * (PDSettings.musicVol() / 10f));
                }
                if ((timeLeft -= Game.elapsed) <= 0) {
                    Game.switchScene(GameScene.class);
                }
                break;

            case STATIC:
                if (error != null) {
                    String errorMsg;
                    if (error instanceof FileNotFoundException)
                        errorMsg = Messages.get(this, "file_not_found");
                    else if (error instanceof IOException)
                        errorMsg = Messages.get(this, "io_error");

                    else
                        throw new RuntimeException("fatal error occured while moving between floors", error);

                    add(new WndError(errorMsg) {
                        public void onBackPressed() {
                            super.onBackPressed();
                            Game.switchScene(StartScene.class);
                        }
                    });
                    error = null;
                } else if ((int) waitingTime == 10) {
                    waitingTime = 11f;
                    PixelDungeonOfTeller.reportException(
                            new RuntimeException("waited more than 10 seconds on levelgen. Seed:" + Dungeon.seed + " depth:" + Dungeon.depth)
                    );
                }
                break;
        }
    }

    private void descend() throws IOException {

        Actor.fixTime();
        if (Dungeon.hero == null) {
            Dungeon.init();
            if (noStory) {
                Dungeon.chapters.add(WndStory.ID_SEWERS);
                noStory = false;
            }
            GameLog.wipe();
        } else {
            Dungeon.saveAll();
        }

        Level level;
        if (Dungeon.depth >= Statistics.deepestFloor) {
            level = Dungeon.newLevel();
        } else {
            Dungeon.depth++;
            level = Dungeon.loadLevel(GamesInProgress.curSlot);
        }
        Dungeon.switchLevel(level, level.entrance);
    }

    private void fall() throws IOException {

        Actor.fixTime();
        Dungeon.saveAll();

        Level level;
        if (Dungeon.depth >= Statistics.deepestFloor) {
            level = Dungeon.newLevel();
        } else {
            Dungeon.depth++;
            level = Dungeon.loadLevel(GamesInProgress.curSlot);
        }
        Dungeon.switchLevel( level, level.fallCell( fallIntoPit ));
    }

    private void ascend() throws IOException {
        Actor.fixTime();

        Dungeon.saveAll();
        Dungeon.depth--;
        Level level = Dungeon.loadLevel(GamesInProgress.curSlot);
        Dungeon.switchLevel(level, level.exit);
    }

    private void returnTo() throws IOException {

        Actor.fixTime();

        Dungeon.saveAll();
        Dungeon.depth = returnDepth;
        Level level = Dungeon.loadLevel(GamesInProgress.curSlot);
        Dungeon.switchLevel(level, returnPos);
    }

    private void restore() throws IOException {

        Actor.fixTime();

        GameLog.wipe();

        Dungeon.loadGame(GamesInProgress.curSlot);
        if (Dungeon.depth == -1) {
            Dungeon.depth = Statistics.deepestFloor;
            Dungeon.switchLevel(Dungeon.loadLevel(GamesInProgress.curSlot), -1);
        } else {
            Level level = Dungeon.loadLevel(GamesInProgress.curSlot);
            Dungeon.switchLevel(level, Dungeon.hero.pos);
        }
    }

    private void resurrect() throws IOException {

        Actor.fixTime();

        if (Dungeon.level.locked) {
            Dungeon.hero.resurrect(Dungeon.depth);
            Dungeon.depth--;
            Level level = Dungeon.newLevel();
            Dungeon.switchLevel(level, level.entrance);
        } else {
            Dungeon.hero.resurrect(-1);
            Dungeon.resetLevel();
        }
    }

    private void reset() throws IOException {

        Actor.fixTime();

        Dungeon.depth--;
        Level level = Dungeon.newLevel();
        //FIXME this only partially addresses issues regarding weak floors.
        RegularLevel.weakFloorCreated = false;
        Dungeon.switchLevel(level, level.entrance);
    }

    @Override
    protected void onBackPressed() {
        //Do nothing
    }

    public enum Mode {
        DESCEND, ASCEND, CONTINUE, RESURRECT, RETURN, FALL, RESET, NONE
    }

    private enum Phase {
        FADE_IN, STATIC, FADE_OUT
    }
}
