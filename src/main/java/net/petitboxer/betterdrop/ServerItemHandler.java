package net.petitboxer.betterdrop;

import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "betterdrop") // Pense à mettre ton modid s'il est personnalisé
public class ServerItemHandler {

    /**
     * Désactive le ramassage automatique des objets au sol lors du contact avec le joueur.
     * S'exécute des deux côtés (serveur et client) car l'annotation Dist.CLIENT a été retirée.
     */
    @SubscribeEvent
    public static void onItemPickup(EntityItemPickupEvent event) {
        event.setCanceled(true);
    }
}