package net.botwithus;

//import net.botwithus.api.game.hud.inventories.Backpack;
import static java.lang.System.currentTimeMillis;
import static net.botwithus.rs3.script.ScriptConsole.println;

import java.util.*;
import net.botwithus.internal.scripts.ScriptDefinition;
import net.botwithus.rs3.game.*;
import net.botwithus.rs3.game.inventories.*;
import net.botwithus.rs3.game.login.*;
import net.botwithus.rs3.game.movement.*;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.imgui.*;
import net.botwithus.rs3.script.*;
import net.botwithus.rs3.script.config.*;

import java.time.*;

enum StopModality {
    LOGOUT,
    AFK,
    GO_TO_GRAND_EXCHANGE_AND_LOGOUT,
}

class BaseScriptState {
    public StopModality stopModality = StopModality.LOGOUT;

    public boolean debug = true;

    public boolean breaksEnabled = false;

    public int breakAfter = 60;

    public int breakFor = 5;

    public BaseScriptState() {
        // default constructor
    }
}

class BasePlayerState {
    public boolean inventoryFull = false;

    public void updatePlayerState(LocalPlayer player, OreTypes oreType) {
        // update player state
        inventoryFull = Backpack.isFull();
    }

    public BasePlayerState() {
        // default constructor
    }
}


public class BaseScript extends LoopingScript {

    private BotState botState = BotState.STOPPED;
    private final transient Random random = new Random();

    private final BaseScriptState state = new BaseScriptState();

    private final BasePlayerState playerState = new BasePlayerState();


    enum Locations {
        NONE_SELECTED,
    }

    public static final EnumMap<Locations, Area> areaEnumMap = new EnumMap<>(Locations.class);

    static {
//        areaEnumMap.put(Locations.BANITE_RELEKKA, new Area.Rectangular(
//            new Coordinate(2710, 3878, 0), // bottom left
//            new Coordinate(2720, 3870, 0) // top right
//        ));
//        areaEnumMap.put(Locations.LIGHT_ANIMICA_ANACHRONIA, new Area.Rectangular(
//            new Coordinate(5338, 2250, 0), // bottom left
//            new Coordinate(5342, 2256, 0) // top right
//        ));
    }

    public long currentRunTime = 0;

    public long startTime = -1;

    public String buttonMessage = "";


    private boolean afkMode = false;

    enum BotState {
        IDLE,
        NAVIGATING,

        NAVIGATE_BUTTON,


        TAKING_BREAK,
//        BANKING,
        STOPPED,
        LOGOUT,
        FAILURE
    }

    public BaseScript(String s, ScriptConfig scriptConfig, ScriptDefinition scriptDefinition) {
        super(s, scriptConfig, scriptDefinition);
        this.sgc = new BaseScriptGraphicsContext(getConsole(), this);
        try {
            loadSettings(state.debug);
            LoginManager.setAutoLogin(true);
        } catch (Exception e) {
            println("Error loading settings: " + e.getMessage());
        }
    }

    @Override
    public void onLoop() {
        try {
            //Loops every 100ms by default, to change:
            //this.loopDelay = 500;
            if (botState == BotState.STOPPED) {
                if (state.debug) {
                    println("Bot is stopped, waiting...");
                }
                Execution.delay(random.nextLong(3000, 7000));
                return;
            }

            LocalPlayer player = Client.getLocalPlayer();
            if (player == null || Client.getGameState() != Client.GameState.LOGGED_IN) { // not logged in
                //wait some time so we dont immediately start on login.
                Execution.delay(random.nextLong(3000, 7000));
                return;
            }


            switch (botState) {
                case IDLE -> {
                    setBotState(playerStateNeedsHandling(player));
                }
                case NAVIGATING -> {
                    //do some code that handles your navigation
                    Execution.delay(random.nextLong(1000, 3000));
                    setBotState(BotState.IDLE);
                }
                case NAVIGATE_BUTTON -> {
                    //do some code that handles your navigation
                    setBotState(BotState.STOPPED);
                }
                case TAKING_BREAK -> takeBreak();
                case LOGOUT -> {
                    //do some code that handles your navigation
                    println("Logging out...");
//                    LocalPlayerUtility.logout();
                    setBotState(BotState.STOPPED);
                }
                case FAILURE -> {
                    //do some code that handles your navigation
                    println("Failure state reached, stopping and logging out.");
                    stopBot();
//                    LocalPlayerUtility.logout();
                }
                default -> {
                    println("Default case reached, unimplemented bot state reached: " + botState);
                    setBotState(BotState.FAILURE);
                }
            }
        } catch (Exception e) {
            println("Error in onLoop: " + e.getMessage());
        }
    }

