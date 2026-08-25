package net.petitboxer.betterdrop;

import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;

@Mod(BetterDrop.MODID)
public class BetterDrop {
    public static final String MODID = "betterdrop";

    public BetterDrop() {
        ModConfig.register();
        NetworkHandler.register();

        // Enregistre l'écran de configuration natif
        ModLoadingContext.get().registerExtensionPoint(
                ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory(
                        (mc, parent) -> new ModConfigScreen(parent)
                )
        );
    }
}