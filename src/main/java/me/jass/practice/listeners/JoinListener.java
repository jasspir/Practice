package me.jass.practice.listeners;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import me.jass.practice.PracticeAPI;
import me.jass.practice.managers.MenuManager;

public class JoinListener implements Listener {
	@EventHandler
	public void onJoin(final PlayerJoinEvent event) {
		final Player player = event.getPlayer();
		final MenuManager menuManager = PracticeAPI.INSTANCE.getMenuManager();

		if (menuManager.whitelistEnabled()) {
			if (!menuManager.isWhitelisted(player)) {
				player.kickPlayer(PracticeAPI.INSTANCE.getMessageManager().getWhitelist());
			}
		}

		if (PracticeAPI.INSTANCE.getConfigManager().isJoinToSpawn()) {
			player.teleport(player.getLocation().getWorld().getSpawnLocation());
		}

		player.setGameMode(GameMode.ADVENTURE);
		player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
		player.setFoodLevel(20);
		player.setExhaustion(0);
		player.setSaturation(0);
		player.setSaturatedRegenRate(0);
		player.setUnsaturatedRegenRate(0);
		player.setLevel(0);
		player.setExp(0);

		PracticeAPI.INSTANCE.getEloManager().load(player);
		PracticeAPI.INSTANCE.getStatManager().load(player);

		Bukkit.getScheduler().runTaskLater(PracticeAPI.INSTANCE.getPlugin(), () -> menuManager.give(player), 5);
		Bukkit.getScheduler().runTaskLater(PracticeAPI.INSTANCE.getPlugin(), () -> menuManager.enableFlight(player), 20);
	}
}
