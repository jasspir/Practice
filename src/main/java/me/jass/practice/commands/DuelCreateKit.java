package me.jass.practice.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Syntax;
import me.jass.practice.PracticeAPI;
import me.jass.practice.datatypes.Goal;
import me.jass.practice.datatypes.QueueType;
import me.jass.practice.duels.Queue;
import me.jass.practice.duels.Selection;
import me.jass.practice.files.Kit;
import me.jass.practice.guis.KitEditorGUI;
import me.jass.practice.managers.MessageManager;
import me.jass.practice.utils.Text;

@CommandAlias("createkit|makekit")
public class DuelCreateKit extends BaseCommand {
	@Default
	@Syntax("<name> (spaces as _)")
	@CommandPermission("practice.edit.kit")
	public void createKit(final CommandSender sender, final String name) {
		final MessageManager messages = PracticeAPI.INSTANCE.getMessageManager();
		if (!(sender instanceof Player)) {
			Text.console(messages.getPlayerRestricted());
			return;
		}

		final Kit kit = new Kit(name);
		final Queue casual = new Queue(QueueType.CASUAL, kit, 2);
		final Queue competitive = new Queue(QueueType.COMPETITIVE, kit, 2);

		final List<PotionEffect> effects = new ArrayList<PotionEffect>();
		effects.addAll(((Player) sender).getActivePotionEffects());
		kit.setItems(Arrays.asList(((Player) sender).getInventory().getContents()));
		kit.setEffects(effects);

		PracticeAPI.INSTANCE.getKitManager().cache(kit);
		PracticeAPI.INSTANCE.getKitManager().save(kit);
		PracticeAPI.INSTANCE.getQueueManager().add(casual);
		PracticeAPI.INSTANCE.getQueueManager().add(competitive);

		final Selection selection = new Selection((Player) sender, Goal.EDIT_KIT);
		selection.setKit(kit);

		PracticeAPI.INSTANCE.getGuiManager().setSelection((Player) sender, selection);
		PracticeAPI.INSTANCE.getGuiManager().open((Player) sender, new KitEditorGUI((Player) sender));
	}
}