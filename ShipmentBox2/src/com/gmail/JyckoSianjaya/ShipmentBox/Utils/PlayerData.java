package com.gmail.JyckoSianjaya.ShipmentBox.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.gmail.JyckoSianjaya.ShipmentBox.ShipmentBox;
import com.gmail.JyckoSianjaya.ShipmentBox.Objects.TheBox;

public class PlayerData {
	private static PlayerData instance;
	private ShipmentBox sbinstance = ShipmentBox.getInstance();
	private Storage storages;
	private File datafolder;
	private PlayerData() {
		datafolder = new File(sbinstance.getDataFolder() + File.separator + "playerdata" + File.separator);
		storages = Storage.getInstance();
		if (!datafolder.exists()) {
			datafolder.mkdir();
		}
	}
	public static PlayerData getInstance() {
		if (instance == null) {
			instance = new PlayerData();
		}
		return instance;
	}
	public void saveLocation(TheBox box) {
		Location loc = box.getBlock().getLocation();
		UUID uuid = box.getOwnerUUID();
		int X = (int) loc.getX();
		int Y = (int) loc.getY();
		int Z = (int) loc.getZ();
		File locationfile = new File(datafolder, "location" + File.separator + X + "-" + Y + "-" + Z + ".yml");
		if (!locationfile.exists()) {
			try {
				locationfile.createNewFile();
			} catch (IOException e) {
				Utility.sendConsole("&6[SB] &cCouldn't create location cache file for uuid " + uuid);
				// TODO Auto-generated catch block
				Utility.sendConsole("&6[SB] &cCouldn't save location file for uuid " + uuid);
			}
		}		YamlConfiguration locyaml = YamlConfiguration.loadConfiguration(locationfile);
		locyaml.set("UUID", box.getOwnerUUID().toString());
		try {
			locyaml.save(locationfile);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	public void saveFile(Player player, TheBox box) {
		UUID uuid = player.getUniqueId();
		File file = new File(datafolder, uuid + ".yml");
		Location loc = box.getBlock().getLocation();
		int X = (int) loc.getX();
		int Y = (int) loc.getY();
		int Z = (int) loc.getZ();
		File locationfile = new File(datafolder, "location" + File.separator + X + "-" + Y + "-" + Z + ".yml");
		if (!locationfile.exists()) {
			try {
				locationfile.createNewFile();
			} catch (IOException e) {
				Utility.sendConsole("&6[SB] &cCouldn't create location cache file for uuid " + uuid);
				// TODO Auto-generated catch block
				Utility.sendConsole("&6[SB] &cCouldn't save location file for uuid " + uuid);
			}
		}
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Utility.sendConsole("&6[SB] &cCouldn't create file for uuid " + uuid);
			}
		}
		YamlConfiguration locyaml = YamlConfiguration.loadConfiguration(locationfile);
		locyaml.set("UUID", box.getOwnerUUID().toString());
		try {
			locyaml.save(locationfile);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
		Inventory inventory = box.getInsideBox();
		String title = inventory.getTitle();
		Long cooldown = box.getCurrentCooldown();
		Boolean isShip = box.IsShipping();
		List<UUID> accesses = box.getAccessed();
		List<String> newacc = new ArrayList<String>();
		for (UUID u : accesses) {
			String str = u.toString();
			newacc.add(str);
		}
		yaml.set("accessed", newacc);
		yaml.set("title", title);
		Location locas = box.getBoxLocation();
		yaml.set("Location", locas);
		for (int i = 0; i < storages.getGuiSize(); i++) {
			ItemStack item = inventory.getItem(i);
			if (item == null) {
				continue;
			}
			yaml.set("Items." + i, item);
		}
		yaml.set("cooldown", cooldown);
		yaml.set("isShip", isShip);
		try {
			yaml.save(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Utility.sendConsole("&6[SB] &cCouldn't save file for uuid " + uuid);
		}
	}
	public void saveFile(UUID uuid, TheBox box) {
		File file = new File(datafolder, uuid + ".yml");
		Location loc = box.getBlock().getLocation();
		int X = (int) loc.getX();
		int Y = (int) loc.getY();
		int Z = (int) loc.getZ();
		File locationfile = new File(datafolder, "location" + File.separator + X + "-" + Y + "-" + Z + ".yml");
		if (!locationfile.exists()) {
			try {
				locationfile.createNewFile();
			} catch (IOException e) {
				Utility.sendConsole("&6[SB] &cCouldn't create location cache file for uuid " + uuid);
				// TODO Auto-generated catch block
				Utility.sendConsole("&6[SB] &cCouldn't save location file for uuid " + uuid);
			}
		}
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Utility.sendConsole("&6[SB] &cCouldn't create file for uuid " + uuid);
			}
		}
		YamlConfiguration locyaml = YamlConfiguration.loadConfiguration(locationfile);
		locyaml.set("UUID", box.getOwnerUUID().toString());
		try {
			locyaml.save(locationfile);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
		Inventory inventory = box.getInsideBox();
		String title = inventory.getTitle();
		Long cooldown = box.getCurrentCooldown();
		Boolean isShip = box.IsShipping();
		List<UUID> accesses = box.getAccessed();
		List<String> newacc = new ArrayList<String>();
		for (UUID u : accesses) {
			String str = u.toString();
			newacc.add(str);
		}
		yaml.set("accessed", newacc);
		yaml.set("title", title);
		Location locas = box.getBoxLocation();
		yaml.set("Location", locas);
		for (int i = 0; i < storages.getGuiSize(); i++) {
			ItemStack item = inventory.getItem(i);
			if (item == null) {
				continue;
			}
			yaml.set("Items." + i, item);
		}
		yaml.set("cooldown", cooldown);
		yaml.set("isShip", isShip);
		try {
			yaml.save(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Utility.sendConsole("&6[SB] &cCouldn't save file for uuid " + uuid);
		}
	}
	public void removeFile(UUID uuid) {
		File file = new File(datafolder, uuid + ".yml");
		if (file.exists()) {
		file.delete();
		}
		TheBox box = storages.getBox(uuid);
		Block block = box.getBlock();
		Location loc = block.getLocation();
		Double x = loc.getX();
		Double y = loc.getY();
		Double z = loc.getZ();
		File file22 = new File(datafolder, File.separator + "location" + File.separator + x + "-" + y + "-" + z + ".yml");
		if (file22.exists()) {
			file22.delete();
		}
	}

}
