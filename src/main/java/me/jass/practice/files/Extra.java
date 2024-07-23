package me.jass.practice.files;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Extra {
	private final String name;
	private List<ItemStack> items = new ArrayList<ItemStack>();
	private Material display = Material.NETHER_STAR;
	private ChatColor color = ChatColor.WHITE;
	private boolean dissonant = false;

	public Extra(final String name) {
		this.name = name.replaceAll(" ", "_").toLowerCase();
	}

	public String getDisplayName() {
		return color + WordUtils.capitalize(name.replaceAll("_", " "));
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

		if (!(o instanceof Extra)) {
			return false;
		}

		final Extra t = (Extra) o;

		return (name.equals(t.getName()));
	}
}
