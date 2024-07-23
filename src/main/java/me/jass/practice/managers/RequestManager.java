package me.jass.practice.managers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;

import me.jass.practice.datatypes.RoundType;
import me.jass.practice.duels.Request;
import me.jass.practice.files.Arena;
import me.jass.practice.files.Kit;

public class RequestManager {
	private final Map<Request, Request> cache = new HashMap<Request, Request>();

	public Request get(final Player requester, final Player receiver) {
		return cache.get(new Request(requester, receiver, null, null, 0, null));
	}

	public Collection<Request> getAll() {
		return cache.values();
	}

	public void add(final Request request) {
		cache.put(request, request);
	}

	public void remove(final Request request) {
		cache.remove(request);
	}

	public void removeAll() {
		cache.clear();
	}

	public void removeAllBy(final Player player) {
		final List<Request> requestsToBeRemoved = new ArrayList<Request>();

		cache.values().forEach(request -> {
			if (request.getRequester() == player) {
				requestsToBeRemoved.add(request);
			}
		});

		for (final Request request : requestsToBeRemoved) {
			cache.remove(request);
		}
	}

	public void send(final Player requester, final Player receiver, final Kit kit, final Arena arena, final int rounds, final RoundType type) {
		add(new Request(requester, receiver, kit, arena, rounds, type));
	}
}
