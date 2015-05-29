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

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.crystalcraftmc.iceball.main.Snowball;
import com.crystalcraftmc.iceball.main.Snowball.InventoryResult;


/**This class provides handy utility methods for Snowball*/
public class Utility {

	
	/**This method checks the inventory of the player; as to
	 * whether it's clear, or is polluted, or has armor pollution
	 * @param p the player we're testing
	 * @return InventoryResult result of this test
	 */
	public static InventoryResult testInventory(Player p) {
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
	 * @return boolean, true if the coordinates are inside the area
	 */
	public static boolean isInsideSnowball(Location loc, Snowball plugin, boolean isClearTP) {
		if(loc.getWorld().getEnvironment() != Environment.NORMAL)
			return false;
		int x = (int)loc.getX();
		int y = (int)loc.getY();
		int z = (int)loc.getZ();
		int lowX, highX, lowY, highY, lowZ, highZ;
		if(plugin.snowballArea[0] < plugin.snowballArea[3]) {
			lowX = plugin.snowballArea[0];
			highX = plugin.snowballArea[3];
		}
		else {
			lowX = plugin.snowballArea[3];
			highX = plugin.snowballArea[0];
		}
		
		if(plugin.snowballArea[1] < plugin.snowballArea[4]) {
			lowY = plugin.snowballArea[1];
			highY = plugin.snowballArea[4];
		}
		else {
			lowY = plugin.snowballArea[4];
			highY = plugin.snowballArea[1];
		}
		
		if(plugin.snowballArea[2] < plugin.snowballArea[5]) {
			lowZ = plugin.snowballArea[2];
			highZ = plugin.snowballArea[5];
		}
		else {
			lowZ = plugin.snowballArea[5];
			highZ = plugin.snowballArea[2];
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
