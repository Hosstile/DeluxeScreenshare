package us.Screenshare.Commands;

import java.awt.TextComponent;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import net.md_5.bungee.api.chat.BaseComponent;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle.EnumTitleAction;
import us.Screenshare.Screenshare;
import us.Screenshare.Utilities.ItemBuilder;
import us.Screenshare.Utilities.utils;

public class Primary implements CommandExecutor, Listener {

	public Screenshare plugin;

	public Primary(Screenshare plugin) {
		this.plugin = plugin;
	}

	public Inventory inv = Bukkit.createInventory(null, 9, utils.translate("&b&lScreenshare Player"));

	private static boolean status = true;
	private static boolean Confirmed = false;

	public static HashMap<UUID, Boolean> frozen = new HashMap<UUID, Boolean>();
	public static HashMap<UUID, UUID> frozenPlayer = new HashMap<UUID, UUID>();

	@SuppressWarnings("unused")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (!(sender instanceof Player)) {

			Bukkit.getConsoleSender()
					.sendMessage(utils.translate("&b&lScreenshare &8&l: &cCommand is disabled for console."));
			return false;
		}

		Player p = (Player) sender;
		ItemStack confirm = new ItemBuilder(Material.STAINED_CLAY).setDyeColor(DyeColor.GREEN)
				.setName(utils.translate("&3&lConfirm")).toItemStack();
		ItemStack decline = new ItemBuilder(Material.STAINED_CLAY).setDyeColor(DyeColor.RED)
				.setName(utils.translate("&3&lDecline")).toItemStack();

		if (!(p.hasPermission("ss.use"))) {

			p.sendMessage(utils.translate("  " + plugin.getConfig().getString("PREFIX") + " "
					+ plugin.getConfig().getString("No-Permission")));
			return false;
		}

		if (cmd.getName().equalsIgnoreCase("ss") && sender.hasPermission("ss.use") || p.isOp()) {

			if (args.length == 0) {

				p.sendMessage(utils
						.translate(plugin.getConfig().getString("PREFIX") + " " + "&cYou must enter a valid target!"));
				return false;

			}

			Player target = Bukkit.getPlayer(args[0]);

			if (target == null) {

				p.sendMessage(
						utils.translate(plugin.getConfig().getString("PREFIX") + " &cThat target cannot be found!"));
				return false;

			} else if (target != null) {

				if (Confirmed == false) {

					inv.setItem(3, confirm);
					inv.setItem(5, decline);

					p.openInventory(inv);
					return false;
				}

				if (Confirmed = true) {

					if (status == true) {

						if (plugin.getConfig().getBoolean("broadcastGlobal") == true) {

							status = !status;

							frozenPlayer.put(target.getUniqueId(), p.getUniqueId());

							Bukkit.broadcastMessage(utils.translate(plugin.getConfig().getString("Broadcast-Message")
									.replaceAll("%player%", p.getName()).replaceAll("%target%", target.getName())));

							utils.createHologram(target.getLocation(),
									utils.translate("&7Player is being &bScreenshared"));

							String sendTitle = utils.translate(plugin.getConfig().getString("Screensharing"));
							sendTitle.replaceAll("%target%", target.getName());

							IChatBaseComponent chatTitle = ChatSerializer.a("{\"text\":\""
									+ utils.translate(sendTitle).replaceAll("%target%", target.getName()) + "\"}");

							PacketPlayOutTitle title = new PacketPlayOutTitle(EnumTitleAction.TITLE, chatTitle);
							PacketPlayOutTitle length = new PacketPlayOutTitle(2, 30, 2);

							((CraftPlayer) p).getHandle().playerConnection.sendPacket(title);
							((CraftPlayer) p).getHandle().playerConnection.sendPacket(length);

							p.sendMessage(utils.translate(plugin.getConfig().getString("PREFIX") + " " + plugin
									.getConfig().getString("Screensharing").replaceAll("%target%", target.getName())));
							frozen.put(target.getUniqueId(), true);

							return false;
						}

						if (Confirmed = true) {

							if (status == true) {

								if (plugin.getConfig().getBoolean("broadcastGlobal") == false) {
									frozenPlayer.put(target.getUniqueId(), p.getUniqueId());
									status = !status;

									utils.createHologram(target.getLocation(),
											utils.translate("&7Player is being &bScreenshared"));

									String sendTitle = utils.translate(plugin.getConfig().getString("Screensharing"));
									sendTitle.replaceAll("%target%", target.getName());

									IChatBaseComponent chatTitle = ChatSerializer.a("{\"text\":\""
											+ utils.translate(sendTitle).replaceAll("%target%", target.getName())
											+ "\"}");

									PacketPlayOutTitle title = new PacketPlayOutTitle(EnumTitleAction.TITLE, chatTitle);
									PacketPlayOutTitle length = new PacketPlayOutTitle(2, 30, 2);

									((CraftPlayer) p).getHandle().playerConnection.sendPacket(title);
									((CraftPlayer) p).getHandle().playerConnection.sendPacket(length);

									p.sendMessage(utils.translate(plugin.getConfig().getString("PREFIX") + " "
											+ plugin.getConfig().getString("Screensharing").replaceAll("%target%",
													target.getName())));
									frozen.put(target.getUniqueId(), true);

									return false;
								}
							}
							if (Confirmed = false) {
								p.closeInventory();
							}

						}
					}

					if (status == false) {

						status = !status;
						frozenPlayer.remove(target.getUniqueId());
						
						utils.removeArmorStand(target.getWorld(), utils.translate("&7Player is being &bScreenshared"));

						frozen.remove(target.getUniqueId());

						String sendTitle = utils.translate(plugin.getConfig().getString("UnScreensharing"));
						sendTitle.replaceAll("%target%", target.getName());

						IChatBaseComponent chatTitle = ChatSerializer.a("{\"text\":\""
								+ utils.translate(sendTitle).replaceAll("%target%", target.getName()) + "\"}");

						PacketPlayOutTitle title = new PacketPlayOutTitle(EnumTitleAction.TITLE, chatTitle);
						PacketPlayOutTitle length = new PacketPlayOutTitle(2, 30, 2);

						((CraftPlayer) p).getHandle().playerConnection.sendPacket(title);
						((CraftPlayer) p).getHandle().playerConnection.sendPacket(length);

						target.sendMessage(utils.translate(plugin.getConfig().getString("PREFIX") + " "
								+ plugin.getConfig().getString("UnFrozen")));

						p.sendMessage(utils.translate(plugin.getConfig().getString("PREFIX") + " " + plugin.getConfig()
								.getString("UnScreensharing").replaceAll("%target%", target.getName())));

						return false;

					}

				}

			}
		}

