package com.example.examplemod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.Optional;

@Mod.EventBusSubscriber(modid = "examplemod", value = Dist.CLIENT)
public class ItemHUDOverlay {

    @SubscribeEvent
    public static void onRenderGuiOverlay(RenderGuiOverlayEvent.Post event) {
        // On s'assure d'afficher le texte au niveau du viseur (CROSSHAIR)
        if (event.getOverlay().id().equals(VanillaGuiOverlay.CROSSHAIR.id())) {
            Minecraft mc = Minecraft.getInstance();
            Player player = mc.player;

            if (player == null || mc.options.hideGui) return;

            // Détection de l'item visé
            ItemEntity targetItem = getTargetedItem(player, 4.0);

            if (targetItem != null && targetItem.isAlive()) {
                GuiGraphics guiGraphics = event.getGuiGraphics();
                Font font = mc.font;

                // Texte à afficher
                Component component = Component.translatable("hud.betterdrop.interact");

                // Calcul du centre de l'écran
                int screenWidth = mc.getWindow().getGuiScaledWidth();
                int screenHeight = mc.getWindow().getGuiScaledHeight();

                int x = (screenWidth - font.width(component)) / 2;
                int y = (screenHeight / 2) + 25; // Placé 15 pixels sous le viseur

                // Affichage du texte avec une ombre portée (couleur blanche : 0xFFFFFF)
                guiGraphics.drawString(font, component, x, y, 0xFFFFFF, true);
            }
        }
    }

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