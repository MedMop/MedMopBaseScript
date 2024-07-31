package net.botwithus;

import static net.botwithus.OreBoxState.OreTypeToRockNameMap;
import static net.botwithus.rs3.script.ScriptConsole.println;

import java.util.*;
import java.util.regex.*;
import net.botwithus.rs3.game.achievemnt.*;
import net.botwithus.rs3.game.queries.builders.items.*;
import net.botwithus.rs3.game.skills.*;


enum OreBoxTier {
    NONE,
    BRONZE,
    IRON,
    STEEL,
    MITHRIL,
    ADAMANT,
    RUNE,
    ORIKALKUM,
    NECRONIUM,
    BANE,
    ELDER_RUNE
}

enum OreTypes {
    COPPER,
    TIN,
    IRON,
    COAL,
    SILVER,
    MITHRIL,
    ADAMANTITE,
    LUMINITE,
    GOLD,
    RUNITE,
    ORICHALCITE,
    DRAKOLITH,
    NECRITE,
    PHASMATITE,
    BANITE,
    LIGHT_ANIMICA,
    DARK_ANIMICA

}

public class OreBoxState {


    public static HashMap<String, OreBoxTier> oreBoxTierMap = new HashMap<>() {{
        put("Bronze ore box", OreBoxTier.BRONZE);
        put("Iron ore box", OreBoxTier.IRON);
        put("Steel ore box", OreBoxTier.STEEL);
        put("Mithril ore box", OreBoxTier.MITHRIL);
        put("Adamant ore box", OreBoxTier.ADAMANT);
        put("Rune ore box", OreBoxTier.RUNE);
        put("Orikalkum ore box", OreBoxTier.ORIKALKUM);
        put("Necronium ore box", OreBoxTier.NECRONIUM);
        put("Bane ore box", OreBoxTier.BANE);
        put("Elder rune ore box", OreBoxTier.ELDER_RUNE);
    }};

    public static HashMap<OreTypes, Integer> oreBoxHighCapacityThreshold = new HashMap<>() {{
        put(OreTypes.COPPER, 7);
        put(OreTypes.TIN, 7);
        put(OreTypes.IRON, 18);
        put(OreTypes.COAL, 29);
        put(OreTypes.SILVER, Integer.MAX_VALUE); // never gets increased capacity
        put(OreTypes.MITHRIL, 37);
        put(OreTypes.ADAMANTITE, 41);
        put(OreTypes.LUMINITE, 41);
        put(OreTypes.GOLD, Integer.MAX_VALUE); // never gets increased capacity
        put(OreTypes.RUNITE, 55);
        put(OreTypes.ORICHALCITE, 66);
        put(OreTypes.DRAKOLITH, 66);
        put(OreTypes.NECRITE, 72);
        put(OreTypes.PHASMATITE, 72);
        put(OreTypes.BANITE, 85);
        put(OreTypes.LIGHT_ANIMICA, 95);
        put(OreTypes.DARK_ANIMICA, 95);
    }};

    public static HashMap<OreTypes, String> OreTypeToOreNameMap = new HashMap<>() {{
        put(OreTypes.COPPER, "Copper ore");
        put(OreTypes.TIN, "Tin ore");
        put(OreTypes.IRON, "Iron ore");
        put(OreTypes.COAL, "Coal");
        put(OreTypes.SILVER, "Silver ore");
        put(OreTypes.MITHRIL, "Mithril ore");
        put(OreTypes.ADAMANTITE, "Adamantite ore");
        put(OreTypes.LUMINITE, "Luminite");
        put(OreTypes.GOLD, "Gold ore");
        put(OreTypes.RUNITE, "Runite ore");
        put(OreTypes.ORICHALCITE, "Orichalcite ore");
        put(OreTypes.DRAKOLITH, "Drakolith");
        put(OreTypes.NECRITE, "Necrite ore");
        put(OreTypes.PHASMATITE, "Phasmatite");
        put(OreTypes.BANITE, "Banite ore");
        put(OreTypes.LIGHT_ANIMICA, "Light animica");
        put(OreTypes.DARK_ANIMICA, "Dark animica");
    }};

