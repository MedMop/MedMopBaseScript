package net.botwithus;

import static net.botwithus.OreBoxState.*;
import static net.botwithus.rs3.script.ScriptConsole.println;

import java.awt.*;
import java.util.*;
import java.util.regex.*;
import net.botwithus.rs3.game.*;
import net.botwithus.rs3.game.inventories.*;
import net.botwithus.rs3.game.js5.types.configs.*;
import net.botwithus.rs3.game.js5.types.vars.*;
import net.botwithus.rs3.game.login.*;
import net.botwithus.rs3.game.minimenu.*;
import net.botwithus.rs3.game.minimenu.actions.*;
import net.botwithus.rs3.game.queries.builders.items.*;
import net.botwithus.rs3.game.scene.entities.characters.*;
import net.botwithus.rs3.game.scene.entities.characters.player.*;
import net.botwithus.rs3.game.vars.*;
import net.botwithus.rs3.script.*;


enum MedMoVarType {
    VARBIT,
    VARP
}

class ClientVariable {
    public MedMoVarType varType;
    public int varKey;

    public ClientVariable(MedMoVarType varType, int varKey) {
        this.varType = varType;
        this.varKey = varKey;
    }

    public int getValue() {
        switch (varType) {
            case VARBIT -> {
                return VarManager.getVarbitValue(varKey);
            }
            case VARP -> {
                VarDomainType type = VarManager.getVarDomain(varKey);
                return VarManager.getVarValue(type, varKey);
            }
            default -> {

                return -1;
            }
        }
    }
}

// static utility class for local player actions
public class LocalPlayerUtility {


    enum VarLabels {
        COPPER_ORE_IN_OREBOX,
        TIN_ORE_IN_OREBOX,
        LIGHT_ANIMICA_IN_OREBOX,
        LIGHT_ANIMICA_SPIRITS_IN_OREBOX,
        RUNITE_ORE_IN_OREBOX,
        BANITE_ORE_IN_OREBOX,
        BANITE_SPIRITS_IN_OREBOX,
        PERFECT_JUJU_TIMER,
        MINING_STAMINA
        //...
    }

    public static final HashMap<VarLabels, ClientVariable> usefulVars = new HashMap<>();

    static {
        usefulVars.put(VarLabels.COPPER_ORE_IN_OREBOX, new ClientVariable(MedMoVarType.VARBIT, 43188));
        usefulVars.put(VarLabels.BANITE_ORE_IN_OREBOX, new ClientVariable(MedMoVarType.VARBIT, 43216));
        usefulVars.put(VarLabels.TIN_ORE_IN_OREBOX, new ClientVariable(MedMoVarType.VARBIT, 43190));
        usefulVars.put(VarLabels.RUNITE_ORE_IN_OREBOX, new ClientVariable(MedMoVarType.VARBIT, 43206));
        usefulVars.put(VarLabels.BANITE_SPIRITS_IN_OREBOX, new ClientVariable(MedMoVarType.VARP, 11528));
        usefulVars.put(VarLabels.PERFECT_JUJU_TIMER, new ClientVariable(MedMoVarType.VARBIT, 26028));
        usefulVars.put(VarLabels.MINING_STAMINA, new ClientVariable(MedMoVarType.VARBIT, 43187));
        usefulVars.put(VarLabels.LIGHT_ANIMICA_IN_OREBOX, new ClientVariable(MedMoVarType.VARBIT, 43218));
        usefulVars.put(VarLabels.LIGHT_ANIMICA_SPIRITS_IN_OREBOX, new ClientVariable(MedMoVarType.VARP, 11529));
    }


    public static boolean playerHasPorterEquipped(LocalPlayer player) {
        Pattern pattern = Pattern.compile("(.*)sign of the porter(.*)");
        var results = InventoryItemQuery.newQuery(94).name(pattern).results();
        return results.size() > 0;
    }


    public static boolean playerHasPorters(LocalPlayer player) {
        Pattern pattern = Pattern.compile("[Ss]ign of the porter (.*)");
        var results = InventoryItemQuery.newQuery(93).name(pattern).results();
        return results.size() > 0;
    }

    public static boolean playerHasPerfectJujuMiningBuff(LocalPlayer player) {
        return usefulVars.get(VarLabels.PERFECT_JUJU_TIMER).getValue() > 0;
    }

    public static boolean playerHasPerfectJujuMiningPotions(LocalPlayer player) {
        Pattern pattern = Pattern.compile("Perfect juju mining (.*)");
        var results = InventoryItemQuery.newQuery(93).name(pattern).results();
        return results.size() > 0;
    }

