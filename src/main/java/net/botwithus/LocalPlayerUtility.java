package net.botwithus;

import static net.botwithus.OreBoxState.*;
import static net.botwithus.rs3.script.ScriptConsole.println;

import java.util.*;
import java.util.regex.*;
import net.botwithus.api.game.hud.inventories.*;
import net.botwithus.api.game.hud.inventories.Inventory;
import net.botwithus.rs3.game.*;
import net.botwithus.rs3.game.hud.interfaces.*;
import net.botwithus.rs3.game.inventories.*;
import net.botwithus.rs3.game.inventories.Backpack;
import net.botwithus.rs3.game.js5.types.*;
import net.botwithus.rs3.game.js5.types.configs.*;
import net.botwithus.rs3.game.js5.types.vars.*;
import net.botwithus.rs3.game.login.*;
import net.botwithus.rs3.game.minimenu.*;
import net.botwithus.rs3.game.minimenu.actions.*;
import net.botwithus.rs3.game.queries.builders.animations.*;
import net.botwithus.rs3.game.queries.builders.characters.*;
import net.botwithus.rs3.game.queries.builders.components.*;
import net.botwithus.rs3.game.queries.builders.items.*;
import net.botwithus.rs3.game.queries.builders.objects.*;
import net.botwithus.rs3.game.scene.entities.characters.*;
import net.botwithus.rs3.game.scene.entities.characters.player.*;
import net.botwithus.rs3.game.vars.*;
import net.botwithus.rs3.input.*;
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


enum TransferOptionType {
    ONE(2, 33882205),
    FIVE(3, 33882208),
    TEN(4, 33882211),
    ALL(7, 33882215),
    X(5, 33882218);

    private final int varbitValue;
    private final int componentValue;

    private TransferOptionType(int varbitValue, int componentValue) {
        this.varbitValue = varbitValue;
        this.componentValue = componentValue;
    }

    public int getComponentValue() {
        return this.componentValue;
    }

    public int getVarbitValue() {
        return this.varbitValue;
    }
}

// static utility class for local player actions
public class LocalPlayerUtility {


    enum VarLabels {
        COPPER_ORE_IN_OREBOX,
        TIN_ORE_IN_OREBOX,
        IRON_ORE_IN_OREBOX,
        IRON_SPIRITS_IN_OREBOX,
        COAL_ORE_IN_OREBOX,
        COAL_SPIRITS_IN_OREBOX,
        COPPER_SPIRITS_IN_OREBOX,
        TIN_SPIRITS_IN_OREBOX,
        MITHRIL_ORE_IN_OREBOX,
        MITHRIL_SPIRITS_IN_OREBOX,
        ADAMANTITE_ORE_IN_OREBOX,
        ADAMANTITE_SPIRITS_IN_OREBOX,
        LUMINITE_ORE_IN_OREBOX,
        LUMINITE_SPIRITS_IN_OREBOX,
        LIGHT_ANIMICA_IN_OREBOX,
        LIGHT_ANIMICA_SPIRITS_IN_OREBOX,
        DARK_ANIMICA_IN_OREBOX,
        DARK_ANIMICA_SPIRITS_IN_OREBOX,
        RUNITE_ORE_IN_OREBOX,
        RUNITE_SPIRITS_IN_OREBOX,
        BANITE_ORE_IN_OREBOX,
        BANITE_SPIRITS_IN_OREBOX,
        PERFECT_JUJU_TIMER,
        MINING_STAMINA,
        TRANSFER_X_QUANTITY,
        PRIMAL_SPIRITS_IN_OREBOX,
        NOVITE_ORE_IN_OREBOX,
        BATHUS_ORE_IN_OREBOX,
        MARMAROS_ORE_IN_OREBOX,
        KRATONIUM_ORE_IN_OREBOX,
        FRACTITE_ORE_IN_OREBOX,
        ZEPHYRIUM_ORE_IN_OREBOX,
        ARGONITE_ORE_IN_OREBOX,
        KATAGON_ORE_IN_OREBOX,
        GORGONITE_ORE_IN_OREBOX,
        PROMETHIUM_ORE_IN_OREBOX,
        //...
    }