    public static HashMap<OreTypes, String> OreTypeToRockNameMap = new HashMap<>() {{
        put(OreTypes.COPPER, "Copper rock");
        put(OreTypes.TIN, "Tin rock");
        put(OreTypes.IRON, "Iron rock");
        put(OreTypes.COAL, "Coal rock");
        put(OreTypes.SILVER, "Silver rock");
        put(OreTypes.MITHRIL, "Mithril rock");
        put(OreTypes.ADAMANTITE, "Adamantite rock");
        put(OreTypes.LUMINITE, "Luminite rock");
        put(OreTypes.GOLD, "Gold rock");
        put(OreTypes.RUNITE, "Runite rock");
        put(OreTypes.ORICHALCITE, "Orichalcite rock");
        put(OreTypes.DRAKOLITH, "Drakolith rock");
        put(OreTypes.NECRITE, "Necrite rock");
        put(OreTypes.PHASMATITE, "Phasmatite rock");
        put(OreTypes.BANITE, "Banite rock");
        put(OreTypes.LIGHT_ANIMICA, "Light animica rock");
        put(OreTypes.DARK_ANIMICA, "Dark animica rock");
    }};

    public static HashMap<OreTypes, LocalPlayerUtility.VarLabels> OreToVarLabelsMap = new HashMap<>() {{
        put(OreTypes.COPPER, LocalPlayerUtility.VarLabels.COPPER_ORE_IN_OREBOX);
        put(OreTypes.TIN, LocalPlayerUtility.VarLabels.TIN_ORE_IN_OREBOX);
//        put(OreTypes.IRON, LocalPlayerUtility.VarLabels.IRON_ORE_IN_OREBOX);
//        put(OreTypes.COAL, LocalPlayerUtility.VarLabels.COAL_IN_OREBOX);
//        put(OreTypes.SILVER, LocalPlayerUtility.VarLabels.SILVER_ORE_IN_OREBOX);
//        put(OreTypes.MITHRIL, LocalPlayerUtility.VarLabels.MITHRIL_ORE_IN_OREBOX);
//        put(OreTypes.ADAMANTITE, LocalPlayerUtility.VarLabels.ADAMANTITE_ORE_IN_OREBOX);
//        put(OreTypes.LUMINITE, LocalPlayerUtility.VarLabels.LUMINITE_ORE_IN_OREBOX);
//        put(OreTypes.GOLD, LocalPlayerUtility.VarLabels.GOLD_ORE_IN_OREBOX);
//        put(OreTypes.RUNITE, LocalPlayerUtility.VarLabels.RUNITE_ORE_IN_OREBOX);
//        put(OreTypes.ORICHALCITE, LocalPlayerUtility.VarLabels.ORICHALCITE_ORE_IN_OREBOX);
//        put(OreTypes.DRAKOLITH, LocalPlayerUtility.VarLabels.DRAKOLITH_ORE_IN_OREBOX);
//        put(OreTypes.NECRITE, LocalPlayerUtility.VarLabels.NECRITE_ORE_IN_OREBOX);
//        put(OreTypes.PHASMATITE, LocalPlayerUtility.VarLabels.PHASMATITE_ORE_IN_OREBOX);
        put(OreTypes.BANITE, LocalPlayerUtility.VarLabels.BANITE_ORE_IN_OREBOX);
        put(OreTypes.LIGHT_ANIMICA, LocalPlayerUtility.VarLabels.LIGHT_ANIMICA_IN_OREBOX);
//        put(OreTypes.DARK_ANIMICA, LocalPlayerUtility.VarLabels.DARK_ANIMICA_IN_OREBOX);
    }};

