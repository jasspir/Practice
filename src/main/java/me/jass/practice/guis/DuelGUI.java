package me.jass.practice.guis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import lombok.Getter;
import me.jass.practice.PracticeAPI;
import me.jass.practice.datatypes.GUI;
import me.jass.practice.datatypes.QueueType;
import me.jass.practice.duels.Selection;
import me.jass.practice.files.Kit;
import me.jass.practice.managers.ArenaManager;
import me.jass.practice.managers.DuelManager;
import me.jass.practice.managers.ExtraManager;
import me.jass.practice.managers.GUIManager;
import me.jass.practice.managers.KitManager;
import me.jass.practice.managers.MenuManager;
import me.jass.practice.managers.QueueManager;
import me.jass.practice.managers.StatManager;
import me.jass.practice.utils.Text;

public abstract class DuelGUI {
	@Getter
	protected Inventory inventory;
	@Getter
	protected Player player;
	@Getter
	protected GUI type;
	protected final GUIManager guiManager = PracticeAPI.INSTANCE.getGuiManager();
	protected final KitManager kitManager = PracticeAPI.INSTANCE.getKitManager();
	protected final ArenaManager arenaManager = PracticeAPI.INSTANCE.getArenaManager();
	protected final ExtraManager extraManager = PracticeAPI.INSTANCE.getExtraManager();
	protected final DuelManager duelManager = PracticeAPI.INSTANCE.getDuelManager();
	protected final QueueManager queueManager = PracticeAPI.INSTANCE.getQueueManager();
	protected final MenuManager menuManager = PracticeAPI.INSTANCE.getMenuManager();
	protected final StatManager statManager = PracticeAPI.INSTANCE.getStatManager();

	public void setGUI(final Inventory inventory, final Player player, final GUI type) {
		this.inventory = inventory;
		this.player = player;
		this.type = type;
		loadGUI();
		PracticeAPI.INSTANCE.getGuiManager().set(player, this);
	}

	public double fixAmount(double amount) {
		if (amount == 0) {
			amount = 1;
		}

		return (18 + (Math.ceil(amount / 7) * 9));
	}

	public void setBorders() {
		final int size = inventory.getSize();
		if (size == 27) {
			inventory.setStorageContents(guiManager.getBorder27().getStorageContents());
		} else if (size == 36) {
			inventory.setStorageContents(guiManager.getBorder36().getStorageContents());
		} else if (size == 45) {
			inventory.setStorageContents(guiManager.getBorder45().getStorageContents());
		} else if (size == 54) {
			inventory.setStorageContents(guiManager.getBorder54().getStorageContents());
		}
	}

	public void fillBorders() {
		final int size = inventory.getSize();
		if (size == 27) {
			inventory.setStorageContents(guiManager.getFilled27().getStorageContents());
		} else if (size == 36) {
			inventory.setStorageContents(guiManager.getFilled36().getStorageContents());
		} else if (size == 45) {
			inventory.setStorageContents(guiManager.getFilled45().getStorageContents());
		} else if (size == 54) {
			inventory.setStorageContents(guiManager.getFilled54().getStorageContents());
		}
	}

	public void fillTypeBorders(final QueueType type) {
		fillBorders();

		for (int i = 0; i < inventory.getSize(); i++) {
			final ItemStack item = inventory.getItem(i);
			if (type == null) {
				final ItemMeta meta = item.getItemMeta();
				meta.setDisplayName(Text.color("#aquaAll"));
				item.setItemMeta(meta);
				item.setType(Material.LIGHT_BLUE_STAINED_GLASS_PANE);
				inventory.setItem(i, item);
			}

			else if (type == QueueType.CASUAL) {
				final ItemMeta meta = item.getItemMeta();
				meta.setDisplayName(Text.color("#limeCasual"));
				item.setItemMeta(meta);
				item.setType(Material.LIME_STAINED_GLASS_PANE);
				inventory.setItem(i, item);
			}

			else if (type == QueueType.COMPETITIVE) {
				final ItemMeta meta = item.getItemMeta();
				meta.setDisplayName(Text.color("#yellowCompetitive"));
				item.setItemMeta(meta);
				item.setType(Material.YELLOW_STAINED_GLASS_PANE);
				inventory.setItem(i, item);
			}
		}
	}

