package me.jass.practice;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.primitives.Ints;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.jass.practice.datatypes.Queues;
import me.jass.practice.files.Elo;
import me.jass.practice.files.Kit;

public class PracticeExpansion extends PlaceholderExpansion {
	private final JavaPlugin plugin;

	public PracticeExpansion(final JavaPlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean persist() {
		return true;
	}

	@Override
	public boolean canRegister() {
		return true;
	}

	@Override
	public String getAuthor() {
		return plugin.getDescription().getAuthors().get(0);
	}

	@Override
	public String getIdentifier() {
		return plugin.getDescription().getName().toLowerCase();
	}

	@Override
	public String getVersion() {
		return plugin.getDescription().getVersion();
	}

	@Override
	public String onPlaceholderRequest(final Player player, final String identifier) {
		if (player == null || identifier == null) {
			return null;
		}

		if (!identifier.startsWith("lb_")) {
			return null;
		}

		final String[] split = identifier.split("_");

		final Kit kit = PracticeAPI.INSTANCE.getKitManager().get(split[2]);

		if (kit == null) {
			return null;
		}

		if (!split[1].equalsIgnoreCase("solo") && !split[1].equalsIgnoreCase("duo")) {
			return null;
		}

		final Integer position = Ints.tryParse(split[3]);

		if (position == null) {
			return null;
		}

		if (position < 1 || position > 10) {
			return null;
		}

		final List<Elo> elo = PracticeAPI.INSTANCE.getEloManager().getLeaderboard(kit, Queues.valueOf(split[1].toUpperCase()));

		if (elo.size() < position) {
			return "&7#" + position + " &rNo Player";
		}

		String color = "&7";
		if (position == 1) {
			color = "&#FFD700";
		}

		else if (position == 2) {
			color = "&#C0C0C0";
		}

		else if (position == 3) {
			color = "&#CD7F32";
		}

		return color + "#" + position + " " + Bukkit.getOfflinePlayer(elo.get(position - 1).getId()).getName() + " &7(&r" + elo.get(position - 1).getElo() + "&7)";
	}
}