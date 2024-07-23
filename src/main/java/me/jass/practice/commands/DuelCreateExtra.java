package me.jass.practice.commands;

import java.util.Arrays;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Syntax;
import me.jass.practice.PracticeAPI;
import me.jass.practice.datatypes.Goal;
import me.jass.practice.duels.Selection;
import me.jass.practice.files.Extra;
import me.jass.practice.guis.ExtraEditorGUI;
import me.jass.practice.managers.MessageManager;
import me.jass.practice.utils.Text;

@CommandAlias("createextra|makeextra")
public class DuelCreateExtra extends BaseCommand {
	@Default
	@Syntax("<name> (spaces as _)")
	@CommandPermission("practice.edit.extra")
	public void createExtra(final CommandSender sender, final String name) {
		final MessageManager messages = PracticeAPI.INSTANCE.getMessageManager();
		if (!(sender instanceof Player)) {
			Text.console(messages.getPlayerRestricted());
			return;
		}

		final Extra extra = new Extra(name.toLowerCase());
		extra.setItems(Arrays.asList(((Player) sender).getInventory().getContents()));

		PracticeAPI.INSTANCE.getExtraManager().cache(extra);
		PracticeAPI.INSTANCE.getExtraManager().save(extra);

		final Selection selection = new Selection((Player) sender, Goal.EDIT_EXTRA);
		selection.setExtra(extra);

		PracticeAPI.INSTANCE.getGuiManager().setSelection((Player) sender, selection);
		PracticeAPI.INSTANCE.getGuiManager().open((Player) sender, new ExtraEditorGUI((Player) sender));
	}
}