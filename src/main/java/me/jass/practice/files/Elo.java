package me.jass.practice.files;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;
import me.jass.practice.datatypes.Queues;

@Getter
public class Elo implements Comparable<Elo> {
	private final UUID id;
	private int elo;
	private final Queues queue;
	@Setter
	private int position;
	private final Kit kit;
	private boolean updated = false;

	public Elo(final UUID id, final Kit kit, final int elo, final Queues queue) {
		this.id = id;
		this.kit = kit;
		this.elo = elo;
		this.queue = queue;
	}

	public void setElo(final int elo) {
		this.elo = elo;
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

		if (!(o instanceof Elo)) {
			return false;
		}

		final Elo t = (Elo) o;

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

	@Override
	public int compareTo(final Elo e) {
		if (elo < e.getElo()) {
			return -1;
		}
		if (elo > e.getElo()) {
			return 1;
		}
		return 0;
	}
}