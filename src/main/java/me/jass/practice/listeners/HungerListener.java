package me.jass.practice.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

import me.jass.practice.PracticeAPI;
import me.jass.practice.duels.Duel;
import me.jass.practice.duels.Duelist;

public class HungerListener implements Listener {
	@EventHandler
	public void onHunger(final FoodLevelChangeEvent event) {
		if (!(event.getEntity() instanceof Player)) {
			return;
		}

		final Player player = (Player) event.getEntity();

		if (!PracticeAPI.INSTANCE.getDuelManager().isDueling(player)) {
			if (!PracticeAPI.INSTANCE.getConfigManager().isUnsafeInteractions()) {
				event.setCancelled(true);
			}
			return;
		}

		final Duel duel = PracticeAPI.INSTANCE.getDuelManager().getIndex(player);
		final Duelist duelist = duel.getDuelist(player);

		if (!duelist.getKit().isHungerDepletion()) {
			event.setCancelled(true);
		}
	}
}