    public BotState playerStateNeedsHandling(LocalPlayer player) {
        if (botState == BotState.STOPPED) {
            return BotState.STOPPED;
        }

        // ...

        return BotState.FAILURE; // should never reach here
    }


    public void performReturningPlayerDelay() {
        if (afkMode) {

        }
        else {
            println("Returning player delay performed in non-afk mode, skipping...");
        }
    }

    // returns how we should stop
    public StopModality getStopModality() {
        return state.stopModality;
    }

    public void setStopModality(StopModality modality) {
        if (state.stopModality != modality) {
            println("Stop Modality: " + modality);
            // update stop modality (for example, if we want to stop after a certain time, etc.
            state.stopModality = modality;
            saveSettings(state.debug);
        }
    }

    public NativeInteger getStopModalityIndex() {
        return new NativeInteger(state.stopModality.ordinal());
    }

    public String[] getStopModalityOptions() {
        return Arrays.stream(StopModality.values()).map(Enum::name).toArray(String[]::new);
    }


    public void updateTimer() {
        if (startTime == -1) {
            startTime = currentTimeMillis();
        }
        currentRunTime = (currentTimeMillis() - startTime) / 1000L;
        if (currentRunTime >= state.breakAfter * 60L) {
            LoginManager.setAutoLogin(false);
            takeBreak();
            resetTimer();
        }
    }

    public BaseScriptState getSettingsFromConfig(ImmutableConfig configuration) {
        BaseScriptState settings = new BaseScriptState();
        try {
            settings.breakAfter = Integer.parseInt(configuration.getProperty("breakAfter"));
            settings.breakFor = Integer.parseInt(configuration.getProperty("breakFor"));
            settings.breaksEnabled = Boolean.parseBoolean(configuration.getProperty("breaksEnabled"));
            settings.stopModality = StopModality.valueOf(configuration.getProperty("stopModality"));
            settings.debug = Boolean.parseBoolean(configuration.getProperty("debug"));
        } catch (Exception e) {
            println("Error getting settings from config: " + e.getMessage());
        }
        return settings;
    }

    public void loadSettings(boolean debug) {
        try {
            // load settings from file
            // bot state should always start as idle
            // serialize porter, juju, etc, delay and break settings to store in file
            BaseScriptState loadedSettings = getSettingsFromConfig(getConfiguration());

            if (state.breakAfter != loadedSettings.breakAfter && debug) {
                println("Break After: " + loadedSettings.breakAfter);
            }
            state.breakAfter = loadedSettings.breakAfter;

            if (state.breakFor != loadedSettings.breakFor && debug) {
                println("Break For: " + loadedSettings.breakFor);
            }
            state.breakFor = loadedSettings.breakFor;

            if (state.breaksEnabled != loadedSettings.breaksEnabled && debug) {
                println("Breaks Enabled: " + loadedSettings.breaksEnabled);
            }
            state.breaksEnabled = loadedSettings.breaksEnabled;

            if (state.stopModality != loadedSettings.stopModality && debug) {
                println("Stop Modality: " + loadedSettings.stopModality);
            }
            state.stopModality = loadedSettings.stopModality;

            if (state.debug != debug) {
                println("Debug: " + debug);
            }
            state.debug = loadedSettings.debug;
        } catch (Exception e) {
            println("Error loading settings: " + e.getMessage());
        }
    }

    public void saveSettings(boolean debug) {
        try {
            if (state.debug) {
                println("Saving settings...");
            }
            configuration.addProperty("breakAfter", String.valueOf(state.breakAfter));
            configuration.addProperty("breakFor", String.valueOf(state.breakFor));
            configuration.addProperty("breaksEnabled", String.valueOf(state.breaksEnabled));
            configuration.addProperty("stopModality", state.stopModality.name());
            configuration.addProperty("debug", String.valueOf(debug));

            configuration.save();
        } catch (Exception e) {
            println("Error saving settings: " + e.getMessage());
        }
    }



    public void takeBreak() {
        if (state.debug) {
            println("Taking a break...");
        }
        // check stop modality
        StopModality modality = getStopModality();
        if (modality == StopModality.LOGOUT) {
            LoginManager.setAutoLogin(false);
            LocalPlayerUtility.logout();
            Execution.delayUntil(5000, () -> Client.getGameState() != Client.GameState.LOGGED_IN);
            if (Client.getGameState() == Client.GameState.LOGGED_IN) {
                println("Failed to logout, stopping bot.");
                setBotState(BotState.STOPPED);
            }
        }
        else if (modality == StopModality.AFK) {
            LoginManager.setAutoLogin(false);
            setBotState(BotState.STOPPED);
        }
        else {
            println("Stop modality not implemented, stopping bot.");
            setBotState(BotState.STOPPED);
        }
    }