    public static HashMap<OreTypes, OreBoxTier> OreBoxThresholds = new HashMap<>() {{
        put(OreTypes.COPPER, OreBoxTier.BRONZE);
        put(OreTypes.TIN, OreBoxTier.BRONZE);
        put(OreTypes.IRON, OreBoxTier.IRON);
        put(OreTypes.COAL, OreBoxTier.STEEL);
        put(OreTypes.SILVER, OreBoxTier.STEEL);
        put(OreTypes.MITHRIL, OreBoxTier.MITHRIL);
        put(OreTypes.ADAMANTITE, OreBoxTier.ADAMANT);
        put(OreTypes.LUMINITE, OreBoxTier.ADAMANT);
        put(OreTypes.GOLD, OreBoxTier.ADAMANT);
        put(OreTypes.RUNITE, OreBoxTier.RUNE);
        put(OreTypes.ORICHALCITE, OreBoxTier.ORIKALKUM);
        put(OreTypes.DRAKOLITH, OreBoxTier.ORIKALKUM);
        put(OreTypes.NECRITE, OreBoxTier.NECRONIUM);
        put(OreTypes.PHASMATITE, OreBoxTier.NECRONIUM);
        put(OreTypes.BANITE, OreBoxTier.BANE);
        put(OreTypes.LIGHT_ANIMICA, OreBoxTier.ELDER_RUNE);
        put(OreTypes.DARK_ANIMICA, OreBoxTier.ELDER_RUNE);
    }};


    public static final HashMap<OreTypes, LocalPlayerUtility.VarLabels> OreToSpiritsLabelsMap = new HashMap<>() {{
        put(OreTypes.BANITE, LocalPlayerUtility.VarLabels.BANITE_SPIRITS_IN_OREBOX);
        put(OreTypes.LIGHT_ANIMICA, LocalPlayerUtility.VarLabels.LIGHT_ANIMICA_SPIRITS_IN_OREBOX);
    }};

    public static boolean playerOreBoxFull(OreTypes oreType) {
        int oreInBox = LocalPlayerUtility.usefulVars.get(OreToVarLabelsMap.get(oreType)).getValue();
        int oreBoxCapacity = getOreBoxCapacity(oreType);
        return oreInBox >= oreBoxCapacity;
    }

    public static int getOreBoxCapacity(OreTypes type) {
        int miningLevel = Skills.MINING.getLevel();
        boolean hasOresome = playerHasOresomeAchievement();
        boolean highCapacityThresholdPassed = miningLevel >= oreBoxHighCapacityThreshold.get(type);
        return 100 + (hasOresome ? 20 : 0) + (highCapacityThresholdPassed ? 20 : 0);
    }

    public static boolean playerHasOresomeAchievement() {
        Achievement oresome = Achievement.byId(2783);
        return oresome.isCompleted();
    }

    public static OreBoxTier getOreBoxTier() {
        Pattern pattern = Pattern.compile("(.*)ore box(.*)");
        var results = InventoryItemQuery.newQuery(93).name(pattern).results();

        if (results.first() == null) {
            return OreBoxTier.NONE;
        }

        if (results.size() == 1) {
            var firstItem = results.first();
            if (firstItem == null) {
                return OreBoxTier.NONE;
            }
            String itemName = firstItem.getName();
            return mapItemNameToTier(itemName);
        }
        else { // multiple ore boxes is erroneous state, act as though we don't have an ore box in inventory
            println("Multiple ore boxes in inventory, not using any.");
            return OreBoxTier.NONE;
        }
    }

    public static boolean oreBoxTierMeetsRequirements(OreTypes oreType) {
        OreBoxTier oreboxTier = getOreBoxTier();
        OreBoxTier threshold = OreBoxThresholds.get(oreType);
        return oreboxTier.compareTo(threshold) >= 0;

    }

    public static OreBoxTier mapItemNameToTier(String itemName) {
        return oreBoxTierMap.getOrDefault(itemName, OreBoxTier.NONE);
    }

    public static String getRockName(OreTypes oreType) {
        return OreTypeToRockNameMap.get(oreType);
    }


}
