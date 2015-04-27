package com.crystalcraftmc.snowball.main;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.crystalcraftmc.snowball.api.Utility;

/**Handles events related to gameplay*/
public class GameplayListener implements Listener {
	
	Snowball plugin;
	
	public GameplayListener(Snowball plugin) {
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void tpBelow(PlayerInteractEvent e) {
		if(e.getAction() == Action.PHYSICAL) {
			Block b = e.getClickedBlock();
			if(Utility.isInsideSnowball(b.getLocation(), plugin, false)) {
				if(b.getType() == Material.IRON_PLATE) {
					Player p = e.getPlayer();
					int centerx = (plugin.snowballArea[0]+plugin.snowballArea[3])/2;
					int centerz = (plugin.snowballArea[2]+plugin.snowballArea[5])/2;
					int xOff = centerx > p.getLocation().getX() ? 1 : -1;
					int zOff = centerz > p.getLocation().getZ() ? 1 : -1;
					Location tp = p.getLocation().add(xOff, -3, zOff);
					tp.setPitch(15);
					if(xOff > 0 && zOff > 0)
						tp.setYaw(-45);
					else if(xOff > 0 && zOff < 0)
						tp.setYaw(-135);
					else if(xOff < 0 && zOff > 0)
						tp.setYaw(45);
					else if(xOff < 0 && zOff < 0)
						tp.setYaw(135);
					p.teleport(tp);
				}
			}
		}
	}
	
	@EventHandler
	public void noPvp(EntityDamageByEntityEvent e) {
		if(e.getEntity() instanceof Player) {
			Player p = (Player)e.getEntity();
			if(Utility.isInsideSnowball(p.getLocation(), plugin, false)) {
				if(e.getDamage() > 0) {
					e.setCancelled(true);
				}
			}
		}
	}
	
}
