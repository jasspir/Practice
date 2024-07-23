package me.jass.practice.managers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import lombok.Getter;
import me.jass.practice.PracticeAPI;
import me.jass.practice.duels.Selection;
import me.jass.practice.guis.DuelGUI;
import me.jass.practice.utils.Text;

public class GUIManager {
	private final Map<Player, DuelGUI> guis = new HashMap<Player, DuelGUI>();
	private final Map<Player, Selection> selections = new HashMap<Player, Selection>();
	private final Set<Player> closing = new HashSet<Player>();
	@Getter
	private final Inventory border27 = Bukkit.createInventory(null, 27);
	@Getter
	private final Inventory border36 = Bukkit.createInventory(null, 36);
	@Getter
	private final Inventory border45 = Bukkit.createInventory(null, 45);
	@Getter
	private final Inventory border54 = Bukkit.createInventory(null, 54);
	@Getter
	private final Inventory filled27 = Bukkit.createInventory(null, 27);
	@Getter
	private final Inventory filled36 = Bukkit.createInventory(null, 36);
	@Getter
	private final Inventory filled45 = Bukkit.createInventory(null, 45);
	@Getter
	private final Inventory filled54 = Bukkit.createInventory(null, 54);

	public void loadDefault() {
		addBorders(border27);
		addBorders(border36);
		addBorders(border45);
		addBorders(border54);
		fillBorders(filled27);
		fillBorders(filled36);
		fillBorders(filled45);
		fillBorders(filled54);
	}

	public void fillBorders(final Inventory inventory) {
		for (int i = 0; i < inventory.getSize(); i++) {
			ItemStack item = inventory.getItem(i);
			if (item == null) {
				item = border();
				inventory.setItem(i, item);
			}
		}
	}

	public void addBorders(final Inventory inventory) {
		for (int i = 0; i < 9; i++) {
			inventory.setItem(i, border());
		}

		if (inventory.getSize() == 27) {
			for (int i = 18; i < 27; i++) {
				inventory.setItem(i, border());
			}

			inventory.setItem(9, border());
			inventory.setItem(17, border());
		}

		if (inventory.getSize() == 36) {
			for (int i = 27; i < 36; i++) {
				inventory.setItem(i, border());
			}

			inventory.setItem(9, border());
			inventory.setItem(17, border());
			inventory.setItem(18, border());
			inventory.setItem(26, border());
		}

		if (inventory.getSize() == 45) {
			for (int i = 36; i < 45; i++) {
				inventory.setItem(i, border());
			}

			inventory.setItem(9, border());
			inventory.setItem(17, border());
			inventory.setItem(18, border());
			inventory.setItem(26, border());
			inventory.setItem(27, border());
			inventory.setItem(35, border());
		}

		if (inventory.getSize() == 54) {
			for (int i = 45; i < 54; i++) {
				inventory.setItem(i, border());
			}

			inventory.setItem(9, border());
			inventory.setItem(17, border());
			inventory.setItem(18, border());
			inventory.setItem(26, border());
			inventory.setItem(27, border());
			inventory.setItem(35, border());
			inventory.setItem(36, border());
			inventory.setItem(44, border());
		}
	}

	public ItemStack border() {
		return createItem(Material.GRAY_STAINED_GLASS_PANE, " ", 1);
	}

	public void unload(final Player player) {
		guis.remove(player);
		selections.remove(player);
		closing.remove(player);
	}

	public DuelGUI get(final Player player) {
		return guis.get(player);
	}

	public void remove(final Player player) {
		guis.remove(player);
	}

	public DuelGUI set(final Player player, final DuelGUI gui) {
		return guis.put(player, gui);
	}

	public void open(final Player player, final DuelGUI gui) {
		gui.open(player);
		guis.put(player, gui);
	}

	public void setSelection(final Player player, final Selection selection) {
		selections.put(player, selection);
	}

	public void removeSelection(final Player player) {
		selections.remove(player);
	}

	public Selection getSelection(final Player player) {
		return selections.get(player);
	}

	public void clearSelections() {
		selections.clear();
	}

	public boolean isClosing(final Player player) {
		return closing.contains(player);
	}

	public void setClosing(final Player player) {
		closing.add(player);
		Bukkit.getScheduler().runTaskLater(PracticeAPI.INSTANCE.getPlugin(), () -> closing.remove(player), 2);
	}

	public ItemStack createItem(final Material material, final String name, final int amount, final String... lore) {
		final ItemStack item = new ItemStack(material, amount);
		final ItemMeta meta = item.getItemMeta();

		for (int i = 0; i < lore.length; i++) {
			lore[i] = Text.color(lore[i]);
		}

		meta.setDisplayName(Text.color(name));
		meta.setLore(Arrays.asList(lore));
		item.setItemMeta(meta);

		return item;
	}
}
