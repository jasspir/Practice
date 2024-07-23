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
import me.jass.practice.guis.ArenaEditorGUI;
import me.jass.practice.guis.ArenasGUI;
import me.jass.practice.managers.MessageManager;
import me.jass.practice.utils.Text;

@CommandAlias("editarena|modifyarena|deletearena|delarena")
public class DuelEditArena extends BaseCommand {
	@Default
	@Syntax("<arena>")
	@CommandCompletion("@arenas")
	@CommandPermission("practice.edit.arena")
	public void editArena(final CommandSender sender, @Optional final String arena) {
		final MessageManager messages = PracticeAPI.INSTANCE.getMessageManager();
		if (!(sender instanceof Player)) {
			Text.console(messages.getPlayerRestricted());
			return;
		}

		final Selection selection = new Selection((Player) sender, Goal.EDIT_ARENA);

		if (arena == null) {
			PracticeAPI.INSTANCE.getGuiManager().setSelection((Player) sender, selection);
			PracticeAPI.INSTANCE.getGuiManager().open((Player) sender, new ArenasGUI((Player) sender, null));
		} else {
			if (PracticeAPI.INSTANCE.getArenaManager().get(arena) == null) {
				Text.tell((Player) sender, messages.getInvalidArena());
				return;
			}

			selection.setArena(PracticeAPI.INSTANCE.getArenaManager().get(arena));
			PracticeAPI.INSTANCE.getGuiManager().setSelection((Player) sender, selection);
			PracticeAPI.INSTANCE.getGuiManager().open((Player) sender, new ArenaEditorGUI((Player) sender));
		}
	}
}