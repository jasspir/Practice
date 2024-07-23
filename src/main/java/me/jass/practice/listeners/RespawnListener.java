package me.jass.practice.listeners;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

import me.jass.practice.PracticeAPI;
import me.jass.practice.duels.Duel;

public class RespawnListener implements Listener {
	@EventHandler
	public void onRespawn(final PlayerRespawnEvent event) {
		final Player player = event.getPlayer();

		if (!PracticeAPI.INSTANCE.getDuelManager().isDueling(player)) {
			PracticeAPI.INSTANCE.getMenuManager().give(player);
			return;
		}

		final Duel duel = PracticeAPI.INSTANCE.getDuelManager().getIndex(player);

		if (duel.getDuelists().size() > 2) {
			player.setGameMode(GameMode.SPECTATOR);
			event.setRespawnLocation(player.getLastDeathLocation());
			return;
		}

		event.setRespawnLocation(duel.getDuelist(player).getArena().getSpawn(duel.getDuelist(player).getTeam()));
	}
}