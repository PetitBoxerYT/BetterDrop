package net.petitboxer.betterdrop.mixin;

import net.petitboxer.betterdrop.ModConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntityRenderer.class)
public class ItemEntityRendererMixin {

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void onRenderCustom(ItemEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight, CallbackInfo ci) {
        if (ModConfig.isFlatItemsEnabled()) {
            ci.cancel();

            ItemStack itemStack = entity.getItem();
            if (itemStack.isEmpty()) return;

            poseStack.pushPose();

            // 1. Hauteur au sol
            poseStack.translate(0.0D, 0.03D, 0.0D);

            // 2. Angle fixe unique par item
            float fixedAngle = (entity.getId() % 8) * 45.0F;
            poseStack.mulPose(Axis.YP.rotationDegrees(fixedAngle));

            // 3. Pose à plat
            poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));

            // 4. Rendu fixe propre sans animation vanilla
            Minecraft.getInstance().getItemRenderer().renderStatic(
                    itemStack,
                    ItemDisplayContext.GROUND,
                    packedLight,
                    OverlayTexture.NO_OVERLAY,
                    poseStack,
                    buffer,
                    entity.level(),
                    entity.getId()
            );

            poseStack.popPose();
        }
    }
}