    public static Random lpuRandom = new Random();

    public static final HashMap<VarLabels, ClientVariable> usefulVars = new HashMap<>();

    static {
        usefulVars.put(VarLabels.COPPER_ORE_IN_OREBOX, new ClientVariable(MedMoVarType.VARBIT, 43188));
        usefulVars.put(VarLabels.BANITE_ORE_IN_OREBOX, new ClientVariable(MedMoVarType.VARBIT, 43216));
        usefulVars.put(VarLabels.TIN_ORE_IN_OREBOX, new ClientVariable(MedMoVarType.VARBIT, 43190));
        usefulVars.put(VarLabels.IRON_ORE_IN_OREBOX, new ClientVariable(MedMoVarType.VARBIT, 43192));
        usefulVars.put(VarLabels.IRON_SPIRITS_IN_OREBOX, new ClientVariable(MedMoVarType.VARP, 11516));
        usefulVars.put(VarLabels.RUNITE_ORE_IN_OREBOX, new ClientVariable(MedMoVarType.VARBIT, 43206));
        usefulVars.put(VarLabels.RUNITE_SPIRITS_IN_OREBOX, new ClientVariable(MedMoVarType.VARP, 11522));
        usefulVars.put(VarLabels.BANITE_SPIRITS_IN_OREBOX, new ClientVariable(MedMoVarType.VARP, 11528));
        usefulVars.put(VarLabels.PERFECT_JUJU_TIMER, new ClientVariable(MedMoVarType.VARBIT, 26028));
        usefulVars.put(VarLabels.MINING_STAMINA, new ClientVariable(MedMoVarType.VARBIT, 43187));
        usefulVars.put(VarLabels.LIGHT_ANIMICA_IN_OREBOX, new ClientVariable(MedMoVarType.VARBIT, 43218));
        usefulVars.put(VarLabels.LIGHT_ANIMICA_SPIRITS_IN_OREBOX, new ClientVariable(MedMoVarType.VARP, 11529));
        usefulVars.put(VarLabels.DARK_ANIMICA_IN_OREBOX, new ClientVariable(MedMoVarType.VARBIT, 43220));
        usefulVars.put(VarLabels.DARK_ANIMICA_SPIRITS_IN_OREBOX, new ClientVariable(MedMoVarType.VARP, 11530));
        usefulVars.put(VarLabels.COPPER_SPIRITS_IN_OREBOX, new ClientVariable(MedMoVarType.VARP, 11514));
        usefulVars.put(VarLabels.TIN_SPIRITS_IN_OREBOX, new ClientVariable(MedMoVarType.VARP, 11515));
        usefulVars.put(VarLabels.COAL_ORE_IN_OREBOX, new ClientVariable(MedMoVarType.VARBIT, 43194));
        usefulVars.put(VarLabels.COAL_SPIRITS_IN_OREBOX, new ClientVariable(MedMoVarType.VARP, 11517));
        usefulVars.put(VarLabels.MITHRIL_ORE_IN_OREBOX, new ClientVariable(MedMoVarType.VARBIT, 43198));
        usefulVars.put(VarLabels.MITHRIL_SPIRITS_IN_OREBOX, new ClientVariable(MedMoVarType.VARP, 11520));
        usefulVars.put(VarLabels.ADAMANTITE_ORE_IN_OREBOX, new ClientVariable(MedMoVarType.VARBIT, 43200));
        usefulVars.put(VarLabels.ADAMANTITE_SPIRITS_IN_OREBOX, new ClientVariable(MedMoVarType.VARP, 11521));
        usefulVars.put(VarLabels.LUMINITE_ORE_IN_OREBOX, new ClientVariable(MedMoVarType.VARBIT, 43202));
        usefulVars.put(VarLabels.LUMINITE_SPIRITS_IN_OREBOX, new ClientVariable(MedMoVarType.VARP, 11523));
        usefulVars.put(VarLabels.TRANSFER_X_QUANTITY, new ClientVariable(MedMoVarType.VARP, 111));
        usefulVars.put(VarLabels.PRIMAL_SPIRITS_IN_OREBOX, new ClientVariable(MedMoVarType.VARP, 11809));
        usefulVars.put(VarLabels.NOVITE_ORE_IN_OREBOX, new ClientVariable(MedMoVarType.VARBIT, 55880));
        usefulVars.put(VarLabels.BATHUS_ORE_IN_OREBOX, new ClientVariable(MedMoVarType.VARBIT, 55883));
        usefulVars.put(VarLabels.MARMAROS_ORE_IN_OREBOX, new ClientVariable(MedMoVarType.VARBIT, 55886));
        usefulVars.put(VarLabels.KRATONIUM_ORE_IN_OREBOX, new ClientVariable(MedMoVarType.VARBIT, 55889));
        usefulVars.put(VarLabels.FRACTITE_ORE_IN_OREBOX, new ClientVariable(MedMoVarType.VARBIT, 55892));
        usefulVars.put(VarLabels.ZEPHYRIUM_ORE_IN_OREBOX, new ClientVariable(MedMoVarType.VARBIT, 55895));
        usefulVars.put(VarLabels.ARGONITE_ORE_IN_OREBOX, new ClientVariable(MedMoVarType.VARBIT, 55898));
        usefulVars.put(VarLabels.KATAGON_ORE_IN_OREBOX, new ClientVariable(MedMoVarType.VARBIT, 55901));
        usefulVars.put(VarLabels.GORGONITE_ORE_IN_OREBOX, new ClientVariable(MedMoVarType.VARBIT, 55904));
        usefulVars.put(VarLabels.PROMETHIUM_ORE_IN_OREBOX, new ClientVariable(MedMoVarType.VARBIT, 55907));
    }