		return false;

	}

	@EventHandler

	public void onFrozen(PlayerMoveEvent e) {

		if (frozen.containsKey(e.getPlayer().getUniqueId())) {

			e.setTo(e.getFrom());

			e.getPlayer().sendMessage(utils
					.translate(plugin.getConfig().getString("PREFIX") + " " + plugin.getConfig().getString("Frozen")));
			return;
		} else {
			return;
		}

	}

	@EventHandler

	public void onClick(InventoryClickEvent e) {

		if (e.getInventory().getTitle().equalsIgnoreCase(utils.translate("&b&lScreenshare Player"))) {

			org.bukkit.inventory.ItemStack clicked = e.getCurrentItem();

			if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(utils.translate("&3&lDecline"))) {

				e.getWhoClicked().sendMessage(utils
						.translate(plugin.getConfig().getString("PREFIX") + " &7You &f&lCANCELLED &7the screenshare."));

				Confirmed = false;
				e.setCancelled(true);
				e.getWhoClicked().closeInventory();
				return;

			}

			if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(utils.translate("&3&lConfirm"))) {

				e.getWhoClicked().sendMessage(utils.translate(plugin.getConfig().getString("PREFIX")
						+ " &7Please re-issue the command for &f&lCONFIRMATION&7."));

				Confirmed = true;
				e.setCancelled(true);
				e.getWhoClicked().closeInventory();
				return;

			}

			if (!clicked.getItemMeta().getDisplayName().equalsIgnoreCase(utils.translate("&3&lConfirm"))) {

				if (!clicked.getItemMeta().getDisplayName().equalsIgnoreCase(utils.translate("&3&lConfirm")))

					e.setCancelled(true);
				return;
			}

		}

	}

	@EventHandler

	public void onJoin(PlayerQuitEvent e) {
		Player p = (Player) e.getPlayer();

		String command = "test";
		String displayedMessage = "Message";

		if (frozen.containsKey(p.getUniqueId())) {

			IChatBaseComponent msg = ChatSerializer.a(command);
			PacketPlayOutChat packet = new PacketPlayOutChat(msg);

			Player player = Bukkit.getPlayer(frozenPlayer.get(p.getUniqueId()));
			
			EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();

			nmsPlayer.playerConnection.sendPacket(packet);

			utils.removeArmorStand(p.getWorld(), utils.translate("&b&lScreenshare Player"));

		} else {
			return;
		}

		return;

	}

}
