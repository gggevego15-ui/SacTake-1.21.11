package gggevego.sactake.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public final class BagWheelScreen extends Screen {
    private static final int[] PASTELS = {
            0xFFFFB7D5, 0xFFFFD1B5, 0xFFFFF0A8, 0xFFC8F7C5,
            0xFFBDEBFF, 0xFFD9C7FF, 0xFFFFC8EE, 0xFFC7F5E8
    };

    private final List<Integer> bundleSlots = new ArrayList<>();
    private int selectedSlot = -1;

    public BagWheelScreen() {
        super(Component.literal("Sactake Bag Wheel"));
    }

    private void refreshBundles() {
        bundleSlots.clear();
        if (minecraft == null || minecraft.player == null) return;
        for (int i = 0; i < minecraft.player.getInventory().getContainerSize(); i++) {
            ItemStack stack = minecraft.player.getInventory().getItem(i);
            if (stack.is(Items.BUNDLE)) bundleSlots.add(i);
        }
        if (!bundleSlots.contains(selectedSlot)) selectedSlot = bundleSlots.isEmpty() ? -1 : bundleSlots.getFirst();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        refreshBundles();
        graphics.fill(0, 0, width, height, 0xBFFFFFFF);
        int cx = width / 2;
        int cy = height / 2;
        int radius = Math.min(105, Math.min(width, height) / 3);

        graphics.drawCenteredString(font, "Sactake • Мешочки", cx, cy - radius - 36, 0xFF8A4FA8);
        graphics.drawCenteredString(font, "Выбери мешочек", cx, cy - radius - 20, 0xFF755A89);

        graphics.fill(cx - radius - 9, cy - radius - 9, cx + radius + 9, cy + radius + 9, 0x66FFD4ED);
        graphics.fill(cx - radius, cy - radius, cx + radius, cy + radius, 0xAAFFF9FD);

        if (bundleSlots.isEmpty()) {
            graphics.drawCenteredString(font, "В инвентаре нет мешочков", cx, cy - 4, 0xFF8A4FA8);
            graphics.drawCenteredString(font, "Положи обычный мешочек в инвентарь", cx, cy + 12, 0xFF8A4FA8);
        } else {
            for (int i = 0; i < bundleSlots.size(); i++) {
                double angle = -Math.PI / 2 + (Math.PI * 2 * i / bundleSlots.size());
                int x = cx + (int) (Math.cos(angle) * radius * 0.72) - 12;
                int y = cy + (int) (Math.sin(angle) * radius * 0.72) - 12;
                int slot = bundleSlots.get(i);
                boolean hovered = mouseX >= x - 5 && mouseX <= x + 29 && mouseY >= y - 5 && mouseY <= y + 29;
                int color = PASTELS[i % PASTELS.length];
                if (hovered || selectedSlot == slot) {
                    graphics.fill(x - 6, y - 6, x + 30, y + 30, 0xFFFF72C6);
                    if (hovered) selectedSlot = slot;
                } else {
                    graphics.fill(x - 4, y - 4, x + 28, y + 28, color);
                }
                ItemStack stack = minecraft.player.getInventory().getItem(slot);
                graphics.renderItem(stack, x + 1, y + 1);
            }
            if (selectedSlot >= 0) {
                ItemStack selected = minecraft.player.getInventory().getItem(selectedSlot);
                graphics.drawCenteredString(font, selected.getHoverName(), cx, cy + radius + 7, 0xFF8A4FA8);
            }
        }
        graphics.drawCenteredString(font, "G — закрыть  •  Нажми на мешочек для выбора", cx, height - 24, 0xFF8A4FA8);
        super.render(graphics, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            refreshBundles();
            int cx = width / 2;
            int cy = height / 2;
            int radius = Math.min(105, Math.min(width, height) / 3);
            for (int i = 0; i < bundleSlots.size(); i++) {
                double angle = -Math.PI / 2 + (Math.PI * 2 * i / bundleSlots.size());
                int x = cx + (int) (Math.cos(angle) * radius * 0.72) - 12;
                int y = cy + (int) (Math.sin(angle) * radius * 0.72) - 12;
                if (mouseX >= x - 6 && mouseX <= x + 30 && mouseY >= y - 6 && mouseY <= y + 30) {
                    selectedSlot = bundleSlots.get(i);
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_G) {
            onClose();
            return true;
        }
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public void onClose() {
        if (minecraft != null) minecraft.setScreen(null);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
