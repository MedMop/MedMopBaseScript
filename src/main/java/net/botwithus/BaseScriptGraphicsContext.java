package net.botwithus;

import static net.botwithus.rs3.script.ScriptConsole.println;

import net.botwithus.rs3.imgui.*;
import net.botwithus.rs3.script.ScriptConsole;
import net.botwithus.rs3.script.ScriptGraphicsContext;

public class BaseScriptGraphicsContext extends ScriptGraphicsContext {

    private BaseScript script;

    private static final String[] locations = new String[] {
        "Select an Option",
    };

    private static final String[] stopModalityOptions = new String[] {
        "Log out",
        "AFK",
        "TP to GE and log out [UNIMPLEMENTED]",
    };


    private NativeInteger selectedLocationIndex = new NativeInteger(0);

    private NativeInteger selectedStopModalityIndex = new NativeInteger(0);

    public BaseScriptGraphicsContext(ScriptConsole scriptConsole, BaseScript script) {
        super(scriptConsole);
        this.script = script;
    }

    @Override
    public void drawSettings() {
        if (ImGui.Begin("Script Name", ImGuiWindowFlag.None.getValue())) {
            if (ImGui.BeginTabBar("Script", ImGuiWindowFlag.None.getValue())) {
                // ------------------- Settings Section -------------------
                if (ImGui.BeginTabItem("Settings", ImGuiWindowFlag.None.getValue())) {
                    ImGui.Text("Info:");

                    ImGui.Separator();
                    if (ImGui.Button("Start")) {
                        script.startBot();
                    }
                    ImGui.SameLine();
                    if (ImGui.Button("Stop")) {
                        script.stopBot();
                    }
                    ImGui.EndTabItem();
                }

                if (ImGui.BeginTabItem("Other", ImGuiWindowFlag.None.getValue())) {
                    ImGui.Text("Other settings");
                    script.setDebug(ImGui.Checkbox("Debug prints?", script.getDebug()));
                    ImGui.EndTabItem();
                }
                ImGui.EndTabBar();
            }

        }
        ImGui.End();
    }

    @Override
    public void drawOverlay() {
        super.drawOverlay();
    }
}
