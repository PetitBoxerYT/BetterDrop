package net.petitboxer.betterdrop;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class ModConfigScreen extends Screen {
    private final Screen parent;

    public ModConfigScreen(Screen parent) {
        super(Component.literal("BetterDrop Configuration"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        // Bouton Toggle pour l'option "Objets à plat"
        this.addRenderableWidget(CycleButton.booleanBuilder(
                                Component.literal("ON"),
                                Component.literal("OFF")
                        )
                        .withInitialValue(ModConfig.isFlatItemsEnabled())
                        .create(this.width / 2 - 100, this.height / 4, 200, 20,
                                Component.literal("Objets à plat au sol"),
                                (button, newValue) -> {
                                    ModConfig.FLAT_ITEMS_ON_GROUND.set(newValue);
                                    ModConfig.CLIENT_SPEC.save();
                                })
        );

        // Bouton Terminé (Retour)
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, button -> this.minecraft.setScreen(this.parent))
                .bounds(this.width / 2 - 100, this.height / 4 + 48, 200, 20)
                .build());
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 20, 0xFFFFFF);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }
}