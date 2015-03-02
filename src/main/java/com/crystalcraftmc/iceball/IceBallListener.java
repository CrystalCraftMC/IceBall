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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class IceBallListener implements Listener {
	private IceBall plugin;
	private String[] nonOpBuildPerms = {"todd5747", "Jwood9198", "Tethtibis"};
	private ArrayList<HitStreak> al = new ArrayList<HitStreak>();
	private ItemStack officialIceBall;
	private ItemStack witchBomb;
	private ItemStack hungerRune;
	private ItemStack sorcerorBomb;
	private Map<String, Integer> woolMap = new HashMap<String, Integer>();
	private ArrayList<Location> eggTracker = new ArrayList<Location>();
	private Random rand = new Random();
	private long eggTime = 0L;
	private long swordList = 0L;
	private long recentBeam = 0L;
	public long astime = 0L;
	private long crystalTime = 0L;
	private File file;
	private PrintWriter pw;
	ItemStack ibis = new ItemStack(Material.SNOW_BALL, 2);
	public IceBallListener(IceBall ib) {
		file = new File("IceBallEasterEgg.txt");
		if(!file.exists()) {
			try {
				pw = new PrintWriter(file);
				pw.println("Jwood's Easter Egg Tracker");
				pw.flush();
				pw.println("==========================");
				pw.flush();
				pw.close();
			}catch(IOException e) {	e.printStackTrace(); }
		}
		plugin = ib;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		createIbis();
		officialIceBall = new ItemStack(Material.SNOW_BALL, 2);
		ItemMeta im = officialIceBall.getItemMeta();
		im.addEnchant(Enchantment.ARROW_INFINITE, 1, false);
		im.setDisplayName("Ice_Ball");
		ArrayList<String> alLore = new ArrayList<String>();
		alLore.add(ChatColor.RED + "Snow Dug From");
		alLore.add(ChatColor.GOLD + "Plastic Beach");
		im.setLore(alLore);
		officialIceBall.setItemMeta(im);
		this.createWitchBomb();
		this.createSorcerorBomb();
		hungerRune = new ItemStack(Material.FLINT_AND_STEEL, 1);
		woolMap.put("red", 14);
		woolMap.put("purple", 10);
		woolMap.put("blue", 3);
		woolMap.put("green", 5);
	}
	@EventHandler (priority=EventPriority.LOWEST)
	public void stopCommands(PlayerCommandPreprocessEvent e) {
		Player p = e.getPlayer();
		if(e.getMessage().equalsIgnoreCase("/snowbuild") && 
				p.getWorld().getEnvironment() == Environment.NORMAL &&
				(p.isOp() || this.hasBuildPermission(e.getPlayer()))) {
			new DelayedTeleport(plugin, p, true);
		}
		else if(e.getMessage().equalsIgnoreCase("/snowfight") && 
				e.getPlayer().getWorld().getEnvironment() == Environment.NORMAL) {
			
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
		else if(e.getMessage().equalsIgnoreCase("/snowfight") && 
				e.getPlayer().getWorld().getEnvironment() != Environment.NORMAL) {
			e.getPlayer().sendMessage(ChatColor.AQUA + "Error; you must be in the" +
				" overworld to use command " + ChatColor.GOLD + "\"snowfight\"" + ChatColor.AQUA + ".");
		}
		else if(e.getMessage().equalsIgnoreCase("/snowleave") &&
				e.getPlayer().getWorld().getEnvironment() == Environment.NORMAL) {
			if(!this.isOutsideArena(p, 19, true)) {
				p.getInventory().clear();
				for(int i = 0; i < al.size(); i++) {
					if(al.get(i).getName().equals(p.getName()))
						al.remove(i);
				}
				this.clearPotions(p);
				this.teleportToSpawn(p);
			}
			else {
				p.sendMessage(ChatColor.RED + "Error; you must be inside the snowball arena to use" +
						" this command.");
			}
		}
		else if(!this.isOutsideArena(p, 35, false) && !this.hasBuildPermission(p) && !p.isOp()) {
			p.sendMessage(ChatColor.RED + "Error; only command allowed at the " + ChatColor.AQUA +
					"CCMC " + ChatColor.RED + "SnowBall arena is: \"" +
					ChatColor.GOLD + "/snowleave" + ChatColor.RED + "\" which will tp you to spawn.");
			e.setCancelled(true);
		}
	}
	@EventHandler (priority= EventPriority.LOWEST)
	public void resetStatsOnLogIn(PlayerLoginEvent e) {
		Player p = e.getPlayer();
		if(p.getWorld().getEnvironment() == Environment.NORMAL) {
			final Player pu = p;
			//death event requires a small delay to work effectively
			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				public void run() {
					if(!this.isOutsideArena(pu, 20, true)) {
						for(int i = 0; i < al.size(); i++) {
							if(pu.getName().equals(al.get(i).getName()))
								al.remove(i);
						}
						pu.setHealth(0);
						pu.sendMessage(ChatColor.GOLD + "Logging in inside the arena will result in death." +
								ChatColor.AQUA + " -J");
					}
				}
				public boolean isOutsideArena(Player p, int constant, boolean checkY) {
					Location loc = p.getLocation();
					if(loc.getWorld().getEnvironment() != Environment.NORMAL)
						return true;
					int x = (int)loc.getX();
					int y = (int)loc.getY();
					int z = (int)loc.getZ();
					if(checkY) {
						if(y < (plugin.Y+5) && y > (plugin.Y-14)) { /*Bukkit.broadcastMessage("Proper y"); */}
						else
							return true;
					}
					if(x > (plugin.X-constant) && x < (plugin.X+constant) &&
							z > (plugin.Z-constant) && z < (plugin.Z+constant)) {
						return false;
					}
					else {
						//if(!(x > (plugin.X-constant))) {Bukkit.broadcastMessage("x: " + x + " is not greater than: " + (plugin.X-constant));}
						//if(!(x < (plugin.X+constant))) {Bukkit.broadcastMessage("x: " + x + " is not less than: " + (plugin.X+constant));}
						//if(!(z > (plugin.Z-constant))) {Bukkit.broadcastMessage("z: " + z + " is not greater than: " + (plugin.Z-constant));}
						//if(!(z < (plugin.Z+constant))) {Bukkit.broadcastMessage("z: " + z + " is not less than: " + (plugin.Z+constant));}
						return true;
					}
				}
			}, 5L);
			
		}
	}
	@EventHandler
	public void blastProt(EntityExplodeEvent e) {
		if(e.getLocation().getWorld().getEnvironment() == Environment.NORMAL){
			if(!this.isOutsideArena(e.getLocation(), 50, false))
				e.setCancelled(true); 		//impenetrable against even tnt cannons
			if(e.getEntityType() == EntityType.CREEPER && !this.isOutsideArena(e.getLocation(), 20, true)) {
				Creeper tom = (Creeper)e.getEntity();
				if(tom.getMaxHealth() == 10) {
				ArrayList<Player> test8 = new ArrayList<Player>(plugin.getServer().getOnlinePlayers());
				for(int i = 0; i < test8.size(); i++) {
					if(this.within10BlockCube(e.getLocation(), test8.get(i).getLocation())) {
						
							test8.get(i).getInventory().clear();
							test8.get(i).sendMessage(ChatColor.GOLD + "The " + ChatColor.AQUA +
								"Crystal Dragon " + ChatColor.GOLD + "Blew Up Your Inventory");
						}	
					}
				}
				else if(tom.getMaxHealth() == 11) {
					ArrayList<Player> test8 = new ArrayList<Player>(plugin.getServer().getOnlinePlayers());
					for(int i = 0; i < test8.size(); i++) {
						if(this.within6BlockCube(e.getLocation(), test8.get(i).getLocation())) {
							
								test8.get(i).sendMessage(ChatColor.GREEN + "The Powered Creeper " +
										"Powered You Up");
								test8.get(i).addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 600, 1));
								test8.get(i).addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 600, 3));
								test8.get(i).sendMessage(ChatColor.GOLD + "The " + ChatColor.AQUA +
									"Crystal Dragon " + ChatColor.GOLD + "Can Be Freed");
							}	
						}
				}
			}
		}
	}
	@EventHandler
	public void cancelBlockPlace(BlockPlaceEvent e) {
		Player p = e.getPlayer();
		if(!this.isOutsideArena(p, 20, false) && e.getPlayer().getWorld().getEnvironment() == Environment.NORMAL &&
				p.getItemInHand().getType() == Material.WOOL) {
			Block aboveB = e.getBlock().getLocation().add(0, 1, 0).getBlock();
			boolean isCancelled = false;
			if((p.isOp() || this.hasBuildPermission(p)) && aboveB.getType() == Material.COBBLESTONE)
				p.sendMessage("You have perms to place wool under cobblestone.");
			else {
				e.setCancelled(true);
				isCancelled = true;
			}
			if(isCancelled) {
				int wdata = e.getBlockPlaced().getData();
				//white = 0 && red = 14 && purp=10 && blue=3 && gtreen=5
				for(int i = 0; i < al.size(); i++) {
					if(al.get(i).getName().equals(p.getName())) {
						if(woolMap.get(al.get(i).getTeam()) == wdata) {
							p.sendMessage(ChatColor.RED + "Error; you are already on the " +
								al.get(i).getTeamColor() + al.get(i).getTeam() + ChatColor.RED +
								" team.");
						}
						else if(wdata == 14) {
							al.get(i).setTeam("red");
							al.get(i).setHitStreak(0);
							p.sendMessage(ChatColor.YELLOW + "Welcome to the " + ChatColor.RED +
								"Rogues Team" + ChatColor.YELLOW + ", " + ChatColor.GOLD + p.getName());
						}
						else if(wdata == 10) {
							al.get(i).setTeam("purple");
							al.get(i).setHitStreak(0);
							p.sendMessage(ChatColor.YELLOW + "Welcome to the " + ChatColor.LIGHT_PURPLE +
								"Witch Team" + ChatColor.YELLOW + ", " + ChatColor.GOLD + p.getName());
						}
						else if(wdata == 3) {
							al.get(i).setTeam("blue");
							al.get(i).setHitStreak(0);
							p.sendMessage(ChatColor.YELLOW + "Welcome to the " + ChatColor.AQUA +
								"Sorceror's Team" + ChatColor.YELLOW + ", " + ChatColor.GOLD + p.getName());
						}
						else if(wdata == 5) {
							al.get(i).setTeam("green");
							al.get(i).setHitStreak(0);
							p.sendMessage(ChatColor.YELLOW + "Welcome to the " + ChatColor.GREEN +
								"Clown's Team" + ChatColor.YELLOW + ", " + ChatColor.GOLD + p.getName());
						}
						else { //different colored wool -- would only be white
							al.get(i).randomizeTeam();
							al.get(i).setHitStreak(0);
							p.sendMessage(ChatColor.BOLD + "Team Randomized");
							p.sendMessage(ChatColor.YELLOW + "Welcome to the " + al.get(i).getTeamColor() +
								String.format("%C%s", al.get(i).getTeam().charAt(0), al.get(i).getTeam().substring(1)) + 
								" Team" + ChatColor.YELLOW + ", " + ChatColor.GOLD + p.getName());
						}
						ItemStack lli = e.getPlayer().getInventory().getItemInHand();
						int amountRune = lli.getAmount();
						lli.setAmount(amountRune-1);
						e.getPlayer().getInventory().setItemInHand(lli);
					}
				}
			}
		}
		else if(!this.isOutsideArena(p, 50, false) && e.getPlayer().getWorld().getEnvironment() == Environment.NORMAL) {
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
				al.remove(i);
			}
		}
	}
	@EventHandler
	public void noFallDeath(EntityDamageEvent e) {
		if(e.getEntity() instanceof Player) {
			Player p = (Player)e.getEntity();
			if(!this.isOutsideArena(p, 18, true) && p.getWorld().getEnvironment() == Environment.NORMAL) {
				if(e.getCause().equals(DamageCause.FALL))
					e.setCancelled(true);
			}
		}
	}
	@EventHandler
	public void cancelBlockBreak(BlockBreakEvent e) {
		Player p = e.getPlayer();
		if(!this.isOutsideArena(p, 50, false)) {
			if(p.isOp() || this.hasBuildPermission(p)) {}
			else {
				if(e.getPlayer().getWorld().getEnvironment() == Environment.NORMAL) {
					e.getPlayer().sendMessage(ChatColor.GOLD + "You do not have permission to break that.");
					e.setCancelled(true);
				}
			}
		}
	}
	@EventHandler (priority=EventPriority.LOW)
	public void cancelTP(PlayerTeleportEvent e) {
		if(!this.isOutsideArena(e.getPlayer(), 40, false) && e.getCause() == TeleportCause.ENDER_PEARL &&
				e.getPlayer().getWorld().getEnvironment() == Environment.NORMAL) {
			if((Math.abs(plugin.X-e.getTo().getX()) > 14) || 
					(Math.abs(plugin.Z-e.getTo().getZ()) > 14) || (plugin.Y-e.getTo().getY() < 2)) {
				e.getPlayer().sendMessage(ChatColor.GREEN + "Ender-Pearling has been disabled " +
					"near the arena walls.");
				e.getPlayer().getInventory().addItem(new ItemStack(Material.ENDER_PEARL, 1));
				e.setCancelled(true);
			}
		}
		else if(!this.isOutsideArena(e.getPlayer(), 40, false) &&
				e.getPlayer().getWorld().getEnvironment() == Environment.NORMAL) {
			if(this.isOutsideArena(e.getPlayer(), 18, true)) {
				if(!e.getPlayer().isOp() && !this.hasBuildPermission(e.getPlayer())) {
					e.getPlayer().sendMessage(ChatColor.RED + "Error; you don't have permission to" +
							" teleport here. " + ChatColor.GREEN + "PowerTool protection.");
					e.setCancelled(true);
				}
				else {
					e.getPlayer().sendMessage(ChatColor.GOLD + "op/creator- granted access to tp in this zone.");
				}
			}
		}
	}
	
	public boolean hasBuildPermission(Player p) {
		for(String name : nonOpBuildPerms) {
			if(name.equals(p.getName()))
				return true;
		}
		return false;
	}
	public boolean isOutsideArena(Player p, int constant, boolean checkY) {
		Location loc = p.getLocation();
		if(loc.getWorld().getEnvironment() != Environment.NORMAL)
			return true;
		int x = (int)loc.getX();
		int y = (int)loc.getY();
		int z = (int)loc.getZ();
		if(checkY) {
			if(y < (plugin.Y+5) && y > (plugin.Y-14)) { /*Bukkit.broadcastMessage("Proper y"); */}
			else
				return true;
		}
		if(x > (plugin.X-constant) && x < (plugin.X+constant) &&
				z > (plugin.Z-constant) && z < (plugin.Z+constant)) {
			return false;
		}
		else {
			//if(!(x > (plugin.X-constant))) {Bukkit.broadcastMessage("x: " + x + " is not greater than: " + (plugin.X-constant));}
			//if(!(x < (plugin.X+constant))) {Bukkit.broadcastMessage("x: " + x + " is not less than: " + (plugin.X+constant));}
			//if(!(z > (plugin.Z-constant))) {Bukkit.broadcastMessage("z: " + z + " is not greater than: " + (plugin.Z-constant));}
			//if(!(z < (plugin.Z+constant))) {Bukkit.broadcastMessage("z: " + z + " is not less than: " + (plugin.Z+constant));}
			return true;
		}
	}
	public boolean isOutsideArena(Location loc, int constant, boolean checkY) {
		if(loc.getWorld().getEnvironment() != Environment.NORMAL)
			return true;
		int x = (int)loc.getX();
		int y = (int)loc.getY();
		int z = (int)loc.getZ();
		if(checkY) {
			if(y < (plugin.Y+5) && y > (plugin.Y-14)) { /*Bukkit.broadcastMessage("Proper y"); */}
			else
				return true;
		}
		if(x > (plugin.X-constant) && x < (plugin.X+constant) &&
				z > (plugin.Z-constant) && z < (plugin.Z+constant)) {
			return false;
		}
		else {
			//if(!(x > (plugin.X-constant))) {Bukkit.broadcastMessage("x: " + x + " is not greater than: " + (plugin.X-constant));}
			//if(!(x < (plugin.X+constant))) {Bukkit.broadcastMessage("x: " + x + " is not less than: " + (plugin.X+constant));}
			//if(!(z > (plugin.Z-constant))) {Bukkit.broadcastMessage("z: " + z + " is not greater than: " + (plugin.Z-constant));}
			//if(!(z < (plugin.Z+constant))) {Bukkit.broadcastMessage("z: " + z + " is not less than: " + (plugin.Z+constant));}
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
	public void teleportToSpawn(Player p) { //only called from /snowleave & reset on log in
		//									which already checks overworld
		Location locSpawn = new Location(p.getWorld(), (double)plugin.SPAWNX,
				(double)plugin.SPAWNY, (double)plugin.SPAWNZ);
		locSpawn.setYaw((float)-90);
		locSpawn.setPitch((float)0);
		p.teleport(locSpawn);
	}
	public boolean inSnowballRange(Location loc) {
		if(loc.getWorld().getEnvironment() != Environment.NORMAL)
			return false;
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
	if(e.getEntityType() == EntityType.ARROW) {
		Arrow arrow = (Arrow)e.getEntity();
		if(arrow.getShooter() instanceof Player) {
			Player shooter = (Player)arrow.getShooter();
			if(!isOutsideArena(shooter, 20, true)) {
				int numArrows = this.countArrows(shooter);
				if(numArrows > 0) {
					if(System.currentTimeMillis()-astime > 20000) {
						new AirStrike(shooter, numArrows, plugin, this);
						e.setCancelled(true);
						//this.delayedGive(shooter, new ItemStack(Material.ARROW, 1));
					}
					else {
						shooter.sendMessage(ChatColor.AQUA + "Error; 20 seconds must pass between " +
								"AirStrikes.");
						e.setCancelled(true);
						//this.delayedGive(shooter, new ItemStack(Material.ARROW, 1));
					}
				}
			}
		}
	}
	else if(e.getEntity() instanceof Snowball && e.getEntity().getShooter() instanceof Player) {
			Snowball sn = (Snowball)e.getEntity();
			Player shooter = (Player)e.getEntity().getShooter();
			ItemStack is = shooter.getItemInHand();
			if(is.isSimilar(officialIceBall) && is.containsEnchantment(Enchantment.ARROW_INFINITE)) {
				sn.setCustomName("Jwood_Launcher");
			}
		}
	}
	@EventHandler
	public void paintingsOpSoExecuteOnCraft(CraftItemEvent e) {
		Player p = (Player)e.getWhoClicked();
		if((e.getCurrentItem().getType() == Material.PAINTING ||
				e.getCurrentItem().getType() == Material.FISHING_ROD) &&
				!this.isOutsideArena(p, 20, true)) {
			p.sendMessage(ChatColor.RED + "Invalid Item; try " + ChatColor.GOLD + "Ladder " +
				ChatColor.RED + "or " + ChatColor.GOLD + "Leads");
			e.setCancelled(true);
		}
		else if(e.getCurrentItem().getType() == Material.BLAZE_POWDER &&
				!this.isOutsideArena(p, 20, true)) {
			e.setCancelled(true);
			p.sendMessage(ChatColor.RED + "Cannot craft this item - right click with " +
					"blaze rod in hand to use."); 
		}
		else if(e.getCurrentItem().getType() == Material.LADDER &&
				!this.isOutsideArena(p, 20, true)) {
			e.getCurrentItem().setAmount(1);
		}
		else if(e.getCurrentItem().getType() == Material.LEASH &&
				!this.isOutsideArena(p, 20, true)) {
			e.getCurrentItem().setAmount(1);
			e.getCurrentItem().setType(Material.GOLDEN_APPLE);
		}
		else if(e.getCurrentItem().getType() == Material.SLIME_BLOCK &&
				!this.isOutsideArena(p, 20, true)) {
			ItemStack mm = new ItemStack(373, 2, (short)8235);
			e.getInventory().setResult(mm);
			e.setCancelled(true);
		}
		else if(e.getCurrentItem().getType() == Material.COAL_BLOCK &&
				!this.isOutsideArena(p, 20, true)) {
			ItemStack mm = new ItemStack(373, 1, (short)8238);
			ItemMeta im = mm.getItemMeta();
			im.setDisplayName(ChatColor.RED + "Ghillie Sniper");
			ArrayList<String> al755 = new ArrayList<String>();
			al755.add(ChatColor.GRAY + "Invisibility (2:00)");
			al755.add(ChatColor.GRAY + "Invisibility (1:00)");
			al755.add(ChatColor.GRAY + "Invisibility (" + ChatColor.MAGIC + "5:29" +
					ChatColor.GRAY + ")");
			al755.add(ChatColor.AQUA + "Like a Coal Miner");
			al755.add(ChatColor.BLUE + "You Blend Into Your Surroundings");
			im.setLore(al755);
			mm.setItemMeta(im);
			e.getInventory().setResult(mm);
			p.sendMessage(String.valueOf(mm.getData()));
			e.setCancelled(true);
		}
		else if(e.getCurrentItem().getType() == Material.TORCH &&
				!this.isOutsideArena(p, 20, true)) {
			e.getCurrentItem().setAmount(2);
		}
		else if(e.getCurrentItem().getType() == Material.ARROW &&
				!this.isOutsideArena(p, 20, true)) {
			e.getCurrentItem().setAmount(2);
		}
		else if(e.getCurrentItem().getType() == Material.BANNER &&
				!this.isOutsideArena(p, 20, true)) {
			e.getCurrentItem().setAmount(2);
		}
		
	}
	@EventHandler
	public void opLegend(PlayerItemConsumeEvent e) {
		final Player p = e.getPlayer();
		if(e.getItem().getType() == Material.GOLDEN_APPLE) {
			if(!this.isOutsideArena(p, 20, true)) {
				p.sendMessage(ChatColor.BLUE + e.getPlayer().getName() + ChatColor.DARK_RED +
						" You Are Entering " + ChatColor.BOLD + " ICEBALL " + ChatColor.DARK_RED +
						"Mode.");
				p.sendMessage(ChatColor.BOLD + "Tip:  " + ChatColor.GREEN + 
						"right-click with various dyes/snowball in your hand to create a different " +
						ChatColor.GOLD + "BEAM");
				p.sendMessage(ChatColor.GOLD + "BEAM!!!  20 block radius.  Did I say beam!?");
				for(int i = 0; i < al.size(); i++) {
					if(al.get(i).getName().equals(p.getName()))
						al.get(i).ateGapple();
				}
				plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					public void run() {
						p.removePotionEffect(PotionEffectType.REGENERATION);
						p.removePotionEffect(PotionEffectType.ABSORPTION);
					}
				}, 2L);
				
			}
		}
		else if(e.getItem().getType() == Material.POTION) {
			if(e.getItem().getData().equals(new MaterialData(Material.POTION, (byte)46))) {
				if(!this.isOutsideArena(p, 20, true)) {
					p.removePotionEffect(PotionEffectType.INVISIBILITY);
					p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 
							rand.nextInt(1200)+600, 2));
				}
			}
		}
	}
	@EventHandler
	public void registerTeamAndWeaponDetection(PlayerInteractEvent e) {
		if(e.getAction() == Action.PHYSICAL) {
			Player p = e.getPlayer();
			if(!this.isOutsideArena(p, 18, true)) { //checks Environment == NORMAL
				Location ploc = e.getClickedBlock().getLocation();
				String whatTeam = "red";
				if(ploc.getX() == (plugin.X-15) && ploc.getZ() == (plugin.Z-15) &&
						ploc.getY() == (plugin.Y+1)) {
					whatTeam = "red";
					//the messages are handled in-game via command blocks.
					//p.sendMessage(ChatColor.RED + "Welcome To The Rogues Team!  Well-balanced Hit-Streaks.");
					Location tp2 = new Location(p.getWorld(), (plugin.X-14.5), (plugin.Y-3), (plugin.Z-14.5));
					tp2.setYaw(-45f);
					tp2.setPitch(15f);
					p.teleport(tp2);
				}
				else if(ploc.getX() == (plugin.X-15) && ploc.getZ() == (plugin.Z+15) &&
						ploc.getY() == (plugin.Y+1)) {
					whatTeam = "purple";
					//p.sendMessage(ChatColor.LIGHT_PURPLE + "Welcome To The Witch Team!  For Potion Addicts.");
					Location tp2 = new Location(p.getWorld(), (plugin.X-14.5), (plugin.Y-3), (plugin.Z+14.5));
					tp2.setYaw(-135f);
					tp2.setPitch(15f);
					p.teleport(tp2);
				}
				else if(ploc.getX() == (plugin.X+15) && ploc.getZ() == (plugin.Z+15) &&
						ploc.getY() == (plugin.Y+1)) {
					whatTeam = "blue";
					/*p.sendMessage(ChatColor.AQUA + "Welcome To The Sorcerors Team!" + ChatColor.GOLD +
							"  For the Mystics " + ChatColor.MAGIC + "&" + ChatColor.GOLD + " Sages.");*/
					Location tp2 = new Location(p.getWorld(), (plugin.X+14.5), (plugin.Y-3), (plugin.Z+14.5));
					tp2.setYaw(135f);
					tp2.setPitch(15f);
					p.teleport(tp2);
				}
				else if(ploc.getX() == (plugin.X+15) && ploc.getZ() == (plugin.Z-15) &&
						ploc.getY() == (plugin.Y+1)) {
					whatTeam = "green";
					/*p.sendMessage(ChatColor.GOLD + "Welcome To Team Clown!" + ChatColor.GREEN +
							"This Class " +
							"Makes the" + ChatColor.DARK_RED + " Joker " + ChatColor.GREEN + "Look Tame.");*/
					Location tp2 = new Location(p.getWorld(), (plugin.X+14.5), (plugin.Y-3), (plugin.Z-14.5));
					tp2.setYaw(45f);
					tp2.setPitch(15f);
					p.teleport(tp2);
				}
				boolean existsInList = false;
				int placeInList = -1;
				for(int i = 0; i < al.size() && placeInList == -1; i++) {
					if(p.getName().equals(al.get(i).getName())) {
						existsInList = true;
						placeInList = i;
					}
				}
				if(existsInList) {
					al.get(placeInList).setTeam(whatTeam);
				}
				else {
					al.add(new HitStreak(p, plugin, whatTeam, this));
				}
			}
			else if(p.isOp() || this.hasBuildPermission(p)) {
				Location clicked = e.getClickedBlock().getLocation();
				if(clicked.getWorld().getEnvironment() == Environment.NORMAL) {
					if((Math.abs(plugin.X-((int)clicked.getX())) == 20) &&
							(Math.abs(plugin.Z-((int)clicked.getZ())) == 20)) {
						this.teleportToSpawn(p);
					}
				}
			}
		}
		else if((e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) &&
				(e.getPlayer().getInventory().getItemInHand().getType() == Material.SNOW_BALL ||
						e.getPlayer().getInventory().getItemInHand().getType() == Material.INK_SACK) &&
				!this.isOutsideArena(e.getPlayer(), 20, true)) {
			boolean hasPower = false;
			for(int i = 0; i < al.size() && !hasPower; i++) {
				if(e.getPlayer().getName().equals(al.get(i).getName())) {
					if(al.get(i).gapple > 0)
						hasPower = true;
				}
			}
			if(hasPower) {
				if((System.currentTimeMillis()-recentBeam) > 5000) {
					recentBeam = System.currentTimeMillis();
					boolean found1 = false;
					for(int i = 0; i < al.size() && !found1; i++) {
						if(al.get(i).getName().equals(e.getPlayer().getName())){
							if(al.get(i).gapple > 0) {
								al.get(i).gapple--;
								ItemStack inH = e.getPlayer().getItemInHand();
								int type99 = 0;
								if(inH.getType() == Material.INK_SACK) {
									String ih1=inH.getData().toString();
									if(ih1.equals("RED DYE(1)")) {
										type99 = 1;
									}
									else if(ih1.equals("PURPLE DYE(5)")) {
										type99 = 2;
									}
									else if(ih1.equals("LIME DYE(10)")) {
										type99 = 3;
									}
									else if(ih1.equals("LIGHT_BLUE DYE(12)")) {
										type99 = 4;
									}
								}
								ArrayList<Player> p3232 = new ArrayList<Player>(plugin.getServer().getOnlinePlayers());
								new Beam(al.get(i), type99, p3232, plugin, al);
								found1=true;
							}
						}
					}
				}
				else {
					e.getPlayer().sendMessage(ChatColor.GRAY + "5 seconds must pass between Beams");
				}
			}
		}
		else if((e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) &&
				e.getPlayer().getInventory().getItemInHand().getType() == Material.STICK &&
				!this.isOutsideArena(e.getPlayer(), 20, true)) {
			ItemStack lli = e.getPlayer().getInventory().getItemInHand();
			int amountRune = lli.getAmount();
			lli.setAmount(amountRune-1);
			e.getPlayer().getInventory().setItemInHand(lli);
			new Leviosa(e.getPlayer(), plugin, false);
		}
		else if((e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) &&
				e.getPlayer().getInventory().getItemInHand().getType() == Material.BANNER &&
				!this.isOutsideArena(e.getPlayer(), 20, true)) {
			if(System.currentTimeMillis() - crystalTime > 30000) {
				crystalTime = System.currentTimeMillis();
				ItemStack lli = e.getPlayer().getInventory().getItemInHand();
				int amountRune = lli.getAmount();
				lli.setAmount(amountRune-1);
				e.getPlayer().getInventory().setItemInHand(lli);
				
				for(int i = 0; i < 9; i++) {
					double iy = i > 3 ? 8-i : i;
					Location basel = new Location(e.getPlayer().getWorld(), plugin.X, plugin.Y-5.3,
							plugin.Z);
					basel = basel.add(0, i/1.55, 0);
					boolean abe = i == 0 ? true : false;
					new RingOfFire(basel, iy/1.7, false, true, e.getPlayer(), abe);
					
				}
				
			}
			else {
				e.getPlayer().sendMessage(ChatColor.RED + 
						"Error; must wait 30 seconds between banner uses");
			}
		}
		else if((e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) &&
				e.getPlayer().getInventory().getItemInHand().getType() == Material.LADDER &&
				!this.isOutsideArena(e.getPlayer(), 20, true)) {
			ItemStack lli = e.getPlayer().getInventory().getItemInHand();
			int amountRune = lli.getAmount();
			lli.setAmount(amountRune-1);
			e.getPlayer().getInventory().setItemInHand(lli);
			e.getPlayer().sendMessage(ChatColor.AQUA + "Welcome To The Gliding Bat" +
					". " + ChatColor.GREEN + "It moves in the direction you're looking," +
					" and to make it stay still, make sure you're holding a snowball.");
			e.getPlayer().sendMessage(ChatColor.GOLD +""+ ChatColor.BOLD + "Enjoy 60 seconds of flight-time"+
					" on the " + ChatColor.AQUA + " invincible " + ChatColor.GOLD + "bat.");
			e.getPlayer().sendMessage(ChatColor.LIGHT_PURPLE + "Toggle the bat's sleep-mode " +
					"by hitting him with a snowball.");
			new Leviosa(e.getPlayer(), plugin, true);
		}
		else if((e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) &&
				e.getPlayer().getInventory().getItemInHand().getType() == Material.TORCH &&
				!this.isOutsideArena(e.getPlayer(), 20, true)) {
			ItemStack lli = e.getPlayer().getInventory().getItemInHand();
			int amountRune = lli.getAmount();
			lli.setAmount(amountRune-1);
			e.getPlayer().getInventory().setItemInHand(lli);
			e.getPlayer().sendMessage(ChatColor.RED + "Ring Of Fire");
			e.getPlayer().sendMessage(ChatColor.GOLD + this.getKeyWord());
			eggTracker.add(e.getPlayer().getLocation());
			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				public void run() {
					eggTracker.remove(0);
				}
			}, 240L);
			this.eggQualify(e.getPlayer());
			new RingOfFire(e.getPlayer());
		}
		else if((e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) &&
				e.getPlayer().getInventory().getItemInHand().getType() == Material.GOLD_SWORD &&
				!this.isOutsideArena(e.getPlayer(), 20, true)) {
			if(System.currentTimeMillis()-swordList > 30000) {
				swordList = System.currentTimeMillis();
				ItemStack lli = e.getPlayer().getInventory().getItemInHand();
				int amountRune = lli.getAmount();
				lli.setAmount(amountRune-1);
				e.getPlayer().getInventory().setItemInHand(lli);
				new JwoodSpecial(e.getPlayer(), plugin, this);
				delayedGive(e.getPlayer(), ibis);
				this.easterEgg(e.getPlayer());
			}
			else {
				e.getPlayer().sendMessage(ChatColor.RED + "Error; 30 seconds must pass between gold-sword uses.");
				e.getPlayer().sendMessage(ChatColor.GOLD + String.valueOf(30-(int)((System.currentTimeMillis()-swordList)/1000)) +
						" Seconds Left.");
			}
		}
		else if((e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) &&
				e.getPlayer().getInventory().getItemInHand().getType() == Material.BLAZE_ROD &&
				!this.isOutsideArena(e.getPlayer(), 20, true)) {
			swordList = System.currentTimeMillis();
			ItemStack lli = e.getPlayer().getInventory().getItemInHand();
			int amountRune = lli.getAmount();
			lli.setAmount(amountRune-1);
			e.getPlayer().getInventory().setItemInHand(lli);
			for(int i = 0; i < al.size(); i++) {
				if(al.get(i).getName().equals(e.getPlayer().getName()))
					al.get(i).blazeSequence();
			}
		}
		else if(e.getAction() == Action.RIGHT_CLICK_BLOCK && !this.isOutsideArena(e.getPlayer(), 20, true)) {
			if(e.getPlayer().getItemInHand().isSimilar(witchBomb)) {
				if(e.getPlayer().getItemInHand().containsEnchantment(Enchantment.LURE)) {
					Block clicked = e.getClickedBlock();
					Location loc = clicked.getLocation();
					ArrayList<Player> players = new ArrayList<Player>(plugin.getServer().getOnlinePlayers());
					for(int i = 0; i < players.size(); i++) {
						if(!this.isOutsideArena(players.get(i), 19, true)) {
							Location l0 = players.get(i).getLocation();
							if(this.within10BlockCube(loc, l0)) {//wont effect spectator area
								final Player pblind = players.get(i);
								final Player pblindShooter = e.getPlayer();
								if(!pblindShooter.getName().equals(pblind.getName())) {
									plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
										public void run() {
											pblind.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 200, 5));
											pblind.sendMessage(ChatColor.AQUA + "You've been hit by " +
												ChatColor.GOLD + pblindShooter.getName() + "\'s " + ChatColor.AQUA +
													"Blindness bomb!  You're blinded for " + ChatColor.RED + "10" +
													ChatColor.AQUA + " seconds.");
										}
									}, 40L);
								}
							}
						}
					}
				}
			}
			else if(e.getPlayer().getItemInHand().isSimilar(sorcerorBomb)) {
				if(e.getPlayer().getItemInHand().containsEnchantment(Enchantment.ARROW_FIRE)) {
					Block clicked = e.getClickedBlock();
					Location loc = clicked.getLocation();
					ArrayList<Player> players = new ArrayList<Player>(plugin.getServer().getOnlinePlayers());
					for(int i = 0; i < players.size(); i++) {
						if(!this.isOutsideArena(players.get(i), 19, true)) {
							Location l0 = players.get(i).getLocation();
							if(this.within10BlockCube(loc, l0)) {//wont effect spectator area
								final Player pblind = players.get(i);
								final Player pblindShooter = e.getPlayer();
								if(!pblindShooter.getName().equals(pblind.getName())) {
									plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
										public void run() {
											pblind.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 240, 4));
											pblind.sendMessage(ChatColor.AQUA + "You've been hit by " +
													ChatColor.GOLD + pblindShooter.getName() + "\'s " + ChatColor.AQUA +
													"Slow-Mo bomb!  You're in slow-mo for " + ChatColor.RED + "12" +
													ChatColor.AQUA + " seconds.");
										}
									}, 40L);
								}
							}
						}
					}
				}
			}else if(e.getPlayer().getItemInHand().isSimilar(hungerRune)) {
				e.setCancelled(true);
				ItemStack lli = e.getPlayer().getInventory().getItemInHand();
				int amountRune = lli.getAmount();
				lli.setAmount(amountRune-1);
				e.getPlayer().getInventory().setItemInHand(lli);
				this.hungerBomb(e.getPlayer(), e.getClickedBlock());
			}
		}
	}
	public boolean within10BlockCube(Location base, Location test) {
		if(test.getY() >= plugin.Y-1)
			return false;
		double xDist = Math.abs(base.getX()-test.getX());
		double zDist = Math.abs(base.getZ()-test.getZ());
		if(xDist < 5 && zDist < 5)
			return true;
		else
			return false;
	}
	public boolean within6BlockCube(Location base, Location test) {
		if(test.getY() >= plugin.Y-1)
			return false;
		double xDist = Math.abs(base.getX()-test.getX());
		double zDist = Math.abs(base.getZ()-test.getZ());
		double yDist = Math.abs(base.getY()-test.getY());
		if(xDist < 3 && zDist < 3 && yDist < 5)
			return true;
		else
			return false;
	}
	@EventHandler
	public void noPvp(EntityDamageByEntityEvent e) {
		if(e.getEntity() instanceof Player) {
			Location loc = e.getEntity().getLocation();
			if(e.getDamage() > 0) {
				e.setCancelled(this.inSnowballRange(loc)); //snowball range checks for overworld
			}
			if(e.getDamager() instanceof Wolf && !this.isOutsideArena(((Player)e.getEntity()), 20, true)) {
				Player chompee = (Player)e.getEntity();
				chompee.sendMessage(ChatColor.BOLD + "Wizard Jr." +
						ChatColor.YELLOW + " Took a Chunk Out Of You. This Causes " + ChatColor.GREEN +
						"Nausia " + ChatColor.YELLOW + "and " + ChatColor.GREEN + "Hunger" +
						ChatColor.YELLOW + ".");
				chompee.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 200, 1));
				chompee.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 300, 2));
			}
			else {
				if(this.inSnowballRange(loc) && e.getDamager() instanceof Snowball &&
						loc.getWorld().getEnvironment() == Environment.NORMAL) {
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
										//Bukkit.broadcastMessage(ChatColor.RED + "Ice Ball Recognized");
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
					else {
						for(int i = 0; i < al.size(); i++) {
							if(al.get(i).getName().equals(e.getEntity().getName()))
								al.get(i).setHitStreak(0);
							
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
				new AnimatePlayer(p7, xVelFin, zVelFin, plugin);
			}
		}, (long)1);
	}
	public void clearPotions(Player p) {
		for(PotionEffect effects : p.getActivePotionEffects())
			p.removePotionEffect(effects.getType());
	}
	public void hungerBomb(Player p, Block b) {
		final Player ppp = p;
		final Block bbb = b;
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run() {
				ArrayList<Player> players = new ArrayList<Player>(plugin.getServer().getOnlinePlayers());
				for(int i = 0; i < players.size(); i++) {
					if(!this.isOutsideArena(players.get(i), 19, true)) {
						if(this.shareXOrZ(ppp, players.get(i), bbb)) { //wont effect spectator area
							if(players.get(i).getFoodLevel() > 2)
								players.get(i).setFoodLevel(2);
							players.get(i).sendMessage(ChatColor.AQUA + "You've been hit by " +
									ChatColor.GOLD + ppp.getName() + "\'s " + ChatColor.AQUA +
									"Hunger-Rune!  Your hunger bar has been reduced to " + ChatColor.RED +
									"1" + ChatColor.AQUA + " bars.");
						}
					}
				}
			}	
			public boolean shareXOrZ(Player immune, Player test, Block bb) {
				if(immune.getName().equals(test.getName()))
					return false;
				Location tloc = test.getLocation();
				Location bloc = bb.getLocation();
				if(tloc.getX() > (bloc.getX()-2) && tloc.getX() < (bloc.getX()+2) ||
					tloc.getZ() > (bloc.getZ()-2) && tloc.getZ() < (bloc.getZ()+2)) {
					return true;
				}
				return false;
			}
			public boolean isOutsideArena(Player pp, int constant, boolean checkY) {
				Location loc = pp.getLocation();
				if(loc.getWorld().getEnvironment() != Environment.NORMAL)
					return true;
				int x = (int)loc.getX();
				int y = (int)loc.getY();
				int z = (int)loc.getZ();
				if(checkY) {
					if(y < (plugin.Y+5) && y > (plugin.Y-14)) { /*Bukkit.broadcastMessage("Proper y"); */}
					else
						return true;
				}
				if(x > (plugin.X-constant) && x < (plugin.X+constant) &&
						z > (plugin.Z-constant) && z < (plugin.Z+constant)) {
					return false;
				}
				else {
					return true;
				}
			}
		}, 10L);
		new ParticleFoodBomb(b.getLocation());
	}
	public void eggQualify(Player p) {
		if(eggTracker.size() < 8) {
			//do nothing
		}
		else {
			boolean victory = false;
			boolean never = false, eat = false, soggy = false, waffles = false;
			for(int i = 0; i < eggTracker.size(); i++) {
				if(eggTracker.get(i).getZ() < (plugin.Z-13) &&
						eggTracker.get(i).getZ() > (plugin.Z-20) &&
						eggTracker.get(i).getX() > (plugin.X-20) &&
						eggTracker.get(i).getX() < (plugin.X-9)) { never = true; }
				if(eggTracker.get(i).getZ() > (plugin.Z-13) &&
						eggTracker.get(i).getZ() < (plugin.Z-7) &&
						eggTracker.get(i).getX() > (plugin.X-20) &&
						eggTracker.get(i).getX() < (plugin.X-9)) { soggy = true; }
				if(eggTracker.get(i).getX() > (plugin.X-13) &&
						eggTracker.get(i).getX() < (plugin.X-7) &&
						eggTracker.get(i).getZ() > (plugin.Z-20) &&
						eggTracker.get(i).getZ() < (plugin.Z-9)) { eat = true; }
				if(eggTracker.get(i).getX() < (plugin.X-13) &&
						eggTracker.get(i).getX() > (plugin.X-20) &&
						eggTracker.get(i).getZ() > (plugin.Z-20) &&
						eggTracker.get(i).getZ() < (plugin.Z-9)) { waffles = true; }
			}
			if(never && eat && soggy && waffles)
				victory = true;
			if(victory && System.currentTimeMillis()-eggTime > 10000) {
				ItemStack is = new ItemStack(Material.GOLD_SWORD, 1);
				ItemMeta im = is.getItemMeta();
				im.setDisplayName(ChatColor.DARK_RED + "Jwood's Portal");
				ArrayList<String> lore = new ArrayList<String>();
				lore.add(ChatColor.DARK_GREEN + "Forged In Jwood's Cave");
				lore.add(ChatColor.DARK_PURPLE + "Summon Inventory-Clearing");
				lore.add(ChatColor.DARK_BLUE + "" + ChatColor.MAGIC + "Hell-Hounds");
				im.setLore(lore);
				is.setItemMeta(im);
				final Player theP = p;
				final ItemStack is7 = is;
				plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					public void run() {	
						theP.getInventory().addItem(is7);
						theP.sendMessage(ChatColor.BLUE + "Easter Egg " + ChatColor.GOLD + "Obtained.");
					}
				}, 5L);
				eggTime = System.currentTimeMillis();
			}
			else if(victory) {
				p.sendMessage(ChatColor.GOLD + "10 seconds must pass between Egg-Attempts.");
			}
		}
	}
	public String getKeyWord() {
		/*
		 * Place the ring of fires in a circle around red team's tower
		 */
		switch(rand.nextInt(14)) {
		case 0:
			return "Place";
		case 1:
			return "the";
		case 2:
			return "fire";
		case 3:
			return "rings";
		case 4:
			return "in";
		case 5:
			return "a";
		case 6:
			return "full";
		case 7:
			return "circular";
		case 8:
			return "pattern";
		case 9:
			return "around";
		case 10:
			return "red";
		case 11:
			return "teams";
		case 12:
			return "tower";
		case 13:
			return "-Jwood9198";
		}
		return "";
	}
	public void createWitchBomb() {
		witchBomb = new ItemStack(Material.FIREWORK, 1);
		FireworkMeta fm = (FireworkMeta) witchBomb.getItemMeta();
		ArrayList<Color> alColor = new ArrayList<Color>();
		alColor.add(Color.PURPLE);
		alColor.add(Color.WHITE);
		alColor.add(Color.MAROON);
		ArrayList<Color> alFade = new ArrayList<Color>();
		alFade.add(Color.SILVER);
		fm.addEffects(FireworkEffect.builder().trail(true).withColor(alColor).withFade(alFade).with(Type.BALL_LARGE).build());
		fm.setPower(0);
		fm.setDisplayName(ChatColor.LIGHT_PURPLE + "Headless-Chicken Bomb");
//####################################################################
		ArrayList<String> lore = new ArrayList<String>();
		lore.add(ChatColor.BLUE + "Made By a Cult Of Witches In a Cave");
		lore.add(ChatColor.RED + "They Call It The" + ChatColor.LIGHT_PURPLE + " Headless-Chicken Bomb");
		fm.setLore(lore);
//####################################################################
		fm.addEnchant(Enchantment.LURE, 1, false);
		witchBomb.setItemMeta(fm);
	}
	public void createSorcerorBomb() {
		sorcerorBomb = new ItemStack(Material.FIREWORK, 1);
		FireworkMeta fm = (FireworkMeta) sorcerorBomb.getItemMeta();
		ArrayList<Color> alColor = new ArrayList<Color>();
		alColor.add(Color.NAVY);
		alColor.add(Color.BLACK);
		alColor.add(Color.ORANGE);
		ArrayList<Color> alFade = new ArrayList<Color>();
		alFade.add(Color.RED);
		fm.addEffects(FireworkEffect.builder().trail(true).withColor(alColor).withFade(alFade).with(Type.STAR).build());
		fm.setPower(0);
		fm.setDisplayName(ChatColor.AQUA + "The_Lag");
//####################################################################
		ArrayList<String> lore = new ArrayList<String>();
		lore.add(ChatColor.RED + "Sss-ll-oo-wwwww  mm-oooo");
		lore.add(ChatColor.GOLD + "Sorcery");
		fm.setLore(lore);
//####################################################################
		fm.addEnchant(Enchantment.ARROW_FIRE, 1, false);
		sorcerorBomb.setItemMeta(fm);
	}
	public void delayedGive(Player z, ItemStack zis) {
		//the purpose of this method is to fix the bug where an item is given to you,
		//but it doesn't appear in your inventory
		final Player p=z;
		final ItemStack is = zis;
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run() {
				p.getInventory().addItem(is);
			}
		}, 5L);
	}
	public void createIbis() {
		ItemMeta im = ibis.getItemMeta();
		im.addEnchant(Enchantment.ARROW_INFINITE, 1, false);
		im.setDisplayName(ChatColor.BLUE + "" + ChatColor.BOLD + "Ice_Ball");
		ArrayList<String> alLore = new ArrayList<String>();
		alLore.add(ChatColor.RED + "Snow Dug From");
		alLore.add(ChatColor.GOLD + "Plastic Beach");
		im.setLore(alLore);
		ibis.setItemMeta(im);
	}
	public int countArrows(Player p) {
		int arrowAccumulator = 0;
		Inventory inv = p.getInventory();
		ItemStack[] invis = inv.getContents();
		for(int i = 0; i < invis.length; i++) {
			if(invis[i] != null) {
				if(invis[i].getType() == Material.ARROW) {
					arrowAccumulator += invis[i].getAmount();
				}
			}
		}
		return arrowAccumulator;
	}
	public void easterEgg(Player t) {
		int lineCount = 0;
		Scanner sin;
		if(file.exists()) {
			try{
				sin = new Scanner(file);
				while(sin.hasNext()) {
					sin.nextLine();
					lineCount++;
				}
			}catch(IOException e) { e.printStackTrace(); }
		}
		if(!file.exists() || lineCount == 2) {
			try {
				pw = new PrintWriter(file);
				pw.println("Jwood's Easter Egg Tracker");
				pw.flush();
				pw.println("==========================");
				pw.flush();
				pw.println(t.getName());
				pw.close();
			}catch(IOException e) {	e.printStackTrace(); }
			Bukkit.broadcastMessage(ChatColor.BLACK + "[" + ChatColor.LIGHT_PURPLE +
					"Acheivement" + ChatColor.BLACK + "]" + ChatColor.GOLD + " " +
					t.getName() + ChatColor.DARK_RED + " Has Earned The Acheivement" +
					ChatColor.AQUA + " Summoning The CrystalDragon");
		}
		else {
			try{
				sin = new Scanner(file);
				sin.nextLine();
				sin.nextLine();
				boolean isNew = true;
				for(int i = 0; i < lineCount-2; i++) {
					if(t.getName().equals(sin.nextLine())) {
						isNew = false;
					}
				}
				if(isNew) {
					FileWriter fw = new FileWriter("IceBallEasterEgg.txt", true);
					pw = new PrintWriter(fw);
					pw.println(t.getName());
					pw.close();
					Bukkit.broadcastMessage(ChatColor.BLACK + "[" + ChatColor.LIGHT_PURPLE +
							"Acheivement" + ChatColor.BLACK + "]" + ChatColor.GOLD + " " +
							t.getName() + ChatColor.DARK_RED + " Has Earned The Acheivement" +
							ChatColor.AQUA + " Summoning The CrystalDragon");
				}
			}catch(IOException e) { e.printStackTrace(); }
		}
	}
}