	public int getSize(final GUI size, final Kit kit) {
		double amount = 9;
		if (size == GUI.KIT_EDITOR || size == GUI.ARENA_EDITOR || size == GUI.EXTRA_EDITOR || size == GUI.SETTINGS || size == GUI.ROUNDS || size == GUI.CHALLENGE || size == GUI.QUEUES) {
			amount = 27;
		}

		else if (size == GUI.VIEWS || size == GUI.LAYOUT_EDITOR || size == GUI.SCORES) {
			amount = 54;
		}

		else {
			if (size == GUI.KITS) {
				amount = kitManager.getAmount(null);
			}

			else if (size == GUI.ARENAS) {
				amount = arenaManager.getAmount(null, null);
			}

			else if (size == GUI.CASUAL_KITS) {
				amount = kitManager.getAmount(QueueType.CASUAL);
			}

			else if (size == GUI.CASUAL_ARENAS) {
				amount = arenaManager.getAmount(QueueType.CASUAL, kit);
			}

			else if (size == GUI.COMPETITIVE_KITS) {
				amount = kitManager.getAmount(QueueType.COMPETITIVE);
			}

			else if (size == GUI.COMPETITIVE_ARENAS) {
				amount = arenaManager.getAmount(QueueType.COMPETITIVE, kit);
			}

			else if (size == GUI.EXTRAS) {
				amount = extraManager.getAmount(player, kit);
			}

			else if (size == GUI.AVAILABLE_PLAYERS) {
				amount = duelManager.availablePlayers();
			}

			else if (size == GUI.UNAVAILABLE_PLAYERS) {
				amount = duelManager.unavailablePlayers();
			}

			amount = fixAmount(amount);
		}

		return (int) amount;
	}

	public ItemStack border() {
		return createItem(Material.GRAY_STAINED_GLASS_PANE, " ", 1);
	}

	public String rawName(final String string) {
		return ChatColor.stripColor(string).replaceAll(" ", "_").toLowerCase();
	}

	public String centerTitle(String title) {
		title = ChatColor.stripColor(Text.color(title));
		final StringBuilder result = new StringBuilder();
		double length = title.length();
		for (int i = 0; i < title.length(); i++) {
			if (title.charAt(i) == 'l' || title.charAt(i) == 'i' || title.charAt(i) == '!' || title.charAt(i) == '\'' || title.charAt(i) == ',' || title.charAt(i) == '.' || title.charAt(i) == ':'
					|| title.charAt(i) == ';') {
				length -= 0.66;
			}

			else if (title.charAt(i) == 't' || title.charAt(i) == '*') {
				length -= 0.33;
			}
		}

		final int spaces = (int) Math.round(((27 - length) / 2) * 1.5);

		for (int i = 0; i < spaces; i++) {
			result.append(" ");
		}

		result.append(title).toString();

		return result.toString();
	}

	public void toggleBorderType() {
		for (int i = 0; i < inventory.getSize(); i++) {
			final ItemStack item = inventory.getItem(i);
			final int slot = i;

			if (item == null || item.getType() == Material.AIR) {
				continue;
			}

			if (isBorder(item)) {
				if (item.getType() == Material.LIGHT_BLUE_STAINED_GLASS_PANE) {
					final ItemMeta meta = item.getItemMeta();
					meta.setDisplayName(Text.color("#limeCasual"));
					item.setItemMeta(meta);
					item.setType(Material.LIME_STAINED_GLASS_PANE);
					inventory.setItem(slot, item);
				}

				else if (item.getType() == Material.YELLOW_STAINED_GLASS_PANE) {
					final ItemMeta meta = item.getItemMeta();
					meta.setDisplayName(Text.color("#aquaAll"));
					item.setItemMeta(meta);
					item.setType(Material.LIGHT_BLUE_STAINED_GLASS_PANE);
					inventory.setItem(slot, item);
				}

				else if (item.getType() == Material.LIME_STAINED_GLASS_PANE) {
					final ItemMeta meta = item.getItemMeta();
					meta.setDisplayName(Text.color("#yellowCompetitive"));
					item.setItemMeta(meta);
					item.setType(Material.YELLOW_STAINED_GLASS_PANE);
					inventory.setItem(slot, item);
				}

				ding();
			}
		}
	}

	public void hideFlags() {
		for (int i = 0; i < inventory.getSize(); i++) {
			final ItemStack item = inventory.getItem(i);
			if (item == null || item.getType() == Material.AIR || item.getType() == Material.PLAYER_HEAD) {
				continue;
			}

			final ItemMeta meta = item.getItemMeta();
			meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			item.setItemMeta(meta);
			inventory.setItem(i, item);
		}
	}

