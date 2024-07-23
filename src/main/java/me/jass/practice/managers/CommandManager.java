package me.jass.practice.managers;

import co.aikar.commands.PaperCommandManager;
import me.jass.practice.PracticeAPI;
import me.jass.practice.commands.DuelChallenge;
import me.jass.practice.commands.DuelCreateArena;
import me.jass.practice.commands.DuelCreateExtra;
import me.jass.practice.commands.DuelCreateKit;
import me.jass.practice.commands.DuelEditArena;
import me.jass.practice.commands.DuelEditExtra;
import me.jass.practice.commands.DuelEditKit;
import me.jass.practice.commands.DuelLayout;
import me.jass.practice.commands.DuelLeaderboards;
import me.jass.practice.commands.DuelLeave;
import me.jass.practice.commands.DuelMain;
import me.jass.practice.commands.DuelPlayer;
import me.jass.practice.commands.DuelQueue;
import me.jass.practice.commands.DuelSettings;
import me.jass.practice.commands.DuelSpectate;
import me.jass.practice.commands.DuelStats;
import me.jass.practice.commands.DuelView;
import me.jass.practice.datatypes.QueueType;

public class CommandManager {
	private static PaperCommandManager commandManager = new PaperCommandManager(PracticeAPI.INSTANCE.getPlugin());

	public void register() {
		commandManager.registerCommand(new DuelChallenge());
		commandManager.registerCommand(new DuelCreateArena());
		commandManager.registerCommand(new DuelCreateExtra());
		commandManager.registerCommand(new DuelCreateKit());
		commandManager.registerCommand(new DuelEditArena());
		commandManager.registerCommand(new DuelEditExtra());
		commandManager.registerCommand(new DuelEditKit());
		commandManager.registerCommand(new DuelMain());
		commandManager.registerCommand(new DuelLayout());
		commandManager.registerCommand(new DuelLeaderboards());
		commandManager.registerCommand(new DuelLeave());
		commandManager.registerCommand(new DuelPlayer());
		commandManager.registerCommand(new DuelQueue());
		commandManager.registerCommand(new DuelSettings());
		commandManager.registerCommand(new DuelSpectate());
		commandManager.registerCommand(new DuelStats());
		commandManager.registerCommand(new DuelView());
	}

	public void registerCompletions() {
		commandManager.getCommandCompletions().registerAsyncCompletion("kits", c -> PracticeAPI.INSTANCE.getKitManager().getNames(null));
		commandManager.getCommandCompletions().registerAsyncCompletion("casualkits", c -> PracticeAPI.INSTANCE.getKitManager().getNames(QueueType.CASUAL));
		commandManager.getCommandCompletions().registerAsyncCompletion("competitivekits", c -> PracticeAPI.INSTANCE.getKitManager().getNames(QueueType.COMPETITIVE));
		commandManager.getCommandCompletions().registerAsyncCompletion("arenas", c -> PracticeAPI.INSTANCE.getArenaManager().getNames());
		commandManager.getCommandCompletions().registerAsyncCompletion("extras", c -> PracticeAPI.INSTANCE.getExtraManager().getNames());
		commandManager.getCommandCompletions().registerAsyncCompletion("duelists", c -> PracticeAPI.INSTANCE.getDuelManager().duelistNames());
	}
}
