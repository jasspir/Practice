package me.jass.practice.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import me.jass.practice.PracticeAPI;
import me.jass.practice.datatypes.ScoreType;
import me.jass.practice.duels.Duel;
import me.jass.practice.duels.Duelist;

public class InteractListener implements Listener {
	@EventHandler
	public void onInteract(final PlayerInteractEvent event) {
		final Player player = event.getPlayer();

		if (!PracticeAPI.INSTANCE.getDuelManager().isDueling(player)) {
			if (event.hasItem()) {
				final String name = event.getItem().getItemMeta().getDisplayName();
				if (name.contains("Casual Queue")) {
					player.performCommand("queue casual");
				}

				else if (name.contains("Competitive Queue")) {
					player.performCommand("queue competitive");
				}

				else if (name.contains("Layout Editor")) {
					player.performCommand("layout");
				}

				else if (name.contains("Spectate")) {
					player.performCommand("spectate");
				}

				else if (name.contains("Stats")) {
					player.performCommand("stats");
				}

				else if (name.contains("Leaderboards")) {
					player.performCommand("leaderboards");
				}

				else if (name.contains("Settings")) {
					player.performCommand("settings");
				}

				else if (name.contains("Leave Queue")) {
					player.performCommand("queue leave");
				}

				if (PracticeAPI.INSTANCE.getMenuManager().get().contains(event.getItem())) {
					event.setCancelled(true);
					return;
				}
			}

			if (!PracticeAPI.INSTANCE.getConfigManager().isUnsafeInteractions() && !player.isOp()) {
				event.setCancelled(true);
			}

			return;
		}

		final Duel duel = PracticeAPI.INSTANCE.getDuelManager().getIndex(player);
		final Duelist duelist = duel.getDuelist(player);

		if (duel.isEnded()) {
			if (event.getItem() != null) {
				final String name = event.getItem().getItemMeta().getDisplayName();
				if (name.contains("Rematch")) {
					event.getItem().setType(Material.POTION);
					final PotionMeta meta = (PotionMeta) event.getItem().getItemMeta();
					meta.setBasePotionData(new PotionData(PotionType.WATER));
					event.getItem().setItemMeta(meta);
					duel.requestRematch(duelist);
				}

				else if (name.contains("Requeue")) {
					duelist.requeue();
				}
			}
		}

		if (duelist.isFrozen()) {
			event.setCancelled(true);
			return;
		}

		if (event.getMaterial() == Material.END_CRYSTAL && event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && event.getClickedBlock().getType() == Material.OBSIDIAN) {
			Bukkit.getScheduler().runTaskLater(PracticeAPI.INSTANCE.getPlugin(), () -> searchForCrystal(player, duel), 1);
		}

		if (duelist.getScoreType() == ScoreType.BOXING) {
			final String material = player.getInventory().getItemInMainHand().getType().toString().toLowerCase();
			double attackSpeed = 1.6;

			if (material.contains("sword")) {
				attackSpeed = 4;
			}

			else if (material.contains("pickaxe")) {
				attackSpeed = 4.4;
			}

			else if (material.contains("axe")) {
				attackSpeed = 4.8;
			}

			else if (material.contains("shovel")) {
				attackSpeed = 4.6;
			}

			else if (material.contains("hoe")) {
				attackSpeed = 4.6;
			}

			else if (material.contains("trident")) {
				attackSpeed = 4.5;
			}

			player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(attackSpeed);
		}
	}

	public void searchForCrystal(final Player player, final Duel duel) {
		for (final Entity entity : player.getNearbyEntities(4, 4, 4)) {
			if (entity instanceof EnderCrystal) {
				entity.setMetadata("duel", new FixedMetadataValue(PracticeAPI.INSTANCE.getPlugin(), duel.getUuid().toString()));
				duel.addEntity(entity);
			}
		}
	}

	@EventHandler
	public void onPlayerInteract(final PlayerInteractEntityEvent event) {
		if (!(event.getRightClicked() instanceof Player) || event.getHand() == EquipmentSlot.OFF_HAND) {
			return;
		}

		final Player player = event.getPlayer();

		if (!PracticeAPI.INSTANCE.getDuelManager().isDueling(player)) {
			if (!PracticeAPI.INSTANCE.getConfigManager().isUnsafeInteractions()) {
				player.performCommand("challenge " + event.getRightClicked().getName());
			}
		}
	}
}