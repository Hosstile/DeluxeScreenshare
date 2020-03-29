package us.Screenshare;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import us.Screenshare.Commands.Primary;
import us.Screenshare.Commands.Reload;

public class Screenshare extends JavaPlugin implements Listener {

	private static Screenshare ss;

	public static Screenshare getScreenshare() {
		return ss;
	}

	public void onEnable() {

		config();
		getCommand("ss").setExecutor(new Primary(this));
		getCommand("ssreload").setExecutor(new Reload(this));

		Bukkit.getPluginManager().registerEvents(new Primary(this), this);
	}

	public void onDisable() {

	}

	private void config() {
		getConfig().options().copyDefaults(true);
		saveConfig();
	}

}
