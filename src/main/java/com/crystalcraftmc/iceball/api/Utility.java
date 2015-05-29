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

package com.crystalcraftmc.iceball.api;

import com.crystalcraftmc.iceball.main.IceBall;
import com.crystalcraftmc.iceball.main.Snowball.InventoryResult;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;


/**This class provides handy utility methods for Snowball*/
public class Utility {

	
	/**This method checks the inventory of the player; as to
	 * whether it's clear, or is polluted, or has armor pollution
	 * @param p the player we're testing
	 * @return InventoryResult result of this test
	 */
	public static IceBall.InventoryResult testInventory(Player p) {
		PlayerInventory pi = p.getInventory();
		if(pi.getHelmet() != null)
			return InventoryResult.ARMOR_POLLUTION;
		if(pi.getChestplate() != null)
			return InventoryResult.ARMOR_POLLUTION;
		if(pi.getLeggings() != null)
			return InventoryResult.ARMOR_POLLUTION;
		if(pi.getBoots() != null)
			return InventoryResult.ARMOR_POLLUTION;
		
		ItemStack is[] = pi.getContents();
		for(int i = 0; i < is.length; i++) {
			if(is[i] != null) {
				if(is[i].getType() != Material.AIR) {
					return InventoryResult.POLLUTED;
				}
			}
		}
		return InventoryResult.CLEAR;
	}
	
	

	
	/**Checks whether a given coordinate is inside of the snowball area
	 * @param Location, the location we're teleporting to
	 * @param Snowball plugin
	 * @param isClearTP, a boolean - true if we're checking the clearTP area
	 * @param plugin
	 * @return boolean, true if the coordinates are inside the area
	 */
	public static boolean isInsideSnowball(Location loc, IceBall plugin, boolean isClearTP) {
		if(loc.getWorld().getEnvironment() != Environment.NORMAL)
			return false;
		int x = (int)loc.getX();
		int y = (int)loc.getY();
		int z = (int)loc.getZ();
		int lowX, highX, lowY, highY, lowZ, highZ;
		if(plugin.iceballArea[0] < plugin.iceballArea[3]) {
			lowX = plugin.iceballArea[0];
			highX = plugin.iceballArea[3];
		}
		else {
			lowX = plugin.iceballArea[3];
			highX = plugin.iceballArea[0];
		}
		
		if(plugin.iceballArea[1] < plugin.iceballArea[4]) {
			lowY = plugin.iceballArea[1];
			highY = plugin.iceballArea[4];
		}
		else {
			lowY = plugin.iceballArea[4];
			highY = plugin.iceballArea[1];
		}
		
		if(plugin.iceballArea[2] < plugin.iceballArea[5]) {
			lowZ = plugin.iceballArea[2];
			highZ = plugin.iceballArea[5];
		}
		else {
			lowZ = plugin.iceballArea[5];
			highZ = plugin.iceballArea[2];
		}
		
		if(isClearTP) {
			int cl = plugin.clearLimit;
			if(x >= lowX+cl && x <= highX-cl &&
					y >= lowY+cl && y <= highY-cl &&
					z >= lowZ+cl && z <= highZ-cl) {
				return true;
			}
		}
		else {
			if(x >= lowX && x <= highX &&
					y >= lowY && y <= highY &&
					z >= lowZ && z <= highZ) {
				return true;
			}
		}
		return false;
	}
	
	/**Tests that a String is a valid int value
	 * @param String varargs the string(s) we're testing
	 * @return boolean; true if the string(s) is an int
	 */
	public static boolean isInt(String... args) {
		try{
			for(String test : args)
				Integer.parseInt(test);
			return true;
		}catch(NumberFormatException e) { return false; }
	}
	
}
