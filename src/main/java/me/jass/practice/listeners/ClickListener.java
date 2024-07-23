package me.jass.practice.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;

import me.jass.practice.PracticeAPI;
import me.jass.practice.datatypes.GUI;
import me.jass.practice.guis.DuelGUI;
import me.jass.practice.utils.Text;

public class ClickListener implements Listener {
	@EventHandler
	public void onInventoryClick(final InventoryClickEvent event) {
		final Player player = (Player) event.getWhoClicked();
		final DuelGUI gui = PracticeAPI.INSTANCE.getGuiManager().get(player);

		if (gui == null) {
			if (!PracticeAPI.INSTANCE.getDuelManager().isDueling(player)) {
				if (PracticeAPI.INSTANCE.getMenuManager().get().contains(event.getCurrentItem())) {
					event.setCancelled(true);
				}
			}
			return;
		}

		if (event.getClickedInventory() == null) {
			event.setCancelled(true);
			return;
		}

		if (event.getClickedInventory().equals(player.getInventory())) {
			if (!gui.canEditSelf()) {
				event.setCancelled(true);
				return;
			}
		}

		if (event.getSlot() < 0) {
			event.setCancelled(true);
			return;
		}

		if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY || event.getAction() == InventoryAction.HOTBAR_SWAP || event.getAction() == InventoryAction.HOTBAR_MOVE_AND_READD) {
			event.setCancelled(true);
			return;
		}

		if (gui.getInventory().equals(event.getClickedInventory())) {
			if (gui.getType() == GUI.LAYOUT_EDITOR) {
				if (event.getAction() == InventoryAction.PLACE_ONE || event.getAction() == InventoryAction.PICKUP_HALF) {
					event.setCancelled(true);
					Text.alert(event.getAction().toString());
					return;
				}
			}

			if (event.getCurrentItem() != null) {
				gui.clicked(event.getCurrentItem(), event.getSlot(), event.getCursor());
				if (!gui.canMove(event.getCurrentItem())) {
					event.setCancelled(true);
					return;
				}
			}
		}
	}
}
