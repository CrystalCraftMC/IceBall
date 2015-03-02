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
import org.bukkit.World.Environment;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.Metrics;

import java.io.IOException;

public class IceBall extends JavaPlugin {
	//TEAM RED/PURPLE MUST BE TOWARDS THE WEST FROM /SNOWFIGHT LOC PERSPECTIVE
	//ARENA MUST BE IN OVERWORLD
	public final int X = -31;
	public final int Y = 70;
	public final int Z = -32;
	public final int SPAWNX = -66;
	public final int SPAWNY = 32;
	public final int SPAWNZ = 95;
	public final int FIGHTPITCH = 15;
	public final int FIGHTYAW = -90;
	
	public void onEnable() {
		this.getLogger().info("IceBall enabled.");
		new IceBallListener(this);

        try {
            Metrics metrics = new Metrics(this);
            metrics.start();
        } catch (IOException e) {
            // Failed to submit the stats :-(
        }
	}
	public void onDisable() {
		this.getLogger().info("IceBall disabled.");
	}
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) {
			Player p = (Player)sender;
			if(p.getLocation().getWorld().getEnvironment() != Environment.NORMAL)
				p.sendMessage(ChatColor.LIGHT_PURPLE + "You must be in the overworld to use these commands");
			if(label.equalsIgnoreCase("snowfight"))
				return true;
			else if(label.equalsIgnoreCase("snowleave"))
				return true;
			else if(label.equalsIgnoreCase("snowbuild"))
				return true;
			
		}
		return false;
	}
}
