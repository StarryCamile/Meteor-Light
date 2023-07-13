package qwq.wumie.systems.modules.misc;

import meteordevelopment.meteorclient.events.game.ReceiveMessageEvent;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;

public class AutoCalc extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Mode> mode = sgGeneral.add(new EnumSetting.Builder<Mode>()
            .name("mode")
            .description("Computing mode.")
            .defaultValue(Mode.Pit)
            .build()
    );

    private final Setting<String> prefixContains = sgGeneral.add(new StringSetting.Builder()
            .name("prefix-contains")
            .defaultValue("速算")
            .visible(() -> mode.get().equals(Mode.Pit))
            .build()
    );

    private final Setting<String> split = sgGeneral.add(new StringSetting.Builder()
            .name("split")
            .description("Split char")
            .defaultValue(":")
            .build()
    );

    private Stage currentStage = Stage.None;

    public AutoCalc() {
        super(Categories.Misc, "auto-calc", "Automatic calculation.");
    }

    @EventHandler
    private void onChat(ReceiveMessageEvent event) {
        String message = event.getMessage().getString();
        if (split.get().isEmpty()) return;
        if (!message.contains(split.get())) return;
        String[] texts = message.split(split.get());
        String precede = texts[0];
        if (mode.get().equals(Mode.Pit) && !precede.contains(prefixContains.get())) return;
        boolean hasEmptyChar = texts[1].startsWith(" ");
        String receive = hasEmptyChar ? message.replace(precede+split.get()+"  ","") : message.replace(precede+split.get(),"");
        currentStage = updateStage(receive);
        if (currentStage.equals(Stage.None)) return;
        switch (mode.get()) {
            case Pit -> {
                double out = 0.0;
                switch (currentStage) {
                    case Subtract -> {
                        String m1 = receive;
                        if (receive.startsWith("-")) {
                            m1 = m1.substring(1);
                        }
                        String[] numbers = m1.split("-");
                        double n1 = Double.parseDouble(receive.startsWith("-") ? "-"+numbers[0]:numbers[0]);
                        double n2 = Double.parseDouble(numbers[1]);
                        out = n1-n2;
                    }
                    case Add -> {
                        String[] numbers = receive.split("\\+");
                        double n1 = Double.parseDouble(numbers[0]);
                        double n2 = Double.parseDouble(numbers[1]);
                        out = n1+n2;
                    }
                    case Divide -> {
                        String[] numbers = (receive.contains("/")) ? receive.split("/") : receive.split("÷");
                        double n1 = Double.parseDouble(numbers[0]);
                        double n2 = Double.parseDouble(numbers[1]);
                        out = n1/n2;
                    }
                    case Multiply -> {
                        String[] numbers = (receive.contains("x")) ? receive.split("x") : receive.split("\\*");
                        double n1 = Double.parseDouble(numbers[0]);
                        double n2 = Double.parseDouble(numbers[1]);
                        out = n1*n2;
                    }
                }
                String outString = String.valueOf(out);
                String send = outString.endsWith(".0") ? String.valueOf(((int) out)) : outString;
                sendChatMessage(send);
            }
        }
    }

    private Stage updateStage(String message) {
        Stage stage = Stage.None;
        if (message.contains("-")) {
            if (message.startsWith("-")) {
                String t = message.substring(1);
                if (t.contains("-")) {
                    stage = Stage.Subtract;
                }
            } else {
                stage = Stage.Subtract;
            }
        }
        if (message.contains("+")) {
            stage = Stage.Add;
        }
        if (message.contains("x") || message.contains("*")) {
            stage = Stage.Multiply;
        }
        if (message.contains("÷") || message.contains("/")) {
            stage = Stage.Divide;
        }
        return stage;
    }

    public enum Stage {
        Multiply,
        Divide,
        Add,
        Subtract,
        None
    }

    public enum Mode {
        Pit
    }
}
