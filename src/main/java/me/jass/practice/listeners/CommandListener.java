package me.jass.practice.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import me.jass.practice.PracticeAPI;
import me.jass.practice.utils.Text;

public class CommandListener implements Listener {
	@EventHandler
	public void onCommand(final PlayerCommandPreprocessEvent event) {
		final Player player = event.getPlayer();
		final String command = event.getMessage().toLowerCase();

		if (!PracticeAPI.INSTANCE.getDuelManager().isDueling(player)) {
			return;
		}

		if (command.contains("/csuite") || command.contains("/skript") || command.contains("/spawn") || command.contains("/coolarena") || command.contains("/ca") || command.contains("/pet")
				|| command.contains("/mpet")) {
			Text.tell(player, "&cYou are currently in a duel!");
			event.setCancelled(true);
		}
	}
}
