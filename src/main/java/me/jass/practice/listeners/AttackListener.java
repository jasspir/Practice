package me.jass.practice.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import me.jass.practice.PracticeAPI;
import me.jass.practice.datatypes.ScoreType;
import me.jass.practice.duels.Duel;
import me.jass.practice.duels.Duelist;

public class AttackListener implements Listener {
	@EventHandler
	public void onAttack(final EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof Player && event.getEntity() instanceof Player)) {
			return;
		}

		final Player damager = (Player) event.getDamager();

		if (!PracticeAPI.INSTANCE.getDuelManager().isDueling(damager)) {
			if (!PracticeAPI.INSTANCE.getConfigManager().isUnsafeInteractions()) {
				damager.performCommand("challenge " + event.getEntity().getName());
				if (!damager.isOp()) {
					event.setCancelled(true);
				}
			}
			return;
		}

		final Duel duel = PracticeAPI.INSTANCE.getDuelManager().getIndex(damager);
		final Duelist duelist = duel.getDuelist(damager);

		if (duelist.isFrozen()) {
			event.setCancelled(true);
			return;
		}

		if (duelist.getTeamMembers().contains(new Duelist((Player) event.getEntity(), null, null, null, null))) {
			if (!PracticeAPI.INSTANCE.getConfigManager().isFriendlyFire()) {
				event.setCancelled(true);
				return;
			}
		}

		if (event.getDamage() > 0) {
			if (duelist.getScoreType() == ScoreType.BOXING) {
				if (System.currentTimeMillis() - duelist.getLastHit() >= 550) {
					duelist.increaseScore();
				}
			}

			if (event.getDamager().getFallDistance() > 0) {
				duelist.addCrit();
			}

			duelist.addHit();
		}

		if (!duelist.getKit().isAttacking()) {
			event.setDamage(0);
		}
	}
}
