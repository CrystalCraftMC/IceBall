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
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.text.DecimalFormat;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Wool;

public class AirStrike implements Listener {
	private int numBombs;
	private Player p;
	private IceBall plugin;
	private Inventory inv;
	private String title;
	private int initialBombCount;
	private ArrayList<Location> spawns = new ArrayList<Location>();
	private Location gridC;
	private ArrayList<Integer> targetIndex = new ArrayList<Integer>();
	private ItemStack as[] = new ItemStack[54];
	private DecimalFormat frm = new DecimalFormat("#0.0");
	private IceBallListener ibl3;
	
	public AirStrike(Player z, int numArrows, IceBall plu, IceBallListener ibl4) {
		this.createAs();
		ibl3=ibl4;
		initialBombCount=numArrows;
		numBombs=numArrows;
		p=z;
		plugin=plu;
		gridC = new Location(z.getWorld(), plugin.X-14, plugin.Y-2.2, plugin.Z+14);
		this.setSpawns();
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		title = ChatColor.DARK_RED + String.valueOf(numArrows) + 
				 " Bombs Ready For Deployment";
		inv = Bukkit.getServer().createInventory(null, 54, title);
		inv.setContents(as);
		p.openInventory(inv);
	}
	@EventHandler
	public void onInvClick(InventoryClickEvent e) {
		//without the line below, exception will be thrown upon clicking outside of the inventory
		if(e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR) {
			if(e.getInventory().equals(inv)) {
				if(e.getCurrentItem().getType()==Material.ICE && numBombs > 0) {
					numBombs--;
					this.setTargetSlot(e.getRawSlot());
					this.updateBombDisplay();
				}
				else if(e.getCurrentItem().getType()==Material.FIREBALL) {
					numBombs++;
					this.setNormalSlot(e.getRawSlot());
					this.updateBombDisplay();
				}
				else if(e.getCurrentItem().getType()==Material.GOLD_BLOCK) {
					p.closeInventory();
					if(initialBombCount == numBombs) {
						p.sendMessage(ChatColor.LIGHT_PURPLE + "AirStrike Waved Off");
					}
					else {
						for(int i = 0; i < as.length; i++) {
							if(as[i].getType() == Material.FIREBALL)
								targetIndex.add(i);
						}
						if(System.currentTimeMillis()-ibl3.astime > 20000) {
							ibl3.astime = System.currentTimeMillis();
							p.sendMessage(ChatColor.AQUA + "AirStrike Inbound");
							this.removeArrows(targetIndex.size());
							new Inbound(targetIndex, spawns, plugin, gridC);
						}
						else {
							p.sendMessage(ChatColor.LIGHT_PURPLE + "AirStrike Waved Off");
							p.sendMessage(ChatColor.AQUA + "Error; 20 seconds must pass between " +
									"AirStrikes.");
						}
					}
				}
				e.setCancelled(true);
			}
		}
	}
	
	public void updateBombDisplay() {
		title = ChatColor.DARK_RED + String.valueOf(numBombs) + 
				 " Bombs Ready For Deployment";
		inv = Bukkit.getServer().createInventory(null, 54, title);
		inv.setContents(as);
		p.openInventory(inv);
	}
	public void setTargetSlot(int index) {
		as[index] = new ItemStack(Material.FIREBALL, 1);
		ItemMeta im = as[index].getItemMeta();
		int x = index%9;
		double y = (int)Math.floor(index/9);
		y = Math.round(((double)y*(8/5.0))*10.0)/10.0;
		im.setDisplayName(ChatColor.DARK_PURPLE + "(" + ChatColor.GOLD + frm.format(x) +
				ChatColor.DARK_RED +"," + ChatColor.GOLD + frm.format(y) +
				ChatColor.DARK_PURPLE+
				")" + ChatColor.GRAY + "  target-area=" + ChatColor.GREEN + "true");
		as[index].setItemMeta(im);
	}
	public void setNormalSlot(int index) {
		as[index] = new ItemStack(Material.ICE, 1);
		ItemMeta im = as[index].getItemMeta();
		int x = index%9;
		double y = (int)Math.floor(index/9);
		y = Math.round(((double)y*(8/5.0))*10.0)/10.0;
		im.setDisplayName(ChatColor.DARK_PURPLE + "(" + ChatColor.GOLD + frm.format(x) +
				ChatColor.DARK_PURPLE +"," + ChatColor.GOLD + frm.format(y) +
				ChatColor.DARK_PURPLE+
				")" + ChatColor.GRAY + "  target-area=" + ChatColor.RED + "false");
		as[index].setItemMeta(im);
	}

