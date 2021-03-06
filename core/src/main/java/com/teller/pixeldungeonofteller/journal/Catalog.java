package com.teller.pixeldungeonofteller.journal;

import com.teller.pixeldungeonofteller.Badges;
import com.teller.pixeldungeonofteller.items.Item;
import com.teller.pixeldungeonofteller.items.armor.ClothArmor;
import com.teller.pixeldungeonofteller.items.armor.LeatherArmor;
import com.teller.pixeldungeonofteller.items.armor.MailArmor;
import com.teller.pixeldungeonofteller.items.armor.PlateArmor;
import com.teller.pixeldungeonofteller.items.armor.ScaleArmor;
import com.teller.pixeldungeonofteller.items.artifacts.CapeOfThorns;
import com.teller.pixeldungeonofteller.items.artifacts.ChaliceOfBlood;
import com.teller.pixeldungeonofteller.items.artifacts.CloakOfShadows;
import com.teller.pixeldungeonofteller.items.artifacts.DriedRose;
import com.teller.pixeldungeonofteller.items.artifacts.EtherealChains;
import com.teller.pixeldungeonofteller.items.artifacts.HornOfPlenty;
import com.teller.pixeldungeonofteller.items.artifacts.LloydsBeacon;
import com.teller.pixeldungeonofteller.items.artifacts.MasterThievesArmband;
import com.teller.pixeldungeonofteller.items.artifacts.SandalsOfNature;
import com.teller.pixeldungeonofteller.items.artifacts.TalismanOfForesight;
import com.teller.pixeldungeonofteller.items.artifacts.TimekeepersHourglass;
import com.teller.pixeldungeonofteller.items.artifacts.UnstableSpellbook;
import com.teller.pixeldungeonofteller.items.potions.PotionOfExperience;
import com.teller.pixeldungeonofteller.items.potions.PotionOfFrost;
import com.teller.pixeldungeonofteller.items.potions.PotionOfHealing;
import com.teller.pixeldungeonofteller.items.potions.PotionOfInvisibility;
import com.teller.pixeldungeonofteller.items.potions.PotionOfLevitation;
import com.teller.pixeldungeonofteller.items.potions.PotionOfLiquidFlame;
import com.teller.pixeldungeonofteller.items.potions.PotionOfMight;
import com.teller.pixeldungeonofteller.items.potions.PotionOfMindVision;
import com.teller.pixeldungeonofteller.items.potions.PotionOfParalyticGas;
import com.teller.pixeldungeonofteller.items.potions.PotionOfPurity;
import com.teller.pixeldungeonofteller.items.potions.PotionOfStrength;
import com.teller.pixeldungeonofteller.items.potions.PotionOfToxicGas;
import com.teller.pixeldungeonofteller.items.rings.RingOfAccuracy;
import com.teller.pixeldungeonofteller.items.rings.RingOfElements;
import com.teller.pixeldungeonofteller.items.rings.RingOfEvasion;
import com.teller.pixeldungeonofteller.items.rings.RingOfForce;
import com.teller.pixeldungeonofteller.items.rings.RingOfFuror;
import com.teller.pixeldungeonofteller.items.rings.RingOfHaste;
import com.teller.pixeldungeonofteller.items.rings.RingOfMight;
import com.teller.pixeldungeonofteller.items.rings.RingOfSharpshooting;
import com.teller.pixeldungeonofteller.items.rings.RingOfTenacity;
import com.teller.pixeldungeonofteller.items.rings.RingOfWealth;
import com.teller.pixeldungeonofteller.items.scrolls.ScrollOfIdentify;
import com.teller.pixeldungeonofteller.items.scrolls.ScrollOfLullaby;
import com.teller.pixeldungeonofteller.items.scrolls.ScrollOfMagicMapping;
import com.teller.pixeldungeonofteller.items.scrolls.ScrollOfMagicalInfusion;
import com.teller.pixeldungeonofteller.items.scrolls.ScrollOfMirrorImage;
import com.teller.pixeldungeonofteller.items.scrolls.ScrollOfPsionicBlast;
import com.teller.pixeldungeonofteller.items.scrolls.ScrollOfRage;
import com.teller.pixeldungeonofteller.items.scrolls.ScrollOfRecharging;
import com.teller.pixeldungeonofteller.items.scrolls.ScrollOfRemoveCurse;
import com.teller.pixeldungeonofteller.items.scrolls.ScrollOfTeleportation;
import com.teller.pixeldungeonofteller.items.scrolls.ScrollOfTerror;
import com.teller.pixeldungeonofteller.items.scrolls.ScrollOfUpgrade;
import com.teller.pixeldungeonofteller.items.wands.WandOfBlastWave;
import com.teller.pixeldungeonofteller.items.wands.WandOfCorruption;
import com.teller.pixeldungeonofteller.items.wands.WandOfDisintegration;
import com.teller.pixeldungeonofteller.items.wands.WandOfFireblast;
import com.teller.pixeldungeonofteller.items.wands.WandOfFrost;
import com.teller.pixeldungeonofteller.items.wands.WandOfLightning;
import com.teller.pixeldungeonofteller.items.wands.WandOfMagicMissile;
import com.teller.pixeldungeonofteller.items.wands.WandOfPrismaticLight;
import com.teller.pixeldungeonofteller.items.wands.WandOfRegrowth;
import com.teller.pixeldungeonofteller.items.wands.WandOfTransfusion;
import com.teller.pixeldungeonofteller.items.wands.WandOfVenom;
import com.teller.pixeldungeonofteller.items.weapon.missiles.Boomerang;
import com.teller.pixeldungeonofteller.items.weapon.weapons.DualWieldWeapon.Dagger;
import com.teller.pixeldungeonofteller.items.weapon.weapons.DualWieldWeapon.Knuckles;
import com.teller.pixeldungeonofteller.items.weapon.weapons.DualWieldWeapon.Sai;
import com.teller.pixeldungeonofteller.items.weapon.weapons.DualWieldWeapon.Sword;
import com.teller.pixeldungeonofteller.items.weapon.weapons.MainHandWeapon.BattleAxe;
import com.teller.pixeldungeonofteller.items.weapon.weapons.MainHandWeapon.Dirk;
import com.teller.pixeldungeonofteller.items.weapon.weapons.MainHandWeapon.Flail;
import com.teller.pixeldungeonofteller.items.weapon.weapons.MainHandWeapon.HandAxe;
import com.teller.pixeldungeonofteller.items.weapon.weapons.MainHandWeapon.Longsword;
import com.teller.pixeldungeonofteller.items.weapon.weapons.MainHandWeapon.Mace;
import com.teller.pixeldungeonofteller.items.weapon.weapons.MainHandWeapon.MagesStaff;
import com.teller.pixeldungeonofteller.items.weapon.weapons.MainHandWeapon.NewShortsword;
import com.teller.pixeldungeonofteller.items.weapon.weapons.MainHandWeapon.Scimitar;
import com.teller.pixeldungeonofteller.items.weapon.weapons.MainHandWeapon.WarHammer;
import com.teller.pixeldungeonofteller.items.weapon.weapons.MainHandWeapon.Whip;
import com.teller.pixeldungeonofteller.items.weapon.weapons.MainHandWeapon.WornShortsword;
import com.teller.pixeldungeonofteller.items.weapon.weapons.Shield.RoundShield;
import com.teller.pixeldungeonofteller.items.weapon.weapons.TwoHandedWeapon.Glaive;
import com.teller.pixeldungeonofteller.items.weapon.weapons.TwoHandedWeapon.Greataxe;
import com.teller.pixeldungeonofteller.items.weapon.weapons.Shield.Greatshield;
import com.teller.pixeldungeonofteller.items.weapon.weapons.TwoHandedWeapon.Greatsword;
import com.teller.pixeldungeonofteller.items.weapon.weapons.TwoHandedWeapon.Quarterstaff;
import com.teller.pixeldungeonofteller.items.weapon.weapons.TwoHandedWeapon.Spear;
import com.watabou.utils.Bundle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

