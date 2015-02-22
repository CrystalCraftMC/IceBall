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

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class IceBall extends JavaPlugin {
	public final int X = -1;
	public final int Y = 73;
	public final int Z = -384;
	public final int SPAWNX = -66;
	public final int SPAWNY = 32;
	public final int SPAWNZ = 95;
	public final int FIGHTPITCH = 15;
	public final int FIGHTYAW = -90;
	
	public void onEnable() {
		this.getLogger().info("IceBall enabled.");
		new IceBallListener(this);
	}
	public void onDisable() {
		this.getLogger().info("IceBall disabled.");
	}
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) {
			if(label.equalsIgnoreCase("snowfight"))
				return true;
			else if(label.equalsIgnoreCase("snowleave"))
				return true;
			
		}
		return false;
	}
}
//
