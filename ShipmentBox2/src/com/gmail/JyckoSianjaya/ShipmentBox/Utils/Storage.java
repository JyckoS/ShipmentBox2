package com.gmail.JyckoSianjaya.ShipmentBox.Utils;

import java.io.File;


import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.JyckoSianjaya.ShipmentBox.ShipmentBox;
import com.gmail.JyckoSianjaya.ShipmentBox.Objects.TheBox;
import com.gmail.filoghost.holographicdisplays.api.Hologram;


public class Storage {
	/*
	 * 
	 *  THIS CLASS STORES MOST OF DATA IN THE .YML files.
	 *   SINGLETON CLASS
	 * 
	 */
	private int maxaccess;
	private int minhour;
	private int maxhour;
	private int minimumitems;
	private Boolean dropitems;
	private Boolean dropbox;
	private Boolean taxtype;
	private Double taxcost;
	private HashMap<Hologram, Integer> livetick = new HashMap<Hologram, Integer>();
	private java.util.List<String> HelpMsg;
	private List<String> KeepLores, ShipLores, Lores, TimeMsg;
	private String Name, KeepName, ShipName, GUIname;
	private File worth;
	private Set<String> worths;
	private HashMap<String, Double> cost = new HashMap<String, Double>();
	private HashMap<UUID, Long> cachecooldown = new HashMap<UUID, Long>();
	private static Storage instance;
	private long Cooldown;
	private ArrayList<Inventory> cacheinventory = new ArrayList<Inventory>();
	private HashMap<UUID, TheBox> Boxes = new HashMap<UUID, TheBox>();
	private HashMap<UUID, Hologram> BoxHolos = new HashMap<UUID, Hologram>();
	private HashMap<Location, TheBox> BoxLocation = new HashMap<Location, TheBox>();
	private ItemStack BoxItem;
	private ItemMeta BoxMeta;
	private ItemStack KeepItem;
	private ItemMeta KeepMeta;
	private Integer ShipSlot;
	private Integer KeepSlot;
	private ItemStack ShipItem;
	private ItemMeta ShipMeta;
	private Material BoxMat;
	private Material KeepMat;
	private Material ShipMat;
	private int guisize;
	private HashMap<Integer, ItemStack> neutrals = new HashMap<Integer, ItemStack>();
	// Messages to be use in EventHandlers, etc.
	// From here is Action bar messages
	private String thereisbox;
	private String nobox;
	private String nothisbox;
	private String boxplaced;
	private String boxdestroyed;
	private String alreadyownbox;
	private String itemkept;
	private String oncooldown;
	private String itemshipab;
	private String noitemsab;
	private String shipothers;
	private String receiveship;
	
	// From here is Text Messages
	private List<String> HologramMessages;
	private String isOffline;
	private String noboxmsg;
	private String hedonthavebox;
	private List<String> nosellable;
	private List<String> itemsgonemsg;
	private List<String> statusmsg;
	private String cancelExplosionMsg;
	private String nopermission;
	private List<String> itemshipmsg;
	private List<String> noitems;
	private List<String> othershipped;
	private List<String> othershippedreceive;
	private List<String> notenoughitems;
	private List<String> cantshipyet;
	// Access messages
	private String accesshelp;
	private List<String> accesslist;
	private String accessadd;
	private String accessremove;
	private String noperson;
	private String noneedaccess;
	private String accessaddhelp;
	private String accessremovehelp;
	
	// From here is the Title Messages
	private String title_teleport;
	private String subtitle_teleport;
	
	// From here is the GUI Things
	
	//From here is the Access Menu
	