public enum Catalog {

    WEAPONS,
    ARMOR,
    WANDS,
    RINGS,
    ARTIFACTS,
    POTIONS,
    SCROLLS;

    private LinkedHashMap<Class<? extends Item>, Boolean> seen = new LinkedHashMap<>();

    public Collection<Class<? extends Item>> items(){
        return seen.keySet();
    }

    public boolean allSeen(){
        for (Class<?extends Item> item : items()){
            if (!seen.get(item)){
                return false;
            }
        }
        return true;
    }

    static {
        WEAPONS.seen.put( WornShortsword.class,             false);
        WEAPONS.seen.put( Knuckles.class,                   false);
        WEAPONS.seen.put( Dagger.class,                     false);
        WEAPONS.seen.put( MagesStaff.class,                 false);
        WEAPONS.seen.put( Boomerang.class,                  false);

        WEAPONS.seen.put( NewShortsword.class,              false);
        WEAPONS.seen.put( HandAxe.class,                    false);
        WEAPONS.seen.put( Spear.class,                      false);
        WEAPONS.seen.put( Quarterstaff.class,               false);
        WEAPONS.seen.put( Dirk.class,                       false);

        WEAPONS.seen.put( Sword.class,                      false);
        WEAPONS.seen.put( Mace.class,                       false);
        WEAPONS.seen.put( Scimitar.class,                   false);
        WEAPONS.seen.put( RoundShield.class,                false);
        WEAPONS.seen.put( Sai.class,                        false);
        WEAPONS.seen.put( Whip.class,                       false);

        WEAPONS.seen.put( Longsword.class,                  false);
        WEAPONS.seen.put( BattleAxe.class,                  false);
        WEAPONS.seen.put( Flail.class,                      false);
        //WEAPONS.seen.put( RunicBlade.class,                 false);
        //WEAPONS.seen.put( AssassinsBlade.class,             false);

        WEAPONS.seen.put( Greatsword.class,                 false);
        WEAPONS.seen.put( WarHammer.class,                  false);
        WEAPONS.seen.put( Glaive.class,                     false);
        WEAPONS.seen.put( Greataxe.class,                   false);
        WEAPONS.seen.put( Greatshield.class,                false);

        ARMOR.seen.put( ClothArmor.class,                   false);
        ARMOR.seen.put( LeatherArmor.class,                 false);
        ARMOR.seen.put( MailArmor.class,                    false);
        ARMOR.seen.put( ScaleArmor.class,                   false);
        ARMOR.seen.put( PlateArmor.class,                   false);
        //ARMOR.seen.put( WarriorArmor.class,                 false);
        //ARMOR.seen.put( MageArmor.class,                    false);
        //ARMOR.seen.put( RogueArmor.class,                   false);
        //ARMOR.seen.put( HuntressArmor.class,                false);

        WANDS.seen.put( WandOfMagicMissile.class,           false);
        WANDS.seen.put( WandOfLightning.class,              false);
        WANDS.seen.put( WandOfDisintegration.class,         false);
        WANDS.seen.put( WandOfFireblast.class,              false);
        WANDS.seen.put( WandOfVenom.class,                  false);
        WANDS.seen.put( WandOfBlastWave.class,              false);
        //WANDS.seen.put( WandOfLivingEarth.class,          false);
        WANDS.seen.put( WandOfFrost.class,                  false);
        WANDS.seen.put( WandOfPrismaticLight.class,         false);
        //WANDS.seen.put( WandOfWarding.class,              false);
        WANDS.seen.put( WandOfTransfusion.class,            false);
        WANDS.seen.put( WandOfCorruption.class,             false);
        WANDS.seen.put( WandOfRegrowth.class,               false);

        RINGS.seen.put( RingOfAccuracy.class,               false);
        //RINGS.seen.put( RingOfEnergy.class,                 false);
        RINGS.seen.put( RingOfElements.class,               false);
        RINGS.seen.put( RingOfEvasion.class,                false);
        RINGS.seen.put( RingOfForce.class,                  false);
        RINGS.seen.put( RingOfFuror.class,                  false);
        RINGS.seen.put( RingOfHaste.class,                  false);
        RINGS.seen.put( RingOfMight.class,                  false);
        RINGS.seen.put( RingOfSharpshooting.class,          false);
        RINGS.seen.put( RingOfTenacity.class,               false);
        RINGS.seen.put( RingOfWealth.class,                 false);

        //ARTIFACTS.seen.put( AlchemistsToolkit.class,      false);
        ARTIFACTS.seen.put( CapeOfThorns.class,             false);
        ARTIFACTS.seen.put( ChaliceOfBlood.class,           false);
        ARTIFACTS.seen.put( CloakOfShadows.class,           false);
        ARTIFACTS.seen.put( DriedRose.class,                false);
        ARTIFACTS.seen.put( EtherealChains.class,           false);
        ARTIFACTS.seen.put( HornOfPlenty.class,             false);
        ARTIFACTS.seen.put( LloydsBeacon.class,             false);
        ARTIFACTS.seen.put( MasterThievesArmband.class,     false);
        ARTIFACTS.seen.put( SandalsOfNature.class,          false);
        ARTIFACTS.seen.put( TalismanOfForesight.class,      false);
        ARTIFACTS.seen.put( TimekeepersHourglass.class,     false);
        ARTIFACTS.seen.put( UnstableSpellbook.class,        false);

        POTIONS.seen.put( PotionOfHealing.class,            false);
        POTIONS.seen.put( PotionOfStrength.class,           false);
        POTIONS.seen.put( PotionOfLiquidFlame.class,        false);
        POTIONS.seen.put( PotionOfFrost.class,              false);
        POTIONS.seen.put( PotionOfToxicGas.class,           false);
        POTIONS.seen.put( PotionOfParalyticGas.class,       false);
        POTIONS.seen.put( PotionOfPurity.class,             false);
        POTIONS.seen.put( PotionOfLevitation.class,         false);
        POTIONS.seen.put( PotionOfMindVision.class,         false);
        POTIONS.seen.put( PotionOfInvisibility.class,       false);
        POTIONS.seen.put( PotionOfExperience.class,         false);
        POTIONS.seen.put( PotionOfMight.class,              false);

        SCROLLS.seen.put( ScrollOfIdentify.class,           false);
        SCROLLS.seen.put( ScrollOfUpgrade.class,            false);
        SCROLLS.seen.put( ScrollOfRemoveCurse.class,        false);
        SCROLLS.seen.put( ScrollOfMagicMapping.class,       false);
        SCROLLS.seen.put( ScrollOfTeleportation.class,      false);
        SCROLLS.seen.put( ScrollOfRecharging.class,         false);
        SCROLLS.seen.put( ScrollOfMirrorImage.class,        false);
        SCROLLS.seen.put( ScrollOfTerror.class,             false);
        SCROLLS.seen.put( ScrollOfLullaby.class,            false);
        SCROLLS.seen.put( ScrollOfRage.class,               false);
        SCROLLS.seen.put( ScrollOfPsionicBlast.class,       false);
        SCROLLS.seen.put( ScrollOfMagicalInfusion.class,    false);
    }

