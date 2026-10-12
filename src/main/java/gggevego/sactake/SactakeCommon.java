package gggevego.sactake;

import gggevego.sactake.network.UseBundleItemPayload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;

public final class SactakeCommon implements ModInitializer {
    @Override
    public void onInitialize() {
        PayloadTypeRegistry.playC2S().register(UseBundleItemPayload.TYPE, UseBundleItemPayload.STREAM_CODEC);
        ServerPlayNetworking.registerGlobalReceiver(UseBundleItemPayload.TYPE, (payload, context) ->
                context.server().execute(() -> useBundleItem(context.player(), payload.bundleSlot(), payload.itemIndex())));
    }

    private static void useBundleItem(Player player, int bundleSlot, int itemIndex) {
        var inventory = player.getInventory();
        if (bundleSlot < 0 || bundleSlot >= Math.min(36, inventory.getContainerSize())) return;
        if (itemIndex < 0) return;

        ItemStack bundle = inventory.getItem(bundleSlot);
        if (bundle.isEmpty() || !bundle.has(DataComponents.BUNDLE_CONTENTS)) return;
        BundleContents contents = bundle.get(DataComponents.BUNDLE_CONTENTS);
        if (contents == null || itemIndex >= contents.size()) return;

        int handSlot = inventory.selected;
        ItemStack handStack = inventory.getItem(handSlot);
        int freeSlot = -1;
        for (int i = 0; i < Math.min(36, inventory.getContainerSize()); i++) {
            if (i != bundleSlot && i != handSlot && inventory.getItem(i).isEmpty()) {
                freeSlot = i;
                break;
            }
        }

        if (bundleSlot == handSlot) {
            if (freeSlot < 0) return;
        } else if (!handStack.isEmpty() && freeSlot < 0) {
            return;
        }

        BundleContents.Mutable mutable = new BundleContents.Mutable(contents);
        if (contents.getSelectedItem() != itemIndex) mutable.toggleSelectedItem(itemIndex);
        ItemStack extracted = mutable.removeOne();
        if (extracted == null || extracted.isEmpty()) return;

        ItemStack updatedBundle = bundle.copy();
        updatedBundle.set(DataComponents.BUNDLE_CONTENTS, mutable.toImmutable());

        if (bundleSlot == handSlot) {
            inventory.setItem(freeSlot, updatedBundle);
        } else {
            inventory.setItem(bundleSlot, updatedBundle);
            if (!handStack.isEmpty()) inventory.setItem(freeSlot, handStack.copy());
        }

        inventory.setItem(handSlot, extracted);
        ItemStack used = extracted.use(player.level(), player, InteractionHand.MAIN_HAND).getObject();
        inventory.setItem(handSlot, used);
        inventory.setChanged();
        player.containerMenu.broadcastChanges();
    }
}
