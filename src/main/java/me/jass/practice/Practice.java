package me.jass.practice;

import org.bukkit.plugin.java.JavaPlugin;

public class Practice extends JavaPlugin {

	@Override
	public void onEnable() {
		PracticeAPI.INSTANCE.start(this);
	}

	@Override
	public void onDisable() {
		PracticeAPI.INSTANCE.stop();
	}
}
