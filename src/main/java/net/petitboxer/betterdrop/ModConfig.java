package net.petitboxer.betterdrop;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig.Type;

public class ModConfig {

    public static final ForgeConfigSpec CLIENT_SPEC;
    public static final ForgeConfigSpec.BooleanValue FLAT_ITEMS_ON_GROUND;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.comment("Configuration visuelle pour BetterDrop").push("visuals");

        FLAT_ITEMS_ON_GROUND = builder
                .comment("Active ou désactive l'affichage des objets posés à plat au sol au lieu de flotter.")
                .define("flatItemsOnGround", true);

        builder.pop();
        CLIENT_SPEC = builder.build();
    }

    public static void register() {
        ModLoadingContext.get().registerConfig(Type.CLIENT, CLIENT_SPEC);
    }

    // Méthode sécurisée pour lire la valeur sans faire crasher le jeu
    public static boolean isFlatItemsEnabled() {
        if (CLIENT_SPEC.isLoaded()) {
            return FLAT_ITEMS_ON_GROUND.get();
        }
        return true; // Valeur par défaut si la config n'est pas encore chargée
    }
}