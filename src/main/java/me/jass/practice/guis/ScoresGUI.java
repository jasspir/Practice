package me.jass.practice.guis;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.jass.practice.datatypes.GUI;
import me.jass.practice.datatypes.ScoreType;
import me.jass.practice.duels.Selection;
import me.jass.practice.files.Kit;

public class ScoresGUI extends DuelGUI {
	public ScoresGUI(final Player player) {
		final Inventory gui = Bukkit.createInventory(null, getSize(GUI.SCORES, null), centerTitle("&8Score"));
		setGUI(gui, player, GUI.SCORES);
	}

	public void setSelected() {
		final Selection selection = guiManager.getSelection(player);

		for (int i = 0; i < inventory.getSize(); i++) {
			final ItemStack item = inventory.getItem(i);
			final int slot = i;

			if (item == null || isBorder(item) || item.getType() == Material.COMPASS || item.getType() == Material.RECOVERY_COMPASS || item.getType() == Material.GOLD_INGOT) {
				continue;
			}

			if (i < 27) {
				if (selection.getKit().getScoreType() == ScoreType.valueOf(rawName(item.getItemMeta().getDisplayName()).toUpperCase())) {
					inventory.setItem(slot, select(item));
				} else {
					inventory.setItem(slot, unselect(item));
				}
			} else {
				final Kit kit = selection.getKit();
				switch (slot) {
				case 36:
					enable(slot, kit.isItemDrops());
					break;
				case 37:
					enable(slot, kit.isItemPickups());
					break;
				case 38:
					enable(slot, kit.isDeathDrops());
					break;
				case 39:
					enable(slot, kit.isHungerDepletion());
					break;
				case 40:
					enable(slot, kit.isNaturalRegeneration());
					break;
				case 41:
					enable(slot, kit.isStartingSaturation());
					break;
				case 42:
					enable(slot, kit.isBlockBreaking());
					break;
				case 43:
					enable(slot, kit.isBlockPlacing());
					break;
				case 44:
					enable(slot, kit.isAttacking());
					break;
				}
			}
		}
	}

	public void removeSelected() {
		for (int i = 0; i < inventory.getSize(); i++) {
			final ItemStack item = inventory.getItem(i);
			final int slot = i;

			if (item.containsEnchantment(Enchantment.ARROW_INFINITE)) {
				inventory.setItem(slot, select(item));
			}
		}
	}

	@Override
	public void loadGUI() {
		fillBorders();

		inventory.setItem(9, createItem(Material.WOODEN_SWORD, "#brownBoxing", 1));
		inventory.setItem(10, createItem(Material.TROPICAL_FISH, "#aquaFishing", 1));
		inventory.setItem(11, createItem(Material.BRICK_STAIRS, "&cBridge", 1));
		inventory.setItem(12, createItem(Material.REDSTONE, "#redKills", 1));
		inventory.setItem(13, createItem(Material.SKELETON_SKULL, "&7Deaths", 1));
		inventory.setItem(14, createItem(Material.BARRIER, "#redNone", 1));

		inventory.setItem(7, arrowUp());
		inventory.setItem(16, createItem(Material.GOLD_INGOT, "#yellowRequired Score", guiManager.getSelection(player).getKit().getRequiredScore()));
		inventory.setItem(25, arrowDown());

		inventory.setItem(36, createItem(Material.ARROW, "&7Item Drops", 1));
		inventory.setItem(37, createItem(Material.HOPPER, "&8Item Pickups", 1));
		inventory.setItem(38, createItem(Material.BONE, "&7Death Drops", 1));
		inventory.setItem(39, createItem(Material.COOKED_BEEF, "&6Hunger Depletion", 1));
		inventory.setItem(40, createItem(Material.POTION, "&dNatural Regeneration", 1));
		inventory.setItem(41, createItem(Material.GOLDEN_CARROT, "&eStarting Saturation", 1));
		inventory.setItem(42, createItem(Material.IRON_PICKAXE, "&7Block Breaking", 1));
		inventory.setItem(43, createItem(Material.STONE, "&7Block Placing", 1));
		inventory.setItem(44, createItem(Material.IRON_SWORD, "&cAttacking", 1));

		hideFlags();
		setSelected();
	}

	@Override
	public void clicked(final ItemStack item, final int slot, final ItemStack with) {
		if (isBorder(item)) {
			return;
		}

		final Selection selection = guiManager.getSelection(player);
		final String name = item.getItemMeta().getDisplayName();
		int amount = 1;

		if (name.contains("More")) {
			amount = inventory.getItem(slot + 9).getAmount();
			if (amount == 64) {
				return;
			}

			if (amount == 63) {
				amount = 99;
			}

			inventory.getItem(slot + 9).setAmount(amount + 1);
			selection.getKit().setRequiredScore(amount + 1);
		}

		else if (name.contains("Less")) {
			amount = inventory.getItem(slot - 9).getAmount();
			if (amount == 1) {
				return;
			}

			inventory.getItem(slot - 9).setAmount(amount - 1);
			selection.getKit().setRequiredScore(amount - 1);
		}

		else if (slot >= 27) {
			final Kit kit = selection.getKit();
			switch (slot) {
			case 36:
				kit.setItemDrops(!kit.isItemDrops());
				enable(slot, kit.isItemDrops());
				break;
			case 37:
				kit.setItemPickups(!kit.isItemPickups());
				enable(slot, kit.isItemPickups());
				break;
			case 38:
				kit.setDeathDrops(!kit.isDeathDrops());
				enable(slot, kit.isDeathDrops());
				break;
			case 39:
				kit.setHungerDepletion(!kit.isHungerDepletion());
				enable(slot, kit.isHungerDepletion());
				break;
			case 40:
				kit.setNaturalRegeneration(!kit.isNaturalRegeneration());
				enable(slot, kit.isNaturalRegeneration());
				break;
			case 41:
				kit.setStartingSaturation(!kit.isStartingSaturation());
				enable(slot, kit.isStartingSaturation());
				break;
			case 42:
				kit.setBlockBreaking(!kit.isBlockBreaking());
				enable(slot, kit.isBlockBreaking());
				break;
			case 43:
				kit.setBlockPlacing(!kit.isBlockPlacing());
				enable(slot, kit.isBlockPlacing());
				break;
			case 44:
				kit.setAttacking(!kit.isAttacking());
				enable(slot, kit.isAttacking());
				break;
			}
		} else if (!name.contains("Required Score")) {
			ScoreType type = ScoreType.NONE;
			if (ScoreType.valueOf(rawName(item.getItemMeta().getDisplayName()).toUpperCase()) != null) {
				type = ScoreType.valueOf(rawName(item.getItemMeta().getDisplayName()).toUpperCase());
			}

			selection.getKit().setScoreType(type);
			removeSelected();
			inventory.setItem(slot, select(item));
		}

		ding();
	}

	@Override
	public boolean canMove(final ItemStack item) {
		return false;
	}
}