    public boolean playerIsIdle(LocalPlayer player) {
        return player.getAnimationId() == -1;
    }

    public void resetTimer() {
        currentRunTime = 0;
        startTime = -1;
        LoginManager.setAutoLogin(false);
        println("Timer reset");
    }



    public void startBot() {
        println("Bot started");
        buttonMessage = "";
        setBotState(BotState.IDLE);
    }

    public void setDebug(boolean debug) {
        if (state.debug != debug) {
            state.debug = debug;
            println("Debug: " + debug);
            saveSettings(state.debug);
        }
    }

    public boolean getDebug() {
        return state.debug;
    }

    public void stopBot() {
        println("Bot stopped");
        buttonMessage = "";
        setBotState(BotState.STOPPED);
    }

    private boolean moveTo(Area location) {
        try {
            println("moveTo");
            LocalPlayer player = Client.getLocalPlayer();
            if (player == null) {
                println("moveTo | Player is null.");
                return false;
            }

            if (location.distanceTo(player.getCoordinate()) < 10) {
                println("moveTo | Already at the target location.");
                return true;
            }

            // Randomize the target location within a 3-tile radius
            Coordinate randomizedLocation = location.getRandomWalkableCoordinate();

            println("moveTo | Traversing to randomized location: " + randomizedLocation);
            NavPath path;
            try {
                path = NavPath.resolve(randomizedLocation);
            } catch (Exception e){
                println("moveTo | Error resolving path: " + e.getMessage());
                return false;
            }
            TraverseEvent.State moveState = Movement.traverse(path);

            switch (moveState) {
                case FINISHED -> {
                    println("moveTo | Successfully moved to the area.");
                    return true;
                }
                case NO_PATH, FAILED -> {
                    println("moveTo | Path state: " + moveState);
                    println("moveTo | No path found or movement failed. Please navigate to the correct area manually.");
                    botState = BotState.STOPPED;
                    return false;
                }
                default -> {
                    println("moveTo | Unexpected state: " + moveState);
                    botState = BotState.STOPPED;
                    return false;
                }
            }
        } catch (Exception e) {
            println("moveTo | Error moving to location: " + e.getMessage());
            return false;
        }
    }

    public void navigateTo(Locations location) {
        afkMode = false;
        try {
            // get valid coordinate
            Area area = areaEnumMap.get(location);

            // walk to coordinate
            if (moveTo(area)) {
                println("Successfully navigated to location: " + location);
            } else {
                println("Failed to navigate to location: " + location);
            }
        } catch (Exception e) {
            println("navigateTo | Error navigating to location: " + e.getMessage());
        }
    }


    // return in MM:SS format
    public String getRunTime() {
        return Duration.ofSeconds(currentRunTime).toString().substring(2);
    }


    public void setStopAfter(int breakAfter) {
        breakAfter = clampBreakAfter(breakAfter);
        if (state.breakAfter != breakAfter) {
            state.breakAfter = breakAfter;
            println("Stop after: " + state.breakAfter + " minutes");
            saveSettings(state.debug);
        }
    }

    public void setBreakFor(int breakFor) {
        breakFor = clampBreakFor(breakFor);
        if (state.breakFor != breakFor) {
            state.breakFor = breakFor;
            println("Break for: " + state.breakFor + " minutes");
            saveSettings(state.debug);
        }
    }

    public int getBreakAfter() {
        return state.breakAfter;
    }

    public int getBreakFor() {
        return state.breakFor;
    }

    public void setBreaksEnabled(boolean breaksEnabled) {
        if (state.breaksEnabled != breaksEnabled) {
            state.breaksEnabled = breaksEnabled;
            println("Stops enabled: " + breaksEnabled);
            saveSettings(state.debug);
        }
    }

    public boolean getBreaksEnabled() {
        return state.breaksEnabled;
    }

    public int clampBreakFor(int breakVal) {
        if (breakVal <= 5) {
            return 5;
        }
        else if (breakVal >= 3600) {
            return 3600;
        }
        return breakVal;
    }

    public int clampBreakAfter(int breakVal) {
        if (breakVal <= 5) {
            return 5;
        }
        else if (breakVal >= 3600) {
            return 3600;
        }
        return breakVal;
    }

    public BotState getBotState() {
        return botState;
    }

    public void setBotState(BotState botState) {
        if (botState == BotState.FAILURE || botState == BotState.LOGOUT || botState == BotState.STOPPED || botState == BotState.TAKING_BREAK) {
            LoginManager.setAutoLogin(false);
        }
        else {
            LoginManager.setAutoLogin(true);
        }
        this.botState = botState;
    }

}
