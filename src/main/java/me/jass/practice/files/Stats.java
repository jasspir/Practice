package me.jass.practice.files;

import java.util.UUID;

import lombok.Getter;
import me.jass.practice.datatypes.Queues;
import me.jass.practice.datatypes.Stat;

@Getter
public class Stats {
	private final UUID id;
	private final Kit kit;
	private final Queues queue;
	private int wins = 0;
	private int losses = 0;
	private boolean updated = false;

	public Stats(final UUID id, final Kit kit, final Queues queue) {
		this.id = id;
		this.kit = kit;
		this.queue = queue;
	}

	public void increase(final Stat stat) {
		set(stat, get(stat) + 1);
	}

	public int getMatches() {
		return wins + losses;
	}

	public int get(final Stat stat) {
		if (stat == Stat.WINS) {
			return wins;
		}

		else if (stat == Stat.LOSSES) {
			return losses;
		}

		else if (stat == Stat.MATCHES) {
			return wins + losses;
		}

		return 0;
	}

	public void set(final Stat stat, int amount) {
		if (amount < 0) {
			amount = 0;
		}

		if (stat == Stat.WINS) {
			wins = amount;
		}

		else if (stat == Stat.LOSSES) {
			losses = amount;
		}

		updated = true;
	}

	public void outdate() {
		updated = false;
	}

	@Override
	public int hashCode() {
		int kitHash = 1;
		int queueHash = 1;

		if (kit != null) {
			kitHash = kit.hashCode();
		}

		if (queue != null) {
			queueHash = queue.hashCode();
		}

		return id.hashCode() + kitHash + queueHash;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null) {
			return false;
		}

		if (o == this) {
			return true;
		}

		if (!(o instanceof Stats)) {
			return false;
		}

		final Stats t = (Stats) o;

		boolean noKit = false;
		boolean noQueue = false;

		if (kit == null || t.getKit() == null) {
			noKit = true;
		}

		if (queue == null || t.getQueue() == null) {
			noQueue = true;
		}

		return (id.equals(t.getId()) && (noKit || kit.equals(t.getKit())) && (noQueue || queue.equals(t.getQueue())));
	}
}
