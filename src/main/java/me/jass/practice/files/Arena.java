package me.jass.practice.files;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;

import lombok.Getter;
import lombok.Setter;
import me.jass.practice.PracticeAPI;
import me.jass.practice.datatypes.Team;

@Getter
@Setter
public class Arena {
	private final String name;
	private Location spawnA;
	private Location spawnB;
	private Material display = Material.GRASS_BLOCK;
	//	@Getter(value = AccessLevel.NONE)
	//	@Setter(value = AccessLevel.NONE)
	//	private boolean casual = true;
	//	@Getter(value = AccessLevel.NONE)
	//	@Setter(value = AccessLevel.NONE)
	//	private boolean competitive = true;
	List<Arena> subArenas = new ArrayList<Arena>();
	private ChatColor color = ChatColor.WHITE;
	private boolean occupied = false;

	public Arena(final String name) {
		this.name = name.replaceAll(" ", "_").toLowerCase();
	}

	public String getDisplayName() {
		return color + WordUtils.capitalize(name.replaceAll("_", " "));
	}

	public void addSubArena(final Arena arena) {
		subArenas.add(arena);
	}

	public void setSpawn(final Team team, final Location location) {
		if (team == Team.A) {
			spawnA = location;
		}

		if (team == Team.B) {
			spawnB = location;
		}
	}

	public Location getSpawn(final Team team) {
		if (team == Team.A) {
			return spawnA;
		}

		if (team == Team.B) {
			return spawnB;
		}

		return null;
	}

	//	public void setUse(final QueueType type, final boolean use) {
	//		if (type == QueueType.CASUAL) {
	//			casual = use;
	//		}
	//
	//		if (type == QueueType.COMPETITIVE) {
	//			competitive = use;
	//		}
	//	}
	//
	//	public boolean canBeUsed(final QueueType type) {
	//		if (type == QueueType.CASUAL) {
	//			return casual;
	//		}
	//
	//		else if (type == QueueType.COMPETITIVE) {
	//			return competitive;
	//		}
	//
	//		return false;
	//	}
	//
	//	public QueueType getUse() {
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

	public Arena getNextAvaliable() {
		return PracticeAPI.INSTANCE.getArenaManager().getAvailableSub(this);
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

		if (!(o instanceof Arena)) {
			return false;
		}

		final Arena t = (Arena) o;

		return (name.equals(t.getName()));
	}
}
