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
import me.jass.practice.datatypes.QueueType;
import me.jass.practice.duels.Selection;
import me.jass.practice.guis.KitsGUI;
import me.jass.practice.guis.PlayersGUI;
import me.jass.practice.managers.MessageManager;
import me.jass.practice.utils.Text;

@CommandAlias("duel|1v1|fight|battle|brawl|request")
public class DuelPlayer extends BaseCommand {
	@Default
	@Syntax("<player>")
	@CommandCompletion("@players")
	@CommandPermission("practice.duel.request")
	public void duel(final CommandSender sender, @Optional final String player, @Optional final String accept) {
		final MessageManager messages = PracticeAPI.INSTANCE.getMessageManager();
		if (!(sender instanceof Player)) {
			Text.console(messages.getPlayerRestricted());
			return;
		}

		if (PracticeAPI.INSTANCE.getDuelManager().isDueling((Player) sender)) {
			Text.tell((Player) sender, messages.getCurrentlyDueling());
			return;
		}

		final Selection selection = new Selection((Player) sender, Goal.DUEL);

		if (player == null) {
			PracticeAPI.INSTANCE.getGuiManager().setSelection((Player) sender, selection);
			PracticeAPI.INSTANCE.getGuiManager().open((Player) sender, new PlayersGUI((Player) sender, GUI.AVAILABLE_PLAYERS));
		} else {
			if (accept != null) {
				if (player.equalsIgnoreCase("accept")) {
					if (Bukkit.getPlayer(accept) == null) {
						Text.tell((Player) sender, messages.getInvalidPlayer());
						return;
					}

					PracticeAPI.INSTANCE.getRequestManager().get(Bukkit.getPlayer(accept), (Player) sender).accept();
					return;
				}
			}

			if (Bukkit.getPlayer(player) == null) {
				Text.tell((Player) sender, messages.getInvalidPlayer());
				return;
			}

			if (!PracticeAPI.INSTANCE.getMenuManager().hasDuelRequests(Bukkit.getPlayer(player))) {
				Text.tell((Player) sender, PracticeAPI.INSTANCE.getMessageManager().getRequestsDisabled());
				return;
			}

			selection.setPlayer(Bukkit.getPlayer(player));
			PracticeAPI.INSTANCE.getGuiManager().setSelection((Player) sender, selection);
			PracticeAPI.INSTANCE.getGuiManager().open((Player) sender, new KitsGUI((Player) sender, QueueType.CASUAL));
		}
	}
}
