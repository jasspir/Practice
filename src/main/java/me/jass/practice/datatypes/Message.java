package me.jass.practice.datatypes;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.entity.Player;

import com.google.common.base.CaseFormat;

import me.jass.practice.PracticeAPI;
import me.jass.practice.duels.Duel;
import me.jass.practice.duels.Duelist;
import me.jass.practice.duels.Queue;
import me.jass.practice.duels.Request;
import me.jass.practice.managers.MessageManager;
import me.jass.practice.utils.Text;

public enum Message {
	DUEL_START, ROUND_END, DUEL_END, COMPETITIVE_DUEL_START, COMPETITIVE_DUEL_END, COUNTDOWN_MAIN, COUNTDOWN_SUB, END_COUNTDOWN_MAIN, END_COUNTDOWN_SUB, DUEL_BAR, SPECTATOR_JOIN, SPECTATOR_LEAVE,
	VIEW_WINNER, VIEW_LOSER, REQUEST_REMATCH, REQUEST_SEND, REQUEST_RECEIVE, REQUESTER_DUELING, QUEUE_JOIN, QUEUE_LEAVE;

	MessageManager manager = PracticeAPI.INSTANCE.getMessageManager();
	String message = "";

	public String formatDuelist(final Duelist duelist) {
		switch (this) {
		case COUNTDOWN_MAIN:
			message = manager.getCountdownMain();
			break;
		case COUNTDOWN_SUB:
			message = manager.getCountdownSub();
			break;
		case END_COUNTDOWN_MAIN:
			message = manager.getEndCountdownMain();
			break;
		case END_COUNTDOWN_SUB:
			message = manager.getEndCountdownSub();
			break;
		case DUEL_BAR:
			message = manager.getDuelBar();
			break;
		case VIEW_WINNER:
			message = manager.getViewWinner();
			break;
		case VIEW_LOSER:
			message = manager.getViewLoser();
			break;
		case REQUEST_REMATCH:
			message = manager.getRequestRematch();
			break;
		case COMPETITIVE_DUEL_START:
			message = manager.getCompetitiveDuelStart();
			break;
		case COMPETITIVE_DUEL_END:
			message = manager.getCompetitiveDuelEnd();
			break;
		case DUEL_START:
			message = duelist.getDuel().isCompetitive() ? manager.getCompetitiveDuelStart() : manager.getDuelStart();
			break;
		case DUEL_END:
			message = duelist.getDuel().isCompetitive() ? manager.getCompetitiveDuelEnd() : manager.getDuelEnd();
			break;
		}

		parseDuelist(duelist);
		return message;
	}

	public String formatPlayer(final Player player) {
		switch (this) {
		case SPECTATOR_JOIN:
			message = manager.getSpectatorJoin();
			break;
		case SPECTATOR_LEAVE:
			message = manager.getSpectatorLeave();
			break;
		}

		parsePlayer(player);
		return message;
	}

	public String formatDuel(final Duel duel) {
		switch (this) {
		case ROUND_END:
			message = manager.getRoundEnd();
			break;
		}

		parseDuel(duel);
		return message;
	}

	public String formatRequest(final Request request) {
		switch (this) {
		case REQUEST_SEND:
			message = manager.getRequestSend();
			break;
		case REQUEST_RECEIVE:
			message = manager.getRequestReceive();
			break;
		case REQUESTER_DUELING:
			message = manager.getRequesterDueling();
			break;
		}

		parseRequest(request);
		return message;
	}

	public String formatQueue(final Queue queue) {
		switch (this) {
		case QUEUE_JOIN:
			message = manager.getQueueJoin();
			break;
		case QUEUE_LEAVE:
			message = manager.getQueueLeave();
			break;
		}

		parseQueue(queue);
		return message;
	}

