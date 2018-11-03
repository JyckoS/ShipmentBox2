package com.gmail.JyckoSianjaya.ShipmentBox.Events;

import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;

import com.gmail.JyckoSianjaya.ShipmentBox.Objects.TheBox;
import com.gmail.JyckoSianjaya.ShipmentBox.Utils.Storage;
import com.gmail.JyckoSianjaya.ShipmentBox.Utils.Utility;

import java.util.UUID;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class Events implements Listener {
	private EventHandlers eventhand = EventHandlers.getInstance();
	@EventHandler
	public void onBoxBreak(BlockBreakEvent e) {
		eventhand.ManageBoxBreak(e);
	}
	@EventHandler
	public void onBoxPlacing(BlockPlaceEvent e) {
		eventhand.ManageBoxPlace(e);
	}
	@EventHandler
	public void onBlockExplosion(BlockExplodeEvent e) {
		eventhand.ManageBoxExplosion(e);
	}
	@EventHandler
	public void onEntityExplosion(EntityExplodeEvent e) {
		eventhand.ManageEntityExplosion(e);
	}
	@EventHandler
	public void onBoxOpen(PlayerInteractEvent e) {
		eventhand.ManageBoxOpening(e);
	}
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		eventhand.ManageBoxClicking(event);
	}
	@EventHandler
	public void onBoxClose(InventoryCloseEvent e) {
		eventhand.ManageBoxClosing(e);
	}
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		eventhand.ManagePlayerQuit(e);
	}
	@EventHandler
	public void ManagePlayerJoin(PlayerJoinEvent e) {
		eventhand.ManagePlayerJoin(e);
	}
}
