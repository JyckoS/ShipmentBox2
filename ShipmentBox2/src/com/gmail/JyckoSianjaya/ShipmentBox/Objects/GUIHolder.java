package com.gmail.JyckoSianjaya.ShipmentBox.Objects;

import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class GUIHolder implements InventoryHolder {
	private Inventory inv;
	private OfflinePlayer p;
	public GUIHolder(OfflinePlayer p) {
		this.p = p;
	}
	@Override
	public Inventory getInventory() {
		// TODO Auto-generated method stub
		return inv;
	}
	public OfflinePlayer getPlayer() {
		return p;
	}
	

}
