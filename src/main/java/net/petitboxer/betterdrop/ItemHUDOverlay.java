package net.petitboxer.betterdrop;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class ItemHUDOverlay {

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;

        if (player == null || mc.options.hideGui) return;

        ItemEntity targetItem = getTargetedItem(player, 4.0);

        if (targetItem != null && targetItem.isAlive()) {
            GuiGraphics guiGraphics = event.getGuiGraphics();
            Font font = mc.font;

            Component attackKeyName = Minecraft.getInstance().options.keyAttack.getTranslatedKeyMessage();
            Component pickupKeyName = ModKeyBindings.PICKUP_KEY.getTranslatedKeyMessage();

            Component text = Component.translatable("hud.betterdrop.interact", attackKeyName, pickupKeyName);

            int screenWidth = mc.getWindow().getGuiScaledWidth();
            int screenHeight = mc.getWindow().getGuiScaledHeight();

            int x = (screenWidth - font.width(text)) / 2;
            int y = (screenHeight / 2) + 20;

            guiGraphics.drawString(font, text, x, y, 0xFFFFFF, true);
        }
    }

    public static ItemEntity getTargetedItem(Player player, double reachDistance) {
        Vec3 eyePos = player.getEyePosition(1.0F);
        Vec3 lookVec = player.getViewVector(1.0F).normalize();

        AABB searchBox = player.getBoundingBox().inflate(reachDistance);
        List<ItemEntity> items = player.level().getEntitiesOfClass(ItemEntity.class, searchBox);

        ItemEntity closestItem = null;
        double closestDistance = reachDistance;

        for (ItemEntity item : items) {
            // Ignorer les objets qui viennent tout juste d'apparaître (moins de 15 ticks / 0.75s)
            if (item.tickCount < 15) continue;

            Vec3 itemPos = item.position().add(0, 0.25, 0);
            Vec3 toItem = itemPos.subtract(eyePos);
            double distance = toItem.length();

            if (distance <= reachDistance) {
                Vec3 toItemNormalized = toItem.normalize();
                double dot = lookVec.dot(toItemNormalized);

                // 0.97 exige d'avoir le viseur bien centré sur l'objet
                if (dot > 0.97 && distance < closestDistance) {
                    closestDistance = distance;
                    closestItem = item;
                }
            }
        }
        return closestItem;
    }
}