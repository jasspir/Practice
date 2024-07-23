package me.jass.practice.guis;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.jass.practice.datatypes.GUI;
import me.jass.practice.duels.Selection;
import me.jass.practice.files.Extra;

public class ExtraEditorGUI extends DuelGUI {
	public ExtraEditorGUI(final Player player) {
		final Inventory gui = Bukkit.createInventory(null, getSize(GUI.EXTRA_EDITOR, null), centerTitle("&8Extra Editor"));
		setGUI(gui, player, GUI.EXTRA_EDITOR);
	}

	@Override
	public void loadGUI() {
		fillBorders();

		final Selection selection = guiManager.getSelection(player);
		final Extra extra = selection.getExtra();

		inventory.setItem(10, createItem(extra.getDisplay(), "#aquaDisplay", 1, "&7Item that displays when selecting", "&7Drag the item you want into this slot"));
		inventory.setItem(12, createItem(getColorDye(extra.getColor()), extra.getColor() + "Color", 1, "&7Color of the display", "&7Drag the color dye into this slot"));
		inventory.setItem(14, createItem(Material.LEATHER_HELMET, "#brownItems", 1, "&7Set items to your current inventory"));
		inventory.setItem(16, createItem(Material.BARRIER, "#redDelete", 1, "&7Delete (cannot be undone)"));

		hideFlags();
	}

	public void reloadDisplay() {
		final Selection selection = guiManager.getSelection(player);
		final Extra extra = selection.getExtra();

		inventory.setItem(10, createItem(extra.getDisplay(), "#aquaDisplay", 1, "&7Item that displays when selecting", "&7Drag the item you want into this slot"));
	}

	public void reloadColor() {
		final Selection selection = guiManager.getSelection(player);
		final Extra extra = selection.getExtra();

		inventory.setItem(12, createItem(getColorDye(extra.getColor()), extra.getColor() + "Color", 1, "&7Color of the display", "&7Drag the color dye into this slot"));
	}

	@Override
	public void clicked(final ItemStack item, final int slot, final ItemStack with) {
		final Selection selection = guiManager.getSelection(player);
		final String name = item.getItemMeta().getDisplayName();
		boolean successful = false;

		if (name.contains("Display")) {
			if (with.getType() != Material.AIR) {
				selection.getExtra().setDisplay(with.getType());
				reloadDisplay();
				successful = true;
			}
		}

		else if (name.contains("Color")) {
			if (with.getType() != Material.AIR) {
				if (with.getType().toString().contains("DYE")) {
					selection.getExtra().setColor(getColor(with.getType().toString()));
					reloadColor();
					successful = true;
				}
			}
		}

		else if (name.contains("Items")) {
			selection.getExtra().setItems(Arrays.asList(player.getInventory().getContents()));
			successful = true;
		}

		else if (name.contains("Delete")) {
			extraManager.delete(selection.getExtra());
			successful = true;
			close();
		}

		if (successful) {
			ding();
		}
	}

	@Override
	public boolean canMove(final ItemStack item) {
		return false;
	}
}
