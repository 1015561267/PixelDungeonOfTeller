package com.teller.pixeldungeonofteller.ui.changelist;

import com.teller.pixeldungeonofteller.Assets;
import com.teller.pixeldungeonofteller.items.ArmorKit;
import com.teller.pixeldungeonofteller.items.food.Food;
import com.teller.pixeldungeonofteller.items.food.HawFlakes;
import com.teller.pixeldungeonofteller.items.wands.WandOfBlastWave;
import com.teller.pixeldungeonofteller.items.wands.WandOfFrost;
import com.teller.pixeldungeonofteller.items.weapon.weapons.AttachedWeapon.HiddenBlade;
import com.teller.pixeldungeonofteller.items.weapon.weapons.DualWieldWeapon.Tamahawk;
import com.teller.pixeldungeonofteller.items.weapon.weapons.FireArm.Flintlock;
import com.teller.pixeldungeonofteller.items.weapon.weapons.MagicBook.OldBook;
import com.teller.pixeldungeonofteller.items.weapon.weapons.MainHandWeapon.Dirk;
import com.teller.pixeldungeonofteller.items.weapon.weapons.MainHandWeapon.WornShortsword;
import com.teller.pixeldungeonofteller.items.weapon.weapons.Shield.SawtoothFrisbee;
import com.teller.pixeldungeonofteller.sprites.ItemSprite;
import com.teller.pixeldungeonofteller.ui.Window;
import com.watabou.noosa.Image;

import java.util.ArrayList;

public class PDoT_Demo_Highlights {

    public static void addAllChanges( ArrayList<ChangeInfo> changeInfos ){
        ChangeInfo changes = new ChangeInfo( "Highlights", true, "");
        changes.hardlight( Window.TITLE_COLOR);
        changeInfos.add(changes);

        add_PDT_Demo_DamageSystem(changeInfos);
        add_PDT_Demo_Weapons(changeInfos);
        add_PDT_Demo_Miscs(changeInfos);

    }

    public static void add_PDT_Demo_DamageSystem(ArrayList<ChangeInfo> changeInfos)
    {
        ChangeInfo changes = new ChangeInfo("Damage System", false, "");
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);

        changes.addButton( new ChangeButton(new ItemSprite(new ArmorKit()), "伤害系统重写",
                "_-_ 新的生存资源属性：护甲(人物装备由此改称防具)\n" +
                        "_-_ 伤害类型被分为物理，魔法与真实\n" +
                        "_-_ 人物的伤害类型由武器决定，护甲与护盾改由防具提供\n" +
                        "_-_ 防具护盾将随时间恢复，越久未受损则恢复的越快\n" +
                        "_-_ 防具护甲无法随时间恢复，但可以用找到的修补工具补满\n"+
                        "_-_ 不同护甲仍然对各种伤害类型有减免，且和最大护甲护盾值一样随等级增长\n"+
                        "\n"+"_-_ 初步完成于_2019-7-17_"+
                        "\n"+"_-_ 改进于_2020-1-16_，仍需优化\n"

        ));

        changes.addButton( new ChangeButton(new ItemSprite(new WornShortsword()), "物理伤害(仍需改进)",
                "_-_ 物理伤害分为切割，穿刺，冲击\n" +
                        "_-_ 伤害类型被分为物理，魔法与真实\n" +
                        "_-_ 切割伤害对血量高效，对护甲效果最差\n" +
                        "_-_ 穿刺伤害有效消耗护甲，对护盾不佳\n"+
                        "_-_ 冲击伤害有效消耗护盾，对血量不佳\n"

        ));

        changes.addButton( new ChangeButton(new ItemSprite(new OldBook()), "法术伤害",
                "_-_ 法术伤害被分成七种元素，目前只有奥能属性伤害对护盾有更强效果\n" +
                        "_-_ 烈焰之拳可造成暗属性+火属性的诅咒烈焰伤害，阅读驱邪卷轴可消除其效果\n"+
                        "_-_ 仍需大量内容填充\n"
        ));