    public static LinkedHashMap<Catalog, Badges.Badge> catalogBadges = new LinkedHashMap<>();
    static {
        //catalogBadges.put(WEAPONS, Badges.Badge.ALL_WEAPONS_IDENTIFIED);
        //catalogBadges.put(ARMOR, Badges.Badge.ALL_ARMOR_IDENTIFIED);
        catalogBadges.put(WANDS, Badges.Badge.ALL_WANDS_IDENTIFIED);
        catalogBadges.put(RINGS, Badges.Badge.ALL_RINGS_IDENTIFIED);
        //catalogBadges.put(ARTIFACTS, Badges.Badge.ALL_ARTIFACTS_IDENTIFIED);
        catalogBadges.put(POTIONS, Badges.Badge.ALL_POTIONS_IDENTIFIED);
        catalogBadges.put(SCROLLS, Badges.Badge.ALL_SCROLLS_IDENTIFIED);
    }

    public static boolean isSeen(Class<? extends Item> itemClass){
        for (Catalog cat : values()) {
            if (cat.seen.containsKey(itemClass)) {
                return cat.seen.get(itemClass);
            }
        }
        return false;
    }

    public static void setSeen(Class<? extends Item> itemClass){
        for (Catalog cat : values()) {
            if (cat.seen.containsKey(itemClass) && !cat.seen.get(itemClass)) {
                cat.seen.put(itemClass, true);
                Journal.saveNeeded = true;
            }
        }
        //Badges.validateItemsIdentified();
    }

