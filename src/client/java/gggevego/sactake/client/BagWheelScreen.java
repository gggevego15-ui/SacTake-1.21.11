package gggevego.sactake.client;

import gggevego.sactake.network.UseBundleItemPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.BundleContents;
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
    private boolean showingContents;
    private int selectedItemIndex = -1;

    public BagWheelScreen() {
        super(Component.literal("Sactake Bag Wheel"));
    }

    private void refreshBundles() {
        bundleSlots.clear();
        if (minecraft == null || minecraft.player == null) return;
        for (int i = 0; i < Math.min(36, minecraft.player.getInventory().getContainerSize()); i++) {
            if (minecraft.player.getInventory().getItem(i).is(Items.BUNDLE)) bundleSlots.add(i);
        }
        if (!bundleSlots.contains(selectedSlot)) {
            selectedSlot = bundleSlots.isEmpty() ? -1 : bundleSlots.getFirst();
            showingContents = false;
            selectedItemIndex = -1;
        }
    }

    private List<ItemStack> selectedContents() {
        if (minecraft == null || minecraft.player == null || selectedSlot < 0) return List.of();
        ItemStack bundle = minecraft.player.getInventory().getItem(selectedSlot);
        BundleContents contents = bundle.get(DataComponents.BUNDLE_CONTENTS);
        return contents == null ? List.of() : contents.items();
    }

    private int wheelRadius() {
        return Math.min(105, Math.min(width, height) / 3);
    }

    private int slotX(int index, int count, int cx, int radius) {
        double angle = -Math.PI / 2 + Math.PI * 2 * index / Math.max(1, count);
        return cx + (int) (Math.cos(angle) * radius * 0.72) - 12;
    }

    private int slotY(int index, int count, int cy, int radius) {
        double angle = -Math.PI / 2 + Math.PI * 2 * index / Math.max(1, count);
        return cy + (int) (Math.sin(angle) * radius * 0.72) - 12;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        refreshBundles();
        graphics.fill(0, 0, width, height, 0xBFFFFFFF);
        int cx = width / 2;
        int cy = height / 2;
        int radius = wheelRadius();

        graphics.drawCenteredString(font, "Sactake • Мешочки", cx, cy - radius - 36, 0xFF8A4FA8);
        graphics.drawCenteredString(font, showingContents ? "Наведи на предмет и отпусти G" : "Выбери мешочек", cx, cy - radius - 20, 0xFF755A89);
        graphics.fill(cx - radius - 9, cy - radius - 9, cx + radius + 9, cy + radius + 9, 0x66FFD4ED);
        graphics.fill(cx - radius, cy - radius, cx + radius, cy + radius, 0xAAFFF9FD);

        if (bundleSlots.isEmpty()) {
            graphics.drawCenteredString(font, "В инвентаре нет мешочков", cx, cy - 4, 0xFF8A4FA8);
            graphics.drawCenteredString(font, "Положи обычный мешочек в инвентарь", cx, cy + 12, 0xFF8A4FA8);
        } else if (!showingContents) {
            for (int i = 0; i < bundleSlots.size(); i++) {
                int x = slotX(i, bundleSlots.size(), cx, radius);
                int y = slotY(i, bundleSlots.size(), cy, radius);
                int slot = bundleSlots.get(i);
                boolean hovered = mouseX >= x - 5 && mouseX <= x + 29 && mouseY >= y - 5 && mouseY <= y + 29;
                graphics.fill(x - 5, y - 5, x + 29, y + 29,
                        hovered || selectedSlot == slot ? 0xFFFF72C6 : PASTELS[i % PASTELS.length]);
                graphics.renderItem(minecraft.player.getInventory().getItem(slot), x + 1, y + 1);
            }
            if (selectedSlot >= 0) {
                ItemStack selected = minecraft.player.getInventory().getItem(selectedSlot);
                graphics.drawCenteredString(font, selected.getHoverName(), cx, cy + radius + 7, 0xFF8A4FA8);
                graphics.drawCenteredString(font, "Нажми на мешочек, чтобы открыть содержимое", cx, cy + radius + 21, 0xFF8A4FA8);
            }
        } else {
            List<ItemStack> items = selectedContents();
            if (items.isEmpty()) {
                graphics.drawCenteredString(font, "Мешочек пуст", cx, cy, 0xFF8A4FA8);
            } else {
                for (int i = 0; i < items.size(); i++) {
                    int x = slotX(i, items.size(), cx, radius);
                    int y = slotY(i, items.size(), cy, radius);
                    boolean hovered = mouseX >= x - 5 && mouseX <= x + 29 && mouseY >= y - 5 && mouseY <= y + 29;
                    graphics.fill(x - 5, y - 5, x + 29, y + 29,
                            hovered || selectedItemIndex == i ? 0xFFFF72C6 : PASTELS[i % PASTELS.length]);
                    graphics.renderItem(items.get(i), x + 1, y + 1);
                    graphics.renderItemDecorations(font, items.get(i), x + 1, y + 1);
                    if (hovered) {
                        selectedItemIndex = i;
                        graphics.renderTooltip(font, items.get(i), mouseX, mouseY);
                    }
                }
                if (selectedItemIndex >= 0 && selectedItemIndex < items.size()) {
                    graphics.drawCenteredString(font, items.get(selectedItemIndex).getHoverName(), cx, cy + radius + 7, 0xFF8A4FA8);
                }
            }
            graphics.drawCenteredString(font, "Backspace — к мешочкам", cx, height - 40, 0xFF8A4FA8);
        }
        graphics.drawCenteredString(font, "Отпусти G — использовать выбранный предмет", cx, height - 24, 0xFF8A4FA8);
        super.render(graphics, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            refreshBundles();
            int cx = width / 2;
            int cy = height / 2;
            int radius = wheelRadius();
            if (!showingContents) {
                for (int i = 0; i < bundleSlots.size(); i++) {
                    int x = slotX(i, bundleSlots.size(), cx, radius);
                    int y = slotY(i, bundleSlots.size(), cy, radius);
                    if (mouseX >= x - 6 && mouseX <= x + 30 && mouseY >= y - 6 && mouseY <= y + 30) {
                        selectedSlot = bundleSlots.get(i);
                        showingContents = true;
                        selectedItemIndex = -1;
                        return true;
                    }
                }
            } else {
                List<ItemStack> items = selectedContents();
                for (int i = 0; i < items.size(); i++) {
                    int x = slotX(i, items.size(), cx, radius);
                    int y = slotY(i, items.size(), cy, radius);
                    if (mouseX >= x - 6 && mouseX <= x + 30 && mouseY >= y - 6 && mouseY <= y + 30) {
                        selectedItemIndex = i;
                        return true;
                    }
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_BACKSPACE && showingContents) {
            showingContents = false;
            selectedItemIndex = -1;
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_G) {
            if (showingContents && selectedSlot >= 0 && selectedItemIndex >= 0
                    && ClientPlayNetworking.canSend(UseBundleItemPayload.TYPE)) {
                ClientPlayNetworking.send(new UseBundleItemPayload(selectedSlot, selectedItemIndex));
            }
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
