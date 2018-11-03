package com.gmail.JyckoSianjaya.ShipmentBox.Utils;

import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.JyckoSianjaya.ShipmentBox.ShipmentBox;

public class Timer {
	private static Timer instance;
	private int seconds = 0;
	private int globalseconds = 0;
	private int minutes = 0; 
	private int hours = 0;
	private Timer() {
		new BukkitRunnable() {
			@Override
			public void run() {
				seconds += 1;
				globalseconds =+ 1;
				if (seconds >= 60) {
					seconds -= 60;
					minutes += 1;
				}
				if (minutes >= 60) {
					hours += 1;
					minutes -= 60;
				}
				if (hours >= 24) {
					globalseconds = 0;
					hours = 0;
					seconds = 0;
					minutes = 0;
					Utility.sendConsole("&6&l[ShipmentBox] &aA new day has been started, time: 0:00");
				}
				// TODO Auto-generated method stub
			}
			
		}.runTaskTimer(ShipmentBox.getInstance(), 20L, 20L);
	}
	public static Timer getInstance() {
		if (instance == null) {
			instance = new Timer();
		}
		return instance;
	}
	public int getGlobalSeconds() {
		return globalseconds;
	}
	public void setHours(int newhour) {
		this.hours = newhour;
	}
	public void setMinutes(int newminute) {
		minutes = newminute;
	}
	public void setSeconds(int newseconds) {
		seconds = newseconds;
	}
	public int getHours() {
		return hours;
	}
	public int getMinutes() {
		return minutes;
	}
	public int getSeconds() {
		return seconds;
	}
}
