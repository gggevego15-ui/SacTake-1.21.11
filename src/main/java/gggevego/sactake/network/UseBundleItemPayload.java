package gggevego.sactake.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record UseBundleItemPayload(int bundleSlot, int itemIndex) implements CustomPacketPayload {
    public static final Type<UseBundleItemPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("sactake_bag_wheel", "use_bundle_item"));

    public static final StreamCodec<RegistryFriendlyByteBuf, UseBundleItemPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT, UseBundleItemPayload::bundleSlot,
                    ByteBufCodecs.VAR_INT, UseBundleItemPayload::itemIndex,
                    UseBundleItemPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
