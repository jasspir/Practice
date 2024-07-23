package me.jass.practice.guis;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.jass.practice.PracticeAPI;
import me.jass.practice.datatypes.GUI;
import me.jass.practice.duels.Selection;

public class ViewGUI extends DuelGUI {
	public ViewGUI(final Player player) {
		final Selection selection = guiManager.getSelection(player);
		final Inventory gui = Bukkit.createInventory(null, getSize(GUI.VIEWS, null), centerTitle("&8" + selection.getPlayer().getName() + "'s inventory"));
		setGUI(gui, player, GUI.VIEWS);
	}

	@Override
	public void loadGUI() {
		final Selection selection = guiManager.getSelection(player);

		final Inventory view = PracticeAPI.INSTANCE.getDuelManager().getView(selection.getPlayer());
		if (view == null) {
			return;
		}

		inventory.setStorageContents(view.getStorageContents());
	}

	@Override
	public void clicked(final ItemStack item, final int slot, final ItemStack with) {
	}

	@Override
	public boolean canMove(final ItemStack item) {
		return false;
	}
}
