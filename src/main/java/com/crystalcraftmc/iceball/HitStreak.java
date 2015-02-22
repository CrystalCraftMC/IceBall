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

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class HitStreak {
	private String name;
	private int hitStreak;
	private Player p;
	public boolean isImmune;
	private IceBall plugin;
	public HitStreak(Player p, IceBall plugin) {
		this.plugin = plugin;
		name = p.getName();
		this.p = p;
		hitStreak = 1;
		isImmune = false;
	}
	public String getName() {
		return name;
	}
	public void anotherSnipe() { //only called if shooter is inside snowball arena
		hitStreak++;
		if(hitStreak == 5) {
			p.sendMessage(ChatColor.LIGHT_PURPLE + "5 Hit Streak! Here's a couple " + ChatColor.GREEN +
					"EnderPearls.");
			p.getInventory().addItem(new ItemStack(Material.ENDER_PEARL, 2));
		}
		if(hitStreak == 10) {
			p.sendMessage(ChatColor.LIGHT_PURPLE + "10 Hit Streak!!  You're Immune from Snowballs" +
					" for 7 seconds");
			isImmune = true;
			p.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				public void run() {
					isImmune = false;
				}
			}, (long)140);
		}
		if(hitStreak == 15) {
			p.sendMessage(ChatColor.YELLOW + "15 Hit Streak!!!  You gain 4 legendary " +
					ChatColor.AQUA + "IceBalls.");
			ItemStack is = new ItemStack(Material.SNOW_BALL, 4);
			ItemMeta im = is.getItemMeta();
			im.addEnchant(Enchantment.ARROW_INFINITE, 1, false);
			im.setDisplayName("Ice_Ball");
			ArrayList<String> alLore = new ArrayList<String>();
			alLore.add(ChatColor.RED + "Snow Dug From");
			alLore.add(ChatColor.GOLD + "Plastic Beach");
			im.setLore(alLore);
			is.setItemMeta(im);
			p.getInventory().addItem(is);
		}
	}
	public int getHitStreak() {
		return hitStreak;
	}
	public void setHitStreak(int hs) {
		hitStreak = hs;
	}
	public void reset() {
		hitStreak = 0;
		isImmune = false;
	}
}
//
