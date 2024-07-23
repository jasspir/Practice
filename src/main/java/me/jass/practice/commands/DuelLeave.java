package me.jass.practice.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import me.jass.practice.PracticeAPI;
import me.jass.practice.duels.Duel;
import me.jass.practice.duels.Duelist;
import me.jass.practice.managers.MessageManager;
import me.jass.practice.utils.Text;

@CommandAlias("leave|forfeit|quit|end|surrender|l|ff")
public class DuelLeave extends BaseCommand {
	@Default
	@CommandPermission("practice.duel.leave")
	public void leave(final CommandSender sender) {
		final MessageManager messages = PracticeAPI.INSTANCE.getMessageManager();
		if (!(sender instanceof Player)) {
			Text.console(messages.getPlayerRestricted());
			return;
		}

		final Player player = (Player) sender;

		if (!PracticeAPI.INSTANCE.getDuelManager().isDueling(player)) {
			final Duel duel = PracticeAPI.INSTANCE.getDuelManager().getSpectating((Player) sender);

			if (duel != null) {
				duel.removeSpectator((Player) sender);
				return;
			}

			Text.tell(player, messages.getNotDueling());
			return;
		}

		final Duelist duelist = PracticeAPI.INSTANCE.getDuelManager().getIndex(player).getDuelist(player);

		if (!duelist.isLeft()) {
			Bukkit.getScheduler().runTaskLater(PracticeAPI.INSTANCE.getPlugin(), () -> duelist.leave(), 1);
			Text.tell(player, messages.getLeaveDuel());
		} else {
			Text.tell(player, messages.getAlreadyLeft());
		}
	}

	public void l(final Duelist duelist) {
		duelist.leave();
	}
}
