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
import me.jass.practice.duels.Duel;
import me.jass.practice.duels.Selection;
import me.jass.practice.guis.PlayersGUI;
import me.jass.practice.managers.MessageManager;
import me.jass.practice.utils.Text;

@CommandAlias("spectate|spec|s|watch|observe")
public class DuelSpectate extends BaseCommand {
	@Default
	@Syntax("<player>")
	@CommandCompletion("@duelists")
	@CommandPermission("practice.duel.spectate")
	public void spectate(final CommandSender sender, @Optional final String player) {
		final MessageManager messages = PracticeAPI.INSTANCE.getMessageManager();
		if (!(sender instanceof Player)) {
			Text.console(messages.getPlayerRestricted());
			return;
		}

		if (PracticeAPI.INSTANCE.getDuelManager().isDueling((Player) sender)) {
			Text.tell((Player) sender, messages.getCurrentlyDueling());
			return;
		}

		final Duel duel = PracticeAPI.INSTANCE.getDuelManager().getSpectating((Player) sender);

		if (duel != null) {
			duel.removeSpectator((Player) sender);
		}

		if (player == null) {
			final Selection selection = new Selection((Player) sender, Goal.SPECTATE);
			PracticeAPI.INSTANCE.getGuiManager().setSelection((Player) sender, selection);
			PracticeAPI.INSTANCE.getGuiManager().open((Player) sender, new PlayersGUI((Player) sender, GUI.UNAVAILABLE_PLAYERS));
			return;
		} else {
			if (Bukkit.getPlayer(player) == null) {
				Text.tell((Player) sender, messages.getInvalidPlayer());
				return;
			}

			if (!PracticeAPI.INSTANCE.getDuelManager().isDueling(Bukkit.getPlayer(player))) {
				Text.tell((Player) sender, messages.getPlayerNotDueling());
				return;
			}

			PracticeAPI.INSTANCE.getDuelManager().getIndex(Bukkit.getPlayer(player)).addSpectator((Player) sender);
		}
	}
}