	public void parseDuel(final Duel duel) {
		message = StringUtils.replaceIgnoreCase(message, "%roundWinners%", "%team" + CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, duel.getRoundWinner().toString()) + "%");
		message = StringUtils.replaceIgnoreCase(message, "%roundLosers%", "%team" + CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, duel.getRoundLoser().toString()) + "%");
		message = StringUtils.replaceIgnoreCase(message, "%winners%", "%team" + CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, duel.getWinner().toString()) + "%");
		message = StringUtils.replaceIgnoreCase(message, "%losers%", "%team" + CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, duel.getLoser().toString()) + "%");
		message = StringUtils.replaceIgnoreCase(message, "%teamA%", manager.getTeamAColor() + StringUtils.join(duel.getTeamNames(Team.A), manager.getSubColor() + ", " + manager.getTeamAColor()));
		message = StringUtils.replaceIgnoreCase(message, "%teamB%", manager.getTeamBColor() + StringUtils.join(duel.getTeamNames(Team.B), manager.getSubColor() + ", " + manager.getTeamBColor()));
		message = StringUtils.replaceIgnoreCase(message, "%teamAColor%", manager.getTeamAColor());
		message = StringUtils.replaceIgnoreCase(message, "%teamBColor%", manager.getTeamBColor());
		message = StringUtils.replaceIgnoreCase(message, "%teamAWins%", String.valueOf(duel.getTeamAWins()));
		message = StringUtils.replaceIgnoreCase(message, "%teamBWins%", String.valueOf(duel.getTeamBWins()));
		message = StringUtils.replaceIgnoreCase(message, "%teamAScore%", String.valueOf(duel.getTeamAScore()));
		message = StringUtils.replaceIgnoreCase(message, "%teamBScore%", String.valueOf(duel.getTeamBScore()));
		message = StringUtils.replaceIgnoreCase(message, "%round%", String.valueOf(duel.getRound()));
		message = StringUtils.replaceIgnoreCase(message, "%rounds%", String.valueOf(duel.getRounds()));
		message = StringUtils.replaceIgnoreCase(message, "%countdown%", String.valueOf(PracticeAPI.INSTANCE.getConfigManager().getDuelCountdownLength() - duel.getCountdown()));
		message = StringUtils.replaceIgnoreCase(message, "%requests%", String.valueOf(duel.getRequestingRematch().size()));
		message = StringUtils.replaceIgnoreCase(message, "%duelists%", String.valueOf(duel.getDuelists().size()));

		if (message.contains("-elo")) {
			message = StringUtils.replaceIgnoreCase(message, "-elo", "");
			for (final Duelist duelist : duel.getDuelists()) {
				message = StringUtils.replaceIgnoreCase(message, duelist.getPlayer().getName(),
						duelist.getPlayer().getName() + manager.getSubColor() + " (&e" + duelist.getElo() + manager.getSubColor() + ")");
			}
		}

		if (message.contains("-ping")) {
			message = StringUtils.replaceIgnoreCase(message, "-ping", "");
			for (final Duelist duelist : duel.getDuelists()) {
				message = StringUtils.replaceIgnoreCase(message, duelist.getPlayer().getName(),
						duelist.getPlayer().getName() + manager.getSubColor() + " (#aqua" + duelist.getPlayer().getPing() + "ms" + manager.getSubColor() + ")");
			}
		}

		if (message.contains("-score")) {
			message = StringUtils.replaceIgnoreCase(message, "-score", "");
			for (final Duelist duelist : duel.getDuelists()) {
				if (duelist.getScoreType() == ScoreType.NONE) {
					message = StringUtils.replaceIgnoreCase(message, duelist.getPlayer().getName(),
							duelist.getPlayer().getName() + manager.getSubColor() + " (&c" + duelist.getHealth() + "❤" + manager.getSubColor() + ")");
				} else {
					message = StringUtils.replaceIgnoreCase(message, duelist.getPlayer().getName(),
							duelist.getPlayer().getName() + manager.getSubColor() + " (#aqua" + duelist.getScore() + manager.getSubColor() + ")");
				}
			}
		}

		message = Text.color(message);
	}

	public void parseDuelist(final Duelist duelist) {
		message = StringUtils.replaceIgnoreCase(message, "%teamColor%", "%team" + CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, duelist.getTeam().toString()) + "Color%");
		message = StringUtils.replaceIgnoreCase(message, "%opponentTeamColor%", "%team" + CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, duelist.getOpposingTeam().toString()) + "Color%");
		message = StringUtils.replaceIgnoreCase(message, "%score%", String.valueOf(duelist.getScore()));
		message = StringUtils.replaceIgnoreCase(message, "%teamScore%", duelist.getScoreType() == ScoreType.NONE ? String.valueOf(duelist.getTeamWins()) : String.valueOf(duelist.getTeamScore()));
		message = StringUtils.replaceIgnoreCase(message, "%opponentTeamScore%",
				duelist.getScoreType() == ScoreType.NONE ? String.valueOf(duelist.getOpposingTeamWins()) : String.valueOf(duelist.getOpposingTeamScore()));
		message = StringUtils.replaceIgnoreCase(message, "%player%", duelist.getPlayer().getName());
		if (duelist.getDuel().isCompetitive()) {
			int difference = 0;
			if (duelist.getDuel().getWinner() == duelist.getTeam()) {
				difference = duelist.getEloIncrement();
				message = StringUtils.replaceIgnoreCase(message, "%eloDifference%", String.valueOf(difference));
			} else {
				difference = duelist.getEloDecrement();
				message = StringUtils.replaceIgnoreCase(message, "%eloDifference%", String.valueOf(difference));
			}

			message = StringUtils.replaceIgnoreCase(message, "%elo%", String.valueOf(duelist.getElo() + difference));
			message = StringUtils.replaceIgnoreCase(message, "%eloIncrement%", "+" + String.valueOf(duelist.getEloIncrement()));
			message = StringUtils.replaceIgnoreCase(message, "%eloDecrement%", String.valueOf(duelist.getEloDecrement()));
		}
		parseDuel(duelist.getDuel());
	}

	public void parseRequest(final Request request) {
		message = StringUtils.replaceIgnoreCase(message, "%requester%", request.getRequester().getName());
		message = StringUtils.replaceIgnoreCase(message, "%receiver%", request.getReceiver().getName());
		message = StringUtils.replaceIgnoreCase(message, "%kit%", request.getKit().getDisplayName());
		message = StringUtils.replaceIgnoreCase(message, "%arena%", request.getArena().getDisplayName());
		message = StringUtils.replaceIgnoreCase(message, "%roundType%", WordUtils.capitalize(request.getType().toString().toLowerCase().replace("_", " ")));
		String rounds = String.valueOf(request.getRounds());
		if (request.getRounds() == 99999) {
			rounds = "Infinity";
		}
		message = StringUtils.replaceIgnoreCase(message, "%rounds%", rounds);
		message = Text.color(message);
	}

	public void parseQueue(final Queue queue) {
		message = StringUtils.replaceIgnoreCase(message, "%kit%", queue.getKit().getDisplayName());
		String mode = "Custom";
		if (queue.getReadySize() == 2) {
			mode = "Solo";
		}

		else if (queue.getReadySize() == 4) {
			mode = "Duo";
		}
		message = StringUtils.replaceIgnoreCase(message, "%mode%", mode);
		message = StringUtils.replaceIgnoreCase(message, "%type%", StringUtils.capitalize(queue.getType().toString().toLowerCase()));
		message = Text.color(message);
	}

	public void parsePlayer(final Player player) {
		message = StringUtils.replaceIgnoreCase(message, "%player%", player.getName());
		message = Text.color(message);
	}
}