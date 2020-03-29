package us.Screenshare.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import us.Screenshare.Screenshare;
import us.Screenshare.Utilities.utils;

public class Reload implements CommandExecutor {

	public Screenshare plugin;

	public Reload(Screenshare plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		Player p = (Player) sender;

		if (cmd.getName().equalsIgnoreCase("ssreload") && sender.hasPermission("ss.reload")) {

			plugin.reloadConfig();
			p.sendMessage(utils.translate("&b&lScreenshare &8&l: &cYou have reloaded the configuration file!"));

			return false;
			
		}
		
		if(!(sender.hasPermission("ss.reload"))) {
			
			p.sendMessage(utils.translate(plugin.getConfig().getString("No-Permission")));
			return false;
			
		}

		return false;
	}

}
