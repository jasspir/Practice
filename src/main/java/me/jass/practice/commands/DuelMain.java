package me.jass.practice.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.HelpCommand;
import co.aikar.commands.annotation.Subcommand;
import me.jass.practice.PracticeAPI;
import me.jass.practice.managers.MessageManager;
import me.jass.practice.utils.Text;

@CommandAlias("practice")
public class DuelMain extends BaseCommand {
	@Default
	@HelpCommand
	@Subcommand("help")
	@CommandPermission("practice.help")
	public void help(final CommandSender sender) {
		final MessageManager messages = PracticeAPI.INSTANCE.getMessageManager();
		if (!(sender instanceof Player)) {
			Text.console(messages.getPlayerRestricted());
			return;
		}

		final Player player = (Player) sender;
		Text.tell(player, "&7Practice Commands:");
		Text.tell(player, "&e/Duel");
		Text.tell(player, "&e/Queue");
		Text.tell(player, "&e/Challenge");
		Text.tell(player, "&e/Spectate");
		Text.tell(player, "&e/Leave");
		Text.tell(player, "&e/Settings");
		Text.tell(player, "&e/Layout");
		Text.tell(player, "&e/Stats");
		Text.tell(player, "&e/Leaderboards");
		Text.tell(player, "&e/View");
		Text.tell(player, "&e/Create");
		Text.tell(player, "&e/Edit");
	}

	@Subcommand("reload")
	@CommandPermission("practice.reload")
	public void reload(final CommandSender sender) {
		PracticeAPI.INSTANCE.reload();
	}
}