	public boolean isBorder(final ItemStack item) {
		if (item == null || item.getType() == Material.AIR) {
			return false;
		}

		return item.getType() == Material.GRAY_STAINED_GLASS_PANE || item.getType() == Material.LIGHT_BLUE_STAINED_GLASS_PANE || item.getType() == Material.LIME_STAINED_GLASS_PANE
				|| item.getType() == Material.YELLOW_STAINED_GLASS_PANE || item.getType() == Material.RED_STAINED_GLASS_PANE;
	}

	public void close() {
		setClosing();
		player.closeInventory();
	}

	public boolean isClosing() {
		return guiManager.isClosing(player);
	}

	public void setClosing() {
		guiManager.setClosing(player);
	}

	public ChatColor getColor(String color) {
		color = color.toUpperCase();
		color = color.replaceAll("_DYE", "");

		if (color.equals("WHITE")) {
			return ChatColor.WHITE;
		}

		else if (color.equals("LIGHT_GRAY")) {
			return ChatColor.GRAY;
		}

		else if (color.equals("GRAY")) {
			return ChatColor.DARK_GRAY;
		}

		else if (color.equals("BLACK")) {
			return ChatColor.BLACK;
		}

		else if (color.equals("BROWN")) {
			return ChatColor.GOLD;
		}

		else if (color.equals("RED")) {
			return ChatColor.RED;
		}

		else if (color.equals("ORANGE")) {
			return ChatColor.GOLD;
		}

		else if (color.equals("YELLOW")) {
			return ChatColor.YELLOW;
		}

		else if (color.equals("GREEN")) {
			return ChatColor.DARK_GREEN;
		}

		else if (color.equals("LIME")) {
			return ChatColor.GREEN;
		}

		else if (color.equals("BLUE")) {
			return ChatColor.DARK_BLUE;
		}

		else if (color.equals("LIGHT_BLUE")) {
			return ChatColor.AQUA;
		}

		else if (color.equals("CYAN")) {
			return ChatColor.BLUE;
		}

		else if (color.equals("PURPLE")) {
			return ChatColor.DARK_PURPLE;
		}

		else if (color.equals("MAGENTA")) {
			return ChatColor.LIGHT_PURPLE;
		}

		else if (color.equals("PINK")) {
			return ChatColor.LIGHT_PURPLE;
		}

		return ChatColor.WHITE;
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

	public Material getColorDye(final ChatColor color) {
		if (color == ChatColor.WHITE) {
			return Material.WHITE_DYE;
		}

		else if (color == ChatColor.GRAY) {
			return Material.LIGHT_GRAY_DYE;
		}

		else if (color == ChatColor.DARK_GRAY) {
			return Material.GRAY_DYE;
		}

		else if (color == ChatColor.BLACK) {
			return Material.BLACK_DYE;
		}

		else if (color == ChatColor.RED) {
			return Material.RED_DYE;
		}

		else if (color == ChatColor.GOLD) {
			return Material.ORANGE_DYE;
		}

		else if (color == ChatColor.YELLOW) {
			return Material.YELLOW_DYE;
		}

		else if (color == ChatColor.DARK_GREEN) {
			return Material.GREEN_DYE;
		}

		else if (color == ChatColor.GREEN) {
			return Material.LIME_DYE;
		}

		else if (color == ChatColor.DARK_BLUE) {
			return Material.BLUE_DYE;
		}

		else if (color == ChatColor.AQUA) {
			return Material.LIGHT_BLUE_DYE;
		}

		else if (color == ChatColor.BLUE) {
			return Material.CYAN_DYE;
		}

		else if (color == ChatColor.DARK_PURPLE) {
			return Material.PURPLE_DYE;
		}

		else if (color == ChatColor.LIGHT_PURPLE) {
			return Material.MAGENTA_DYE;
		}

		return Material.WHITE_DYE;
	}

	public ItemStack arrowUp() {
		//		final ItemStack head = createItem(Material.PLAYER_HEAD, Text.color("&7More"), 1);
		//		final SkullMeta meta = (SkullMeta) head.getItemMeta();
		//		meta.setOwningPlayer(Bukkit.getOfflinePlayer("MHF_arrowup"));
		//		head.setItemMeta(meta);
		return createItem(Material.COMPASS, Text.color("&7More"), 1);
	}

	public ItemStack arrowDown() {
		//		final ItemStack head = createItem(Material.PLAYER_HEAD, Text.color("&7Less"), 1);
		//		final SkullMeta meta = (SkullMeta) head.getItemMeta();
		//		meta.setOwningPlayer(Bukkit.getOfflinePlayer("MHF_arrowdown"));
		//		head.setItemMeta(meta);
		return createItem(Material.RECOVERY_COMPASS, Text.color("&7Less"), 1);
	}

	public void ding() {
		player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, 1, 1);
	}

