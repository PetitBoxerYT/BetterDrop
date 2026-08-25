package net.petitboxer.betterdrop;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.Supplier;

public class NetworkHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation("examplemod", "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void register() {
        int id = 0;
        INSTANCE.registerMessage(id++, PacketItemAction.class, PacketItemAction::encode, PacketItemAction::decode, PacketItemAction::handle);
    }

    public static class PacketItemAction {
        private final int entityId;
        private final int actionType;

        public PacketItemAction(int entityId, int actionType) {
            this.entityId = entityId;
            this.actionType = actionType;
        }

        public static void encode(PacketItemAction msg, FriendlyByteBuf buf) {
            buf.writeInt(msg.entityId);
            buf.writeInt(msg.actionType);
        }

        public static PacketItemAction decode(FriendlyByteBuf buf) {
            return new PacketItemAction(buf.readInt(), buf.readInt());
        }

        public static void handle(PacketItemAction msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                ServerPlayer player = ctx.get().getSender();
                if (player != null) {
                    Entity entity = player.level().getEntity(msg.entityId);
                    if (entity instanceof ItemEntity targetItem && targetItem.isAlive()) {
                        if (player.distanceToSqr(targetItem) <= 36.0) {
                            if (msg.actionType == 0) {
                                boolean added = player.getInventory().add(targetItem.getItem());
                                if (added) {
                                    player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                                            SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F, 1.0F);
                                    targetItem.discard();
                                }
                            } else if (msg.actionType == 1) {
                                player.level().playSound(null, targetItem.getX(), targetItem.getY(), targetItem.getZ(),
                                        SoundEvents.ITEM_BREAK, SoundSource.PLAYERS, 0.8F, 1.0F);
                                targetItem.discard();
                            }
                        }
                    }
                }
            });
            ctx.get().setPacketHandled(true);
        }
    }
}