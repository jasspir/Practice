package me.jass.practice.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Syntax;
import me.jass.practice.PracticeAPI;
import me.jass.practice.datatypes.Goal;
import me.jass.practice.datatypes.QueueType;
import me.jass.practice.duels.Selection;
import me.jass.practice.guis.KitsGUI;
import me.jass.practice.guis.QueuesGUI;
import me.jass.practice.managers.MessageManager;
import me.jass.practice.utils.Text;

@CommandAlias("queue|q")
public class DuelQueue extends BaseCommand {
	@Default
	@Syntax("<type>")
	@CommandCompletion("casual|competitive")
	@CommandPermission("practice.duel.queue")
	public void queue(final CommandSender sender, @Optional final String competitive) {
		final MessageManager messages = PracticeAPI.INSTANCE.getMessageManager();
		if (!(sender instanceof Player)) {
			Text.console(messages.getPlayerRestricted());
			return;
		}

		if (PracticeAPI.INSTANCE.getDuelManager().isDueling((Player) sender)) {
			Text.tell((Player) sender, messages.getCurrentlyDueling());
			return;
		}

		Selection selection = new Selection((Player) sender, Goal.CASUAL_QUEUE);

		if (competitive == null) {
			PracticeAPI.INSTANCE.getGuiManager().setSelection((Player) sender, selection);
			PracticeAPI.INSTANCE.getGuiManager().open((Player) sender, new KitsGUI((Player) sender, QueueType.CASUAL));
		} else {
			if (competitive.equalsIgnoreCase("competitive")) {
				selection = new Selection((Player) sender, Goal.COMPETITIVE_QUEUE);
				PracticeAPI.INSTANCE.getGuiManager().setSelection((Player) sender, selection);
				PracticeAPI.INSTANCE.getGuiManager().open((Player) sender, new QueuesGUI((Player) sender, QueueType.COMPETITIVE));
			} else if (competitive.equalsIgnoreCase("casual")) {
				PracticeAPI.INSTANCE.getGuiManager().setSelection((Player) sender, selection);
				PracticeAPI.INSTANCE.getGuiManager().open((Player) sender, new QueuesGUI((Player) sender, QueueType.CASUAL));
			} else if (competitive.equalsIgnoreCase("leave")) {
				PracticeAPI.INSTANCE.getQueueManager().leave((Player) sender);
				PracticeAPI.INSTANCE.getMenuManager().give((Player) sender);
				Text.tell((Player) sender, messages.getLeaveQueue());
			} else {
				Text.tell((Player) sender, messages.getInvalidQueue());
			}
		}
	}
}