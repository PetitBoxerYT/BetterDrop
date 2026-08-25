package net.petitboxer.betterdrop;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class ItemInteractionHandler {

    @SubscribeEvent
    public static void onClickInput(InputEvent.InteractionKeyMappingTriggered event) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        // Clic Gauche (Détruire)
        if (event.isAttack()) {
            ItemEntity targetItem = ItemHUDOverlay.getTargetedItem(player, 4.0);
            if (targetItem != null && targetItem.isAlive()) {
                NetworkHandler.INSTANCE.sendToServer(new NetworkHandler.PacketItemAction(targetItem.getId(), 1));
                event.setCanceled(true);
                event.setSwingHand(false);
            }
        }

        // Clic Droit (Ramasser)
        if (event.isUseItem()) {
            ItemEntity targetItem = ItemHUDOverlay.getTargetedItem(player, 4.0);
            if (targetItem != null && targetItem.isAlive()) {
                NetworkHandler.INSTANCE.sendToServer(new NetworkHandler.PacketItemAction(targetItem.getId(), 0));
                event.setCanceled(true);
                event.setSwingHand(false);
            }
        }
    }
}