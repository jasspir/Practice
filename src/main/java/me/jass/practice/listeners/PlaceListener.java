package me.jass.practice.listeners;

import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;

import me.jass.practice.PracticeAPI;
import me.jass.practice.duels.Duel;
import me.jass.practice.duels.Duelist;

public class PlaceListener implements Listener {
	@EventHandler
	public void onBlockPlace(final BlockPlaceEvent event) {
		final Player player = event.getPlayer();
		final Duel duel = PracticeAPI.INSTANCE.getDuelManager().getIndex(player);
		final Duelist duelist = duel.getDuelist(player);

		if (duelist.isFrozen() || !duelist.getKit().isBlockPlacing() || !duel.isActive()) {
			event.setCancelled(true);
			return;
		}

		final BlockState block = event.getBlockReplacedState();
		duel.addWorldDamage(block);

		if (event.getBlock().getType() == Material.RESPAWN_ANCHOR || event.getBlock().getType() == Material.TNT) {
			PracticeAPI.INSTANCE.getDuelManager().addExplosive(event.getBlock().getLocation(), duel);
		}
	}

	@EventHandler
	public void onBucketEmpty(final PlayerBucketEmptyEvent event) {
		final Player player = event.getPlayer();
		if (!PracticeAPI.INSTANCE.getDuelManager().isDueling(player)) {
			if (!PracticeAPI.INSTANCE.getConfigManager().isUnsafeInteractions() && !player.isOp()) {
				event.setCancelled(true);
			}
			return;
		}

		final Duel duel = PracticeAPI.INSTANCE.getDuelManager().getIndex(player);
		final Duelist duelist = duel.getDuelist(player);

		if (duelist.isFrozen() || !duelist.getKit().isBlockPlacing()) {
			event.setCancelled(true);
			return;
		}

		final BlockState block = event.getBlock().getState();
		duel.addWorldDamage(block);
	}

	@EventHandler
	public void onBucketFill(final PlayerBucketFillEvent event) {
		final Player player = event.getPlayer();
		if (!PracticeAPI.INSTANCE.getDuelManager().isDueling(player)) {
			if (!PracticeAPI.INSTANCE.getConfigManager().isUnsafeInteractions() && !player.isOp()) {
				event.setCancelled(true);
			}
			return;
		}

		final Duel duel = PracticeAPI.INSTANCE.getDuelManager().getIndex(player);
		final Duelist duelist = duel.getDuelist(player);

		if (duelist.isFrozen() || !duelist.getKit().isBlockPlacing()) {
			event.setCancelled(true);
			return;
		}

		final BlockState block = event.getBlock().getState();
		duel.addWorldDamage(block);
	}
}