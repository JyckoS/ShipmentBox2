package com.gmail.JyckoSianjaya.ShipmentBox;

import java.io.File;



import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import com.gmail.JyckoSianjaya.ShipmentBox.Events.EventHandlers;
import com.gmail.JyckoSianjaya.ShipmentBox.Events.Events;
import com.gmail.JyckoSianjaya.ShipmentBox.Objects.TheBox;
import com.gmail.JyckoSianjaya.ShipmentBox.Utils.PlayerData;
import com.gmail.JyckoSianjaya.ShipmentBox.Utils.ShipmentCommand;
import com.gmail.JyckoSianjaya.ShipmentBox.Utils.Storage;
import com.gmail.JyckoSianjaya.ShipmentBox.Utils.Timer;
import com.gmail.JyckoSianjaya.ShipmentBox.Utils.Utility;

import net.milkbowl.vault.economy.Economy;

public class ShipmentBox extends JavaPlugin {
	private static Economy economy;
	private static ShipmentBox instance;

	public static Economy getEconomy() {
		return economy;
	}

	@Override
	public void onEnable() {
		Metrics metrics = new Metrics(this);
		this.reloadConfig();
		this.getConfig().options().copyDefaults(true);
		this.saveConfig();
		File worth = new File(this.getDataFolder(), "prices" + ".yml");
		File messages = new File(this.getDataFolder(), "messages" + ".yml");
		if (!worth.exists()) {
			this.saveResource("prices.yml", true);
		}
		if (!messages.exists()) {
			this.saveResource("messages.yml", true);
		}
		YamlConfiguration worthyml = YamlConfiguration.loadConfiguration(worth);
		YamlConfiguration messagesyml = YamlConfiguration.loadConfiguration(messages);
		messagesyml.options().copyDefaults(true);
		try {
			worthyml.save(worth);
			messagesyml.save(messages);
		} catch (IOException e) {
			e.printStackTrace();
			Utility.sendConsole("&a[ShipmentBox] &fCouldn't save prices.yml & messages.yml");
		}
		instance = this;
		// Done Loading Files
		// Beyond this then load variables (Dont load files below!)
		Storage storage = Storage.getInstance();
		EventHandlers eventh = EventHandlers.getInstance();
		Timer timer = Timer.getInstance();
		PlayerData pdata = PlayerData.getInstance();
		if (!setupVault()) {
			this.setEnabled(false);
		}
		File locandbox = new File(this.getDataFolder() + File.separator + "playerdata" + File.separator + "location");
		if (!locandbox.exists()) {
			locandbox.mkdir();
		}
		this.getCommand("shipmentbox").setExecutor(new ShipmentCommand());
		ZoneId id = storage.getTimeZone();
		ZonedDateTime trex = ZonedDateTime.now(id);
		int chour = trex.getHour();
		int cminutes = trex.getMinute();
		int second = trex.getSecond();
		Utility.sendConsole(
				"Time from TimeZone " + id + " is " + "[" + chour + " : " + cminutes + " ; " + second + "]");
		timer.setHours(chour);
		timer.setMinutes(cminutes);
		timer.setSeconds(second);
		Bukkit.getServer().getPluginManager().registerEvents(new Events(), this);
		if (!shouldEnable()) {
			Utility.sendConsole("&6[ShipmentBox] &bHolographicDisplays &cNot Found! Disabling...");
			this.setEnabled(false);
			return;
		}
		Utility.sendConsole("&6[ShipmentBox] &bHolographicDisplays &afound! hooking..");
	}

	public Boolean shouldEnable() {
		return getServer().getPluginManager().isPluginEnabled("HolographicDisplays");
	}

	@Override
	public void onDisable() {
		Storage storages = Storage.getInstance();
		PlayerData pdata = PlayerData.getInstance();
		for (Player p : Bukkit.getOnlinePlayers()) {
			TheBox box = storages.getBox(p);
			if (box == null) {
				continue;
			}
			if (box != null) {
				pdata.saveFile(p, box);
			}
			if (!box.HasHologram()) {
				continue;
			}
			box.killHologram();
		}
	}

	public static ShipmentBox getInstance() {
		return instance;
	}

	public boolean setupVault() {
		if (!this.getServer().getPluginManager().isPluginEnabled("Vault")) {
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		economy = rsp.getProvider();
		return true;
	}

}
