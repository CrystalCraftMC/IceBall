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

package com.crystalcraftmc.iceball.main;


import com.crystalcraftmc.iceball.api.Utility;
import com.crystalcraftmc.iceball.main.IceBall.InventoryResult;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;

/**Handles events related to limiting what can be done*/
public class IceBallLimitListener implements Listener {
	
	private IceBall plugin;
	
	public IceBallLimitListener(IceBall plugin) {
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	
	@EventHandler
	public void snowballNoItemInOrOutTP(PlayerTeleportEvent e) {
		if(plugin.hasSnowballPerms(e.getPlayer()))
				return;
		//1. clear inventory if tping out of library
		//2. permit tping into library only if clear inventory
		if(Utility.isInsideSnowball(e.getPlayer().getLocation(), plugin) && 
				e.getPlayer().getWorld().getEnvironment() == Environment.NORMAL) {
			e.getPlayer().getInventory().clear();
		}
		else if(Utility.isInsideSnowball(e.getTo(), plugin)) {
			Player p = e.getPlayer();
			InventoryResult ir = Utility.testInventory(p);
			if(ir == InventoryResult.ARMOR_POLLUTION) {
				p.sendMessage(ChatColor.GOLD + "Error; you need a clear inventory " +
						"to enter the IceBall arena (armor slots included)");
				e.setCancelled(true);
			}
			else if(ir == InventoryResult.POLLUTED) {
				p.sendMessage(ChatColor.GOLD + "Error; you need a clear inventory " +
						"to enter the IceBall arena.");
				e.setCancelled(true);
			}
			else if(ir == InventoryResult.CLEAR) {
				for(ItemStack clear : plugin.noPT) {
					e.getPlayer().setItemInHand(clear);
					e.getPlayer().performCommand("pt");
					e.getPlayer().getInventory().clear();
				}
			}
		}
	}
	
	
	
	
	@EventHandler
	public void noBreak(BlockBreakEvent e) {
		if(Utility.isInsideSnowball(e.getBlock().getLocation(), plugin)) {
			if(!plugin.hasSnowballPerms(e.getPlayer())) {
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void noPlace(BlockPlaceEvent e) {
		if(Utility.isInsideSnowball(e.getBlock().getLocation(), plugin)) {
			if(!plugin.hasSnowballPerms(e.getPlayer())) {
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void noCommand(PlayerCommandPreprocessEvent e) {
			if(!plugin.hasSnowballPerms(e.getPlayer()) &&
					Utility.isInsideSnowball(e.getPlayer().getLocation(), plugin)) {
			String cmd = e.getMessage();
			if(cmd == null)
				return;
			cmd = cmd.indexOf(" ") != -1 ? cmd.substring(0, cmd.indexOf(" ")) : cmd;
			if(cmd.charAt(0) == '/' && cmd.length() > 1)
				cmd = cmd.substring(1);
			for(int i = 0; i < plugin.validCommands.length; i++) {
				if(cmd.equalsIgnoreCase(plugin.validCommands[i]))
					return;
			}
			e.setCancelled(true);
			e.getPlayer().sendMessage(ChatColor.DARK_AQUA + "You are only permitted to use /spawn, /home, and " +
					"/warp while in the IceBall arena.");
		}
	}
	
	@EventHandler
	public void blastProt(EntityExplodeEvent e) {
		if(e.getLocation().getWorld().getEnvironment() == Environment.NORMAL){
			if(Utility.isInsideSnowball(e.getLocation(), plugin))
				e.setCancelled(true); 		//impenetrable against tnt cannons
		}
	}
	
	@EventHandler
	public void noFallDeath(EntityDamageEvent e) {
		if(e.getEntity() instanceof Player) {
			Player p = (Player)e.getEntity();
			if(e.getCause().equals(DamageCause.FALL)) {
				Location loc = p.getLocation();
				if(loc.getWorld().getEnvironment() == Environment.NORMAL) {
					if(Utility.isInsideSnowball(loc, plugin))
						e.setCancelled(true);
				}
			}
		}
	}
	
}
