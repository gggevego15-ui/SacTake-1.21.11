package gggevego.sactake.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public final class SactakeClient implements ClientModInitializer {
    private static KeyMapping openWheel;

    @Override
    public void onInitializeClient() {
        openWheel = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.sactake_bag_wheel.open",
                GLFW.GLFW_KEY_G,
                KeyMapping.Category.MISC
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openWheel.consumeClick()) {
                if (client.player != null && client.screen == null) {
                    client.setScreen(new BagWheelScreen());
                }
            }
        });
    }

    static Minecraft minecraft() {
        return Minecraft.getInstance();
    }
}