	private ZoneId TimeZonex;
	private Storage() {
		resetConfig();
		LoadItems();
		RunnableHolograms();
	}
	public static Storage getInstance() {
		if (instance == null) {
			instance = new Storage();
		}
		return instance;
	}
	public void LoadItems() {
		// Create the Items
		BoxItem = new ItemStack(BoxMat);
		KeepItem = new ItemStack(KeepMat);
		ShipItem = new ItemStack(ShipMat);
		// Set the Meta Data 1 by 1 
		BoxMeta = BoxItem.getItemMeta();
		BoxMeta.setLore(Lores);
		BoxMeta.setDisplayName(Name);
		//
		KeepMeta = KeepItem.getItemMeta();
		KeepMeta.setLore(KeepLores);
		KeepMeta.setDisplayName(KeepName);
		//
		ShipMeta = ShipItem.getItemMeta();
		ShipMeta.setDisplayName(ShipName);
		ShipMeta.setLore(ShipLores);
		// Set The Items Meta Datas.
		BoxItem.setItemMeta(BoxMeta);
		KeepItem.setItemMeta(KeepMeta);
		ShipItem.setItemMeta(ShipMeta);
	}
	@SuppressWarnings("deprecation")
	public void resetConfig() {
		try {
			//
			// Load The Main Class & The Files
			//
			ShipmentBox myShipmentbox = ShipmentBox.getInstance();
			myShipmentbox.reloadConfig();
			FileConfiguration config = myShipmentbox.getConfig();
			File messages = new File(myShipmentbox.getDataFolder(), "messages" + ".yml");
			neutrals.clear();
		YamlConfiguration msgyml = YamlConfiguration.loadConfiguration(messages);
		//
		// End Loading
		// Start Getting value from config.yml
		//
		maxaccess = config.getInt("Options.maximum_trustees");
		/**
		 *
		 * From here starts getting the Menu stuff
		 * 
		 */

			  /**
			   * @Loads
			   * the neutrals items
			   */
		//
		dropitems = config.getBoolean("Options.drop_items_on_break");
		dropbox = config.getBoolean("Options.drop_boxitem_on_break");
		taxtype = config.getBoolean("Options.shipping_cost.tax_in_percentage");
		taxcost = config.getDouble("Options.shipping_cost.tax_cost");
		minhour = config.getInt("Options.minimum_ship_hour");
		maxhour = config.getInt("Options.maximum_ship_hour");
		minimumitems = config.getInt("Options.minimum_ship_items");
		//
		BoxMat = Material.valueOf(Utility.TransColor(config.getString("Item.shipmentbox.Material")));
		KeepMat = Material.valueOf(Utility.TransColor(config.getString("In_GUI.keep.Material")));
		ShipMat = Material.valueOf(Utility.TransColor(config.getString("In_GUI.ship.Material")));
	Lores = Utility.TransColor(config.getStringList("Item.shipmentbox.Lores"));
	KeepLores = Utility.TransColor(config.getStringList("In_GUI.keep.Lores"));
	ShipLores = Utility.TransColor(config.getStringList("In_GUI.ship.Lores"));
	Name = Utility.TransColor(config.getString("Item.shipmentbox.Name"));
	KeepName = Utility.TransColor(config.getString("In_GUI.keep.Name"));
	ShipName = Utility.TransColor(config.getString("In_GUI.ship.Name"));
	GUIname = Utility.TransColor(config.getString("In_GUI.GUI_Name"));
	Cooldown = config.getLong("ship-cooldown") * 1000;
	guisize = config.getInt("In_GUI.GUI_Size");
	ConfigurationSection neutrals = config.getConfigurationSection("In_GUI.Neutral_Item");
	Set<String> keys = neutrals.getKeys(false);
	for (String key : keys) {
		ItemStack item = new ItemStack(Material.valueOf(config.getString("In_GUI.Neutral_Item." + key + ".Material")));
		ItemMeta metar = item.getItemMeta();
		metar.setDisplayName(Utility.TransColor(config.getString("In_GUI.Neutral_Item." + key + ".Name")));
		List<String> lorax = Utility.TransColor(config.getStringList("In_GUI.Neutral_Item." + key + ".Lores"));
		metar.setLore(lorax);
		item.setDurability(Short.valueOf("" + config.getInt("In_GUI.Neutral_Item." + key + ".Block_Value")));
		item.setItemMeta(metar);
		this.neutrals.put(config.getInt("In_GUI.Neutral_Item." + key + ".Slot"), item);
		}
	
	//
	// End
	//
	// Starts to load Messages.yml things.
	/*
	 * You can do Title_Subtitle.(MessageType).<Title/Subtitle>
	 * Or ActionBar.(MessageType)
	 */
	//
	title_teleport = Utility.TransColor(msgyml.getString("Titles.Teleport.title"));
	subtitle_teleport = Utility.TransColor(msgyml.getString("Titles.Teleport.subtitle"));

	shipothers = Utility.TransColor(msgyml.getString("ActionBar.others_ship"));
	receiveship = Utility.TransColor(msgyml.getString("ActionBar.ship_receive_other"));
	notenoughitems = Utility.TransColor(msgyml.getStringList("not_enough_items"));
	accesslist = Utility.TransColor(msgyml.getStringList("box_access_list"));
	accesshelp = Utility.TransColor(msgyml.getString("box_access_help"));
	accessaddhelp = Utility.TransColor(msgyml.getString("box_access_add"));
	accessremovehelp = Utility.TransColor(msgyml.getString("box_access_remove"));
	noperson = Utility.TransColor(msgyml.getString("person_doesnt_exist"));
	noneedaccess = Utility.TransColor(msgyml.getString("person_access_less"));
	accessadd = Utility.TransColor(msgyml.getString("box_accessed"));
	accessremove = Utility.TransColor(msgyml.getString("box_unaccessed"));
	othershipped = Utility.TransColor(msgyml.getStringList("item_shipped_others"));
	othershippedreceive = Utility.TransColor(msgyml.getStringList("ship_received_others"));
	HologramMessages = Utility.TransColor(msgyml.getStringList("hologram_messages"));
	thereisbox = Utility.TransColor(msgyml.getString("ActionBar.box_nearby"));
	noboxmsg = Utility.TransColor(msgyml.getString("have_no_box"));
	hedonthavebox = Utility.TransColor(msgyml.getString("target_no_box"));
	nosellable = Utility.TransColor(msgyml.getStringList("nothing_sellable"));
	isOffline = Utility.TransColor(msgyml.getString("player_offline"));
	statusmsg = Utility.TransColor(msgyml.getStringList("box_status"));
	TimeMsg = Utility.TransColor(msgyml.getStringList("time_messages"));
	nopermission = Utility.TransColor(msgyml.getString("no_permission"));
	cantshipyet = Utility.TransColor(msgyml.getStringList("cant_ship_yet"));
	nobox = Utility.TransColor(msgyml.getString("ActionBar.no_box"));
	boxplaced = Utility.TransColor(msgyml.getString("ActionBar.box_placed"));
	boxdestroyed = Utility.TransColor(msgyml.getString("ActionBar.box_destroyed"));
	alreadyownbox = Utility.TransColor(msgyml.getString("ActionBar.already_have_box"));
	itemsgonemsg = Utility.TransColor(msgyml.getStringList("items_gone"));
	cancelExplosionMsg = Utility.TransColor(msgyml.getString("cancel_explosion"));
	HelpMsg = Utility.TransColor(msgyml.getStringList("help_messages"));
	nothisbox = Utility.TransColor(msgyml.getString("ActionBar.not_its_box"));
	oncooldown = Utility.TransColor(msgyml.getString("ActionBar.on_cooldown"));
	itemshipab = Utility.TransColor(msgyml.getString("ActionBar.item_shipped"));
	itemkept = Utility.TransColor(msgyml.getString("ActionBar.box_item_kept"));
	itemshipmsg = Utility.TransColor(msgyml.getStringList("item_shipped"));
	noitemsab = Utility.TransColor(msgyml.getString("ActionBar.no_items"));
	noitems = Utility.TransColor(msgyml.getStringList("no_items"));
	//
	// Ending Messages.yml loading
	// Start loading config.yml's TimeZone
	//
	TimeZonex = ZoneId.of(config.getString("TIMEZONE_ID"));
	ShipSlot = config.getInt("In_GUI.ship.Slot");
	KeepSlot = config.getInt("In_GUI.keep.Slot");
	//
	// End
	// Start to load Prices.yml
	//
	worth = new File(myShipmentbox.getDataFolder() + File.separator + "prices" + ".yml");
	YamlConfiguration yaml = YamlConfiguration.loadConfiguration(worth);
	ConfigurationSection prices = yaml.getConfigurationSection("Prices");
	yaml.options().copyDefaults(true);
	// Getting the cost & items prices
	worths = prices.getKeys(false);
	cost.clear();
	Utility.sendConsole("&b&m------&a&lShipmentBox&b&m------");
	for (String st : worths) {
		if (Material.getMaterial(st) == null) {
			Utility.sendConsole("&cERROR ITEM: &f" + st + "&c, from &fprices.yml&c. &7(It's not a valid Material)" );
			continue;
		}
		cost.put(st, yaml.getDouble("Prices." + st));

	}
	//
	// end
	// Start to use the results, e.g: Timers
	//
	Timer timer = Timer.getInstance();
	ZonedDateTime trex = ZonedDateTime.now(TimeZonex);
	final int chour = trex.getHour();
	final int cminutes = trex.getMinute();
	final int second = trex.getSecond();
	timer.setHours(chour);
	timer.setMinutes(cminutes);
	timer.setSeconds(second);
	LoadItems();
	/*
	 * 
	 * DEBUGGING SECTIOn
	 * 
	 */
	// END OF DEBUG
	for (TheBox box : Boxes.values()) {
		box.setDefaultCD();
		box.setHologramMessages(HologramMessages);
	}
		} catch (Exception e) {
			e.printStackTrace();
		Utility.sendConsole("SHIPIMENTBOX CONSOLE LOGGER > An error has occured in the config.yml.");
		Utility.sendConsole("Try to fix any mistakes, example double quotation marks, etc.");
		Utility.sendConsole("If Error still occurs, try contacting the developer (me), my name in Spigot is Gober.");
		}
	}	
	public int getMaxTrust() {
		return maxaccess;
	}
	public int getShipSlot() {
		return ShipSlot;
	}
	public int getKeepSlot() {
		return KeepSlot;
	}
	public String getTitleTeleport() {
		return title_teleport;
	}
	public String getSubTitleTeleport() {
		return subtitle_teleport;
	}
	public List<Inventory> getInventories() {
		return cacheinventory;
	}
	public Material getMaterialBoxItem() {
		return BoxMat;
	}
	public int getMinHours() {
		return minhour;
	}
	public int getMaxHours() {
		return maxhour;
	}
	public int getMinItems() {
		return minimumitems;
	}
	public String getABShipOthers() {
		return shipothers;
	}
	public String getABReceiveShip() {
		return receiveship;
	}
	public List<String> getCantShip() {
		return cantshipyet;
	}
	public Boolean doesDropItems() {
		return dropitems;
	}
	public Boolean doesDropBoxItem() {
		return dropbox;
	}
	public List<String> getNotEnoughItems() {
		return notenoughitems;
	}
	public Boolean getTaxType() {
		return taxtype;
	}
	public Double getTaxCost() {
		return taxcost;
	}
	public List<String> getOtherShip() {
		return othershipped;
	}
	public List<String> getOtherShipReceive() {
		return othershippedreceive;
	}
	public boolean isBoxInventory(final Inventory inv) {
		return cacheinventory.contains(inv);
	}
	private void UpdateMessages() {
		ShipmentBox maininstance = ShipmentBox.getInstance();
		File file = new File(maininstance.getDataFolder() + File.separator + "messages" + ".yml");
		File originalfile = new File(maininstance.getClass().getClassLoader().getResource("messages.yml").getFile());
		YamlConfiguration mainfile = YamlConfiguration.loadConfiguration(file);
		YamlConfiguration originalyml = YamlConfiguration.loadConfiguration(originalfile);
		Set<String> keys = originalyml.getKeys(false);
		Set<String> currentkeys = mainfile.getKeys(false);
		for (String str : keys) {
			if (!currentkeys.contains(str)) {
				mainfile.set(str, originalyml.get(str));
				Utility.sendConsole("[SB] Added the section '&e" + str + "&r' to the messages.yml ");
			}
		}
		try {
			mainfile.save(file);
		} catch (IOException ex) {
			Utility.sendConsole("&cCouldn't update the messages.yml");
		}
	}
	public Set<Integer> getGuiNeutralSlots() {
		return neutrals.keySet();
	}
	public ItemStack getNeutral(final Integer slot) {
		return neutrals.get(slot);
	}
	public boolean hasHologram(UUID uuid) {
		if (BoxHolos.get(uuid) != null) {
			return true;
		}
		return false;
	}
	public void setHologram(final UUID uuid, final Hologram holo) {
		BoxHolos.put(uuid, holo);
	}
	public List<String> getAccessList() {
		return accesslist;
	}
	public int getGuiSize() {
		return guisize;
	}
	public String getAccessHelp() {
		return accesshelp;
	}
	public String getNoPerson() {
		return noperson;
	}
	public String getNoNeedAccess() {
		return noneedaccess;
	}
	public String getAccessed() {
		return accessadd;
	}
	public String getUnAccessed() {
		return accessremove;
	}
	public String getAccessAddHelp() {
		return accessaddhelp;
	}
	public String getAccessRemoveHelp() {
		return accessremovehelp;
	}
	public Hologram getHologram(final UUID uuid) {
		return BoxHolos.get(uuid);
	}
	public double getPrice(final String material) {
		return cost.get(material);
	}
	public TheBox getBoxFromLocation(final Location loc) {
		return BoxLocation.get(loc);
	}
	public void setBoxOnLocation(final Location loc, final TheBox box) {
		BoxLocation.put(loc, box);
	}
	public boolean hasPrice(final String material) {
		if (cost.get(material) == null) {
			return false;
		}
		else {
			return true;
		}
		}
	public void GiveBoxItem(final Player p, final int amount) {
		Inventory inv = p.getInventory();
		ItemStack boxdummy = BoxItem;
		ItemMeta boxmetadummy = boxdummy.getItemMeta();
		String namedummy = Name;
		namedummy = namedummy.replaceAll("%OWNER%", p.getName());
		boxmetadummy.setDisplayName(namedummy);
		boxdummy.setItemMeta(boxmetadummy);
		boxdummy.setAmount(amount);
		inv.addItem(boxdummy);
	}
	public void GiveBoxItem(final Player p) {
		Inventory inv = p.getInventory();
		ItemStack boxdummy = BoxItem.clone();
		ItemMeta boxmetadummy = boxdummy.getItemMeta();
		String namedummy = Name;
		namedummy = namedummy.replaceAll("%OWNER%", p.getName());
		boxmetadummy.setDisplayName(namedummy);
		boxdummy.setItemMeta(boxmetadummy);
		inv.addItem(boxdummy);
	}
	public ItemStack GetBoxItem(final Player p) {
		ItemStack boxdummy = BoxItem.clone();
		ItemMeta boxmetadummy = boxdummy.getItemMeta();
		String namedummy = Name;
		namedummy = namedummy.replaceAll("%OWNER%", p.getName());
		boxmetadummy.setDisplayName(namedummy);
		boxdummy.setItemMeta(boxmetadummy);
		return boxdummy;
				}
	public ItemStack GetBoxItem(final String ownername) {
		ItemStack boxdummy = BoxItem.clone();
		ItemMeta boxmetadummy = boxdummy.getItemMeta();
		String namedummy = Name;
		namedummy = namedummy.replaceAll("%OWNER%", ownername);
		boxmetadummy.setDisplayName(namedummy);
		boxdummy.setItemMeta(boxmetadummy);
		return boxdummy;
				}
	public String getABBoxNearby() {
		return thereisbox;
	}
	public String getHaveNoBox() {
		return noboxmsg;
	}
	public List<String> getHologramMessages() {
		return HologramMessages;
	}
	public Long getCacheCooldown(final Player player) {
		return cachecooldown.get(player.getUniqueId());
	}
	public Long getCacheCooldown(final OfflinePlayer player) {
		return cachecooldown.get(player.getUniqueId());
	}
	public void setCacheCooldown(final UUID uuid, final Long cooldown) {
		cachecooldown.put(uuid, cooldown);
		if (cooldown < 1) {
			cachecooldown.remove(uuid);
		}
	}
	public String getTargetNoBox() {
		return hedonthavebox;
	}
	public List<String> getUnsellable() {
		return nosellable;
	}
	public List<String> getStatusMsg() {
		return statusmsg;
	}
	public String getOfflineMsg() {
		return isOffline;
	}
	public String getABnoitems() {
		return noitemsab;
	}
	public List<String> getNoItemsmsg() {
		return noitems;
	}
	public String getABgetShipMsg() {
		return itemshipab;
	}
	public List<String> getListStringShipMsg() {
		return itemshipmsg;
	}
	public String getABOncooldown() {
		return oncooldown;
	}
	public String getMessageNoPermission() {
		return nopermission;
	}
	public String getABBoxPlaced() {
		return boxplaced;
	}
	public void removeHologram(final UUID uuid) {
		BoxHolos.remove(uuid);
	}
	public void AddHologram(final Hologram holo, final int tick) {
		livetick.put(holo, tick);
	}
	private void RunnableHolograms() {
		new BukkitRunnable() {
			@Override
			public void run() {
				for (Hologram armor : livetick.keySet()) {
				int ctime = livetick.get(armor);
				if (livetick.get(armor) < 1) {
					armor.delete();
				}
				else {
					livetick.put(armor, ctime - 20);
				}
			}
			}
		}.runTaskTimerAsynchronously(ShipmentBox.getInstance(), 20L, 20L);
	}
	public String getABBoxDestroyed() {
		return boxdestroyed;
	}
	public String getHoloExplosionCancel() {
		return cancelExplosionMsg;
	}
	public String getABNotHisBox() {
		return nothisbox;
	}
	public List<String> getMsgItemsGoneMsg() {
		return itemsgonemsg;
	}
	public String getABNoBox() {
		return nobox;
	}
	public String getABItemKept() {
		return itemkept;
	}
	public String getABAlreadyHaveBox() {
		return alreadyownbox;
	}
	public ZoneId getTimeZone() {
		return TimeZonex;
	}
	public List<String> getTimeMessage() {
		return TimeMsg;
	}
	public void SaveBox(final UUID uuid, final TheBox Box) {
		Boxes.put(uuid, Box);
		cacheinventory.remove(Box.getInsideBox());
		cacheinventory.add(Box.getInsideBox());
	}
	public void SaveBox(final Player player, final TheBox Box) {
		UUID uuid = player.getUniqueId();
		Boxes.put(uuid, Box);
		cacheinventory.remove(Box.getInsideBox());
		cacheinventory.add(Box.getInsideBox());
	}
	public Collection<ItemStack> getNeutralItems() {
		return neutrals.values();
	}
	public TheBox getBox(final UUID uuid) {
		return Boxes.get(uuid);
	}
	public TheBox getBox(final Player player) {
		return Boxes.get(player.getUniqueId());
	}
	public void ClearBox(final UUID uuid) {
		Boxes.put(uuid, null);
		Boxes.remove(uuid);
	}
	public void ClearBox(final Player player) {
		UUID uuid = player.getUniqueId();
		Boxes.put(uuid, null);
		Boxes.remove(uuid);
	}
	public Set<String> getWorths() {
		return worths;
	}
	public long getCooldown() {
		return Cooldown;
	}
	public ItemStack getKeepItem() {
		return KeepItem;
	}
	public ItemStack getShipItem() {
		return ShipItem;
	}
	public String getName() {
		return Name;
	}
	public String getKeepItemName() {
		return KeepName;
	}
	public String getShipItemName() {
		return ShipName;
	}
	public String getGuiName() {
		return GUIname;
	}
	public List<String> getHelpMsg() {
		return HelpMsg;
	}
	public List<String> getKeepLores() {
		return KeepLores;
	}
	public List<String> getShipLores() {
		return ShipLores;
	}
	public List<String> getChestLores() {
		return Lores;
	}
}
