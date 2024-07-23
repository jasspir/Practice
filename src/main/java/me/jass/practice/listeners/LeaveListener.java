package me.jass.practice.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import me.jass.practice.PracticeAPI;

public class LeaveListener implements Listener {
	@EventHandler
	public void onLeave(final PlayerQuitEvent event) {
		final Player player = event.getPlayer();

		if (PracticeAPI.INSTANCE.getDuelManager().isDueling(player)) {
			PracticeAPI.INSTANCE.getDuelManager().getIndex(player).getDuelist(player).leave();
		}

		PracticeAPI.INSTANCE.getDuelManager().removeSpectator(player);
		PracticeAPI.INSTANCE.getDuelManager().removeView(player);
		PracticeAPI.INSTANCE.getGuiManager().removeSelection(player);
	}
}