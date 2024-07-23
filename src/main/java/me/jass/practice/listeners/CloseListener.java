package me.jass.practice.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

import me.jass.practice.PracticeAPI;
import me.jass.practice.datatypes.GUI;
import me.jass.practice.datatypes.Goal;
import me.jass.practice.datatypes.QueueType;
import me.jass.practice.duels.Selection;
import me.jass.practice.guis.ArenasGUI;
import me.jass.practice.guis.DuelGUI;
import me.jass.practice.guis.KitEditorGUI;
import me.jass.practice.guis.KitsGUI;
import me.jass.practice.guis.LayoutEditorGUI;
import me.jass.practice.guis.PlayersGUI;
import me.jass.practice.managers.GUIManager;

public class CloseListener implements Listener {
	@EventHandler
	public void onInventoryClose(final InventoryCloseEvent event) {
		final Player player = (Player) event.getPlayer();

		final GUIManager guiManager = PracticeAPI.INSTANCE.getGuiManager();

		final DuelGUI gui = guiManager.get(player);

		if (gui == null) {
			return;
		}

		player.setItemOnCursor(null);

		final Selection selection = guiManager.getSelection(player);

		if (gui.getInventory().equals(event.getInventory())) {
			guiManager.remove(player);

			if (gui.isClosing()) {
				return;
			}

			if (gui.getType() == GUI.ARENA_EDITOR) {
				PracticeAPI.INSTANCE.getArenaManager().save(selection.getArena());
			}

			else if (gui.getType() == GUI.KIT_EDITOR) {
				PracticeAPI.INSTANCE.getKitManager().save(selection.getKit());
			}

			else if (gui.getType() == GUI.EXTRA_EDITOR) {
				PracticeAPI.INSTANCE.getExtraManager().save(selection.getExtra());
			}

			if (selection == null) {
				return;
			}

			if (selection.getGoal() == Goal.DUEL) {
				if (gui.getType() == GUI.KITS) {
					Bukkit.getScheduler().runTaskLater(PracticeAPI.INSTANCE.getPlugin(), () -> guiManager.open(player, new PlayersGUI(player, GUI.AVAILABLE_PLAYERS)), 1);
				}

				else if (gui.getType() == GUI.ARENAS) {
					Bukkit.getScheduler().runTaskLater(PracticeAPI.INSTANCE.getPlugin(), () -> guiManager.open(player, new KitsGUI(player, QueueType.CASUAL)), 1);
				}

				else if (gui.getType() == GUI.ROUNDS) {
					Bukkit.getScheduler().runTaskLater(PracticeAPI.INSTANCE.getPlugin(), () -> guiManager.open(player, new ArenasGUI(player, QueueType.CASUAL)), 1);
				}
			}

			else if (selection.getGoal() == Goal.EDIT_KIT) {
				if (gui.getType() == GUI.ARENAS) {
					Bukkit.getScheduler().runTaskLater(PracticeAPI.INSTANCE.getPlugin(), () -> guiManager.open(player, new KitEditorGUI(player)), 1);
				}

				else if (gui.getType() == GUI.EXTRAS) {
					Bukkit.getScheduler().runTaskLater(PracticeAPI.INSTANCE.getPlugin(), () -> guiManager.open(player, new KitEditorGUI(player)), 1);
				}

				else if (gui.getType() == GUI.SCORES) {
					Bukkit.getScheduler().runTaskLater(PracticeAPI.INSTANCE.getPlugin(), () -> guiManager.open(player, new KitEditorGUI(player)), 1);
				}
			}

			else if (selection.getGoal() == Goal.EDIT_LAYOUT) {
				if (gui.getType() == GUI.EXTRAS) {
					Bukkit.getScheduler().runTaskLater(PracticeAPI.INSTANCE.getPlugin(), () -> guiManager.open(player, new LayoutEditorGUI(player)), 1);
				}

				else if (gui.getType() == GUI.LAYOUT_EDITOR) {
					gui.saveLayout();
				}
			}
		}
	}
}
