package me.jass.practice.duels;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import lombok.Getter;
import me.jass.practice.PracticeAPI;
import me.jass.practice.datatypes.Message;
import me.jass.practice.datatypes.QueueType;
import me.jass.practice.datatypes.RoundType;
import me.jass.practice.files.Arena;
import me.jass.practice.files.Kit;
import me.jass.practice.utils.Text;

@Getter
public class Queue {
	private final QueueType type;
	private final Kit kit;
	private final int readySize;
	private final List<Duelist> queued = new ArrayList<Duelist>();
	private List<Duelist> ready = new ArrayList<Duelist>();

	public Queue(final QueueType type, final Kit kit, final int readySize) {
		this.type = type;
		this.kit = kit;
		this.readySize = readySize;
	}

	public void startIfReady() {
		if (readySize > getSize()) {
			return;
		}

		if (type == QueueType.CASUAL) {
			if (readySize == getSize()) {
				ready.addAll(queued);
				startDuel();
			}
			return;
		}

		for (final Duelist duelist : queued) {
			final int basePing = duelist.getPlayer().getPing();
			final int baseRange = PracticeAPI.INSTANCE.getMenuManager().getPingRange(duelist.getPlayer());
			final List<Duelist> ready = new ArrayList<Duelist>();
			ready.add(duelist);

			for (final Duelist other : queued) {
				if (duelist == other) {
					continue;
				}

				final int otherPing = other.getPlayer().getPing();
				final int otherRange = PracticeAPI.INSTANCE.getMenuManager().getPingRange(other.getPlayer());
				final int pingDifference = Math.abs(basePing - otherPing);

				if (pingDifference <= baseRange || baseRange == 1 && pingDifference <= otherRange || otherRange == 1) {
					ready.add(other);

					if (ready.size() == readySize) {
						this.ready = ready;
						break;
					}
				}
			}

			if (ready.size() == readySize) {
				startDuel();
				return;
			}
		}
	}

	public int getSize() {
		return queued.size();
	}

	public void add(final Duelist duelist) {
		Text.tell(duelist.getPlayer(), Message.QUEUE_JOIN.formatQueue(this));
		queued.add(duelist);
		startIfReady();
	}

	public void remove(final Duelist duelist, final boolean forced) {
		if (!forced) {
			Text.tell(duelist.getPlayer(), Message.QUEUE_LEAVE.formatQueue(this));
		}

		queued.remove(duelist);
		PracticeAPI.INSTANCE.getQueueManager().removeIndex(duelist.getPlayer());
	}

	public void removePlayer(final Player player) {
		remove(new Duelist(player, null, null, null, null), false);
	}

	public boolean hasDuelist(final Duelist duelist) {
		return queued.contains(duelist);
	}

	public boolean hasPlayer(final Player player) {
		return queued.contains(new Duelist(player, null, null, null, null));
	}

	public void startDuel() {
		final Arena arena = PracticeAPI.INSTANCE.getArenaManager().getRandom(kit, type);

		if (arena == null) {
			Text.staff("[debug] No arenas found please make more/assign them to your kits");

			for (final Duelist duelist : ready) {
				remove(duelist, true);
			}
			ready.clear();
			return;
		}

		final List<Duelist> duelists = new ArrayList<Duelist>();
		for (final Duelist duelist : ready) {
			duelists.add(new Duelist(duelist.getPlayer(), duelist.getTeam(), duelist.getKit(), arena, duelist.getQueue()));
			remove(duelist, true);
		}

		PracticeAPI.INSTANCE.getDuelManager().start(duelists, 1, RoundType.BEST_OF, type == QueueType.COMPETITIVE);

		ready.clear();
	}

	public int getAmountQueued() {
		return queued.size();
	}

	@Override
	public int hashCode() {
		return type.hashCode() + kit.hashCode() + Integer.hashCode(readySize);
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null) {
			return false;
		}

		if (o == this) {
			return true;
		}

		if (!(o instanceof Queue)) {
			return false;
		}

		final Queue t = (Queue) o;

		return (type.equals(t.getType()) && kit.equals(t.getKit()) && readySize == t.getReadySize());
	}
}
