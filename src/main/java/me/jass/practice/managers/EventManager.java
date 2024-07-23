package me.jass.practice.managers;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import me.jass.practice.PracticeAPI;
import me.jass.practice.listeners.AttackListener;
import me.jass.practice.listeners.BlockExplodeListener;
import me.jass.practice.listeners.BreakListener;
import me.jass.practice.listeners.ChangeListener;
import me.jass.practice.listeners.ChatListener;
import me.jass.practice.listeners.ClickListener;
import me.jass.practice.listeners.CloseListener;
import me.jass.practice.listeners.CommandListener;
import me.jass.practice.listeners.DeathListener;
import me.jass.practice.listeners.DragListener;
import me.jass.practice.listeners.DropListener;
import me.jass.practice.listeners.EntityExplodeListener;
import me.jass.practice.listeners.FishListener;
import me.jass.practice.listeners.HungerListener;
import me.jass.practice.listeners.InteractListener;
import me.jass.practice.listeners.JoinListener;
import me.jass.practice.listeners.LeaveListener;
import me.jass.practice.listeners.PickupListener;
import me.jass.practice.listeners.PlaceListener;
import me.jass.practice.listeners.ProjectileListener;
import me.jass.practice.listeners.RespawnListener;
import me.jass.practice.listeners.SpawnListener;
import me.jass.practice.listeners.TeleportListener;

public class EventManager {
	public void registerAll() {
		register(new AttackListener());
		register(new BlockExplodeListener());
		register(new BreakListener());
		register(new ChangeListener());
		register(new ChatListener());
		register(new ClickListener());
		register(new CloseListener());
		register(new CommandListener());
		register(new DeathListener());
		register(new DragListener());
		register(new DropListener());
		register(new EntityExplodeListener());
		register(new FishListener());
		register(new HungerListener());
		register(new InteractListener());
		register(new JoinListener());
		register(new LeaveListener());
		register(new PickupListener());
		register(new PlaceListener());
		register(new ProjectileListener());
		register(new RespawnListener());
		register(new SpawnListener());
		register(new TeleportListener());
	}

	public void register(final Listener listener) {
		Bukkit.getServer().getPluginManager().registerEvents(listener, PracticeAPI.INSTANCE.getPlugin());
	}
}
