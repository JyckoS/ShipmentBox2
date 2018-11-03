package com.gmail.JyckoSianjaya.ShipmentBox.Events;

import java.io.File;
import java.util.Collection;
import java.util.List;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import com.gmail.JyckoSianjaya.ShipmentBox.Utils.PlayerData;

import com.gmail.JyckoSianjaya.ShipmentBox.Utils.Storage;
import com.gmail.JyckoSianjaya.ShipmentBox.Utils.Timer;
import com.gmail.JyckoSianjaya.ShipmentBox.Utils.Utility;

import net.md_5.bungee.api.ChatColor;

import com.gmail.JyckoSianjaya.ShipmentBox.ShipmentBox;
import com.gmail.JyckoSianjaya.ShipmentBox.Objects.GUIHolder;
import com.gmail.JyckoSianjaya.ShipmentBox.Objects.TheBox;

public class EventHandlers {
	private static EventHandlers instance;
	private Storage storages = Storage.getInstance();
	private ShipmentBox maininst = ShipmentBox.getInstance();
	private String BoxItemName = storages.getName().replaceAll("%OWNER%", "");
	private String BoxItemName_Raw = storages.getName();
	private String GuiName = storages.getGuiName().replaceAll("%OWNER%", "");
	private String GuiName_Raw = storages.getGuiName();
	private String BlockPlacedMsg = storages.getABBoxPlaced();
	private String BlockBroken = storages.getABBoxDestroyed();
	private List<String> itemsgone = storages.getMsgItemsGoneMsg();
	private String notyourbox = storages.getABNotHisBox();
	private String nobox = storages.getABNoBox();
	private String noperm = storages.getMessageNoPermission();
	private String alreadyhave = storages.getABAlreadyHaveBox();
	private String explosioncancel = storages.getHoloExplosionCancel();
	private String ItemsKept = storages.getABItemKept();
	private int keepslot = storages.getKeepSlot();
	private int shipslot = storages.getShipSlot();
	private Collection<ItemStack> items = storages.getNeutralItems();
	//
	private PlayerData myplayerdata = PlayerData.getInstance();
	private EventHandlers() {
	}
	public static EventHandlers getInstance() {
		if (instance == null) {
			instance = new EventHandlers();
		}
		return instance;
	}
	public void UpdateVariables() {
		items = storages.getNeutralItems();
		BoxItemName = storages.getName().replaceAll("%OWNER%", "");
		BoxItemName_Raw = storages.getName();
		GuiName = storages.getGuiName().replaceAll("%OWNER%", "");
		GuiName_Raw = storages.getGuiName();
		 BlockPlacedMsg = storages.getABBoxPlaced();
		 BlockBroken = storages.getABBoxDestroyed();
		 itemsgone = storages.getMsgItemsGoneMsg();
		 notyourbox = storages.getABNotHisBox();
		 nobox = storages.getABNoBox();
		 noperm = storages.getMessageNoPermission();
		 alreadyhave = storages.getABAlreadyHaveBox();
		 explosioncancel = storages.getHoloExplosionCancel();
		 ItemsKept = storages.getABItemKept();
		 storages.getKeepSlot();
		 storages.getShipSlot();
		 
	}
	public void ManageHopperInsert(InventoryMoveItemEvent event) {
		Inventory inv = event.getDestination();
		String invtitle = inv.getTitle();
		if (!invtitle.contains(BoxItemName)) {
			return;
		}
		event.setCancelled(true);
	}
	public void ManageBoxBreak(BlockBreakEvent event) {
		Player p = event.getPlayer();
		Block block = event.getBlock();
		UUID uuid = p.getUniqueId();
		if (block.getType() == Material.CHEST) {
			Chest chest = (Chest) block.getState();
			Inventory inv = chest.getInventory();
			String title = inv.getTitle();
			String pname = p.getName();
			if (title.contains(BoxItemName) && !title.contains(pname)) {
				if (p.hasPermission("shipmentbox.break.others")) {
					Utility.sendMsg(p, "&c&l[&e&l!&c&l] &7If you would like to destroy the box, use /sb remove <Player>");
				}
				Utility.sendActionBar(p, notyourbox);
				event.setCancelled(true);
				return;
			}
			if (!title.contains(BoxItemName) && !title.contains(pname)) return;
			Utility.sendActionBar(p, BlockBroken);
			for (String sta : itemsgone) {
				Utility.sendMsg(p, sta);
			}
			Utility.PlaySound(p, Sound.ENTITY_ZOMBIE_BREAK_DOOR_WOOD, 0.4F, 1.3F);
			TheBox box = storages.getBox(uuid);
			if (box == null) {
				block.setType(Material.AIR);
				Location bloc = block.getLocation();
				Utility.sendConsole("&6[SB] &cThe block at: &f" + bloc.getX() + ", " + bloc.getY() + ", " + bloc.getZ() + " &chas been removed due to a null.");
				return;
			}
			myplayerdata.removeFile(uuid);
			Long cooldown = box.getCurrentCooldown();
			box.killHologram();
			storages.setCacheCooldown(uuid, cooldown);
			box.deleteBox();
		}
	}
	private boolean isABox(Block block) {
		if (!(block.getType() == Material.CHEST)) {
			return false;
		}
		Chest chest = (Chest) block.getState();
		String title = chest.getInventory().getTitle();
		if (!title.contains(BoxItemName)) {
			return false;
		}
		else {
			return true;
		}
	}
	private boolean isAChest(Block block) {
		if (!(block.getType() == Material.CHEST)) {
			return false;
		}
		else {
			return true;
		}
		
	}
	public void ManageBoxPlace(BlockPlaceEvent event) {
		Player p = event.getPlayer();
		Block block = event.getBlockPlaced();
		if (block.getType() == Material.CHEST) {
			Chest chest = (Chest) block.getState();
			Inventory inv = chest.getInventory();
			String title = inv.getTitle();
			String pname = p.getName();
			Location loc = block.getLocation();
			Location loc1 = loc.clone();
			loc1.setX(loc1.getX() + 1.0);
			Location loc2 = loc.clone();
			loc2.setX(loc2.getX() - 1.0);
			Location loc3 = loc.clone();
			loc3.setZ(loc3.getZ() + 1.0);
			Location loc4 = loc.clone();
			loc4.setZ(loc4.getZ() - 1.0);
			Block b1 = loc1.getBlock();
			Block b2 = loc2.getBlock();
			Block b3 = loc3.getBlock();
			Block b4 = loc4.getBlock();
			if (isABox(b1) == true || isABox(b2) == true || isABox(b3) == true || isABox(b4) == true) {
				Utility.sendActionBar(p, storages.getABBoxNearby());
				event.setCancelled(true);
				return;
			}
			if (!title.contains(BoxItemName) && !title.contains(pname)) return;
			if (Utility.HasBox(p) == true) {
				event.setCancelled(true);
				Utility.sendActionBar(p, alreadyhave);
				return;
			}
			if (title.contains(BoxItemName)) {
				if (!title.contains(pname)) {
					Utility.sendActionBar(p, storages.getABNotHisBox());
					return;
				}
				if (isAChest(b1) == true|| isAChest(b2) == true|| isAChest(b3) == true|| isAChest(b4) == true) {
					event.setCancelled(true);
					Utility.sendActionBar(p, storages.getABBoxNearby());
					return;
				}
				Utility.sendActionBar(p, BlockPlacedMsg);
				Utility.PlaySound(p, Sound.BLOCK_ANVIL_PLACE, 0.7F, 1.5F);
				Location locas = block.getLocation();
				locas = locas.add(0, 1, 0);
				TheBox box = new TheBox(0L, p, locas);
				}
		}
	}
	public void ManageBoxExplosion(BlockExplodeEvent event) {
		java.util.List<Block> block = event.blockList();
		for (Block g : block) {
			if (g.getType() == Material.CHEST) {
				Chest c = (Chest) g.getState();
				World world = g.getWorld();
				Location loc = g.getLocation();
				Inventory inv = c.getInventory();
				if (inv.getTitle().contains(BoxItemName)) {
					event.setCancelled(true);
					Utility.SpawnHolo(g, explosioncancel);
					world.playEffect(loc, Effect.EXTINGUISH, 10);
					return;
				}
			}
		}
	}
	public void ManageEntityExplosion(EntityExplodeEvent event) {
		java.util.List<Block> block = event.blockList();
		for (Block g : block) {
			if (g.getType() == Material.CHEST) {
				Chest c = (Chest) g.getState();
				Inventory inv = c.getInventory();
				World w = g.getWorld();
				Location loc = g.getLocation();
				if (inv.getTitle().contains(BoxItemName)) {
					event.setCancelled(true);
					Utility.SpawnHolo(g, explosioncancel);
					w.playEffect(loc, Effect.EXTINGUISH, 10);
					return;
				}
			}
		}
	}
	public void ManageBoxOpening(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		String name = player.getName();
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Block b = event.getClickedBlock();
			if (b.getType() != Material.CHEST) {
				return;
			}
			Chest chest = (Chest) b.getState();
			Inventory inv = chest.getInventory();
			String g = inv.getTitle();
			UUID u = player.getUniqueId();
			if (g.contains(BoxItemName) && !g.contains(name)) {
				String str = inv.getTitle().replaceAll(BoxItemName, "");
				@SuppressWarnings("deprecation")
				Player tplayer = Bukkit.getPlayer(str);
				if (tplayer == null) {
					Location loc = event.getClickedBlock().getLocation();
					int eX = (int) loc.getX();
					int eY = (int) loc.getY();
					int eZ = (int) loc.getZ();
					File locfile = new File(maininst.getDataFolder(), "playerdata" + File.separator + "location" + File.separator + eX + "-" + eY + "-" + eZ + ".yml");
					YamlConfiguration locyaml = YamlConfiguration.loadConfiguration(locfile);
					String uux = locyaml.getString("UUID");
					if (uux == null) {
						event.setCancelled(true);
						Location bloc = b.getLocation();
						Utility.sendConsole("&6[SB] &cThe block at: &f" + bloc.getX() + ", " + bloc.getY() + ", " + bloc.getZ() + " &chas been removed due to a null.");
						b.setType(Material.AIR);
						return;
					}
					UUID uuid = UUID.fromString(uux);
					File file = new File(maininst.getDataFolder() + File.separator + "playerdata" + File.separator + uuid + ".yml");
					YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
					Long cooldowns = yaml.getLong("cooldown");
					Location location = (Location) yaml.get("Location");
					Long cd = new Long(cooldowns);
					OfflinePlayer p = Bukkit.getOfflinePlayer(uuid);
					TheBox box = new TheBox(cd, p, location);
					Inventory dinv = box.getInsideBox();
					box.setAllAccessFromString(yaml.getStringList("accessed"));
					for (int i = 0; i < storages.getGuiSize(); i++) {
					ItemStack dummy = yaml.getItemStack("Items." + i);
					if (dummy == null) continue;
					dinv.setItem(i, dummy);
					}
					box.setInsideBox(dinv);
					Boolean isship = (Boolean) yaml.get("isShip");
					box.setCooldown(cooldowns);
					box.setShipping(isship);
					storages.SaveBox(uuid, box);
					if (!box.isAccessed(u)) {
						Utility.sendActionBar(player, storages.getABNotHisBox());
						event.setCancelled(true);
						return;
					}
					box.OpenBox(player);
					event.setCancelled(true);
					Utility.PlaySound(player, Sound.BLOCK_CHEST_OPEN, 0.5F, 0.8F);
					Utility.PlaySound(player, Sound.BLOCK_ANVIL_STEP, 0.2F, 1.3F);
					return;
				}
				UUID uuid = tplayer.getUniqueId();
				TheBox box = storages.getBox(uuid);
				if (box != null) {
					if (!box.isAccessed(u)) {
						Utility.sendActionBar(player, storages.getABNotHisBox());
						event.setCancelled(true);
						return;
					}
					box.OpenBox(player);
					event.setCancelled(true);
					Utility.PlaySound(player, Sound.BLOCK_CHEST_OPEN, 0.5F, 0.8F);
					Utility.PlaySound(player, Sound.BLOCK_ANVIL_STEP, 0.2F, 1.3F);
					return;
				}
				if (box == null) {
					Location loc = event.getClickedBlock().getLocation();
					Double eX = loc.getX();
					Double eY = loc.getY();
					Double eZ = loc.getZ();
					File locfile = new File(maininst.getDataFolder(), "playerdata" + File.separator + "location" + File.separator + eX + "-" + eY + "-" + eZ + ".yml");
					YamlConfiguration locyaml = YamlConfiguration.loadConfiguration(locfile);
					uuid = UUID.fromString(locyaml.getString("UUID"));
					File file = new File(maininst.getDataFolder() + File.separator + "playerdata" + File.separator + uuid + ".yml");
					YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
					Long cooldowns = yaml.getLong("cooldown");
					Location location = (Location) yaml.get("Location");
					Long cd = new Long(cooldowns);
					Player p = (Player) Bukkit.getOfflinePlayer(uuid);
					box = new TheBox(cd, p, location);
					Inventory dinv = box.getInsideBox();
					box.setAllAccessFromString(yaml.getStringList("accessed"));
					for (int i = 0; i < storages.getGuiSize(); i++) {
					ItemStack dummy = yaml.getItemStack("Items." + i);
					if (dummy == null) continue;
					dinv.setItem(i, dummy);
					}
					box.setInsideBox(dinv);
					Boolean isship = (Boolean) yaml.get("isShip");
					box.setCooldown(cooldowns);
					box.setShipping(isship);
					storages.SaveBox(uuid, box);
				}
				if (!box.isAccessed(u)) {
					Utility.sendActionBar(player, storages.getABNotHisBox());
					event.setCancelled(true);
					return;
				}
				box.OpenBox(player);
				event.setCancelled(true);
				Utility.PlaySound(player, Sound.BLOCK_CHEST_OPEN, 0.5F, 0.8F);
				Utility.PlaySound(player, Sound.BLOCK_ANVIL_STEP, 0.2F, 1.3F);
				return;
			}
			if (g.contains(BoxItemName) && g.contains(player.getName())) {
					event.setCancelled(true);
					Utility.PlaySound(player, Sound.BLOCK_CHEST_OPEN, 0.5F, 0.8F);
					Utility.PlaySound(player, Sound.BLOCK_ANVIL_STEP, 0.2F, 1.3F);
					TheBox box = storages.getBox(u);
					if (box == null) {
						b.setType(Material.AIR);
						Location bloc = b.getLocation();
						Utility.sendConsole("&6[SB] &cThe block at: &f" + bloc.getX() + ", " + bloc.getY() + ", " + bloc.getZ() + " &chas been removed due to a null.");
						return;
					}
					box.OpenBox();

			}
		}
	}
	public void ManageBoxClosing(InventoryCloseEvent event) {
		Inventory inv = event.getInventory();
		Player play = (Player) event.getPlayer();
		String rdummyg = GuiName_Raw;
		rdummyg = rdummyg.replaceAll("%OWNER%", "");
		if (inv.getTitle().contains(rdummyg)) {
			TheBox box = storages.getBox(play);
			if (box == null) {
				return;
			}
			Utility.sendActionBar(play, ItemsKept);
			Utility.PlaySoundAt(play.getWorld(), play.getLocation(), Sound.BLOCK_CHEST_CLOSE, 0.5F, 0.7F);
			Utility.PlaySound(play, Sound.BLOCK_GLASS_STEP, 2F, 1.4F);
			storages.SaveBox(play, box);
		}
	}
	@SuppressWarnings("unused")
	public void ManageBoxClicking(InventoryClickEvent event) {
		Player p = (Player) event.getWhoClicked();
		String pname = p.getName();
		Inventory inv = event.getInventory();
		if (inv.getType().equals(InventoryType.ANVIL)) {
			try {
			if (event.getSlot() != 2 || inv.getItem(0).getItemMeta().getDisplayName() == null) {
				return;
			}
			} catch (Exception e) {
			}
			String displaynm = inv.getItem(0).getItemMeta().getDisplayName();
			if (displaynm.contains(BoxItemName)) {
					event.setCancelled(true);
					Utility.PlaySound(p, Sound.BLOCK_ANVIL_BREAK, 1F, 0F);
					Utility.PlaySound(p, Sound.BLOCK_GLASS_BREAK, 1F, 0F);
					Utility.PlaySound(p, Sound.BLOCK_NOTE_BASEDRUM, 1F, 0F);
					return;
			}
		}
		int Slot = event.getSlot();
		UUID uuid = p.getUniqueId();
		String title = inv.getTitle();
		InventoryHolder invh = inv.getHolder();
		if (!storages.isBoxInventory(inv)) {
			return;
		}
		TheBox box = null;
		Boolean other = false;
		if (!title.contains(pname)) {
			GUIHolder paar = ((GUIHolder) inv.getHolder());
			OfflinePlayer paa = paar.getPlayer();
			UUID uuidx = paa.getUniqueId();
			box = storages.getBox(uuidx);
			if (box == null) {
				event.setCancelled(true);
				p.closeInventory();
				return;
			}
			other = true;
			uuid = uuidx;
		}
		if (title.contains(pname)) {
			box = storages.getBox(uuid);
		}
		ItemStack currentitem = event.getCurrentItem();
		if (currentitem == null) {
			return;
		}
		if (items.contains(currentitem)) {
			event.setCancelled(true);
			return;
		}
		if (!currentitem.equals(storages.getShipItem()) && !currentitem.equals(storages.getKeepItem())) {
			return;
		}
		if (Slot == shipslot || currentitem.equals(storages.getShipItem())) {
			if (box == null) {
				event.setCancelled(true);
				p.closeInventory();
				return;
			}
			event.setCancelled(true);
			p.closeInventory();
			if (other) {
				box.Ship(p);
				return;
			}
			box.Ship();
			return;
			/*
			 * Do something to Ship the Items
			 * First set the Time and Runnable.
			 * Wait until certain hour.
			 * If time reached.
			 * Sell items
			 * and Message to Player
			 * Money w
			 */
		}
		if (Slot == keepslot || currentitem.equals(storages.getKeepItem())) {
			if (box == null) {
				event.setCancelled(true);
				p.closeInventory();
				return;
			}
			event.setCancelled(true);
			p.closeInventory();
			Utility.sendActionBar(p, ItemsKept);
			Utility.PlaySound(p, Sound.BLOCK_CHEST_CLOSE, 0.5F, 0.7F);
			storages.SaveBox(uuid, box);
		}
	}
	public void ManagePlayerQuit(PlayerQuitEvent e) {
		Player player = e.getPlayer();
		UUID uuid = player.getUniqueId();
		if (storages.getBox(uuid) == null) {
			return;
		}
		TheBox box = storages.getBox(uuid);
		SaveFile(uuid, box);
		storages.ClearBox(uuid);
	}
	public void ManagePlayerJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		UUID uuid = p.getUniqueId();
		Storage storages = Storage.getInstance();

		File file = new File(maininst.getDataFolder() + File.separator + "playerdata" + File.separator + uuid + ".yml");
		if (file.exists()) {
			YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
			Long cooldowns = yaml.getLong("cooldown");
			Location location = (Location) yaml.get("Location");
			Long cd = new Long(cooldowns);
			TheBox box = new TheBox(cd, p, location);
			box.setAllAccessFromString(yaml.getStringList("accessed"));
			Inventory dinv = box.getInsideBox();
			for (int i = 0; i < storages.getGuiSize(); i++) {
			ItemStack dummy = yaml.getItemStack("Items." + i);
			if (dummy == null) continue;
			dinv.setItem(i, dummy);
			}
			box.setInsideBox(dinv);
			Boolean isship = (Boolean) yaml.get("isShip");
			box.setCooldown(cooldowns);
			box.setShipping(isship);
			storages.SaveBox(uuid, box);
		}
	}
	private void SaveFile(UUID uuid, TheBox box) {
		myplayerdata.saveFile(uuid, box);
	}
}