        changes.addButton( new ChangeButton(new Image(Assets.SKELETON, 0, 0, 12, 15), "敌人改动",
                "_-_ 有些“能量型”的敌人，如骷髅，现在没有血量，而是有护盾作为生存资源，一般当护盾归零时将死亡\n" +
                        "_-_ 考虑使用一些能造成冲击伤害的武器可以在对抗时获得一定优势\n"
        ));
    }

    public static void add_PDT_Demo_Weapons(ArrayList<ChangeInfo> changeInfos)
    {
        ChangeInfo changes = new ChangeInfo("Weapons", false, "");
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);

        changes.addButton( new ChangeButton(new ItemSprite(new WornShortsword()), "武器逻辑",
                "_-_ 人物拥有两个武器位，武器按装备逻辑分为五种\n" +
                        "_-_ 主手武器：常规的单手剑，刀，锤等，只能装备在主手位\n"+
                        "_-_ 副手武器：副手盾牌，魔法书等，只能装备在副手位\n"+
                        "_-_ 双手武器：长柄或重型武器，只能装备在主手位且副手只能装备附加武器\n"+
                        "_-_ 双持武器：可以装备在主副手且可以装备一对，此时往往有额外加成\n"+
                        "_-_ 附加武器：奇门或轻便武器装置，只能装备在副手位但主手可以装备双手武器\n"+

                        "\n"+"_-_ 初步完成于_2019-7-27_"+
                        "\n"+"_-_ 底层重构于_2021-3-25_"

        ));

        changes.addButton( new ChangeButton(new ItemSprite(new OldBook()), "法术书",
                "_-_ 副手武器，通过消耗法力值施展法术，自身可定制\n" +
                        "\n"+"_-_ 老旧的魔法书完成于_2020-1-24_"+
                        "\n"+"_-_ 圣光之书完成于_2020-1-29_"+
                        "\n"+"_-_ 残页机制完成于_2021-3-19_\n"
        ));

        changes.addButton( new ChangeButton(new ItemSprite(new Flintlock()), "火器",
                "_-_ 种类不定，但都可以在非移动回合中填弹并远程攻击\n" +
                        "\n"+"_-_ 燧发枪（双持）完成于_2020-1-18_"+
                        "\n"+"_-_ 冲锋枪（副手）完成于_2020-8-28_，相关bug于_2021-4-16_修复"+
                        "\n"+"_-_ 手炮（副手）  完成于_2021-4-18_，仍需优化\n"
        ));

        changes.addButton( new ChangeButton(new ItemSprite(new SawtoothFrisbee()), "盾牌",
                "_-_ 种类不定，功能也未完全确定\n" +
                        "\n"+"_-_ 锯齿飞盘（副手）_初步完成_于_2020-3-28_"+
                        "\n"+"_-_ 更多相关内容有待添加\n"

        ));

        changes.addButton( new ChangeButton(new ItemSprite(new HiddenBlade()), "袖剑",
                "_-_ 目前为近战反击效果，刺杀相关仍在制作中\n" +
                        "\n"+"_-_ 初步完成于_2019-7-27_"+
                        "\n"+"_-_ 等待重做中\n"

        ));

        changes.addButton( new ChangeButton(new ItemSprite(new Tamahawk()), "飞斧重做",
                "_-_ 飞斧现在是双持武器，可近战，也可以投掷出去造成可观的输出和流血（如果敌人会流血的话）\n" +
                        "\n"+"_-_ “大斧头飞过来啦！”"+
                        "\n"+"_-_ “啊我得去把斧头捡回来”"+
                        "\n"+"_-_ _完成_于_2019-7-25_\n"
        ));
    }

    public static void add_PDT_Demo_Miscs(ArrayList<ChangeInfo> changeInfos) {
        ChangeInfo changes = new ChangeInfo("Miscs", false, "");
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);

        changes.addButton( new ChangeButton(new ItemSprite(new WandOfBlastWave()), "击退效果",
                "_-_ 思路与代码指导来源于YAPD，击退效果现在将展示在障碍物上撞击的效果，而且若被撞击的是可以动的单位，受撞击者也会被击退\n" +
                        "\n"+"_-_ _初步完成_于_2021-4-14_"+
                        "\n"+"_-_ 撞击效果与伤害缺乏数值，仍需改动\n"
        ));

        changes.addButton( new ChangeButton(new ItemSprite(new WandOfFrost()), "冰面地形",
                "_-_ 加入冰面地形，移动到此地形上时将无法停留，而是按照方向自动滑到对角格\n" +
                        "\n"+"_-_ _初步完成_于_2021-4-14_"+
                        "\n"+"_-_ 仍有bug，正在修复\n"

        ));

        changes.addButton( new ChangeButton(new ItemSprite(new Food()), "饥饿系统",
                "_-_ 增大了饥饿的惩罚，但也强化了休息时的自然回血，思路参考自NNYPD\n" +
                        "\n"+"_-_ _初步完成_于_2021-5-17_"+
                        "\n"+"_-_ 需要进一步改进\n"

        ));

        changes.addButton( new ChangeButton(new ItemSprite(new HawFlakes()), "杂项",
                "_-_ 山楂片！(于_2019-8-10_)\n" +
                        "\n"+"_-_ 暗杀之刃与符文之刃暂时被移除(于_2019-7-2_)"+
                        "\n"+"_-_ 新武器：双截棍(双手)，东方棍(双持)，刺剑(副手)，肋差(副手)，带刺链球(副手)，臂铠(附加)等(于_2019-8-8_)\n"
        ));
    }
}
