package net.petitboxer.betterdrop;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public class ModKeyBindings {
    public static final KeyMapping PICKUP_KEY = new KeyMapping(
            "key.betterdrop.pickup",
            InputConstants.Type.MOUSE,
            GLFW.GLFW_MOUSE_BUTTON_2, // GLFW_MOUSE_BUTTON_2 correspond au clic droit dans les bindings Minecraft
            "key.categories.betterdrop"
    );
}