    public static boolean playerHasPorterEquipped(LocalPlayer player) {
        Pattern pattern = Pattern.compile("(.*)[Ss]ign of the porter (.*)");
        var results = InventoryItemQuery.newQuery(94).name(pattern).results();
        return results.size() > 0;
    }


    public static boolean playerHasPorters() {
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

    public static boolean playerHasOreBox() {
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
        if (playerHasPorters() && !playerHasPorterEquipped(player)) {
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

        if (playerHasOreBox() && backpackContainsUnnotedOre(oreType) && !playerHasOreBoxFull(oreType)) {
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

    public static boolean depositOresAtFurnace() {
        // check that there is indeed a furnace nearby
        var obj = SceneObjectQuery.newQuery().name("Furnace").results().nearest();
        if (obj == null) {
            println("No furnace found.");
            return false;
        }
        // interact with the furnace
        return obj.interact("Deposit-All (Into Metal Bank)");
    }

    public static boolean depositOresAtForge() {
        // check that there is indeed a forge nearby
        var obj = SceneObjectQuery.newQuery().name("Forge").results().nearest();
        if (obj == null) {
            println("No forge found.");
            return false;
        }
        // interact with the forge
        return obj.interact("Deposit-all (into metal bank)");
    }

    public static boolean emptyOreBoxInBank(OreTypes oreType) {
        if (!Bank.isOpen()) {
            Bank.open();
            if (!Execution.delayUntil(5000, Bank::isOpen)) {
                return false;
            }
        }

        Pattern oreBoxPattern = Pattern.compile("(.*) ore box");
        if (playerHasOreBox()) {
            var result = InventoryItemQuery.newQuery(93).name(oreBoxPattern).results().first();
            if (result == null) {
                println("No ore box found.");
                return false;
            }
            var slot = result.getSlot();

            println("Emptying ore box...");
            MiniMenu.interact(ComponentAction.COMPONENT.getType(), 8, slot, 33882127);
            if (Execution.delayUntil(5000, () -> playerHasOreBoxEmpty(oreType))) {
                println("Ore box emptied.");
                return true;
            }
            else {
                println("Failed to empty ore box.");
                return false;
            }
        }
        else {
            println("No ore box found.");
            return true;
        }
    }

    /**
     * Deposits ores at the bank. DOES NOT CLOSE THE BANK INTERFACE
     * @return success
     */
    public static boolean depositOresAtBank(OreTypes oreType) {
        // check if bank interface is open
        if (!Bank.isOpen()) {
            println("Bank interface not open.");
            Bank.open();
            if (!Execution.delayUntil(5000, Bank::isOpen)) {
                return false;
            }
            Execution.delay(lpuRandom.nextLong(250, 500));
        }

        // in theory bank should be open now

        // empty ore box
        println("Emptying ore box...");
        if (!emptyOreBoxInBank(oreType)) {
            println("Failed to empty ore box.");
            return false;
        }

        Execution.delay(lpuRandom.nextLong(1000, 3500));
        Pattern pattern = Pattern.compile("^(?!.*ore box)(.* ore|Coal|Drakolith|Luminite|Phasmatite|Light animica|Dark animica)$");
        println("Checking inventory for ores...");
        if (Bank.depositAll(pattern)) {
            println("Deposited ores.");
            return true;
        }
        else {
            println("Failed to deposit ores.");
            return false;
        }
    }

    public static boolean withdrawFiveFromBank(String itemName) {
        // check if bank interface is open
        if (!Bank.isOpen()) {
            Bank.open();
            if (!Execution.delayUntil(5000, Bank::isOpen)) {
                return false;
            }
        }
        // in theory bank should be open now
        Pattern pattern = Pattern.compile(itemName);
        return Bank.withdraw(pattern, TransferOptionType.FIVE.getVarbitValue());
    }

    public static boolean withdrawTenFromBank(String itemName) {
        // check if bank interface is open
        if (!Bank.isOpen()) {
            Bank.open();
            if (!Execution.delayUntil(5000, Bank::isOpen)) {
                return false;
            }
        }
        // in theory bank should be open now
        Pattern pattern = Pattern.compile(itemName);
        return Bank.withdraw(pattern, TransferOptionType.TEN.getVarbitValue());
    }

    public static boolean withdrawOneOfItem(String itemName) {
        if (!Bank.isOpen()) {
            Bank.open();
            if (!Execution.delayUntil(5000, Bank::isOpen)) {
                return false;
            }
        }

        Pattern pattern = Pattern.compile(itemName);
        return Bank.withdraw(pattern, TransferOptionType.ONE.getVarbitValue());
    }

    public static boolean withdrawAllOfItem(String itemName) {
        if (!Bank.isOpen()) {
            Bank.open();
            if (!Execution.delayUntil(5000, Bank::isOpen)) {
                return false;
            }
        }

        Pattern pattern = Pattern.compile(itemName);
        Execution.delay(lpuRandom.nextLong(2000, 4000));
        return Bank.withdrawAll(pattern);
    }

    public static int getTransferXQuantity() {
        return usefulVars.get(VarLabels.TRANSFER_X_QUANTITY).getValue();
    }

    public static String getCurrentTransferXInputString() {
        var comp = ComponentQuery.newQuery(517).componentIndex(112).results().first();
        if (comp != null) {
            return comp.getText();
        }
        return "";
    }

    public static Queue<Character> getInputFromStr(String str) {
        Queue<Character> queue = new LinkedList<>();
        for (char c : str.toCharArray()) {
            queue.add(c);
        }
        return queue;
    }

    public static boolean modifyTransferXValue(int value) {
        String val = String.valueOf(value);
        if (!Bank.isOpen()) {
            Bank.open();
            if (!Execution.delayUntil(5000, Bank::isOpen)) {
                return false;
            }
        }
        if (getTransferXQuantity() == value) {
            return true;
        }


        if (!MiniMenu.interact(ComponentAction.COMPONENT.getType(), 1, -1, 33882434)) {
            println("Could not perform the cancel input");
        }
        else {
            println("Cancelled input");
            Execution.delay(lpuRandom.nextLong(250, 500));
        }

        // click text box
        if (MiniMenu.interact(ComponentAction.COMPONENT.getType(), 1, -1, 33882226)) {
            Execution.delay(lpuRandom.nextLong(250, 500));
            // type in value
            KeyboardInput.enter(val);  // attempt to type in value
            if (!Execution.delayUntil(30000, () -> getCurrentTransferXInputString().equals(val))) {
                println("Failed to type in value.");
                return false;
            }
            // ideally, we have finished typing the value
            Execution.delay(lpuRandom.nextLong(250, 500));
            // press enter
            KeyboardInput.enter("\n");
        }

        return Execution.delayUntil(1000, () -> getTransferXQuantity() == value);
    }

    public static boolean withdrawXOfItem(String itemName, int quantity) {
        if (!Bank.isOpen()) {
            Bank.open();
            if (!Execution.delayUntil(5000, Bank::isOpen)) {
                return false;
            }
        }

        if (quantity == 1) {
            println("Withdrawing one of " + itemName);
            return withdrawOneOfItem(itemName);
        }

        if (quantity == 5) {
            println("Withdrawing five of " + itemName);
            return withdrawFiveFromBank(itemName);
        }

        if (quantity == 10) {
            println("Withdrawing ten of " + itemName);
            return withdrawTenFromBank(itemName);
        }

        Pattern pattern = Pattern.compile(itemName);
        for (int i = 1; i <= 5; i++) {
            if (!modifyTransferXValue(quantity)) {
                println("Failed to set transfer quantity (" + i + "/5)");
                if (i == 5) {
                    return false;
                }
            }
            else {
                println("Set transfer quantity to " + quantity);
                break;
            }
        }

        for (int i = 1; i <= 5; i++) {
            if (!setTransferOption(TransferOptionType.X)) {
                println("Failed to set transfer option (" + i + "/5)");
                if (i == 5) {
                    return false;
                }
            }
            else {
                println("Set transfer option to X");
                break;
            }
        }

        Execution.delay(lpuRandom.nextLong(1000, 2000));

        // get slot of item
        println("Performing item query for " + itemName + " (might take a while...)");
        var item = InventoryItemQuery.newQuery(95).name(pattern).results().first();
        if (item == null) {
            return false;
        }
        // click the item
        if (!MiniMenu.interact(ComponentAction.COMPONENT.getType(), 1, item.getSlot(), 33882307)) {
            println("Failed to click the item");
            return false;
        }

        Execution.delay(lpuRandom.nextLong(1000, 2000));
        return setTransferOption(TransferOptionType.ONE);
    }

    public static boolean withdrawItemsFromBank(String itemName, int targetQuantity) {
        // check if bank interface is open
        if (!Bank.isOpen()) {
            Bank.open();
            if (!Execution.delayUntil(5000, Bank::isOpen)) {
                return false;
            }
        }
        // in theory bank should be open now
        Pattern pattern = Pattern.compile(itemName);
        // get quantity of item currently in inventory
        var itemsInInventory = InventoryItemQuery.newQuery(93).name(pattern).results();
        int itemQuantity = itemsInInventory.size();
        var item = itemsInInventory.first();
        if (item == null) {
            println("No " + itemName + " found in inventory.");
        }
        else {

            // if stackable, get stack count
            // if not stackable, use total count.
            ItemType type = ConfigManager.getItemType(item.getId());
            if (type == null) {
                return false;
            }
            ItemType.Stackability stackability = type.getStackability();
            switch (stackability) {
                case ALWAYS -> itemQuantity = item.getStackSize();
                case NEVER, SOMETIMES -> itemQuantity = itemsInInventory.size();
            }
        }


        // get quantity of item currently in bank
        var result = InventoryItemQuery.newQuery(95).name(pattern).results().first();
        if (result == null) {
            println("No " + itemName + " found in bank.");
            return false;
        }
        int itemQuantityInBank = result.getStackSize();

//        int itemQuantityInBank = Bank.getCount(pattern);
        // no items in bank
        if (itemQuantityInBank == 0) {
            println("No " + itemName + " found in bank.");
            return false;
        }

        int itemDifference = targetQuantity - itemQuantity;
        println("Item quantity difference for " + itemName + " is: " + itemDifference);

        if (itemDifference <= 0) {
            return true; // we already have desired quantity in inventory
        }

        // how many we need to withdraw to get to target quantity
        if (itemQuantityInBank < itemDifference) {
            withdrawAllOfItem(itemName);
            return true;
        }

        return withdrawXOfItem(itemName, itemDifference);
    }

    public static boolean setTransferOption(TransferOptionType option) {
        int depositOptionState = VarManager.getVarbitValue(45189);
        if (depositOptionState == option.getVarbitValue()) {
            return true;
        }

        var result = MiniMenu.interact(ComponentAction.COMPONENT.getType(), 1, -1, option.getComponentValue());
        Execution.delay(lpuRandom.nextLong(250, 500));
        return result;
    }

    public static void useOneItemOnAnother(int itemId1, int itemId2) {
        try {
            var item1 = InventoryItemQuery.newQuery(93).ids(itemId1).results().first();
            if (item1 == null) {
                println("Item 1 not found.");
                return;
            }
            var item2 = InventoryItemQuery.newQuery(93).ids(itemId2).results().first();
            if (item2 == null) {
                println("Item 2 not found.");
                return;
            }
            MiniMenu.interact(SelectableAction.SELECTABLE_COMPONENT.getType(), 0, item1.getSlot(), 96534533);
            Execution.delay(500);
//            MiniMenu.interact(ComponentAction.COMPONENT.getType(), 8, item2.getSlot(), 96534533); // this drops the item
//            MiniMenu.interact(ComponentAction.COMPONENT.getType(), 10, item2.getSlot(), 96534533); // this examines the item
            MiniMenu.interact(SelectableAction.SELECT_COMPONENT_ITEM.getType(), 0, item2.getSlot(), 96534533);
        } catch (Exception e) {
            println("Error using one item on another: " + e);
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

    public static boolean playerHasOreBoxEmpty(OreTypes oreType) {
        int oreInBox = usefulVars.get(OreToVarLabelsMap.get(oreType)).getValue();
        return oreInBox == 0;
    }

    public static boolean playerHasNoStoneSpirits(LocalPlayer player, OreTypes oreType) {
        int spiritsInBox = usefulVars.get(OreToSpiritsLabelsMap.get(oreType)).getValue();
        return spiritsInBox == 0;
    }

    public static boolean captureSerenSpiritIfPresent() {
        // search for the "Seren spirit" npc
        var results = NpcQuery.newQuery().name("Seren spirit").results();
        if (results.size() == 0) {
            return false;
        }
        var serenSpirit = results.first();
        if (serenSpirit == null) {
            return false;
        }

        // interact with the seren spirit
        return serenSpirit.interact("Capture");
    }

    public static boolean isSerenSpiritPresent() {
        // search for the "Seren spirit" npc
        var results = NpcQuery.newQuery().name("Seren spirit").results();
        return results.size() > 0;
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

    public static boolean rockertunityNearby() {
        var rockertunitySparkles = SpotAnimationQuery.newQuery().ids(7164, 7165).results();
        return rockertunitySparkles.size() > 0;
    }
}