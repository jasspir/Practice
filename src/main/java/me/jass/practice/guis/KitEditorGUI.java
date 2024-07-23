package me.jass.practice.guis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import me.jass.practice.datatypes.GUI;
import me.jass.practice.datatypes.QueueType;
import me.jass.practice.duels.Selection;
import me.jass.practice.files.Kit;

public class KitEditorGUI extends DuelGUI {
	public KitEditorGUI(final Player player) {
		final Inventory gui = Bukkit.createInventory(null, getSize(GUI.KIT_EDITOR, null), centerTitle("&8Kit Editor"));
		setGUI(gui, player, GUI.KIT_EDITOR);
	}

	@Override
	public void loadGUI() {
		fillBorders();
		final Selection selection = guiManager.getSelection(player);
		final Kit kit = selection.getKit();

		inventory.setItem(10, createItem(kit.getDisplay(), "#aquaDisplay", 1, "&7Item that displays when selecting", "&7Drag the item you want into this slot"));
		inventory.setItem(11, createItem(getColorDye(kit.getColor()), kit.getColor() + "Color", 1, "&7Color of the display", "&7Drag the color dye into this slot"));
		inventory.setItem(12, createItem(Material.GOLD_INGOT, "#yellowScore", 1, "&7Score type and related kit settings"));
		inventory.setItem(13, createItem(Material.NETHER_STAR, "#aquaExtras", 1, "&7Available extras"));
		inventory.setItem(14, createItem(Material.GRASS_BLOCK, "#limeArenas", 1, "&7Available arenas"));
		inventory.setItem(15, createItem(Material.LEATHER_HELMET, "#brownKit", 1, "&7Set items & effects to your current inventory"));
		inventory.setItem(16, createItem(Material.BARRIER, "#redDelete", 1, "&7Delete (cannot be undone)"));

		hideFlags();
	}

	public void reloadDisplay() {
		final Selection selection = guiManager.getSelection(player);
		final Kit kit = selection.getKit();

		inventory.setItem(10, createItem(kit.getDisplay(), "#aquaDisplay", 1, "&7Item that displays when selecting", "&7Drag the item you want into this slot"));
	}

	public void reloadColor() {
		final Selection selection = guiManager.getSelection(player);
		final Kit kit = selection.getKit();

		inventory.setItem(11, createItem(getColorDye(kit.getColor()), kit.getColor() + "Color", 1, "&7Color of the display", "&7Drag the color dye into this slot"));
	}

	@Override
	public void clicked(final ItemStack item, final int slot, final ItemStack with) {
		final Selection selection = guiManager.getSelection(player);
		final String name = item.getItemMeta().getDisplayName();
		boolean successful = false;

		if (isBorder(item)) {
			//			if (name.contains("Casual")) {
			//				selection.getKit().setQueue(QueueType.CASUAL, true);
			//				selection.getKit().setQueue(QueueType.COMPETITIVE, false);
			//				toggleBorderType();
			//			}
			//
			//			else if (name.contains("All")) {
			//				selection.getKit().setQueue(QueueType.CASUAL, true);
			//				selection.getKit().setQueue(QueueType.COMPETITIVE, true);
			//				toggleBorderType();
			//			}
			//
			//			else if (name.contains("Competitive")) {
			//				selection.getKit().setQueue(QueueType.CASUAL, true);
			//				selection.getKit().setQueue(QueueType.COMPETITIVE, false);
			//				toggleBorderType();
			//			}

			return;
		}

		if (name.contains("Display")) {
			if (with.getType() != Material.AIR) {
				selection.getKit().setDisplay(with.getType());
				reloadDisplay();
				successful = true;
			}
		}

		else if (name.contains("Color")) {
			if (with.getType() != Material.AIR) {
				if (with.getType().toString().contains("DYE")) {
					selection.getKit().setColor(getColor(with.getType().toString()));
					reloadColor();
					successful = true;
				}
			}
		}

		else if (name.contains("Score")) {
			guiManager.open(player, new ScoresGUI(player));
			successful = true;
		}

		else if (name.contains("Extras")) {
			guiManager.open(player, new ExtrasGUI(player));
			successful = true;
		}

		else if (name.contains("Arenas")) {
			guiManager.open(player, new ArenasGUI(player, QueueType.CASUAL));
			successful = true;
		}

		else if (name.contains("Kit")) {
			final List<PotionEffect> effects = new ArrayList<PotionEffect>();
			effects.addAll(player.getActivePotionEffects());

			selection.getKit().setItems(Arrays.asList(player.getInventory().getContents()));
			selection.getKit().setEffects(effects);
			successful = true;
		}

		else if (name.contains("Delete")) {
			kitManager.delete(selection.getKit());
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
