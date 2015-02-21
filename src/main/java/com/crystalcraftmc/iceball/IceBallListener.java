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

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class IceBallListener implements Listener {
	private IceBall plugin;
	private String[] nonOpBuildPerms = {"todd5747"}; //will add Jwood and Teth after testing 4 testing
	private ArrayList<HitStreak> al = new ArrayList<HitStreak>();
	private ItemStack officialIceBall;
	public IceBallListener(IceBall ib) {
		plugin = ib;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		
		officialIceBall = new ItemStack(Material.SNOW_BALL, 2);
		ItemMeta im = officialIceBall.getItemMeta();
		im.addEnchant(Enchantment.ARROW_INFINITE, 1, false);
		im.setDisplayName("Ice_Ball");
		ArrayList<String> alLore = new ArrayList<String>();
		alLore.add(ChatColor.RED + "Snow Dug From");
		alLore.add(ChatColor.GOLD + "Plastic Beach");
		im.setLore(alLore);
		officialIceBall.setItemMeta(im);
	}
	@EventHandler (priority=EventPriority.LOWEST)
	public void stopCommands(PlayerCommandPreprocessEvent e) {
		Player p = e.getPlayer();
		if(e.getMessage().equalsIgnoreCase("/snowfight")) {
			if(!this.isOutsideArena(p, 33, false)) {
				p.sendMessage(ChatColor.RED + "Error; you must be off the snowball-arena's premises " +
							"to use /snowfight");
			}
			else {
				if(this.hasEmptyInventory(p)) {
					new DelayedTeleport(plugin, p);
				}
				else {
					p.sendMessage(ChatColor.RED + "Error; your inventory must be empty in order to" +
							" use this command. Armor slots included.");
				}
			}
		}
		else if(e.getMessage().equalsIgnoreCase("/snowleave")) {
			if(!this.isOutsideArena(p, 21, true)) {
				p.getInventory().clear();
				for(int i = 0; i < al.size(); i++) {
					if(al.get(i).getName().equals(p.getName()))
						al.get(i).reset();
				}
				this.teleportToSpawn(p);
			}
			else {
				p.sendMessage(ChatColor.RED + "Error; you must be inside the snowball arena to use" +
						" this command.");
			}
		}
		else if(!this.isOutsideArena(p, 33, false) && !this.hasBuildPermission(p) && !p.isOp()) {
			p.sendMessage(ChatColor.RED + "Error; only command allowed at the " + ChatColor.AQUA +
					"CCMC " + ChatColor.RED + "SnowBall arena is: \"" +
					ChatColor.GOLD + "/snowleave" + ChatColor.RED + "\" which will tp you to spawn.");
			e.setCancelled(true);
		}
	}
	@EventHandler
	public void resetStatsOnLogIn(PlayerLoginEvent e) {
		Player p = e.getPlayer();
			for(int i = 0; i < al.size(); i++) {
				if(al.get(i).getName().equals(p.getName()))
					al.get(i).reset();
			}
		
	}
	@EventHandler
	public void cancelBlockPlace(BlockPlaceEvent e) {
		Player p = e.getPlayer();
		if(!this.isOutsideArena(p, 33, false)) {
			if(p.isOp() || this.hasBuildPermission(p)) {}
			else {
				e.getPlayer().sendMessage(ChatColor.GOLD + "You do not have permission to place a block" +
						" at the " + ChatColor.AQUA + "CCMC " + ChatColor.GOLD + "SnowBall arena.");
				e.setCancelled(true);
			}
		}
	}
	@EventHandler
	public void resetOnDeath(PlayerDeathEvent e) {
		Player p = e.getEntity();
		for(int i = 0; i < al.size(); i++) {
			if(al.get(i).getName().equals(p.getName())) {
				al.get(i).reset();
			}
		}
	}
	@EventHandler
	public void noFallDeath(EntityDamageEvent e) {
		if(e.getEntity() instanceof Player) {
			Player p = (Player)e.getEntity();
			if(!this.isOutsideArena(p, 18, true)) {
				if(e.getCause().equals(DamageCause.FALL))
					e.setCancelled(true);
			}
		}
	}
	@EventHandler
	public void cancelBlockBreak(BlockBreakEvent e) {
		Player p = e.getPlayer();
		if(!this.isOutsideArena(p, 33, false)) {
			if(p.isOp() || this.hasBuildPermission(p)) {}
			else {
				e.getPlayer().sendMessage(ChatColor.GOLD + "You do not have permission to break that.");
				e.setCancelled(true);
			}
		}
	}
	@EventHandler (priority=EventPriority.LOW)
	public void cancelTP(PlayerTeleportEvent e) {
		if(!this.isOutsideArena(e.getPlayer(), 28, false)) {
			if(this.isOutsideArena(e.getPlayer(), 18, true)) {
				if(!e.getPlayer().isOp()) {
					e.getPlayer().sendMessage(ChatColor.RED + "Error; you don't have permission to" +
							" teleport here. " + ChatColor.GREEN + "PowerTool protection.");
					e.setCancelled(true);
				}
				else {
					e.getPlayer().sendMessage(ChatColor.GOLD + "op- granted access to tp in this zone.");
				}
			}
		}
	}
	public boolean hasBuildPermission(Player p) {
		for(String name : nonOpBuildPerms) {
			if(name.equalsIgnoreCase(p.getName()))
				return true;
		}
		return false;
	}
	public boolean isOutsideArena(Player p, int constant, boolean checkY) {
		Location loc = p.getLocation();
		int x = (int)loc.getX();
		int y = (int)loc.getY();
		int z = (int)loc.getZ();
		if(checkY) {
			if(y < (plugin.Y+5) && y > (plugin.Y-14)) { Bukkit.broadcastMessage("Proper y");}
			else
				return true;
		}
		if(x > (plugin.X-constant) && x < (plugin.X+constant) &&
				z > (plugin.Z-constant) && z < (plugin.Z+constant)) {
			Bukkit.broadcastMessage("Proper x/z");
			return false;
		}
		else {
			if(!(x > (plugin.X-constant))) {Bukkit.broadcastMessage("x: " + x + " is not greater than: " + (plugin.X-constant));}
			if(!(x < (plugin.X+constant))) {Bukkit.broadcastMessage("x: " + x + " is not less than: " + (plugin.X+constant));}
			if(!(z > (plugin.Z-constant))) {Bukkit.broadcastMessage("z: " + z + " is not greater than: " + (plugin.Z-constant));}
			if(!(z < (plugin.Z+constant))) {Bukkit.broadcastMessage("z: " + z + " is not less than: " + (plugin.Z+constant));}
			return true;
		}
	}
	public boolean hasEmptyInventory(Player p) {
		if(p.getInventory().getHelmet() != null) 
			return false;
		else if(p.getInventory().getChestplate() != null)
			return false;
		else if(p.getInventory().getLeggings() != null) 
			return false;
		else if(p.getInventory().getBoots() != null)
			return false;
		else {
			for(ItemStack is : p.getInventory().getContents()) {
				if(is != null)
					return false;
			}
			return true;
		}
	}
	public void teleportToSpawn(Player p) {
		Location locSpawn = new Location(p.getWorld(), (double)plugin.SPAWNX,
				(double)plugin.SPAWNY, (double)plugin.SPAWNZ);
		locSpawn.setYaw((float)-90);
		locSpawn.setPitch((float)0);
		p.teleport(locSpawn);
	}
	public boolean inSnowballRange(Location loc) {
		if((int)loc.getX() > (plugin.X-18) && (int)loc.getX() < (plugin.X+18)) {
			if((int)loc.getZ() > (plugin.Z-18) && (int)loc.getZ() < (plugin.Z+18)) {
				if((int)loc.getY() > (plugin.Y-14) && (int)loc.getY() < (plugin.Y+5)) {
					return true;
				}
			}
		}
		return false;
	}
	@EventHandler
	public void isolateIceBalls(ProjectileLaunchEvent e) {
		if(e.getEntity() instanceof Snowball && e.getEntity().getShooter() instanceof Player) {
			Snowball sn = (Snowball)e.getEntity();
			Player shooter = (Player)e.getEntity().getShooter();
			ItemStack is = shooter.getItemInHand();
			if(is.isSimilar(officialIceBall) && is.containsEnchantment(Enchantment.ARROW_INFINITE)) {
				sn.setCustomName("Jwood_Launcher");
			}
		}
	}
	@EventHandler
	public void noPvp(EntityDamageByEntityEvent e) {
		if(e.getEntity() instanceof Player) {
			Location loc = e.getEntity().getLocation();
			if(e.getDamage() > 0) {
				e.setCancelled(this.inSnowballRange(loc));
			}
			else {
				if(this.inSnowballRange(loc) && e.getDamager() instanceof Snowball) {
					Snowball sn = (Snowball)e.getDamager();
					
					if(sn.getShooter() instanceof Player) {
						Player shooter = (Player)sn.getShooter();
						Player target = (Player)e.getEntity();
						if(this.inSnowballRange(shooter.getLocation())) {
							boolean existsInList = false;
							boolean immunitySkip = false;
							int placeInList = -1;
							for(int i = 0; i < al.size(); i++) {
								if(al.get(i).getName().equals(target.getName())) {
									if(al.get(i).isImmune)
										immunitySkip = true;
								}
							}
							if(immunitySkip) {
								shooter.sendMessage(ChatColor.YELLOW + target.getName() +
										ChatColor.BLUE + " Currently has a 7 second snowball immunity" +
										" from a 15 hit-streak, and cannot be hit!");
								e.setCancelled(true);
							}
							else {
								
								if(sn.getCustomName() != null) {
									if(sn.getCustomName().equals("Jwood_Launcher")) {
										this.godPunch(target);
										Bukkit.broadcastMessage(ChatColor.RED + "Ice Ball Recognized");
									}
								}
								for(int i = 0; i < al.size(); i++) {
									if(al.get(i).getName().equals(shooter.getName())) {
										existsInList = true;
										placeInList = i;
									}
								}
								if(existsInList) {
									al.get(placeInList).anotherSnipe();
									Bukkit.broadcastMessage("Player hit increase to: " +
											String.valueOf(al.get(placeInList).getHitStreak()));
								}
								else {
									al.add(new HitStreak(shooter, plugin));
									Bukkit.broadcastMessage("Player added to hitstreak arraylist");
								}
								existsInList = false;
								for(int i = 0; i < al.size(); i++) {
									if(al.get(i).getName().equals(target.getName())) {
											al.get(i).setHitStreak(0);
									}
								}
							}
						}
					}
				}
			}
		}
	}
	public void godPunch(Player p) {
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
		final Player p7 = p;
		final double xVelFin = xVel;
		final double zVelFin = zVel;
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run() {
				new AnimatePlayer(p7, xVelFin, zVelFin);
			}
		}, (long)1);
	}
}
