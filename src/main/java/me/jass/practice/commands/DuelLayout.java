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
import me.jass.practice.guis.KitsGUI;
import me.jass.practice.guis.LayoutEditorGUI;
import me.jass.practice.managers.MessageManager;
import me.jass.practice.utils.Text;

@CommandAlias("layout")
public class DuelLayout extends BaseCommand {
	@Default
	@Syntax("<kit>")
	@CommandCompletion("@kits")
	@CommandPermission("practice.layout")
	public void layout(final CommandSender sender, @Optional final String kit) {
		final MessageManager messages = PracticeAPI.INSTANCE.getMessageManager();
		if (!(sender instanceof Player)) {
			Text.console(messages.getPlayerRestricted());
			return;
		}

		final Selection selection = new Selection((Player) sender, Goal.EDIT_LAYOUT);

		if (kit == null) {
			PracticeAPI.INSTANCE.getGuiManager().setSelection((Player) sender, selection);
			PracticeAPI.INSTANCE.getGuiManager().open((Player) sender, new KitsGUI((Player) sender, null));
		} else {
			if (PracticeAPI.INSTANCE.getKitManager().get(kit) == null) {
				Text.tell((Player) sender, messages.getInvalidKit());
				return;
			}

			selection.setKit(PracticeAPI.INSTANCE.getKitManager().get(kit));
			PracticeAPI.INSTANCE.getGuiManager().setSelection((Player) sender, selection);
			PracticeAPI.INSTANCE.getGuiManager().open((Player) sender, new LayoutEditorGUI((Player) sender));
		}
	}
}
