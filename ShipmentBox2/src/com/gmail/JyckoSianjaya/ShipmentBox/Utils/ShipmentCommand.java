package com.gmail.JyckoSianjaya.ShipmentBox.Utils;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.JyckoSianjaya.ShipmentBox.Events.EventHandlers;
import com.gmail.JyckoSianjaya.ShipmentBox.Objects.TheBox;

public final class ShipmentCommand implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("shipmentbox")) {
			Storage storages = Storage.getInstance();
		Player player = null;
		UUID myuuid = null;
		int length = args.length;
		if (sender instanceof Player) {
			player = (Player) sender;
			myuuid = player.getUniqueId();
		}
		if (length == 0) {
			for (String msge : storages.getHelpMsg()) {
				Utility.sendMsg(sender, msge);
			}
			if (!sender.hasPermission("shipmentbox.admin")) {
				return true;
			}
			Utility.sendMsg(sender, "&c&lAdmin Commands:");
			Utility.sendMsg(sender, "&8 > &7/shipmentbox &creload");
			Utility.sendMsg(sender, "&8 > &7/shipmentbox &cgive &f<Player> <Amount>");
			Utility.sendMsg(sender, "&8 > &7/shipmentbox &ctime set &e<hour/minute/second> <integer>");
			Utility.sendMsg(sender, "&8 > &7/shipmentbox &cremove &f<Player>");

			return true;
		}
		if (length > 0) {
			if (args[0].equalsIgnoreCase("status")) {
				if (!(sender instanceof Player)) {
					Utility.sendMsg(sender, "&cOnly players can do this!");
					return true;
				}
				if (!(player.hasPermission("shipmentbox.status"))) {
					Utility.sendMsg(sender, storages.getMessageNoPermission());
					return true;
				}
				if (length == 1) {
					UUID uuid = player.getUniqueId();
					TheBox box = storages.getBox(uuid);
					if (box == null) {
						Utility.sendActionBar(player, storages.getHaveNoBox());
						return true;
					}
					Location loc = box.getBoxLocation();
					double locx = loc.getX();
					double locy = loc.getY();
					double locz = loc.getZ();
					String pname = player.getName();
					Long cooldownaa = storages.getCooldown();
					Long currenttimemil = System.currentTimeMillis();
					Long currentdiff = currenttimemil - box.getCurrentCooldown();
						String cd = null;
						Long currentcoold = (cooldownaa - currentdiff) / 1000;
						int currentint = Integer.parseInt("" + currentcoold);
						if (currentint < 0) currentint = 0;
						cd = Utility.normalizeTime(currentint);
					for (String st : storages.getStatusMsg()) {
						st = st.replaceAll("%LOCATION_X%", "" + locx);
						st = st.replaceAll("%LOCATION_Y%", "" + locy);
						st = st.replaceAll("%LOCATION_Z%", "" + locz);
						st = st.replaceAll("%PLAYER%", pname);
						st = st.replaceAll("%COOLDOWN%", "" + cd);
						Utility.sendMsg(player, st);
					}
					return true;
				}
				if (length > 1) {
					if (!(player.hasPermission("shipmentbox.status.others"))) {
						Utility.sendMsg(sender, storages.getMessageNoPermission());
						return true;
					}
					Player target = Bukkit.getPlayer(args[1]);
					if (target == null) {
						Utility.sendMsg(sender, storages.getOfflineMsg());
						return true;
					}
					if (!target.isOnline()) {
						Utility.sendMsg(sender, storages.getOfflineMsg());
						return true;
					}
					String pname = target.getName();
					TheBox box = storages.getBox(target);
					if (box == null) {
						String notargetbox = storages.getTargetNoBox();
						notargetbox = notargetbox.replaceAll("%PLAYER%", pname);
						Utility.sendActionBar(player, notargetbox);
						return true;
					}
					Location loc = box.getBoxLocation();
					double locx = loc.getX();
					double locy = loc.getY();
					double locz = loc.getZ();
					Long cooldownaa = storages.getCooldown();
					Long currenttimemil = System.currentTimeMillis();
					Long currentdiff = currenttimemil - box.getCurrentCooldown();
						String cd = null;
						Long currentcoold = (cooldownaa - currentdiff) / 1000;
						int currentint = Integer.parseInt("" + currentcoold);
						if (currentint < 0) currentint = 0;
						cd = Utility.normalizeTime(currentint);
					for (String st : storages.getStatusMsg()) {
						st = st.replaceAll("%LOCATION_X%", "" + locx);
						st = st.replaceAll("%LOCATION_Y%", "" + locy);
						st = st.replaceAll("%LOCATION_Z%", "" + locz);
						st = st.replaceAll("%PLAYER%", pname);
						st = st.replaceAll("%COOLDOWN%", "" + cd);
						Utility.sendMsg(player, st);
					}
					return true;
				}
			}
			if (args[0].equalsIgnoreCase("access")) {
				if (!(sender instanceof Player)) {
					return true;
				}
				if (!(sender.hasPermission("shipmentbox.access"))) {
					Utility.sendMsg(player, storages.getMessageNoPermission());
					return true;
				}
				if (length == 1) {
					Utility.sendMsg(player, storages.getAccessHelp());
					return true;
				}
				TheBox box = storages.getBox(player);
				if (box == null) {
					Utility.sendMsg(sender, storages.getHaveNoBox());
					return true;
				}
				switch (args[1]) {
					case "lists":
					case "list":
					if (!player.hasPermission("shipmentbox.access.list")) {
						Utility.sendMsg(player, storages.getMessageNoPermission());
						return true;
					}
					String str = "";
					for (UUID uuid : box.getAccessed()) {
						OfflinePlayer p = Bukkit.getOfflinePlayer(uuid);
						if (p.getName() == null) {
							continue;
						}
						str = str + "&a" + p.getName() + "&f, ";
					}
					for (String aa : storages.getAccessList()) {
						aa = aa.replaceAll("%OWNER%", player.getName());
						aa = aa.replaceAll("%ACCESS%", str);
						Utility.sendMsg(player, aa);
					}
					return true;
					case "add":
					case "adds":
					case "put":
						if (!player.hasPermission("shipmentbox.access.add")) {
							Utility.sendMsg(player, storages.getMessageNoPermission());
							return true;
						}
						if (length == 2) {
							Utility.sendMsg(player, storages.getAccessAddHelp());
							return true;
						}
						OfflinePlayer p = Bukkit.getOfflinePlayer(args[2]);
						if (p == player) {
							Utility.sendMsg(player, storages.getNoNeedAccess());
							return true;
						}
						if (p == null) {
							Utility.sendMsg(player, storages.getNoPerson());
							return true;
						}
						UUID uuid = p.getUniqueId();
						box.setAccess(uuid);
						Utility.sendMsg(player, storages.getAccessed().replaceAll("%PERSON%", args[2]));
						return true;
					case "remove":
					case "delete":
					case "kill":
						if (!player.hasPermission("shipmentbox.access.remove")) {
							Utility.sendMsg(player, storages.getMessageNoPermission());
							return true;
						}
						if (length == 2) {
							Utility.sendMsg(player, storages.getAccessRemoveHelp());
							return true;
						}
						OfflinePlayer pa = Bukkit.getOfflinePlayer(args[2]);
						if (pa == null) {
							Utility.sendMsg(player, storages.getNoPerson());
							return true;
						}
						UUID uuidx = pa.getUniqueId();
						box.removeAccess(uuidx);
						Utility.sendMsg(player, storages.getUnAccessed().replaceAll("%PERSON%", args[2]));
						return true;
					default:
						if (!player.hasPermission("shipmentbox.access.help")) {
							Utility.sendMsg(player, storages.getMessageNoPermission());
							return true;
						}
						Utility.sendMsg(player, storages.getAccessHelp());
						return true;
				}
			}
			if (args[0].equalsIgnoreCase("remove")) {
				if (!(sender.hasPermission("shipmentbox.break.others"))) {
					Utility.sendMsg(sender, storages.getMessageNoPermission());
					return true;
				}
				if (length == 1) {
					Utility.sendMsg(sender, "&c&lOops, &7please use &e/shipmentbox remove <Player>");
					return true;
				}
				Player p = Bukkit.getPlayer(args[1]);
				if (p == null) {
					Utility.sendMsg(sender, "&c&lOops! &7That person does not exist!");
					return true;
				}
				TheBox box = storages.getBox(p);
				box.deleteBox();
				Utility.sendMsg(sender, "&a&l[!] &7Box succesfully removed.");
				return true;
			}
			if (args[0].equalsIgnoreCase("teleport")) {
				if (!(sender instanceof Player)) {
					Utility.sendMsg(sender, "&cOnly players can do this!");
					return true;
				}
				if (!(player.hasPermission("shipmentbox.teleport"))) {
					Utility.sendMsg(sender, storages.getMessageNoPermission());
					return true;
				}
				if (length == 1) {
					UUID uuid = player.getUniqueId();
					TheBox box = storages.getBox(uuid);
					if (box == null) {
						Utility.sendActionBar(player, storages.getHaveNoBox());
						return true;
					}
					Utility.sendTitle(player, 5, 60, 60, storages.getTitleTeleport(), storages.getSubTitleTeleport());	
					Utility.PlaySound(player, Sound.ENTITY_ENDERMEN_TELEPORT, 2F, 2F);
					player.teleport(box.getBoxLocation());
					return true;
				}
				if (length > 1) {
					if (!(player.hasPermission("shipmentbox.teleport.others"))) {
						Utility.sendMsg(sender, storages.getMessageNoPermission());
						return true;
					}
					Player target = Bukkit.getPlayer(args[1]);
					String pname = target.getName();
					if (target == null) {
						Utility.sendMsg(sender, storages.getOfflineMsg());
						return true;
					}
					if (!target.isOnline()) {
						Utility.sendMsg(sender, storages.getOfflineMsg());
						return true;
					}
					TheBox tbox = storages.getBox(target);
					if (tbox == null) {
						String notargetbox = storages.getTargetNoBox();
						notargetbox = notargetbox.replaceAll("%PLAYER%", pname);
						Utility.sendActionBar(player, notargetbox);
						return true;
					}
					player.teleport(tbox.getBoxLocation());
					Utility.sendActionBar(player, "&2You have been teleported to &6" + pname +  "&6's box.");
					return true;
				}
			}
			if (args[0].equalsIgnoreCase("time")) {
				if (!sender.hasPermission("shipmentbox.time")) {
					Utility.sendMsg(sender, "&cYou don't have permission.");
					return true;
				}
				Timer timer = Timer.getInstance();
				int chour = timer.getHours();
				int cminute = timer.getMinutes();
				int csec = timer.getSeconds();
				String hours = Integer.toString(chour);
				String minutes = Integer.toString(cminute);
				String seconds = Integer.toString(csec);
				if (chour < 10) {
					hours = "0" + hours;
				}
				if (cminute < 10) {
					minutes = "0" + minutes;
				}
				if (csec < 10) {
					seconds = "0" + seconds;
				}
				List<String> timemsg = storages.getTimeMessage();
				switch (Integer.toString(length)) {
				case "1":
					for (String str : timemsg) {
						str = str.replace("%HOURS%", hours);
						str = str.replace("%MINUTES%", minutes);
						str = str.replace("%SECONDS%", seconds);
				Utility.sendMsg(sender, str);
					}
					return true;
				case "2":
					Utility.sendMsg(sender, "&c&lOOPS! &aCorrect: &7/shipmentbox time set <hour/minute/second> <integer>");
					return true;
				case "3":
					Utility.sendMsg(sender, "&c&lOOPS! &aCorrect: &7/shipmentbox time set <hour/minute/second> <integer>");
					return true;
				case "4":
					int newint = 0;
					try {
						newint = Integer.parseInt(args[3]);
					} catch (NumberFormatException e) {
						Utility.sendMsg(sender, "&c&lOOPS! &aCorrect: &7/shipmentbox time set <hour/minute/second> <integer>");
						return true;
					}
					switch (args[1]) {
					case "set":
						if (!sender.hasPermission("shipmentbox.time.modify")) {
							return true;
						}
						switch (args[2]) {
						case "hour":
						case "h":
						case "hours":
							timer.setHours(newint);
							for (String str : timemsg) {
									str = str.replace("%HOURS%", hours);
									str = str.replace("%MINUTES%", minutes);
									str = str.replace("%SECONDS%", seconds);
							Utility.sendMsg(sender, str);
							}
							Utility.sendMsg(sender, "&a&lSuccesfully changed the hour to &f&l&n" + newint);
							return true;
						case "min":
						case "minute":
						case "m":
						case "minutes":
							timer.setMinutes(newint);
							for (String str : timemsg) {
								str = str.replace("%HOURS%", hours);
								str = str.replace("%MINUTES%", minutes);
								str = str.replace("%SECONDS%", seconds);
						Utility.sendMsg(sender, str);
						}
							Utility.sendMsg(sender, "&a&lSuccesfully changed the minutes to &f&l&n" + newint);
							return true;
						case "sec":
						case "s":
						case "second":
						case "seconds":
							timer.setSeconds(newint);
							for (String str : timemsg) {
								str = str.replace("%HOURS%", hours);
								str = str.replace("%MINUTES%", minutes);
								str = str.replace("%SECONDS%", seconds);
						Utility.sendMsg(sender, str);
							}
							Utility.sendMsg(sender, "&a&lSuccesfully changed the seconds to &f&l&n" + newint);
							return true;
						default:
							Utility.sendMsg(sender, "&c&lOOPS! &aCorrect: &7/shipmentbox time set <hour/minute/second> <integer>");
							return true;
					}
					}
				}
			}
			if (args[0].equalsIgnoreCase("reload")) {
					if (!sender.hasPermission("shipment.reload")) {
						Utility.sendMsg(sender, "&cYou can't do that");
						return true;
						}
					storages.resetConfig();
					Utility.sendMsg(sender, "&e&lShipment Box &f&l>> &aConfig has been reloaded.");
					EventHandlers handler = EventHandlers.getInstance();
					handler.UpdateVariables();
					return true;
				}
			if (args[0].equalsIgnoreCase("give")) {
					if (!sender.hasPermission("shipment.give")) {
							Utility.sendMsg(player, "&cYou can't do that.");
							return true;
						}
						if (length == 1) {
							Utility.sendMsg(sender, "&e&lShipment Box &f&l>> &cThat person doesn't exist/Wrong Argument.");
							return true;
						}
						if (length > 1) {
							try {
								Player target = Bukkit.getPlayer(args[1]);
								if (length < 3) {
									storages.GiveBoxItem(target);
									return true;
								}
								else if (length > 2) {
									try {
										Integer amount = Integer.valueOf(args[2]);
										storages.GiveBoxItem(target, amount);
										return true;
									} catch (Exception e) {
										Utility.sendMsg(sender, "&e&lShipmentBox &f&l>> &cOops, is that a number? No commas? &7(/sb give <Player> <Amount>");
										return true;
									}
								}
							} catch (Exception e) {
								Utility.sendMsg(sender, "&e&lShipment Box &f&l>> &cOops, an error occured, does that player exist? &7(Online?)");
								Utility.sendMsg(sender, "&cCorrect Usage: &f/shipmentbox give <Player> <Amount>");
								return true;
							}
						}
						}
					}
		}
		// TODO Auto-generated method stub
		return false;
	}

}
