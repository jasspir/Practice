package me.jass.practice.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

import me.jass.practice.PracticeAPI;
import me.jass.practice.duels.Duel;
import me.jass.practice.duels.Duelist;

public class DropListener implements Listener {
	@EventHandler
	public void onDrop(final PlayerDropItemEvent event) {
		final Player player = event.getPlayer();

		if (!PracticeAPI.INSTANCE.getDuelManager().isDueling(player)) {
			if (!PracticeAPI.INSTANCE.getConfigManager().isUnsafeInteractions() && !player.isOp()) {
				event.setCancelled(true);
				return;
			}

			if (PracticeAPI.INSTANCE.getMenuManager().get().contains(event.getItemDrop().getItemStack())) {
				event.setCancelled(true);
				return;
			}

			return;
		}

		final Duel duel = PracticeAPI.INSTANCE.getDuelManager().getIndex(player);
		final Duelist duelist = duel.getDuelist(player);

		if (!duelist.getKit().isItemDrops()) {
			event.setCancelled(true);
		}
	}
}
