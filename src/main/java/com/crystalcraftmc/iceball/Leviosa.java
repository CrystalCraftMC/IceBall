/*
 * Copyright 2015 CrystalCraftMC
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.crystalcraftmc.iceball;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Bat;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class Leviosa implements Listener {
	private Timer tim;
	private final Player p;
	private IceBall plugin;
	private final int LEV = -5; //blocks below /snowfight y where it levatates
	private final double SPEED = .25;
	private Bat b;
	private final boolean isLadder;
	private int accumulator = 0;
	private Location lastLoc;
	private int timeOnBat;
	private boolean sleepMode;
	
	public Leviosa(Player z, IceBall ib, boolean isLadder73) {
		
		lastLoc = z.getLocation();
		isLadder = isLadder73;
		timeOnBat = isLadder ? 1300 : 500;
		p = z;
		plugin = ib;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		for(int i = 0; i < 15; i++)
			this.particleWarmup(i);
		tim = new Timer(50, new LeviosaUpdate());
		tim.start();
	}
	private class LeviosaUpdate implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			ItemStack hand = p.getItemInHand();
			if(isLadder) {
				if(hand.getType() != Material.SNOW_BALL)
					lastLoc = p.getLocation();
			}
			accumulator++;
			if(accumulator == 90) {
				boolean cancelE = p.getLocation().getY() > (plugin.Y-4) ? true : false;
				if(cancelE) {
					tim.stop();
					p.sendMessage(ChatColor.RED + "Error; you are too close to the ceilling" +
							" to start riding the bat.");
					ItemStack giveBack = isLadder ? new ItemStack(Material.LADDER, 1) :
						new ItemStack(Material.STICK, 1);
					delayedGive(p, giveBack);
				}
				else {
					plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
						public void run() {
							b = (Bat)p.getWorld().spawnEntity(p.getLocation().add(0, 1, 0), EntityType.BAT);
							b.setPassenger(p);
							b.setCustomName(ChatColor.GOLD + "Herme's Sandals");
							b.setCustomNameVisible(true);
						}
					}, 1L);
				}
			}
			if(accumulator < 100) {}
			else if(accumulator < timeOnBat) {
				if(isLadder)
					b.setHealth(8);
				int yaw = (int)p.getLocation().getYaw();
				double xVel = 0, zVel = 0;
				if(yaw >= 0 && yaw < 90) {
					xVel = -(yaw/90.0);
					zVel = 1-(yaw/90.0);
				}
				else if(yaw >= 90 && yaw < 180) {
					xVel = -(1-((yaw-90)/90.0));
					zVel = -((yaw-90)/90.0);
				}
				else if(yaw >= 180 && yaw < 270) {
					xVel = (yaw-180)/90.0;
					zVel = -(1-((yaw-180)/90.0));
				}
				else if(yaw >= 270 && yaw < 361) {
					xVel = 1-((yaw-270)/90.0);
					zVel = (yaw-270)/90.0;
				}
				if(sleepMode && isLadder)
					b.setAwake(false);
				else if(isLadder && hand.getType() != Material.SNOW_BALL) {
					if(b.getLocation().getY() < (plugin.Y+LEV))
						b.setVelocity(new Vector(xVel*SPEED, .1, zVel*SPEED));
					else
						b.setVelocity(new Vector(xVel*SPEED, -.1, zVel*SPEED));
				}
				else if(isLadder && hand.getType() == Material.SNOW_BALL) {
					double x, y, z;
					Location current = p.getLocation();
					x = current.getX() > lastLoc.getX() ? -.1 : .1;
					y = current.getY() > lastLoc.getY() ? -.1 : .1;
					z = current.getZ() > lastLoc.getZ() ? -.1 : .1;
					b.setVelocity(new Vector(x, y, z));
				}
				else if(b.getLocation().getY() < (plugin.Y+LEV))
					b.setVelocity(new Vector(xVel*SPEED, .1, zVel*SPEED));
				else
					b.setVelocity(new Vector(xVel*SPEED, -.1, zVel*SPEED));
				Location loc = b.getLocation();
				if(!isLadder) {
					ParticleEffect.LAVA.display((float).3, (float).0, (float).3,
						(float)(1), (int)(2), new Location(loc.getWorld(), 
								loc.getX(),loc.getY()-1, loc.getZ()), 90.0);
				}
				else {
					ParticleEffect.PORTAL.display((float).1, (float).0, (float).1,
							(float)(1), (int)(10), new Location(loc.getWorld(), 
									loc.getX(),loc.getY()-1, loc.getZ()), 90.0);
				}
			}
			else {
				if(!b.isDead())
					b.setHealth(0);
				tim.stop();
			}
		}
	}
	
	public void particleWarmup(int numb) {
		final int num = numb;
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run() {
				if(!isLadder) {
					Location loc = p.getLocation();
					ParticleEffect.REDSTONE.display((float).3, (float).3, (float).3,
						(float)(num), (int)(num), new Location(loc.getWorld(), 
								loc.getX(),loc.getY()+1, loc.getZ()), 90.0);
				}
				else {
					Location loc = p.getLocation();
					ParticleEffect.HEART.display((float).3, (float)1, (float).3,
						(float)(1), (int)(3), new Location(loc.getWorld(), 
								loc.getX(),loc.getY()+1, loc.getZ()), 90.0);
				}
			}
			
		}, (long)(8*num));
	}
	@EventHandler
	public void toggleSleep(EntityDamageByEntityEvent e) {
		if(e.getEntity() instanceof Bat) {
			if(e.getDamager() instanceof Snowball) {
				Snowball sn = (Snowball)e.getDamager();
				if(sn.getShooter() instanceof Player) {
					Player sh = (Player)sn.getShooter();
					if(sh.getName().equals(p.getName()))
						sleepMode = !sleepMode;
				}
			}
		}
	}
	public void delayedGive(Player qq, ItemStack zis) {
		//the purpose of this method is to fix the bug where an item is given to you,
		//but it doesn't appear in your inventory
		final Player zz=qq;
		final ItemStack is = zis;
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run() {
				zz.getInventory().addItem(is);
			}
		}, 5L);
	}
}