    private static final String CATALOGS = "catalogs";

    public static void store( Bundle bundle ){

        Badges.loadGlobal();

        ArrayList<String> seen = new ArrayList<>();

        //if we have identified all items of a set, we use the badge to keep track instead.
        if (!Badges.isUnlocked(Badges.Badge.ALL_ITEMS_IDENTIFIED)) {
            for (Catalog cat : values()) {
                if (!Badges.isUnlocked(catalogBadges.get(cat))) {
                    for (Class<? extends Item> item : cat.items()) {
                        if (cat.seen.get(item)) seen.add(item.getSimpleName());
                    }
                }
            }
        }

        bundle.put( CATALOGS, seen.toArray(new String[0]) );

    }

    public static void restore( Bundle bundle ){

        Badges.loadGlobal();

        //logic for if we have all badges
        if (Badges.isUnlocked(Badges.Badge.ALL_ITEMS_IDENTIFIED)){
            for ( Catalog cat : values()){
                for (Class<? extends Item> item : cat.items()){
                    cat.seen.put(item, true);
                }
            }
            return;
        }

        //catalog-specific badge logic
        for (Catalog cat : values()){
            if (Badges.isUnlocked(catalogBadges.get(cat))){
                for (Class<? extends Item> item : cat.items()){
                    cat.seen.put(item, true);
                }
            }
        }

        //general save/load
        if (bundle.contains(CATALOGS)) {
            List<String> seen = Arrays.asList(bundle.getStringArray(CATALOGS));

            for (Catalog cat : values()) {
                for (Class<? extends Item> item : cat.items()) {
                    if (seen.contains(item.getSimpleName())) {
                        cat.seen.put(item, true);
                    }
                }
            }
        }
    }


}
