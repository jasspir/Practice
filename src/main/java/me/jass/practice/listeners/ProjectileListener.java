package me.jass.practice.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;

import me.jass.practice.PracticeAPI;
import me.jass.practice.duels.Duel;

public class ProjectileListener implements Listener {
	@EventHandler
	public void onProjectile(final ProjectileLaunchEvent event) {
		if (!(event.getEntity().getShooter() instanceof Player)) {
			return;
		}

		final Player player = (Player) event.getEntity().getShooter();

		if (!PracticeAPI.INSTANCE.getDuelManager().isDueling(player)) {
			return;
		}

		final Duel duel = PracticeAPI.INSTANCE.getDuelManager().getIndex(player);

		duel.addEntity(event.getEntity());
	}
}
