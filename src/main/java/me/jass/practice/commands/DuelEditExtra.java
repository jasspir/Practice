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
import me.jass.practice.duels.Selection;
import me.jass.practice.guis.ExtraEditorGUI;
import me.jass.practice.guis.ExtrasGUI;
import me.jass.practice.managers.MessageManager;
import me.jass.practice.utils.Text;

@CommandAlias("editextra|deleteextra|delextra")
public class DuelEditExtra extends BaseCommand {
	@Default
	@Syntax("<extra>")
	@CommandCompletion("@extras")
	@CommandPermission("practice.edit.extra")
	public void deleteExtra(final CommandSender sender, @Optional final String extra) {
		final MessageManager messages = PracticeAPI.INSTANCE.getMessageManager();
		if (!(sender instanceof Player)) {
			Text.console(messages.getPlayerRestricted());
			return;
		}

		final Selection selection = new Selection((Player) sender, Goal.EDIT_EXTRA);

		if (extra == null) {
			PracticeAPI.INSTANCE.getGuiManager().setSelection((Player) sender, selection);
			PracticeAPI.INSTANCE.getGuiManager().open((Player) sender, new ExtrasGUI((Player) sender));
		} else {
			if (PracticeAPI.INSTANCE.getExtraManager().get(extra) == null) {
				Text.tell((Player) sender, messages.getInvalidExtra());
				return;
			}

			selection.setExtra(PracticeAPI.INSTANCE.getExtraManager().get(extra));
			PracticeAPI.INSTANCE.getGuiManager().setSelection((Player) sender, selection);
			PracticeAPI.INSTANCE.getGuiManager().open((Player) sender, new ExtraEditorGUI((Player) sender));
		}
	}
}