	public void setSpawns() {
		for(int i = 0; i < 54; i++) {
			if((i >= 2 && i <= 6) || (i <= 51 && i >= 47) ||
					i == 18 || i == 26 || i == 27 || i == 35) {
				double locX = gridC.getX() + g2b((8.0/5)*((int)(i/9)));
				double locZ = gridC.getZ() - g2b(i%9);
				spawns.add(new Location(p.getWorld(), locX, gridC.getY(), 
						locZ));
				if(i >= 2 && i <=6)
					spawns.get(spawns.size()-1).setYaw((float)-90);
				else if(i <= 51 && i >= 47)
					spawns.get(spawns.size()-1).setYaw((float)90);
				else if(i == 18 || i == 27)
					spawns.get(spawns.size()-1).setYaw((float)-180);
				else if(i == 26 || i == 35)
					spawns.get(spawns.size()-1).setYaw((float)0);
				//spawns.get(spawns.size()-1).getBlock().setType(Material.GOLD_BLOCK);
			}
			
		}
	}
	public double g2b(double in) {
		return ((double)(in*3.444444));
	}
	public double b2g(double in) {
		return ((double)(in/3.444444));
	}
	
	
	private void createAs() {
		for(int i = 0; i < as.length; i++) {
			if(i == as.length-1) {
				as[i] = new ItemStack(Material.GOLD_BLOCK, 1);
				ItemMeta im = as[i].getItemMeta();
				im.setDisplayName(ChatColor.DARK_PURPLE + "Execute Airstrike");
				as[i].setItemMeta(im);
			}
			else if(i%9 < 2 && i < 16) {
				as[i] = new Wool(DyeColor.PURPLE).toItemStack(1);
				ItemMeta im = as[i].getItemMeta();
				im.setDisplayName(ChatColor.LIGHT_PURPLE + "Witch Tower");
				as[i].setItemMeta(im);
			}
			else if(i%9 > 6 && i < 25) {
				as[i] = new Wool(DyeColor.RED).toItemStack(1);
				ItemMeta im = as[i].getItemMeta();
				im.setDisplayName(ChatColor.RED + "Rogues Tower");
				as[i].setItemMeta(im);
			}
			else if(i%9 < 2 && i > 35) {
				//as[i] = new Wool(DyeColor.BLUE).toItemStack(1);
				as[i] = new ItemStack(Material.LAPIS_BLOCK, 1);
				ItemMeta im = as[i].getItemMeta();
				im.setDisplayName(ChatColor.AQUA + "Sorceror Tower");
				as[i].setItemMeta(im);
			}
			else if(i%9 > 6 && i > 42) {
				as[i] = new Wool(DyeColor.LIME).toItemStack(1);
				ItemMeta im = as[i].getItemMeta();
				im.setDisplayName(ChatColor.GREEN + "Clown Tower");
				as[i].setItemMeta(im);
			}
			else {
				as[i] = new ItemStack(Material.ICE, 1);
				ItemMeta im = as[i].getItemMeta();
				int x = i%9;
				double y = (int)Math.floor(i/9);
				y = Math.round(((double)y*(8/5.0))*10.0)/10.0;
				im.setDisplayName(ChatColor.DARK_PURPLE + "(" + ChatColor.GOLD + frm.format(x) +
						ChatColor.RED +"," + ChatColor.GOLD + frm.format(y) +
						ChatColor.DARK_PURPLE+
						")" + ChatColor.GRAY + "  target-area=" + ChatColor.RED + "false");
				as[i].setItemMeta(im);
			}
		}
	}
	public void removeArrows(int bombC) {
		int prev = countArrows();
		ItemStack[] pinv = p.getInventory().getContents();
		int accA1 = 0;
		boolean removedShare = false;
		for(int iii = 0; iii < 3000 && !removedShare; iii++) {
			int i = iii%36;
			if(pinv[i] != null) {
				if(pinv[i].getType() == Material.ARROW) {
					for(int ii = 0; ii < pinv[i].getAmount() && !removedShare; ii++) {
						if(pinv[i].getAmount() == 1)
							pinv[i].setType(Material.SNOW_BALL);
						else {
							int newAmount = pinv[i].getAmount()-1;
							pinv[i].setAmount(newAmount);
						}
						int newa = countArrows();
						if(newa != prev) {
							accA1++;
							prev = newa;
						}
						if(accA1 >= bombC)
							removedShare = true;
					}
				}
			}
		}
	}
	public int countArrows() {
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
}
