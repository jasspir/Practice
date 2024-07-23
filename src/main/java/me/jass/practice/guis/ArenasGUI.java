package me.jass.practice.guis;

import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.jass.practice.datatypes.GUI;
import me.jass.practice.datatypes.Goal;
import me.jass.practice.datatypes.QueueType;
import me.jass.practice.duels.Selection;
import me.jass.practice.files.Arena;

public class ArenasGUI extends DuelGUI {
	private final QueueType type;

	public ArenasGUI(final Player player, final QueueType type) {
		this.type = type;

		GUI size = GUI.ARENAS;

		if (type == QueueType.CASUAL) {
			size = GUI.CASUAL_ARENAS;
		}

		else if (type == QueueType.COMPETITIVE) {
			size = GUI.COMPETITIVE_ARENAS;
		}

		final Inventory gui = Bukkit.createInventory(null, getSize(size, null), centerTitle("&8Arenas"));
		setGUI(gui, player, GUI.ARENAS);
	}

	public void setSelected() {
		final Selection selection = guiManager.getSelection(player);

		for (int i = 0; i < inventory.getSize(); i++) {
			final ItemStack item = inventory.getItem(i);
			final int slot = i;

			if (isBorder(item) || item == null) {
				continue;
			}

			if (selection.getGoal() == Goal.EDIT_KIT) {
				if (selection.getKit().getArenas().contains(new Arena(rawName(item.getItemMeta().getDisplayName())))) {
					inventory.setItem(slot, select(item));
				} else {
					inventory.setItem(slot, unselect(item));
				}
			}
		}
	}

	@Override
	public void loadGUI() {
		setBorders();
		addArenas(type);
		hideFlags();

		final Selection selection = guiManager.getSelection(player);

		if (selection.getGoal() == Goal.EDIT_KIT) {
			setSelected();
		}
	}

	@Override
	public void clicked(final ItemStack item, final int slot, final ItemStack with) {
		if (isBorder(item)) {
			return;
		}

		final Selection selection = guiManager.getSelection(player);
		final String name = rawName(item.getItemMeta().getDisplayName());
		selection.setArena(arenaManager.get(name));

		final Goal goal = selection.getGoal();

		if (goal == Goal.DUEL) {
			guiManager.open(player, new RoundsGUI(player));
		}

		else if (goal == Goal.EDIT_ARENA) {
			guiManager.open(player, new ArenaEditorGUI(player));
		}

		else if (goal == Goal.EDIT_KIT) {
			inventory.setItem(slot, select(item));

			if (item.containsEnchantment(Enchantment.ARROW_INFINITE)) {
				selection.getKit().addArena(arenaManager.get(name));
			} else {
				selection.getKit().removeArena(arenaManager.get(name));
			}
		}

		ding();
	}

	@Override
	public boolean canMove(final ItemStack item) {
		return false;
	}

	public void addArenas(final QueueType type) {
		final Selection selection = guiManager.getSelection(player);
		arenaManager.getAll().forEach(arena -> {
			//			if ((type == null || arena.canBeUsed(type))) {
			if (selection.getGoal() == Goal.DUEL) {
				if (selection.getKit().hasArena(arena)) {
					inventory.addItem(createItem(arena.getDisplay(), arena.getDisplayName(), 1));
				}
			} else {
				inventory.addItem(createItem(arena.getDisplay(), arena.getDisplayName(), 1));
			}
			//			}
		});
	}
}