	public ItemStack select(final ItemStack item) {
		if (isBorder(item)) {
			return item;
		}

		final List<String> selected;

		if (item.containsEnchantment(Enchantment.ARROW_INFINITE) && item.getEnchantmentLevel(Enchantment.ARROW_INFINITE) == 10) {
			item.removeEnchantment(Enchantment.ARROW_INFINITE);
			selected = Arrays.asList(Text.color("&7Selected: #redNo"));
		} else {
			item.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 10);
			selected = Arrays.asList(Text.color("&7Selected: #limeYes"));
		}

		final ItemMeta meta = item.getItemMeta();
		meta.setLore(selected);
		item.setItemMeta(meta);

		return item;
	}

	public void enable(final int slot, final boolean enabled) {
		final ItemStack item = inventory.getItem(slot);
		final List<String> lore;
		String name;
		Material type;

		if (enabled) {
			lore = Arrays.asList(Text.color("&7Enabled: #limeYes"));
			name = "#limeEnabled";
			type = Material.LIME_STAINED_GLASS_PANE;
		} else {
			lore = Arrays.asList(Text.color("&7Enabled: #redNo"));
			name = "#redDisabled";
			type = Material.RED_STAINED_GLASS_PANE;
		}

		final ItemMeta meta = item.getItemMeta();
		meta.setLore(lore);
		item.setItemMeta(meta);

		final ItemStack border = createItem(type, name, 1);

		inventory.setItem(slot, item);
		inventory.setItem(slot + 9, border);
		inventory.setItem(slot - 9, border);
	}

	public ItemStack unselect(final ItemStack item) {
		item.removeEnchantment(Enchantment.ARROW_INFINITE);
		final ItemMeta meta = item.getItemMeta();
		meta.setLore(Arrays.asList(Text.color("&7Selected: #redNo")));
		item.setItemMeta(meta);
		return item;
	}

	public boolean canEditSelf() {
		if (type == GUI.KIT_EDITOR || type == GUI.ARENA_EDITOR || type == GUI.EXTRA_EDITOR) {
			return true;
		} else {
			return false;
		}
	}

	public void open(final Player player) {
		setClosing();
		player.openInventory(inventory);
	}

	public void saveLayout() {
		final Selection selection = guiManager.getSelection(player);
		final List<ItemStack> oldItems = new ArrayList<ItemStack>(selection.getKit().getDefaultItems(player));
		final List<ItemStack> newItems = new ArrayList<ItemStack>(oldItems);
		final List<Integer> layout = IntStream.of(kitManager.getDefaultLayout()).boxed().collect(Collectors.toList());
		int slot = 0;

		for (int i = 1; i < 7; i++) {
			final ItemStack item = inventory.getItem(i);
			if (i == 6) {
				slot = 40;
			} else if (i == 5) {
				continue;
			} else {
				slot = 40 - i;
			}

			newItems.set(slot, item);
		}

		for (int i = 36; i < 45; i++) {
			final ItemStack item = inventory.getItem(i);
			slot = i - 36;
			newItems.set(slot, item);
		}

		for (int i = 9; i < 36; i++) {
			final ItemStack item = inventory.getItem(i);
			slot = i;
			newItems.set(slot, item);
		}

		for (int i = 0; i < 41; i++) {
			final ItemStack item = newItems.get(i);
			if (item == null || !oldItems.contains(item)) {
				continue;
			}

			final int oldSlot = oldItems.indexOf(item);
			oldItems.set(oldSlot, null);
			final int newSlot = i;

			layout.set(oldSlot, newSlot);
			layout.set(newSlot, oldSlot);
		}

		kitManager.setLayout(player, selection.getKit(), layout.stream().mapToInt(i -> i).toArray());
	}

	public abstract void loadGUI();

	public abstract void clicked(ItemStack item, int slot, ItemStack with);

	public abstract boolean canMove(ItemStack item);
}
