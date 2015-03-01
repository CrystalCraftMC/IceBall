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

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

public class DelayedTeleport implements Listener {
	private IceBall plugin;
	private boolean hasMoved;
	private int accumulator;
	private final Player thePlayer;
	private long startTime;
	private double playerX, playerY, playerZ;
	public DelayedTeleport(IceBall ib, Player p) {
		Location llo = p.getLocation();
		playerX = llo.getX();
		playerY = llo.getY();
		playerZ = llo.getZ();
		startTime = System.currentTimeMillis();
		thePlayer = p; //final variable needed for run method
		accumulator = 0;
		plugin = ib;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		hasMoved = false;
		p.sendMessage(ChatColor.GOLD + "Teleportation will commence in " + ChatColor.RED + "5 seconds" +
				ChatColor.GOLD + ". don't move.");
		p.sendMessage(ChatColor.AQUA + "Any potion effects will be removed upon arrival.");
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run() {
				if(!hasMoved) {
					for(PotionEffect effects : thePlayer.getActivePotionEffects())
						thePlayer.removePotionEffect(effects.getType());
					Location locS = new Location(thePlayer.getWorld(), (double)plugin.X,
							(double)(plugin.Y+1), (double)plugin.Z);
					locS.setYaw(plugin.FIGHTYAW);
					locS.setPitch(plugin.FIGHTPITCH);
					thePlayer.teleport(locS);
					thePlayer.getInventory().clear();
					final ItemStack[] arenaItemList = { new ItemStack(Material.SNOW_BALL, 1),
							new ItemStack(Material.COOKED_BEEF, 1), new ItemStack(Material.POTION, 1),
							new ItemStack(Material.ENDER_PEARL, 1), new ItemStack(Material.WOOL, 1),
							new ItemStack(Material.INK_SACK, 1), new ItemStack(Material.CARPET, 1),
							new ItemStack(Material.MUTTON, 1), new ItemStack(Material.LADDER, 1),
							new ItemStack(Material.FLINT_AND_STEEL, 1), new ItemStack(Material.FIREWORK, 1),
							new ItemStack(Material.COOKED_MUTTON, 1), new ItemStack(Material.STICK),
							new ItemStack(Material.SNOW_BLOCK, 1), new ItemStack(Material.PAINTING, 1),
							new ItemStack(Material.GOLDEN_APPLE, 1), new ItemStack(Material.BOOK_AND_QUILL, 1),
							new ItemStack(Material.WRITTEN_BOOK, 1), new ItemStack(Material.BOOK, 1),
							new ItemStack(Material.STRING, 1), new ItemStack(Material.FLINT, 1),
							new ItemStack(Material.TORCH, 1), new ItemStack(Material.BANNER, 1),
							new ItemStack(Material.BANNER, 1),
							new ItemStack(Material.SLIME_BALL, 1), new ItemStack(Material.ARROW, 1),
							new ItemStack(Material.BOW, 1), new ItemStack(Material.FEATHER, 1),
							new ItemStack(Material.GOLD_SWORD), new ItemStack(Material.SKULL_ITEM),
							new ItemStack(Material.SULPHUR), new ItemStack(Material.FIREWORK_CHARGE),
							new ItemStack(Material.SLIME_BLOCK), new ItemStack(Material.BLAZE_ROD),
							new ItemStack(Material.COAL), new ItemStack(Material.COAL_BLOCK)};
					for(int i = 0; i < arenaItemList.length; i++) {
							thePlayer.setItemInHand(arenaItemList[i]);
							thePlayer.performCommand("pt");
					}
					thePlayer.getInventory().clear();
					thePlayer.sendMessage(ChatColor.GOLD + "Welcome to the" + ChatColor.AQUA +
							" CCMC " + ChatColor.GOLD + "SnowBall Arena!");
					thePlayer.sendMessage(ChatColor.RED + "Note that commands have been disabled here.");
					thePlayer.sendMessage(ChatColor.RED + "Type " + ChatColor.BOLD + "/snowleave" +
							ChatColor.RED + "  to leave the arena.");
					plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
						public void run() {
								if(thePlayer.getInventory().getHelmet() != null)
									thePlayer.getInventory().getHelmet().setType(Material.SNOW_BALL);
								if(thePlayer.getInventory().getChestplate() != null)
									thePlayer.getInventory().getChestplate().setType(Material.SNOW_BALL);
								if(thePlayer.getInventory().getLeggings() != null)
									thePlayer.getInventory().getLeggings().setType(Material.SNOW_BALL);
								if(thePlayer.getInventory().getBoots() != null)
									thePlayer.getInventory().getBoots().setType(Material.SNOW_BALL);
						}
					}, (long)2L);
				}
			}
		}, (long)100);
		
	}
	@EventHandler
	public void moveEvent(PlayerMoveEvent e) {
		if(e.getPlayer().getName().equals(thePlayer.getName()) &&
			(System.currentTimeMillis()-startTime) <= 5500) {
			if(playerHasDifferentCoords()) {
				hasMoved = true; //extra half-second for safety
				accumulator++;
				if(accumulator == 1 && (System.currentTimeMillis()-startTime) <= 4900)
					e.getPlayer().sendMessage(ChatColor.RED + "Teleport cancelled due to your movement.");
				if(accumulator > 100)
					accumulator = 10;
			}
		}
	}
	public boolean playerHasDifferentCoords() {
		Location currentLoc = thePlayer.getLocation();
		if(playerX < (currentLoc.getX()-.1) || playerX > (currentLoc.getX() +.1) ||
				playerZ < (currentLoc.getZ()-.1) || playerZ > (currentLoc.getZ() +.1) ||
				playerY < (currentLoc.getY()-.1) || playerY > (currentLoc.getY() +.1)) {
			return true;
		}
		return false;
	}
	public DelayedTeleport(IceBall ib, Player p, boolean fromSnowBuild) {
		thePlayer = p; //final variable needed for run method
		if(fromSnowBuild) {
			Location llo = p.getLocation();
			playerX = llo.getX();
			playerY = llo.getY();
			playerZ = llo.getZ();
			startTime = System.currentTimeMillis();
			
			accumulator = 0;
			plugin = ib;
			plugin.getServer().getPluginManager().registerEvents(this, plugin);
			hasMoved = false;
			p.sendMessage(ChatColor.GOLD + "Teleportation will commence in " + ChatColor.RED + "5 seconds" +
				ChatColor.GOLD + ". don't move.");
			p.sendMessage(ChatColor.AQUA + "TP allowed to ops & creators.");
			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run() {
				if(!hasMoved) {
						Location locS = new Location(thePlayer.getWorld(), (double)plugin.X+19,
							(double)(plugin.Y-7), (double)plugin.Z);
						locS.setYaw(plugin.FIGHTYAW);
						locS.setPitch(plugin.FIGHTPITCH);
						thePlayer.teleport(locS);
						thePlayer.sendMessage(ChatColor.RED + "Tp Complete.");
						thePlayer.sendMessage(ChatColor.RED + "Type " + ChatColor.BOLD + "/snowleave" +
							ChatColor.RED + "  to leave the arena.");
					}
				}
			}, (long)100);
		}
	}
}
