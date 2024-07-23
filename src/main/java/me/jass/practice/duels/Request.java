package me.jass.practice.duels;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

import lombok.Getter;
import me.jass.practice.PracticeAPI;
import me.jass.practice.datatypes.Message;
import me.jass.practice.datatypes.Queues;
import me.jass.practice.datatypes.RoundType;
import me.jass.practice.files.Arena;
import me.jass.practice.files.Kit;
import me.jass.practice.utils.Text;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

@Getter
public class Request {
	private Player requester;
	private Player receiver;

	private Kit kit;
	private Arena arena;

	private int rounds;
	private RoundType type;

	private Long requestTime;

	public Request(final Player requester, final Player receiver, final Kit kit, final Arena arena, final int rounds, final RoundType type) {
		this.requester = requester;
		this.receiver = receiver;
		this.kit = kit;
		this.arena = arena;
		this.rounds = rounds;
		this.type = type;
		requestTime = System.currentTimeMillis();

		if (kit == null || arena == null || type == null) {
			return;
		}

		if (requester == receiver) {
			Text.tell(requester, PracticeAPI.INSTANCE.getMessageManager().getDuelSelf());
			return;
		}

		if (PracticeAPI.INSTANCE.getDuelManager().getIndex(requester) != null || PracticeAPI.INSTANCE.getDuelManager().getIndex(receiver) != null) {
			Text.tell(requester, PracticeAPI.INSTANCE.getMessageManager().getDuelFailure());
			return;
		}

		if (!PracticeAPI.INSTANCE.getMenuManager().hasDuelRequests(receiver)) {
			Text.tell(requester, PracticeAPI.INSTANCE.getMessageManager().getRequestsDisabled());
			return;
		}

		send();
	}

	public void send() {
		PracticeAPI.INSTANCE.getRequestManager().add(this);

		Text.tell(requester, Message.REQUEST_SEND.formatRequest(this));
		Text.tell(receiver, Message.REQUEST_RECEIVE.formatRequest(this));

		final TextComponent accept = new TextComponent(Text.color(PracticeAPI.INSTANCE.getMessageManager().getAcceptRequest()));
		accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/duel accept " + requester.getName().toLowerCase()));

		receiver.spigot().sendMessage(accept);
		ding();
	}

	public void ding() {
		receiver.playSound(receiver.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, 1, 1);
	}

	public void remove() {
		PracticeAPI.INSTANCE.getRequestManager().remove(this);
	}

	public void accept() {
		remove();

		if (kit == null || arena == null || type == null || requester.isDead() || receiver.isDead() || !requester.isOnline() || !receiver.isOnline()) {
			Text.tell(receiver, PracticeAPI.INSTANCE.getMessageManager().getInvalidRequest());
			return;
		}

		if (PracticeAPI.INSTANCE.getDuelManager().isDueling(requester)) {
			Text.tell(receiver, Message.REQUESTER_DUELING.formatRequest(this));
			return;
		}

		if (PracticeAPI.INSTANCE.getDuelManager().isDueling(receiver)) {
			Text.tell(receiver, PracticeAPI.INSTANCE.getMessageManager().getCurrentlyDueling());
			return;
		}

		final List<Player> teamA = new ArrayList<Player>();
		final List<Player> teamB = new ArrayList<Player>();
		teamA.add(requester);
		teamB.add(receiver);

		PracticeAPI.INSTANCE.getDuelManager().start(teamA, teamB, kit, arena, rounds, type, false, Queues.NONE);
	}

	public boolean isExpired() {
		final Long time = System.currentTimeMillis();
		final int timeLimit = PracticeAPI.INSTANCE.getConfigManager().getRequestTimeLimit();

		if (time - getRequestTime() > (timeLimit * 1000)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return requester.hashCode() + receiver.hashCode();
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null) {
			return false;
		}

		if (o == this) {
			return true;
		}
		if (!(o instanceof Request)) {
			return false;
		}

		final Request t = (Request) o;

		return (requester.equals(t.getRequester()) && receiver.equals(t.getReceiver()));
	}
}
