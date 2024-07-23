package me.jass.practice.listeners;

import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;

import me.jass.practice.PracticeAPI;
import me.jass.practice.duels.Duel;

public class ChangeListener implements Listener {
	@EventHandler
	public void onBlockFromTo(final BlockFromToEvent event) {
		if (event.getBlock().getType() == Material.WATER || event.getBlock().getType() == Material.LAVA) {
			final Duel duel = PracticeAPI.INSTANCE.getDuelManager().getClosestDuel(event.getToBlock().getLocation());

			if (duel == null || !duel.isActive()) {
				if (!PracticeAPI.INSTANCE.getConfigManager().isUnsafeInteractions()) {
					event.setCancelled(true);
				}
				return;
			}

			duel.addWorldDamage(event.getToBlock().getState());
		} else {
			if (!PracticeAPI.INSTANCE.getConfigManager().isUnsafeInteractions()) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onBlockForm(final BlockFormEvent event) {
		if (event.getNewState().getType() == Material.OBSIDIAN || event.getNewState().getType() == Material.STONE || event.getNewState().getType() == Material.COBBLESTONE) {
			final Duel duel = PracticeAPI.INSTANCE.getDuelManager().getClosestDuel(event.getBlock().getLocation());

			if (duel == null || !duel.isActive()) {
				if (!PracticeAPI.INSTANCE.getConfigManager().isUnsafeInteractions()) {
					event.setCancelled(true);
				}
				return;
			}

			final BlockState state = event.getBlock().getState();
			state.setType(Material.AIR);

			duel.addWorldDamage(state);
		} else {
			if (!PracticeAPI.INSTANCE.getConfigManager().isUnsafeInteractions()) {
				event.setCancelled(true);
			}
		}
	}
}
