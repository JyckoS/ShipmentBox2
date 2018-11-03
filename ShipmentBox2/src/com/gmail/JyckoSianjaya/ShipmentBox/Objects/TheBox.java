package com.gmail.JyckoSianjaya.ShipmentBox.Objects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;


import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import com.gmail.JyckoSianjaya.ShipmentBox.ShipmentBox;
import com.gmail.JyckoSianjaya.ShipmentBox.Utils.PlayerData;
import com.gmail.JyckoSianjaya.ShipmentBox.Utils.Storage;
import com.gmail.JyckoSianjaya.ShipmentBox.Utils.Timer;
import com.gmail.JyckoSianjaya.ShipmentBox.Utils.Utility;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import net.milkbowl.vault.economy.Economy;

public class TheBox {
	private Storage storages = Storage.getInstance();
	private Inventory insidethebox;
	private Long currentCooldown;
	private OfflinePlayer theowner;
	private UUID owneruuid;
	private Block boxblock;
	private Hologram hologram = null;
	private Boolean isOnShip;
	private Location boxlocation;
	private List<String> holomsgs;
	private String ownername;
	private Economy economy = ShipmentBox.getEconomy();
	private Long defaultcd = storages.getCooldown();
	private List<UUID> accessed = new ArrayList<UUID>();
	private Collection<ItemStack> neutrals = storages.getNeutralItems();
	private PlayerData pdata = PlayerData.getInstance();
	public TheBox(Long Cooldown, Player player, Location loc) {
		isOnShip = false;
		InitializeBox(player);
		UUID uuid = player.getUniqueId();
		owneruuid = uuid;
		theowner = player;
		boxlocation = loc;
		ownername = theowner.getName();
		Location newloc = loc.clone();
		newloc.add(0.0, -1.0, 0.0);
		this.boxblock = newloc.getBlock();
		if (!storages.hasHologram(owneruuid)) {
		storages.SaveBox(uuid, this);
		hologram = HologramsAPI.createHologram(ShipmentBox.getInstance(), boxlocation);
		List<String> hologrammsgs = storages.getHologramMessages();
		setHologramMessages(hologrammsgs);
		}
		else {
		hologram = storages.getHologram(owneruuid);
		}
		if (storages.getCacheCooldown(player) != null) {
			this.currentCooldown = storages.getCacheCooldown(player);
		}
		pdata.saveLocation(this);
	}
	public TheBox(Long cd, OfflinePlayer p, Location location) {
		// TODO Auto-generated constructor stub
		isOnShip = false;
		InitializeBox(p);
		UUID uuid = p.getUniqueId();
		owneruuid = uuid;
		theowner = p;
		boxlocation = location;
		Location newloc = location.clone();
		newloc.add(0.0, -1.0, 0.0);
		this.boxblock = newloc.getBlock();
		if (!storages.hasHologram(owneruuid)) {
		storages.SaveBox(uuid, this);
		hologram = HologramsAPI.createHologram(ShipmentBox.getInstance(), boxlocation);
		List<String> hologrammsgs = storages.getHologramMessages();
		setHologramMessages(hologrammsgs);
		}
		else {
		hologram = storages.getHologram(owneruuid);
		}
		if (storages.getCacheCooldown(p) != null) {
			this.currentCooldown = storages.getCacheCooldown(p);
		}
		pdata.saveLocation(this);
	}
	public void OpenBox() {
		((HumanEntity) theowner).openInventory(insidethebox);
	}
	public void OpenBox(Player player) {
		player.openInventory(insidethebox);
	}
	public void deleteBox() {
		killHologram();
		List<HumanEntity> viewers = insidethebox.getViewers();
		ArrayList<HumanEntity> newa = new ArrayList<>(viewers);
		for (HumanEntity pp : newa) {
			pp.closeInventory();
		}
		boxblock.setType(Material.AIR);
		boxblock.breakNaturally();
		storages.ClearBox(owneruuid);
		storages.removeHologram(owneruuid);
		World world = boxblock.getWorld();
		if (storages.doesDropItems()) {
			for (ItemStack item : insidethebox.getStorageContents()) {
				if (item == null) {
					continue;
				}
				if (item.equals(storages.getKeepItem()) || item.equals(storages.getShipItem())) {
					continue;
				}
				if (neutrals.contains(item)) {
					continue;
				}
				world.dropItem(boxlocation, item);
			}
		}
		if (storages.doesDropBoxItem()) {
			ItemStack item = storages.GetBoxItem(ownername);
			if (!theowner.isOnline()) {
				world.dropItem(boxlocation, item);
			}
			else {
				Player p = (Player) theowner;
				Inventory inv = p.getInventory();
				inv.addItem(item);
				if (!inv.contains(item)) {
					world.dropItem(p.getLocation(), item);
				}
			}
		}
	}
	public void Ship(Player p) {
		if (!ShipAllowed()) {
			for (String ga : storages.getCantShip()) {
				Utility.sendMsg(p, ga);
			}
			return;
		}
		Long cooldownaa = storages.getCooldown();
		Long currenttimemil = System.currentTimeMillis();
		Long currentdiff = currenttimemil - currentCooldown;
		if (currentdiff < cooldownaa) {
			String cd = storages.getABOncooldown();
			Long currentcoold = (cooldownaa - currentdiff) / 1000;
			int currentint = Integer.parseInt("" + currentcoold);
			cd = cd.replaceAll("%COOLDOWN%", "" + Utility.normalizeTime(currentint));
			Utility.sendActionBar(p, cd);
			Utility.PlaySound(p, Sound.ENTITY_VILLAGER_NO, 2.0F, 0.9F);
			return;
		}
		ItemStack[] st = insidethebox.getContents();
		ArrayList<ItemStack> allitems = new ArrayList<ItemStack>();
		for (ItemStack current : st) {
			if (current != null) {
			allitems.add(current);
			}
		}
		allitems.remove(storages.getKeepItem());
		allitems.remove(storages.getShipItem());
		if (allitems.isEmpty()) {
			for (String stee : storages.getNoItemsmsg()) {
			Utility.sendMsg(p, stee);
			}
			Utility.sendActionBar(p, storages.getABnoitems());
			Utility.PlaySound(p, Sound.ENTITY_VILLAGER_TRADING, 2F, 0.85F);
			return;
		}
		if (currentdiff > cooldownaa) {
			ArrayList<ItemStack> unsellable = new ArrayList<ItemStack>();
			Double total = 0.0;
			int iamount = 0;
			for (ItemStack neu : neutrals) {
				allitems.remove(neu);
			}
			for (ItemStack item : allitems) {
				String material = item.getType().toString();
				if (item.equals(storages.getKeepItem()) || item.equals(storages.getShipItem())) {
					continue;
				}
				if (!storages.hasPrice(material)) {
					unsellable.add(item);
					continue;
				}
				final int iamounta = item.getAmount();
				Double price = storages.getPrice(material) * iamounta;
				total += price;
				iamount += iamounta;
			}
			for (ItemStack item : unsellable) {
				allitems.remove(item);
			}
			if (allitems.isEmpty()) {
				for (String stee : storages.getUnsellable()) {
				Utility.sendMsg(p, stee);
				}
				Utility.sendActionBar(p, storages.getABnoitems());
				Utility.PlaySound(p, Sound.ENTITY_VILLAGER_TRADING, 2F, 0.75F);
				return;
			}
			if (iamount < storages.getMinItems()) {
				for (String notenuff : storages.getNotEnoughItems()) {
					Utility.sendMsg(p, notenuff);
				}
				return;
			}
			if (total <= 0.0) {
				for (String stee : storages.getUnsellable()) {
				Utility.sendMsg(p, stee);
				}
				Utility.sendActionBar(p, storages.getABnoitems());
				Utility.PlaySound(p, Sound.ENTITY_VILLAGER_TRADING, 2F, 0.75F);
				return;
			}
			final Boolean tax = storages.getTaxType();
			Double finaletotal = total;
			finaletotal = (tax == true ? finaletotal * (100 - storages.getTaxCost()) / 100 : finaletotal - storages.getTaxCost());
			economy.depositPlayer(theowner, finaletotal);
			Utility.sendActionBar(p, storages.getABShipOthers().replaceAll("%TOTAL%", "" + finaletotal));
			if (isOwnerOnline()) {
			Utility.sendActionBar((Player) theowner, storages.getABReceiveShip().replaceAll("%TOTAL%", "" + finaletotal));
			}
			Utility.PlaySound(p, Sound.BLOCK_LAVA_POP, 2F, 1.65F);
			Utility.PlaySound(p, Sound.BLOCK_GRASS_PLACE, 2F, 2F);
			Utility.PlaySound(p, Sound.ENTITY_ARROW_HIT_PLAYER, 2F, 1.5F);
			if (isOwnerOnline()) {
			insidethebox = InitializeInventory((Player) theowner);
			}
			else {
				insidethebox = InitializeInventory(theowner);

			}
			for (ItemStack unsell : unsellable) {
			insidethebox.addItem(unsell);
			}
			String actbar = storages.getABgetShipMsg();
			actbar = actbar.replace("%TOTAL%", String.valueOf(finaletotal));
			Utility.sendActionBar(p, actbar);
			currentCooldown = System.currentTimeMillis();
			for (String gg : storages.getOtherShip()) {
				Utility.sendMsg(p, gg.replaceAll("%OWNER%", ownername).replaceAll("%TOTAL%", "" + finaletotal));
			}
			if (isOwnerOnline()) {
				Player pada = (Player) theowner;
			for (String spigot : storages.getOtherShipReceive()) {
				Utility.sendMsg(pada, spigot.replaceAll("%SHIPPER%", p.getName()).replaceAll("%TOTAL%", "" + finaletotal));
			}
			}
		}
		storages.SaveBox(owneruuid, this);
	}
	final public boolean isOwnerOnline() {
		return theowner.isOnline();
	}
	final public boolean ShipAllowed() {
		Timer time = Timer.getInstance();
		int chour = time.getHours();
		if (chour == 0) {
			chour = 24;
		}
		if (chour < storages.getMinHours()) {
			return false;
		}
		if (chour > storages.getMaxHours()) {
			return false;
		}
		return true;
	}
	public void Ship() {
		Player theowner = (Player) this.theowner;
		if (!ShipAllowed()) {
			for (String str : storages.getCantShip()) {
				Utility.sendMsg(theowner, str);
			}
			return;
		}
		Long cooldownaa = storages.getCooldown();
		Long currenttimemil = System.currentTimeMillis();
		Long currentdiff = currenttimemil - currentCooldown;
		if (currentdiff < cooldownaa) {
			String cd = storages.getABOncooldown();
			Long currentcoold = (cooldownaa - currentdiff) / 1000;
			int currentint = Integer.parseInt("" + currentcoold);
			cd = cd.replaceAll("%COOLDOWN%", "" + Utility.normalizeTime(currentint));
			Utility.sendActionBar(theowner, cd);
			Utility.PlaySound(theowner, Sound.ENTITY_VILLAGER_NO, 2.0F, 0.9F);
			return;
		}
		ItemStack[] st = insidethebox.getContents();
		ArrayList<ItemStack> allitems = new ArrayList<ItemStack>();
		for (ItemStack current : st) {
			if (current != null) {
			allitems.add(current);
			}
		}
		allitems.remove(storages.getKeepItem());
		allitems.remove(storages.getShipItem());
		if (allitems.isEmpty()) {
			for (String stee : storages.getNoItemsmsg()) {
			Utility.sendMsg(theowner, stee);
			}
			Utility.sendActionBar(theowner, storages.getABnoitems());
			Utility.PlaySound(theowner, Sound.ENTITY_VILLAGER_TRADING, 2F, 0.85F);
			return;
		}
		if (currentdiff > cooldownaa) {
			ArrayList<ItemStack> unsellable = new ArrayList<ItemStack>();
			Double total = 0.0;
			int iamount = 0;
			for (ItemStack neu : neutrals) {
				allitems.remove(neu);
			}
			for (ItemStack item : allitems) {
				String material = item.getType().toString();
				if (item.equals(storages.getKeepItem()) || item.equals(storages.getShipItem())) {
					continue;
				}
				if (!storages.hasPrice(material)) {
					unsellable.add(item);
					continue;
				}
				final int itemamount = item.getAmount();
				Double price = storages.getPrice(material) * itemamount;
				total += price;
				iamount += itemamount;
			}
			for (ItemStack item : unsellable) {
				allitems.remove(item);
			}
			if (allitems.isEmpty()) {
				for (String stee : storages.getUnsellable()) {
				Utility.sendMsg(theowner, stee);
				}
				Utility.sendActionBar(theowner, storages.getABnoitems());
				Utility.PlaySound(theowner, Sound.ENTITY_VILLAGER_TRADING, 2F, 0.75F);
				return;
			}
			if (iamount < storages.getMinItems()) {
				for (String notenuff : storages.getNotEnoughItems()) {
					Utility.sendMsg(theowner, notenuff);
				}
				return;
			}
			if (total <= 0.0) {
				for (String stee : storages.getUnsellable()) {
				Utility.sendMsg(theowner, stee);
				}
				Utility.sendActionBar(theowner, storages.getABnoitems());
				Utility.PlaySound(theowner, Sound.ENTITY_VILLAGER_TRADING, 2F, 0.75F);
				return;
			}
			Boolean tax = storages.getTaxType();
			Double finaletotal = total;
			finaletotal = (tax == true ? finaletotal * (100 - storages.getTaxCost()) / 100 : finaletotal - storages.getTaxCost());
			economy.depositPlayer(theowner, finaletotal);
			Utility.PlaySound(theowner, Sound.BLOCK_LAVA_POP, 2F, 1.65F);
			Utility.PlaySound(theowner, Sound.BLOCK_GRASS_PLACE, 2F, 2F);
			Utility.PlaySound(theowner, Sound.ENTITY_ARROW_HIT_PLAYER, 2F, 1.5F);
			insidethebox = InitializeInventory((Player) theowner);
			for (ItemStack unsell : unsellable) {
			insidethebox.addItem(unsell);
			}
			for (String ttr : storages.getListStringShipMsg()) {
				ttr = ttr.replaceAll("%TOTAL%", String.valueOf(finaletotal));
				Utility.sendMsg(theowner, ttr);
			}
			String actbar = storages.getABgetShipMsg();
			actbar = actbar.replace("%TOTAL%", String.valueOf(finaletotal));
			Utility.sendActionBar(theowner, actbar);
			currentCooldown = System.currentTimeMillis();
		}
		storages.SaveBox(owneruuid, this);
	}
	private void InitializeBox(Player player) {
		if (currentCooldown == null) {
			currentCooldown = defaultcd;
		}
		insidethebox = InitializeInventory(player);
	}
	private void InitializeBox(OfflinePlayer player) {
		if (currentCooldown == null) {
			currentCooldown = defaultcd;
		}
		insidethebox = InitializeInventory(player);
	}
	private Inventory InitializeInventory(Player p) {
		Inventory inventorydummy = Bukkit.createInventory(new GUIHolder(p), 36, storages.getGuiName().replaceAll("%OWNER%", p.getName()));
		inventorydummy.setItem(storages.getShipSlot(), storages.getShipItem());
		inventorydummy.setItem(storages.getKeepSlot(), storages.getKeepItem());
		Set<Integer> integers = storages.getGuiNeutralSlots();
		for (int a : integers) {
			ItemStack aitem = storages.getNeutral(a);
			
			if (aitem == null) {
				continue;
			}
			inventorydummy.setItem(a, aitem);
		}
		return inventorydummy;
	}
	private Inventory InitializeInventory(OfflinePlayer p) {
		Inventory inventorydummy = Bukkit.createInventory(new GUIHolder(p), 36, storages.getGuiName().replaceAll("%OWNER%", p.getName()));
		inventorydummy.setItem(storages.getShipSlot(), storages.getShipItem());
		inventorydummy.setItem(storages.getKeepSlot(), storages.getKeepItem());
		Set<Integer> integers = storages.getGuiNeutralSlots();
		for (int a : integers) {
			ItemStack aitem = storages.getNeutral(a);
			
			if (aitem == null) {
				continue;
			}
			inventorydummy.setItem(a, aitem);
		}
		return inventorydummy;
	}
/*
 * Start from here will have methods to return all vars
 */
	public List<UUID> getAccessed() {
		return accessed;
	}
	public void setAllAccess(List<UUID> rar) {
		accessed = rar;
	}
	public void setAllAccessFromString(List<String> rarx) {
		accessed.clear();
		for (String str : rarx) {
			UUID uuid = UUID.fromString(str);
			accessed.add(uuid);
		}
	}
	public void setAccess(UUID uuid) {
		if (accessed.contains(uuid) ) {
			return;
		}
		accessed.add(uuid);
	}
	public void removeAccess(UUID uuid) {
		if (!accessed.contains(uuid)) {
			return;
		}
		accessed.remove(uuid);
	}
	public boolean isAccessed(UUID uuid) {
		return accessed.contains(uuid);
	}
	public Block getBlock() {
		return boxblock;
	}
	public boolean HasHologram() {
		if (hologram.isDeleted()) {
			return false;
		}
		if (hologram == null) {
			return false;
		}
		return true;
	}
	public void killHologram() {
		if (hologram == null) {
			return;
		}
		hologram.delete();
	}
	public void setDefaultCD() {
		defaultcd = storages.getCooldown();
	}
	public Location getBoxLocation() {
		return boxlocation;
	}
	public void setLocation(Location loc) {
		boxlocation = loc;
	}
	public Long getCurrentCooldown() {
		return currentCooldown;
	}
	public UUID getOwnerUUID() {
		return owneruuid;
	}
	public Boolean IsShipping() {
		return isOnShip;
	}
	public List<String> getHologramMsgs() {
		return holomsgs;
	}
	public void setHologramMessages(List<String> messages) {
		if (hologram == null) {
			hologram = HologramsAPI.createHologram(ShipmentBox.getInstance(), boxlocation);
		}
		hologram.clearLines();
		int cline = 0;
		ArrayList<String> dummy = new ArrayList<String>();
		for (String string : messages) {
			string = string.replaceAll("%OWNER%", ownername);
			dummy.add(string);
			hologram.insertTextLine(cline, string);
			cline++;
		}
		holomsgs = dummy;
		Location newloc = boxblock.getLocation();
		Double current = 1.5 + messages.size() * 0.18;
		newloc = newloc.add(0.5, current, 0.5);
		hologram.teleport(newloc);
		storages.setHologram(owneruuid, hologram);
	}
	public OfflinePlayer getOwner() {
		if (!isOwnerOnline()) {
			return theowner;
		}
		return (Player) theowner;
	}
	public Inventory getInsideBox() {
		return insidethebox;
	}
	public void setInsideBox(Inventory inv) {
		insidethebox = inv;
	}
	public void setOwner(Player p) {
		theowner = p;
	}
	public void setUUID(UUID uuid) {
		owneruuid = uuid;
	}
	public void setCooldown(Long cooldown) {
		currentCooldown = cooldown;
	}
	public void setShipping(Boolean isShip) {
		isOnShip = isShip;
	}
}
