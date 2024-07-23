package me.jass.practice.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryDragEvent;

import me.jass.practice.PracticeAPI;
import me.jass.practice.datatypes.GUI;
import me.jass.practice.guis.DuelGUI;

public class DragListener implements Listener {
	@EventHandler
	public void onInventoryDrag(final InventoryDragEvent event) {
		final Player player = (Player) event.getWhoClicked();
		final DuelGUI gui = PracticeAPI.INSTANCE.getGuiManager().get(player);

		if (gui == null || event.getInventory() == null) {
			return;
		}

		if (gui.getType() == GUI.LAYOUT_EDITOR) {
			for (final Integer i : event.getRawSlots()) {
				if (i > 53) {
					event.setCancelled(true);
					return;
				}
			}
		}
	}
}