    public static boolean playerHasOreBox(LocalPlayer player) {
        Pattern pattern = Pattern.compile("(.*) ore box");
        var results = InventoryItemQuery.newQuery(93).name(pattern).results();
        return results.size() > 0;
    }

    public static boolean playerHasMagicNotepaper(LocalPlayer player) {
        Pattern pattern = Pattern.compile("Magic notepaper");
        var results = InventoryItemQuery.newQuery(93).name(pattern).results();
        return results.size() > 0;
    }

    public static boolean playerHasNoMiningStamina(LocalPlayer player) {
        var headbars = player.getHeadbars();
        for (Headbar headbar : headbars) {
            if (headbar.getId() == 5) {
//                println("Mining stamina: " + headbar.getWidth());
                return (headbar.getWidth() == 0);
            }
        }
        return false;
    }

    // returns success
    public static boolean drinkPerfectJujuMiningPotion(LocalPlayer player) {
        Pattern pattern = Pattern.compile("Perfect juju mining (.*)");
        if (playerHasPerfectJujuMiningPotions(player) && !playerHasPerfectJujuMiningBuff(player)) {
            println("Drinking perfect juju mining potion...");
            var result = InventoryItemQuery.newQuery(93).name(pattern).results().first();
            if (result == null) {
                println("No perfect juju mining potion found.");
                return false;
            }
            String name = result.getName();
            Backpack.interact(name, "Drink");
            Execution.delayUntil(5000, () -> playerHasPerfectJujuMiningBuff(player));
            if (!playerHasPerfectJujuMiningBuff(player)) {
                println("Failed to drink perfect juju mining potion.");
                return false;
            }
            return true;
        }
        else if (playerHasPerfectJujuMiningBuff(player)) {
            println("Already have perfect juju mining buff.");
            return true;
        }
        else {
            println("No perfect juju mining potion found.");
            return false;
        }
    }

    public static boolean equipPorter(LocalPlayer player) {
        Pattern activePorterPattern = Pattern.compile("Active ([sS])ign of the porter (.*)");
        Pattern pattern = Pattern.compile("([sS])ign of the porter (.*)");
        if (playerHasPorters(player) && !playerHasPorterEquipped(player)) {
            println("Equipping sign of the porter...");
            var result = InventoryItemQuery.newQuery(93).name(activePorterPattern).results().first();
            if (result == null) {
                println("No active sign of the porter found.");
                result = InventoryItemQuery.newQuery(93).name(pattern).results().first();
            }

            if (result == null) {
                println("No sign of the porter found.");
                return false;
            }
            Backpack.interact(result.getName(), "Wear");
            Execution.delayUntil(5000, () -> playerHasPorterEquipped(player));
            if (!playerHasPorterEquipped(player)) {
                println("Failed to equip sign of the porter.");
                return false;
            }
            return true;
        }
        else if (playerHasPorterEquipped(player)) {
            println("Already have sign of the porter equipped.");
            return true;
        }
        else {
            println("No sign of the porter found.");
            return false;
        }
    }

    public static boolean fillOreBox(LocalPlayer player, OreTypes oreType) {
        Pattern oreBoxPattern = Pattern.compile("(.*) ore box");
        if (!oreBoxTierMeetsRequirements(oreType)) {
            println("Ore box tier too low.");
            return false;
        }

        if (playerHasOreBox(player) && backpackContainsUnnotedOre(oreType) && !playerHasOreBoxFull(oreType)) {
            println("Filling ore box...");
            var result = InventoryItemQuery.newQuery(93).name(oreBoxPattern).results().first();
            if (result == null) {
                println("No ore box found.");
                return false;
            }
            Backpack.interact(result.getName(), "Fill");
            Execution.delayUntil(5000, () -> backpackContainsUnnotedOre(oreType));
            if (!backpackContainsUnnotedOre(oreType)) {
                println("Failed to fill ore box.");
                return false;
            }
            println("Ore box filled.");
            return true;
        }
        else {
            println("No ore box found or backpack does not contain ores.");
            return true;
        }
    }

    public static boolean useMagicNotepaperOnOre(LocalPlayer player, OreTypes oreType) {
        try {
            println("Attempting to use magic notepaper on ore...");
            if (playerHasMagicNotepaper(player)) {
                var result = InventoryItemQuery.newQuery(93).ids(30372).results().first();
                if (result == null) {
                    println("No magic notepaper found.");
                    return false;
                }
                var oreId = getUnnotedOreId(oreType);
                if (oreId == -1) {
                    println("No ore { " + oreType.name() + " } found.");
                    return false;
                }
                var result2 = InventoryItemQuery.newQuery(93).ids(oreId).results().first();
                if (result2 == null) {
                    println("No ore { " + oreType.name() + " } found.");
                    println("Item id: " + oreId);
                    return false;
                }
                useOneItemOnAnother(result.getId(), result2.getId());
                Execution.delayUntil(5000, () -> !backpackContainsUnnotedOre(oreType));
                if (backpackContainsUnnotedOre(oreType) && playerHasMagicNotepaper(player)) {
                    println("Failed to use magic notepaper, unnoted ore found.");
                    return false;
                }
                return true;
            } else {
                println("No magic notepaper found.");
                return false;
            }
        } catch (Exception e) {
            println("Error using magic notepaper: " + e);
            return false;
        }
    }

