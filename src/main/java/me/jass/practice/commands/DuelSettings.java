package me.jass.practice.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import me.jass.practice.PracticeAPI;
import me.jass.practice.guis.SettingsGUI;
import me.jass.practice.managers.MessageManager;
import me.jass.practice.utils.Text;

@CommandAlias("settings|pingrange|requests|chat|channel")
public class DuelSettings extends BaseCommand {
	@Default
	@CommandPermission("practice.settings")
	public void settings(final CommandSender sender) {
		final MessageManager messages = PracticeAPI.INSTANCE.getMessageManager();
		if (!(sender instanceof Player)) {
			Text.console(messages.getPlayerRestricted());
			return;
		}

		PracticeAPI.INSTANCE.getGuiManager().open((Player) sender, new SettingsGUI((Player) sender));
	}
}
