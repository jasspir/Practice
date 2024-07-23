package me.jass.practice.guis;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.jass.practice.datatypes.GUI;

public class SettingsGUI extends DuelGUI {
	public SettingsGUI(final Player player) {
		final Inventory gui = Bukkit.createInventory(null, getSize(GUI.SETTINGS, null), centerTitle("&8Settings"));
		setGUI(gui, player, GUI.SETTINGS);
	}

	public void loadDuelRequests() {
		final String duelRequests;

		if (menuManager.hasDuelRequests(player)) {
			inventory.setItem(2, createItem(Material.LIME_STAINED_GLASS_PANE, "#limeEnabled", 1));
			inventory.setItem(20, createItem(Material.LIME_STAINED_GLASS_PANE, "#limeEnabled", 1));

			duelRequests = "#limeYes";
		} else {
			inventory.setItem(2, createItem(Material.RED_STAINED_GLASS_PANE, "#redDisabled", 1));
			inventory.setItem(20, createItem(Material.RED_STAINED_GLASS_PANE, "#redDisabled", 1));

			duelRequests = "#redNo";
		}

		inventory.setItem(11, createItem(Material.BELL, "#yellowDuel Requests", 1, "&7Enabled: " + duelRequests));

	}

	public void loadDuelChat() {
		final String duelChat;

		if (menuManager.hasDuelChat(player)) {
			inventory.setItem(6, createItem(Material.LIME_STAINED_GLASS_PANE, "#limeEnabled", 1));
			inventory.setItem(24, createItem(Material.LIME_STAINED_GLASS_PANE, "#limeEnabled", 1));

			duelChat = "#limeYes";
		} else {
			inventory.setItem(6, createItem(Material.RED_STAINED_GLASS_PANE, "#redDisabled", 1));
			inventory.setItem(24, createItem(Material.RED_STAINED_GLASS_PANE, "#redDisabled", 1));

			duelChat = "#redNo";
		}

		inventory.setItem(15, createItem(Material.PAPER, "&fDuel Chat", 1, "&7Enabled: " + duelChat));
	}

	public void toggle(final Material material) {
		if (material == Material.BELL) {
			menuManager.setDuelRequests(player, !menuManager.hasDuelRequests(player));
			loadDuelRequests();
		}

		else if (material == Material.PAPER) {
			menuManager.setDuelChat(player, !menuManager.hasDuelChat(player));
			loadDuelChat();
		}
	}

	@Override
	public void loadGUI() {
		fillBorders();

		loadDuelRequests();
		loadDuelChat();

		inventory.setItem(13, createItem(Material.BEACON, "#aquaPing Range", 1));

		inventory.setItem(4, arrowUp());
		inventory.setItem(22, arrowDown());

		hideFlags();
	}

	@Override
	public void clicked(final ItemStack item, final int slot, final ItemStack with) {
		if (isBorder(item)) {
			return;
		}

		final String name = item.getItemMeta().getDisplayName();

		if (name.contains("Duel Requests") || name.contains("Duel Chat")) {
			toggle(item.getType());
			ding();
		}

		else if (name.contains("More") || name.contains("Less")) {
			int amount = 0;
			if (name.contains("More")) {
				amount = inventory.getItem(slot + 9).getAmount();
				if (amount == 64) {
					return;
				}

				inventory.getItem(slot + 9).setAmount(amount + 1);
			}

			else if (name.contains("Less")) {
				amount = inventory.getItem(slot - 9).getAmount();
				if (amount == 1) {
					return;
				}

				inventory.getItem(slot - 9).setAmount(amount - 1);
			}

			menuManager.setPingRange(player, amount);
			ding();
		}
	}

	@Override
	public boolean canMove(final ItemStack item) {
		return false;
	}
}
