package me.jass.practice.listeners;

import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import me.jass.practice.PracticeAPI;
import me.jass.practice.duels.Duel;
import me.jass.practice.duels.Duelist;

public class BreakListener implements Listener {
	@EventHandler
	public void onBlockBreak(final BlockBreakEvent event) {
		final Player player = event.getPlayer();
		final Duel duel = PracticeAPI.INSTANCE.getDuelManager().getIndex(player);
		final Duelist duelist = duel.getDuelist(player);

		if (duelist.isFrozen() || !duelist.getKit().isBlockBreaking() || !duel.isActive()) {
			event.setCancelled(true);
			return;
		}

		final BlockState block = event.getBlock().getState();
		duel.addWorldDamage(block);
	}
}
