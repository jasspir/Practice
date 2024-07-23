package me.jass.practice.listeners;

import java.util.UUID;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

import me.jass.practice.PracticeAPI;
import me.jass.practice.duels.Duel;

public class EntityExplodeListener implements Listener {
	@EventHandler
	public void onEntityExplode(final EntityExplodeEvent event) {
		if (!event.getEntity().hasMetadata("duel")) {
			if (!PracticeAPI.INSTANCE.getConfigManager().isUnsafeExplosives()) {
				event.setCancelled(true);
			}

			return;
		}

		final Duel duel = PracticeAPI.INSTANCE.getDuelManager().get(UUID.fromString(event.getEntity().getMetadata("duel").get(0).asString()));

		if (duel == null || !duel.isActive()) {
			if (!PracticeAPI.INSTANCE.getConfigManager().isUnsafeExplosives()) {
				event.setCancelled(true);
			}

			return;
		}

		for (final Block block : event.blockList()) {
			duel.addWorldDamage(block.getState());
		}
	}
}