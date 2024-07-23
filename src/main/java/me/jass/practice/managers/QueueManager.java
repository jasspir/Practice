package me.jass.practice.managers;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import me.jass.practice.PracticeAPI;
import me.jass.practice.datatypes.QueueType;
import me.jass.practice.datatypes.Queues;
import me.jass.practice.datatypes.Team;
import me.jass.practice.duels.Duelist;
import me.jass.practice.duels.Queue;
import me.jass.practice.files.Kit;

public class QueueManager {
	private final Map<Queue, Queue> cache = new HashMap<Queue, Queue>();
	private final Map<Player, Queue> index = new HashMap<Player, Queue>();

	public void load() {
		PracticeAPI.INSTANCE.getKitManager().getAll().forEach(kit -> {
			//			if (kit.hasQueue(QueueType.CASUAL)) {
			add(new Queue(QueueType.CASUAL, kit, 2));
			add(new Queue(QueueType.CASUAL, kit, 4));
			//			}

			//			if (kit.hasQueue(QueueType.COMPETITIVE)) {
			add(new Queue(QueueType.COMPETITIVE, kit, 2));
			add(new Queue(QueueType.COMPETITIVE, kit, 4));
			//			}
		});
	}

	public void unload() {
		cache.clear();
		index.clear();
	}

	public Queue getSolo(final Kit kit, final QueueType type) {
		return cache.get(new Queue(type, kit, 2));
	}

	public Queue getDuo(final Kit kit, final QueueType type) {
		return cache.get(new Queue(type, kit, 4));
	}

	public Collection<Queue> getAll() {
		return cache.values();
	}

	public void add(final Queue queue) {
		cache.put(queue, queue);
	}

	public void remove(final Queue queue) {
		cache.remove(queue);
	}

	public int getSoloAmount(final Kit kit, final QueueType type) {
		return getSolo(kit, type).getSize();
	}

	public int getDuoAmount(final Kit kit, final QueueType type) {
		return getDuo(kit, type).getSize();
	}

	public void removeIndex(final Player player) {
		index.remove(player);
	}

	public Queue getIndex(final Player player) {
		return index.get(player);
	}

	public boolean isQueued(final Player player) {
		return index.get(player) != null;
	}

	public void leave(final Player player) {
		if (index.get(player) != null) {
			index.get(player).removePlayer(player);
		}
	}

	public void joinSolo(final Player player, final QueueType type, final Kit kit) {
		leave(player);
		player.getInventory().setItem(type == QueueType.CASUAL ? 0 : 1, PracticeAPI.INSTANCE.getGuiManager().createItem(Material.REDSTONE, "#redLeave Queue", 1));
		final Queue queue = getSolo(kit, type);
		index.put(player, queue);
		queue.add(new Duelist(player, Team.NONE, kit, null, Queues.SOLO));
	}

	public void joinDuo(final Player player, final QueueType type, final Kit kit) {
		leave(player);
		player.getInventory().setItem(type == QueueType.CASUAL ? 0 : 1, PracticeAPI.INSTANCE.getGuiManager().createItem(Material.REDSTONE, "#redLeave Queue", 1));
		final Queue queue = getDuo(kit, type);
		index.put(player, queue);
		queue.add(new Duelist(player, Team.NONE, kit, null, Queues.DUO));
	}
}
