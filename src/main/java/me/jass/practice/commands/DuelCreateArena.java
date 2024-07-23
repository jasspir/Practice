package me.jass.practice.commands;

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
import me.jass.practice.files.Arena;
import me.jass.practice.guis.ArenaEditorGUI;
import me.jass.practice.managers.MessageManager;
import me.jass.practice.utils.Text;

@CommandAlias("createarena|makearena")
public class DuelCreateArena extends BaseCommand {
	@Default
	@Syntax("<name> (spaces as _) (subarenas as <name>1, <name>2, etc")
	@CommandPermission("practice.edit.arena")
	public void createArena(final CommandSender sender, final String name) {
		final MessageManager messages = PracticeAPI.INSTANCE.getMessageManager();
		if (!(sender instanceof Player)) {
			Text.console(messages.getPlayerRestricted());
			return;
		}

		final Arena arena = new Arena(name.toLowerCase());

		if (name.matches(".*[0-9].*")) {
			PracticeAPI.INSTANCE.getArenaManager().giveSub(arena);
		}

		PracticeAPI.INSTANCE.getArenaManager().cache(arena);
		PracticeAPI.INSTANCE.getArenaManager().save(arena);

		final Selection selection = new Selection((Player) sender, Goal.EDIT_ARENA);
		selection.setArena(arena);

		PracticeAPI.INSTANCE.getGuiManager().setSelection((Player) sender, selection);
		PracticeAPI.INSTANCE.getGuiManager().open((Player) sender, new ArenaEditorGUI((Player) sender));
	}
}