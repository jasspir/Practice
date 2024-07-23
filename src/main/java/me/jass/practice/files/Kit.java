package me.jass.practice.files;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import lombok.Getter;
import lombok.Setter;
import me.jass.practice.PracticeAPI;
import me.jass.practice.datatypes.ScoreType;

@Getter
@Setter
public class Kit {
	private final String name;
	private List<ItemStack> items = new ArrayList<ItemStack>();
	private List<PotionEffect> effects = new ArrayList<PotionEffect>();
	private Material display = Material.IRON_SWORD;
	//	@Getter(value = AccessLevel.NONE)
	//	@Setter(value = AccessLevel.NONE)
	//	private boolean casual = true;
	//	@Getter(value = AccessLevel.NONE)
	//	@Setter(value = AccessLevel.NONE)
	//	private boolean competitive = false;
	private ScoreType scoreType = ScoreType.NONE;
	private int requiredScore;
	private List<Arena> arenas = new ArrayList<Arena>();
	private List<Extra> extras = new ArrayList<Extra>();
	private ChatColor color = ChatColor.WHITE;
	private boolean itemDrops = false;
	private boolean itemPickups = false;
	private boolean deathDrops = false;
	private boolean hungerDepletion = false;
	private boolean naturalRegeneration = false;
	private boolean startingSaturation = false;
	private boolean blockBreaking = false;
	private boolean blockPlacing = false;
	private boolean attacking = true;

	public Kit(final String name) {
		this.name = name.replaceAll(" ", "_").toLowerCase();
	}

	public List<String> getArenaNames() {
		final List<String> names = new ArrayList<String>();
		for (final Arena arena : arenas) {
			names.add(arena.getName());
		}
		return names;
	}

	public List<String> getExtraNames() {
		final List<String> names = new ArrayList<String>();
		for (final Extra extra : extras) {
			names.add(extra.getName());
		}
		return names;
	}

	public boolean hasExtra(final Extra extra) {
		return extras.contains(extra);
	}

	public void addExtra(final Extra extra) {
		extras.add(extra);
	}

	public void removeExtra(final Extra extra) {
		extras.remove(extra);
	}

	public String getDisplayName() {
		return color + WordUtils.capitalize(name.replaceAll("_", " "));
	}

	public boolean hasArena(final Arena arena) {
		return arenas.contains(arena);
	}

	public void addArena(final Arena arena) {
		arenas.add(arena);
	}

	public void removeArena(final Arena arena) {
		arenas.remove(arena);
	}

	//	public void setQueue(final QueueType type, final boolean hasQueue) {
	//		if (type == QueueType.CASUAL) {
	//			casual = hasQueue;
	//		}
	//
	//		if (type == QueueType.COMPETITIVE) {
	//			competitive = hasQueue;
	//		}
	//	}
	//
	//	public boolean hasQueue(final QueueType type) {
	//		if (type == QueueType.CASUAL) {
	//			return casual;
	//		}
	//
	//		if (type == QueueType.COMPETITIVE) {
	//			return competitive;
	//		}
	//
	//		return false;
	//	}
	//
	//	public QueueType getQueueType() {
	//		if (casual && competitive) {
	//			return null;
	//		}
	//
	//		else if (competitive) {
	//			return QueueType.COMPETITIVE;
	//		}
	//
	//		else {
	//			return QueueType.CASUAL;
	//		}
	//	}

	public List<ItemStack> getFixedItems(final Player player) {
		final List<ItemStack> defaultItems = getDefaultItems(player);
		final int[] layout = PracticeAPI.INSTANCE.getKitManager().getLayout(player, this);
		final List<ItemStack> items = new ArrayList<ItemStack>(defaultItems);

		for (int i = 0; i < 41; i++) {
			final int slot = layout[i];
			final ItemStack item = defaultItems.get(i);
			items.set(slot, item);
		}

		return items;
	}

	public List<ItemStack> getDefaultItems(final Player player) {
		final List<ItemStack> baseItems = items;
		final List<Extra> extras = PracticeAPI.INSTANCE.getExtraManager().getSet(player, this);
		final List<ItemStack> items = new ArrayList<ItemStack>(baseItems);

		for (int i = 0; i < 41; i++) {
			final int slot = i;
			ItemStack item = baseItems.get(slot);

			for (final Extra extra : extras) {
				if (extra.getItems().get(slot) != null) {
					item = extra.getItems().get(slot);
				}
			}

			items.set(slot, item);
		}

		return items;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null) {
			return false;
		}

		if (o == this) {
			return true;
		}

		if (!(o instanceof Kit)) {
			return false;
		}

		final Kit t = (Kit) o;

		return (name.equals(t.getName()));
	}
}
