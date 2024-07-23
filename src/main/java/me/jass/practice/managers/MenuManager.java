package me.jass.practice.managers;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.jass.practice.PracticeAPI;
import me.jass.practice.datatypes.QueueType;
import me.jass.practice.datatypes.Setting;
import me.jass.practice.utils.Text;

public class MenuManager {
	private final Inventory menu = Bukkit.createInventory(null, 9);
	private boolean whitelist;

	public Inventory get() {
		return menu;
	}

	public void give(final Player player) {
		player.getInventory().setContents(menu.getContents());
		player.setItemOnCursor(null);

		if (PracticeAPI.INSTANCE.getQueueManager().isQueued(player)) {
			final int slot = PracticeAPI.INSTANCE.getQueueManager().getIndex(player).getType() == QueueType.CASUAL ? 0 : 1;
			final ItemStack unqueue = PracticeAPI.INSTANCE.getGuiManager().createItem(Material.REDSTONE, "#redLeave Queue", 1);
			player.getInventory().setItem(slot, unqueue);
		}
	}

	public void load() {
		final GUIManager guiManager = PracticeAPI.INSTANCE.getGuiManager();
		menu.setItem(0, guiManager.createItem(Material.DIAMOND_SWORD, Text.color("#aquaCasual Queue"), 1));
		menu.setItem(1, guiManager.createItem(Material.NETHERITE_SWORD, Text.color("#redCompetitive Queue"), 1));
		menu.setItem(2, guiManager.createItem(Material.BOOK, Text.color("&6Layout Editor"), 1));
		menu.setItem(4, guiManager.createItem(Material.ENDER_EYE, Text.color("&3Spectate"), 1));
		menu.setItem(6, guiManager.createItem(Material.PAPER, Text.color("&7Stats"), 1));
		menu.setItem(7, guiManager.createItem(Material.GOLD_INGOT, Text.color("#yellowLeaderboards"), 1));
		menu.setItem(8, guiManager.createItem(Material.HOPPER, Text.color("&8Settings"), 1));
	}

	public boolean isWhitelisted(final Player player) {
		return player.hasPermission("Practice.whitelist");
	}

	public boolean whitelistEnabled() {
		return whitelist;
	}

	public void enableWhitelist() {
		whitelist = true;

		for (final Player player : Bukkit.getOnlinePlayers()) {
			if (!isWhitelisted(player)) {
				player.kickPlayer(PracticeAPI.INSTANCE.getMessageManager().getWhitelist());
			}
		}
	}

	public void disableWhitelist() {
		whitelist = false;
	}

	public void setFlight(final Player player, final boolean flight) {
		player.setAllowFlight(flight);
		player.setFlying(flight);
	}

	public void enableFlight(final Player player) {
		if (player.hasPermission("jduels.fly")) {
			setFlight(player, true);
		}
	}

	public void disableFlight(final Player player) {
		if (player.hasPermission("jduels.fly")) {
			setFlight(player, false);
		}
	}

	public int getPingRange(final Player player) {
		return Setting.PING_RANGE.get(player);
	}

	public boolean hasDuelRequests(final Player player) {
		return Setting.DUEL_REQUESTS.get(player) == 0 ? false : true;
	}

	public boolean hasDuelChat(final Player player) {
		return Setting.DUEL_CHAT.get(player) == 0 ? false : true;
	}

	public void setPingRange(final Player player, final int range) {
		Setting.PING_RANGE.set(player, range);
	}

	public void setDuelRequests(final Player player, final boolean enabled) {
		final int value = enabled ? 1 : 0;
		Setting.DUEL_REQUESTS.set(player, value);
	}

	public void setDuelChat(final Player player, final boolean enabled) {
		final int value = enabled ? 1 : 0;
		Setting.DUEL_CHAT.set(player, value);
	}
}
