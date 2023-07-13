package qwq.wumie.systems.modules.misc;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;

public class BedrockBreaker extends Module {
    public BedrockBreaker() {
        super(Categories.Misc, "bedrock-breaker", "A module to mine bedrock!");
    }

    @EventHandler
    private void onTick(TickEvent.Pre e) {

    }
}
