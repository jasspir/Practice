package me.jass.practice.commands;

import org.bukkit.Bukkit;
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
import me.jass.practice.datatypes.GUI;
import me.jass.practice.datatypes.Goal;
import me.jass.practice.duels.Selection;
import me.jass.practice.guis.PlayersGUI;
import me.jass.practice.guis.ViewGUI;
import me.jass.practice.managers.MessageManager;
import me.jass.practice.utils.Text;

@CommandAlias("view|inventory|inv|viewinventory|viewinv|invview")
public class DuelView extends BaseCommand {
	@Default
	@Syntax("<player>")
	@CommandCompletion("@players")
	@CommandPermission("practice.duel.view")
	public void view(final CommandSender sender, @Optional final String player) {
		final MessageManager messages = PracticeAPI.INSTANCE.getMessageManager();
		if (!(sender instanceof Player)) {
			Text.console(messages.getPlayerRestricted());
			return;
		}

		final Selection selection = new Selection((Player) sender, Goal.VIEW);

		if (player == null) {
			PracticeAPI.INSTANCE.getGuiManager().setSelection((Player) sender, selection);
			PracticeAPI.INSTANCE.getGuiManager().open((Player) sender, new PlayersGUI((Player) sender, GUI.AVAILABLE_PLAYERS));
			return;
		} else {
			if (Bukkit.getPlayer(player) == null) {
				Text.tell((Player) sender, messages.getInvalidPlayer());
				return;
			}

			selection.setPlayer(Bukkit.getPlayer(player));
			PracticeAPI.INSTANCE.getGuiManager().setSelection((Player) sender, selection);
			PracticeAPI.INSTANCE.getGuiManager().open((Player) sender, new ViewGUI((Player) sender));
		}
	}
}