    public static boolean useOneItemOnAnother(int itemId1, int itemId2) {
        try {
            var item1 = InventoryItemQuery.newQuery(93).ids(itemId1).results().first();
            if (item1 == null) {
                println("Item 1 not found.");
                return false;
            }
            var item2 = InventoryItemQuery.newQuery(93).ids(itemId2).results().first();
            if (item2 == null) {
                println("Item 2 not found.");
                return false;
            }
            MiniMenu.interact(SelectableAction.SELECTABLE_COMPONENT.getType(), 0, item1.getSlot(), 96534533);
            Execution.delay(500);
//            MiniMenu.interact(ComponentAction.COMPONENT.getType(), 8, item2.getSlot(), 96534533); // this drops the item
//            MiniMenu.interact(ComponentAction.COMPONENT.getType(), 10, item2.getSlot(), 96534533); // this examines the item
            MiniMenu.interact(SelectableAction.SELECT_COMPONENT_ITEM.getType(), 0, item2.getSlot(), 96534533);
            return true;
        } catch (Exception e) {
            println("Error using one item on another: " + e);
            return false;
        }
    }

    public static boolean backpackContainsUnnotedOre(OreTypes oreType) {
        try {
            String oreName = OreTypeToOreNameMap.get(oreType);
            if (oreName == null) {
                println("Invalid ore type.");
                return false;
            }
            Pattern pattern = Pattern.compile(oreName);
            var results = InventoryItemQuery.newQuery(93).name(pattern).results();
            for (var result : results) {
                var type = ConfigManager.getItemType(result.getId());
                if (type == null) {
                    continue;
                }
                if (!type.isNote()) {
                    return true;
                }
            }
            return false;
        }
        catch (Exception e) {
            println("Error checking backpack for unnoted ore: " + e);
            return false;
        }
    }

    public static int getUnnotedOreId(OreTypes oreType) {
        try {
            String oreName = OreTypeToOreNameMap.get(oreType);
            if (oreName == null) {
                println("Invalid ore type.");
                return -1;
            }
            Pattern pattern = Pattern.compile(oreName);
            var results = InventoryItemQuery.newQuery(93).name(pattern).results();
            for (var result : results) {
                var type = ConfigManager.getItemType(result.getId());
                if (type == null) {
                    continue;
                }
                if (!type.isNote()) {
                    println("Unnoted ore id: " + result.getId());
                    return result.getId();
                }
            }
            return -1;
        }
        catch (Exception e) {
            println("Error getting unnoted ore id: " + e);
            return -1;
        }
    }

    public static boolean playerHasOreBoxFull(OreTypes oreType) {
        int oreInBox = usefulVars.get(OreToVarLabelsMap.get(oreType)).getValue();
        int oreBoxCapacity = getOreBoxCapacity(oreType);
        return oreInBox >= oreBoxCapacity;
    }

    public static boolean playerHasNoStoneSpirits(LocalPlayer player, OreTypes oreType) {
        int spiritsInBox = usefulVars.get(OreToSpiritsLabelsMap.get(oreType)).getValue();
        return spiritsInBox == 0;
    }

    public static void logout() {
        println("Logging out...");
        if (Client.getGameState() != Client.GameState.LOGGED_IN) {
            return;
        }
        LoginManager.setAutoLogin(false);
        MiniMenu.interact(ComponentAction.COMPONENT.getType(), 1, -1, 93913156);
        Execution.delayUntil(5000, () -> Client.getGameState() != Client.GameState.LOGGED_IN);
        println("Logged out.");
    }

    public static boolean login() {
        println("Logging in...");
        if (Client.getGameState() == Client.GameState.LOGGED_IN) {
            return true;
        }
        LoginManager.setAutoLogin(true);
        Execution.delayUntil(5000, () -> Client.getGameState() == Client.GameState.LOGGED_IN);
        LoginManager.setAutoLogin(false);
        if (Client.getGameState() != Client.GameState.LOGGED_IN) {
            println("Failed to log in.");
            return false;
        }
        println("Logged in.");
        return true;
    }
}
