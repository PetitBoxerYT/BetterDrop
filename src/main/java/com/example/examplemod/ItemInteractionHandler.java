package com.example.examplemod;

import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.Optional;

@Mod.EventBusSubscriber(modid = "examplemod")
public class ItemInteractionHandler {

    // 1. Désactive le ramassage automatique au toucher
    @SubscribeEvent
    public static void onItemPickup(EntityItemPickupEvent event) {
        event.setCanceled(true);
    }

    // 2. Clic droit (Ramasser)
    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        processPickup(event.getEntity());
    }

    @SubscribeEvent
    public static void onRightClickEmpty(PlayerInteractEvent.RightClickEmpty event) {
        processPickup(event.getEntity());
    }

    // 3. Clic gauche (Détruire) - Détecté directement via l'entrée du joueur
    @SubscribeEvent
    public static void onClickInput(InputEvent.InteractionKeyMappingTriggered event) {
        if (event.isAttack()) { // Si c'est un clic gauche (attaque)
            Player player = Minecraft.getInstance().player;
            if (player != null) {
                ItemEntity targetItem = getTargetedItem(player, 4.0);
                if (targetItem != null && targetItem.isAlive()) {
                    // Supprime l'objet et joue le son
                    player.level().playSound(player, targetItem.getX(), targetItem.getY(), targetItem.getZ(),
                            SoundEvents.ITEM_BREAK, SoundSource.PLAYERS, 0.8F, 1.0F);
                    targetItem.discard();

                    // Annule le coup de poing dans le vide / l'attaque normale
                    event.setCanceled(true);
                    event.setSwingHand(false);
                }
            }
        }
    }

    private static void processPickup(Player player) {
        ItemEntity targetItem = getTargetedItem(player, 4.0);
        if (targetItem != null && targetItem.isAlive()) {
            if (!player.level().isClientSide()) {
                boolean added = player.getInventory().add(targetItem.getItem());
                if (added) {
                    player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                            SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F, 1.0F);
                    targetItem.discard();
                }
            }
        }
    }

    // Calcule quel objet au sol le joueur regarde
    private static ItemEntity getTargetedItem(Player player, double reachDistance) {
        Vec3 eyePos = player.getEyePosition(1.0F);
        Vec3 viewVector = player.getViewVector(1.0F);
        Vec3 reachVec = eyePos.add(viewVector.x * reachDistance, viewVector.y * reachDistance, viewVector.z * reachDistance);

        AABB searchBox = player.getBoundingBox().expandTowards(viewVector.scale(reachDistance)).inflate(1.0);
        List<ItemEntity> items = player.level().getEntitiesOfClass(ItemEntity.class, searchBox);

        ItemEntity closestItem = null;
        double closestDistance = reachDistance;

        for (ItemEntity item : items) {
            AABB itemBox = item.getBoundingBox().inflate(0.3);
            Optional<Vec3> hit = itemBox.clip(eyePos, reachVec);

            if (hit.isPresent()) {
                double distance = eyePos.distanceTo(hit.get());
                if (distance < closestDistance) {
                    closestDistance = distance;
                    closestItem = item;
                }
            }
        }
        return closestItem;
    }
}