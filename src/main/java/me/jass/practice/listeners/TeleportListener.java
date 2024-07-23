package me.jass.practice.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import me.jass.practice.PracticeAPI;
import me.jass.practice.duels.Duelist;

public class TeleportListener implements Listener {
	@EventHandler
	public void onTeleport(final PlayerTeleportEvent event) {
		if (event.getCause() != TeleportCause.ENDER_PEARL && event.getCause() != TeleportCause.CHORUS_FRUIT) {
			return;
		}

		final Player player = event.getPlayer();

		if (!PracticeAPI.INSTANCE.getDuelManager().isDueling(player)) {
			if (!PracticeAPI.INSTANCE.getConfigManager().isUnsafeInteractions() && !player.isOp()) {
				event.setCancelled(true);
			}
			return;
		}

		final Duelist duelist = PracticeAPI.INSTANCE.getDuelManager().getIndex(player).getDuelist(player);

		if (duelist.isFrozen()) {
			event.setCancelled(true);
			return;
		}
	}
}