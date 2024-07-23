package me.jass.practice.guis;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.jass.practice.datatypes.GUI;
import me.jass.practice.datatypes.Team;
import me.jass.practice.duels.Selection;
import me.jass.practice.files.Arena;

public class ArenaEditorGUI extends DuelGUI {
	public ArenaEditorGUI(final Player player) {
		final Inventory gui = Bukkit.createInventory(null, getSize(GUI.ARENA_EDITOR, null), centerTitle("&8Arena Editor"));
		setGUI(gui, player, GUI.ARENA_EDITOR);
	}

	@Override
	public void loadGUI() {
		fillBorders();
		final Selection selection = guiManager.getSelection(player);
		final Arena arena = selection.getArena();

		//		fillTypeBorders(arena.getUse());

		inventory.setItem(10, createItem(arena.getDisplay(), "#aquaDisplay", 1, "&7Item that displays when selecting this arena", "&7Drag the item you want into this slot"));
		inventory.setItem(11, createItem(getColorDye(arena.getColor()), arena.getColor() + "Color", 1, "&7Color of the display", "&7Drag the color dye into this slot"));
		inventory.setItem(13, createItem(Material.RED_BED, "#redTeam A Spawn", 1, "&7Set spawnpoint of players on Team A"));
		inventory.setItem(14, createItem(Material.BLUE_BED, "#blueTeam B Spawn", 1, "&7Set spawnpoint of players on Team B"));
		inventory.setItem(16, createItem(Material.BARRIER, "#redDelete", 1, "&7Delete (cannot be undone)"));

		hideFlags();
	}

	public void reloadDisplay() {
		final Selection selection = guiManager.getSelection(player);
		final Arena arena = selection.getArena();

		inventory.setItem(10, createItem(arena.getDisplay(), "#aquaDisplay", 1, "&7Item that displays when selecting this arena", "&7Drag the item you want into this slot"));
	}

	public void reloadColor() {
		final Selection selection = guiManager.getSelection(player);
		final Arena arena = selection.getArena();

		inventory.setItem(11, createItem(getColorDye(arena.getColor()), arena.getColor() + "Color", 1, "&7Color of the display", "&7Drag the color dye into this slot"));
	}

	@Override
	public void clicked(final ItemStack item, final int slot, final ItemStack with) {
		final Selection selection = guiManager.getSelection(player);
		final String name = item.getItemMeta().getDisplayName();
		boolean successful = false;

		if (isBorder(item)) {
			//			if (name.contains("Casual")) {
			//				selection.getArena().setUse(QueueType.CASUAL, true);
			//				selection.getArena().setUse(QueueType.COMPETITIVE, false);
			//				toggleBorderType();
			//			}
			//
			//			else if (name.contains("All")) {
			//				selection.getArena().setUse(QueueType.CASUAL, true);
			//				selection.getArena().setUse(QueueType.COMPETITIVE, true);
			//				toggleBorderType();
			//			}
			//
			//			else if (name.contains("Competitive")) {
			//				selection.getArena().setUse(QueueType.CASUAL, true);
			//				selection.getArena().setUse(QueueType.COMPETITIVE, false);
			//				toggleBorderType();
			//			}

			return;
		}

		if (name.contains("Display")) {
			if (with.getType() != Material.AIR) {
				selection.getArena().setDisplay(with.getType());
				reloadDisplay();
				successful = true;
			}
		}

		else if (name.contains("Color")) {
			if (with.getType() != Material.AIR) {
				if (with.getType().toString().contains("DYE")) {
					selection.getArena().setColor(getColor(with.getType().toString()));
					reloadColor();
					successful = true;
				}
			}
		}

		else if (name.contains("Team A Spawn")) {
			selection.getArena().setSpawn(Team.A, player.getLocation());
			successful = true;
		}

		else if (name.contains("Team B Spawn")) {
			selection.getArena().setSpawn(Team.B, player.getLocation());
			successful = true;
		}

		else if (name.contains("Delete")) {
			arenaManager.delete(selection.getArena());
			close();
			successful = true;
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
