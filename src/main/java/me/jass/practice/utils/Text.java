package me.jass.practice.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import lombok.experimental.UtilityClass;
import me.jass.practice.PracticeAPI;
import net.md_5.bungee.api.ChatColor;

@UtilityClass
public class Text {
	String prefix = "&8[&7Practice&8] &r";
	boolean hex = false;

	public void load() {
		prefix = PracticeAPI.INSTANCE.getMessageManager().getPrefix();

		if (prefix == null) {
			prefix = "";
		}

		String version = StringUtils.substringBetween(Bukkit.getVersion(), "(MC: 1.", ")");

		if (version.contains(".")) {
			version = StringUtils.substringBefore(version, ".");
		}

		if (Integer.parseInt(version) >= 16 && PracticeAPI.INSTANCE.getConfigManager().isHexColors()) {
			hex = true;
		}
	}

	public void all(final String message) {
		for (final Player player : Bukkit.getOnlinePlayers()) {
			tell(player, message);
		}
	}

	public void staff(final String message) {
		for (final Player player : Bukkit.getOnlinePlayers()) {
			if (player.hasPermission("practice.staff")) {
				tell(player, message);
			}
		}
	}

	public void tell(final Player player, final String message) {
		player.sendMessage(color(prefix() + message));
	}

	public void alert(final String message) {
		staff(message);
		console(message);
	}

	public void console(final String message) {
		System.out.println(color(prefix() + message));
	}

	public static String prefix() {
		return prefix;
	}

	private static final Pattern rgbPattern = Pattern.compile("&#[a-fA-F0-9]{6}");

	public static String color(String msg) {
		if (hex && msg.contains("#")) {
			msg = msg.replaceAll("#red", "&#FF0000");
			msg = msg.replaceAll("#orange", "&#FFA500");
			msg = msg.replaceAll("#yellow", "&#FFD700");
			msg = msg.replaceAll("#green", "&#008000");
			msg = msg.replaceAll("#blue", "&#0000FF");
			msg = msg.replaceAll("#purple", "&#4B0082");
			msg = msg.replaceAll("#pink", "&#FF69B4");
			msg = msg.replaceAll("#aqua", "&#00FFFF");
			msg = msg.replaceAll("#lime", "&#00FF00");
			msg = msg.replaceAll("#brown", "&#964B00");

			Matcher rgbMatch = rgbPattern.matcher(msg);
			while (rgbMatch.find()) {
				final String color = msg.substring(rgbMatch.start(), rgbMatch.end());
				msg = msg.replace(color, ChatColor.of(color.substring(1)) + "");
				rgbMatch = rgbPattern.matcher(msg);
			}
		} else {
			if (msg.contains("#")) {
				msg = msg.replaceAll("#red", "&c");
				msg = msg.replaceAll("#orange", "&6");
				msg = msg.replaceAll("#yellow", "&e");
				msg = msg.replaceAll("#green", "&2");
				msg = msg.replaceAll("#blue", "&9");
				msg = msg.replaceAll("#purple", "&5");
				msg = msg.replaceAll("#pink", "&d");
				msg = msg.replaceAll("#aqua", "&b");
				msg = msg.replaceAll("#lime", "&a");
				msg = msg.replaceAll("#brown", "&6");
			}
		}

		return ChatColor.translateAlternateColorCodes('&', msg);
	}
}
