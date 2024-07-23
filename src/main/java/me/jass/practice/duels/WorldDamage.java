package me.jass.practice.duels;

import org.bukkit.Location;
import org.bukkit.block.BlockState;

import lombok.Getter;
import me.jass.practice.utils.Text;

@Getter
public class WorldDamage {
	private final BlockState state;
	private final Location location;

	public WorldDamage(final BlockState state) {
		if (state == null) {
			Text.alert("nulled");
		}
		this.state = state;
		this.location = state.getLocation();
	}

	public void repair() {
		location.getWorld().setBlockData(location, state.getBlockData());
	}

	@Override
	public int hashCode() {
		return location.hashCode();
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null) {
			return false;
		}

		if (o == this) {
			return true;
		}

		if (!(o instanceof WorldDamage)) {
			return false;
		}

		final WorldDamage t = (WorldDamage) o;

		return (location.equals(t.getLocation()));
	}
}
