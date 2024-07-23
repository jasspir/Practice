package me.jass.practice.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import me.jass.practice.PracticeAPI;
import me.jass.practice.duels.Duel;
import me.jass.practice.duels.Duelist;

public class DeathListener implements Listener {
	@EventHandler
	public void onDeath(final PlayerDeathEvent event) {
		final Player player = event.getEntity();
		if (!PracticeAPI.INSTANCE.getDuelManager().isDueling(player)) {
			return;
		}

		final Duel duel = PracticeAPI.INSTANCE.getDuelManager().getIndex(player);
		final Duelist duelist = duel.getDuelist(player);

		if (!duelist.getKit().isDeathDrops()) {
			event.getDrops().clear();
			event.setDroppedExp(0);
		}

		event.setDeathMessage(null);

		duelist.setView();
		duelist.setHealth();

		Bukkit.getScheduler().runTaskLater(PracticeAPI.INSTANCE.getPlugin(), () -> {
			player.spigot().respawn();
			duel.addDeath(duel.getDuelist(player));
		}, 5);
	}
}