package me.jass.practice.listeners;

import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

import me.jass.practice.PracticeAPI;
import me.jass.practice.datatypes.ScoreType;
import me.jass.practice.duels.Duel;
import me.jass.practice.duels.Duelist;

public class FishListener implements Listener {
	@EventHandler
	public void onFish(final PlayerFishEvent event) {
		final Player player = event.getPlayer();

		if (!PracticeAPI.INSTANCE.getDuelManager().isDueling(player)) {
			return;
		}

		final Duel duel = PracticeAPI.INSTANCE.getDuelManager().getIndex(player);
		final Duelist duelist = duel.getDuelist(player);

		if (duelist.getScoreType() != ScoreType.FISHING) {
			return;
		}

		if (event.getCaught() instanceof Item) {
			final Item item = (Item) event.getCaught();
			final ItemStack fish = item.getItemStack();
			fish.setAmount(1);
		}
	}
}
