package com.teller.pixeldungeonofteller.ui.changelist;

import com.teller.pixeldungeonofteller.Assets;
import com.teller.pixeldungeonofteller.items.Ankh;
import com.teller.pixeldungeonofteller.items.ArmorKit;
import com.teller.pixeldungeonofteller.items.food.HawFlakes;
import com.teller.pixeldungeonofteller.items.weapon.weapons.MagicBook.OldBook;
import com.teller.pixeldungeonofteller.items.weapon.weapons.MainHandWeapon.WornShortsword;
import com.teller.pixeldungeonofteller.sprites.ItemSprite;
import com.teller.pixeldungeonofteller.sprites.ItemSpriteSheet;
import com.teller.pixeldungeonofteller.ui.Icons;
import com.teller.pixeldungeonofteller.ui.Window;
import com.watabou.noosa.Image;
import com.watabou.utils.Random;

import java.util.ArrayList;

import javax.swing.Icon;

public class PDoT_Demo_Changes {

    public static void addAllChanges( ArrayList<ChangeInfo> changeInfos ){

        ChangeInfo changes = new ChangeInfo( "Pixel Dungeon of Teller Demo", true, "");
        changes.hardlight( Window.TITLE_COLOR);
        changeInfos.add(changes);

        add_PDT_Demo_FromSPD(changeInfos);
        add_PDT_Demo_Info(changeInfos);
    }

    public static void add_PDT_Demo_Info(ArrayList<ChangeInfo> changeInfos)
    {
        ChangeInfo changes = new ChangeInfo("About background", false, "");
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);

        changes.addButton( new ChangeButton(new Image(Icons.get(Icons.TELLER)), "写在前列",
                "_-_ 本地牢基于破碎的像素地牢SPD _0.4.3c_ 版本开发\n" +
                        "_-_ 然而其已经，并将会融合SPD新版的部分内容\n" +
                        "_-_ 部分代码，设计与原始贴图启发自众多地牢，包括但不限于另类地牢(YAPD)及其衍生无名地牢(NNYPD),黑暗地牢(DPD),月光地牢(MPD)等,设计思路如有与某游戏相同,纯属巧合\n" +

                        "\n" +"_-_ 本地牢正式立项于2019年8月7日,QQ开发群号：901355376"+
                        "\n" +"_-_ 开源于2021年3月17日,github地址：https://github.com/1015561267/Pixel_Dungeon_Of_Teller"+
                        "\n" +"_-_ 在所谓'正式'版本发布前，版本号将维持并将长期维持为Demo"+
                        "\n" +"_-_ 因鸽人原因，开发过程将十分漫长且不断产生众多bug，如您不幸遭遇上述情况，可设法与作者取得联系"+
                        "\n" +"_-_ 个人邮箱：1015561267@qq.com\n"
        ));

        changes.addButton( new ChangeButton(new Image( (Icons.get( Random.Int(2) == 0 ? Icons.PASSERBY : Icons.TRIDENT))), "开发支持",
                "_-_ 作者仅在代码上略知一二，连数值平衡设计都停留在纸笔计算水平！\n" +
                        "_-_ 因此本地牢开发过程中收到众多人员帮助，包括但不限于代码支持，贴图提供，数值设计\n"+
                        "_-_ 如您有意向协助，请联系作者\n"
        ));
    }

    public static void add_PDT_Demo_FromSPD(ArrayList<ChangeInfo> changeInfos)
    {
        ChangeInfo changes = new ChangeInfo("FromSPD", false, "");
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);

        changes.addButton( new ChangeButton(new Image(Icons.get(Icons.SHPX)), "破碎引入",
                "_-_ SPD_0.4.3c_ 是一个个人特性比较弱且建议入手的2d版本，但这也意味着无法使用后来加入的诸多功能！\n" +
                        "_-_ 本项目在0.4.3c基础上，引入众多更高版本功能，以提升游戏体验\n"
        ));

        changes.addButton( new ChangeButton(new Image(Assets.WARRIOR, 0, 90, 12, 15), "2.5D画面",
                "_-_ 画面使用_0.5系列_中引入的2.5D效果\n" +
                        "_-_ 初步完成于_2020-7-12_，物品截取问题导致的“浮空”效果修复于_2020-11-13_\n"
        ));

        changes.addButton( new ChangeButton(new Image(Icons.get(Icons.DEPTH_LG)), "存档位机制",
                "_-_ 使用来自_0.6.4_的多存档位取代原有基于角色的存档\n" +
                        "_-_ 完成于_2020-8-28_\n"
        ));


        changes.addButton( new ChangeButton(new Image(Icons.get(Icons.NOTES)), "地图生成与日志",
                "_-_ 使用来自_0.6_的地图生成算法，与_0.6.2_的日志显示\n" +
                        "_-_ 地图生成算法改动完成于_2020-7-13_\n"+
                        "_-_ 日志显示改动完成于_2020-7-14_\n"
        ));

        changes.addButton( new ChangeButton(new Image(Icons.get(Icons.CHALLENGE_ON)), "成就系统",
                "_-_ 使用来自_0.9.0_的徽章显示效果与逻辑\n" +
                        "_-_ 完成于_2021-5-10_\n"
        ));

        changes.addButton( new ChangeButton(new Image(Icons.get(Icons.WARNING)), "Libgdx",
                "_-_ 底层支持采用来自_0.7.5_的libgdx底层重构\n" +
                        "_-_ 这意味着可以打包出桌面版等，并改进代码结构\n" +
                        "_-_ 附带包括但不限于：语言设置移入设置界面，镜头平滑滚动，本更新界面等\n" +
                        "_-_ 初步完成于_2021-10-6_\n"
        ));
    }
}
