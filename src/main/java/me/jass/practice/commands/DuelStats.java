package me.jass.practice.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import me.jass.practice.PracticeAPI;
import me.jass.practice.datatypes.Goal;
import me.jass.practice.datatypes.QueueType;
import me.jass.practice.duels.Selection;
import me.jass.practice.guis.QueuesGUI;
import me.jass.practice.managers.MessageManager;
import me.jass.practice.utils.Text;

@CommandAlias("statistics|statistic|stats|stat|info")
public class DuelStats extends BaseCommand {
	@Default
	@CommandPermission("practice.stats")
	public void stats(final CommandSender sender) {
		final MessageManager messages = PracticeAPI.INSTANCE.getMessageManager();
		if (!(sender instanceof Player)) {
			Text.console(messages.getPlayerRestricted());
			return;
		}

		final Selection selection = new Selection((Player) sender, Goal.STATS);
		PracticeAPI.INSTANCE.getGuiManager().setSelection((Player) sender, selection);
		PracticeAPI.INSTANCE.getGuiManager().open((Player) sender, new QueuesGUI((Player) sender, QueueType.COMPETITIVE));
	}
}