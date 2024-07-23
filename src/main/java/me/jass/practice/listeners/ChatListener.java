package me.jass.practice.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import me.jass.practice.PracticeAPI;
import me.jass.practice.utils.Text;

public class ChatListener implements Listener {
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onChat(final AsyncPlayerChatEvent event) {
		final Player player = event.getPlayer();
		if (!PracticeAPI.INSTANCE.getDuelManager().isDueling(player) || !PracticeAPI.INSTANCE.getMenuManager().hasDuelChat(player) || !PracticeAPI.INSTANCE.getConfigManager().isDuelChat()) {
			return;
		}

		event.getRecipients().clear();
		event.getRecipients().addAll(PracticeAPI.INSTANCE.getDuelManager().getIndex(player).getAllPlayers());
		event.setFormat(Text.color("&8[&7Duel&8] &r") + event.getFormat());
	}
}