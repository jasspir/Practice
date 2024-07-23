package me.jass.practice.duels;

import org.bukkit.entity.Player;

import lombok.Getter;
import lombok.Setter;
import me.jass.practice.PracticeAPI;
import me.jass.practice.datatypes.Goal;
import me.jass.practice.datatypes.Queues;
import me.jass.practice.datatypes.RoundType;
import me.jass.practice.files.Arena;
import me.jass.practice.files.Extra;
import me.jass.practice.files.Kit;

@Getter
@Setter
public class Selection {
	private final Player selector;
	private final Goal goal;
	private Player player;
	private Kit kit;
	private Arena arena;
	private Extra extra;
	private int rounds;
	private RoundType type;
	private Queues queue;

	public Selection(final Player selector, final Goal goal) {
		this.selector = selector;
		this.goal = goal;
	}

	public void sendRequest() {
		PracticeAPI.INSTANCE.getRequestManager().send(selector, player, kit, arena, rounds, type);
	}
}
