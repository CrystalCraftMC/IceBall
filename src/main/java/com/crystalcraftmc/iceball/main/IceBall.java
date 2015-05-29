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
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class IceBall extends JavaPlugin {

	/**Holds snowball arena area*/
	public int[] iceballArea = new int[6];
	
	/**Holds snowball maintenance warp location*/
	public int[] iceballMaintenance = new int[3];
	
	/**Holds different types of inventories*/
	public enum InventoryResult {
		CLEAR, POLLUTED, ARMOR_POLLUTION
	}
	
	/**Lists of ppl who have snowball perms*/
	ArrayList<String> iceballPerms = new ArrayList<String>();
	
	/**Items to de-powertool*/
	ArrayList<ItemStack> noPT = new ArrayList<ItemStack>();
	
	/**List of valid commands inside snowball arena*/
	String[] validCommands = {"spawn", "home", "warp"};
	
	/**Holds how much to limit area for clear inv tp*/
	public int clearLimit = 12;
	
	public void onEnable() {
		
		this.initializeSnowballArea();
		this.initializeSnowballPerms();
		this.initializeSnowballMaintenance();
		this.initializeSnowballClearLimit();
		new IceBallLimitListener(this);
		new GameplayListener(this);
		noPT.add(new ItemStack(Material.COOKED_BEEF, 1));
		noPT.add(new ItemStack(Material.WRITTEN_BOOK, 1));
		noPT.add(new ItemStack(Material.SNOW_BALL, 1));
		noPT.add(new ItemStack(Material.SNOW_BLOCK, 1));
		noPT.add(new ItemStack(Material.BOOK, 1));
		noPT.add(new ItemStack(Material.BOOK_AND_QUILL, 1));
		noPT.add(new ItemStack(Material.SNOW, 1));
		
		if(!iceballPerms.contains("Jwood9198")) {
			iceballPerms.add("Jwood9198");
		}
		if(!iceballPerms.contains("Tethtibis")) {
			iceballPerms.add("Tethtibis");
		}
		if(!iceballPerms.contains("Todd5747")) {
			iceballPerms.add("Todd5747");
		}
	}
	public void onDisable() {
		
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) {
			Player p = (Player)sender;
			if(this.hasSnowballPerms(p)) {
				if(label.equalsIgnoreCase("createsnowball")) {
					if(args.length == 6) {
						for(int i = 0; i < 6; i++) {
							if(!Utility.isInt(args[i])) {
								p.sendMessage(ChatColor.RED + "Error; your 6 arguments were not all valid " +
										"int values");
								return false;
							}
						}
						for(int i = 0; i < 6; i++) {
							iceballArea[i] = Integer.parseInt(args[i]);
						}
						this.updateSnowballArea();
						p.sendMessage("Snowball area updated.");
						this.showSnowball(p);
						return true;
					}
					else {
						return false;
					}
				}
				else if(label.equalsIgnoreCase("deleteSnowball")) {
					if(args.length == 0) {
						for(int i = 0; i < 6; i++)
							iceballArea[i] = 1234567899;
						this.updateSnowballArea();
						return true;
					}
					else {
						return false;
					}
				}
				else if(label.equalsIgnoreCase("showsnowball")) {
					this.showSnowball(p);
					return true;
				}
				else if(label.equalsIgnoreCase("snowballperms")) {
					if(args.length == 0) {
						p.sendMessage(ChatColor.DARK_AQUA + "List of players with Snowball Perms:");
						for(int i = 0; i < iceballPerms.size(); i++) {
							if(i%2 == 0)
								p.sendMessage(ChatColor.AQUA + iceballPerms.get(i));
							else
								p.sendMessage(ChatColor.BLUE + iceballPerms.get(i));
						}
						return true;
					}
					else if(args.length == 2) {
						if(args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("remove")) {
							boolean isAdd = args[0].equalsIgnoreCase("add") ? true : false;
							if(isAdd) {
								for(String check : iceballPerms) {
									if(check.equalsIgnoreCase(args[1])) {
										p.sendMessage(ChatColor.RED + "Error; " + ChatColor.GOLD + args[1] +
											ChatColor.RED + " is already in the Snowball Perms list.");
										p.sendMessage(ChatColor.DARK_AQUA + "Use /snowballPerms to list players " +
											"who currently have Snowball perms.");
										return true;
									}
								}
								iceballPerms.add(args[1]);
								p.sendMessage(ChatColor.GOLD + args[1] + ChatColor.DARK_AQUA + " added to " +
										"the Snowball perms list.");
								this.updatePerms();
								return true;
							}
							else {
								for(int i = 0; i < iceballPerms.size(); i++) {
									if(iceballPerms.get(i).equalsIgnoreCase(args[1])) {
										iceballPerms.remove(i);
										p.sendMessage(ChatColor.DARK_AQUA + "Player successfully removed.");
										this.updatePerms();
										return true;
									}
								}
								p.sendMessage(ChatColor.RED + "Error; " + ChatColor.GOLD + args[1]+
										ChatColor.RED + " was not found in the Snowball Perms list.");
								p.sendMessage(ChatColor.DARK_AQUA + "Use /snowballPerms to list players " +
											"who currently have Snowball perms.");
								return true;
							}
						}
						else {
							p.sendMessage(ChatColor.RED + "Error; your first argument was not " +
									"\'add\' or \'remove\'.");
							return false;
						}
					}
					return false;
				}
				else if(label.equalsIgnoreCase("snowballmaintenance")) {
					if(args.length == 0) {
						if(p.getWorld().getEnvironment() == Environment.NORMAL) {
							p.teleport(new Location(p.getWorld(), (double)iceballMaintenance[0],
									(double)iceballMaintenance[1], (double)iceballMaintenance[2]));
							p.sendMessage(ChatColor.BLUE + "To maintenance...");
							return true;
						}
						else {
							p.sendMessage(ChatColor.GOLD + "Error; you must be in the " +
									"overworld to use that command.");
							return true;
						}
					}
					else if(args.length == 3) {
						if(Utility.isInt(args[0], args[1], args[2])) {
							for(int i = 0; i < 3; i++)
								iceballMaintenance[i] = Integer.parseInt(args[i]);
							this.updateSnowballMaintenance();
							p.sendMessage(ChatColor.GOLD + "IceBall Maintenance area updated to " +
									ChatColor.GRAY + "(" + ChatColor.LIGHT_PURPLE + args[0] + ", " + args[1] +
									", " + args[2] + ChatColor.GRAY + ")");
							return true;
						}
						else {
							p.sendMessage(ChatColor.AQUA + "Error; your 3 arguments were not all valid int values.");
							return false;
						}
					}
					else
						return false;
				}
				else if(label.equalsIgnoreCase("snowballclearlimit") && args.length == 1) {
					if(Utility.isInt(args[0])) {
						clearLimit = Integer.parseInt(args[0]);
						this.updateSnowballClearLimit();
					}
					else {
						p.sendMessage(ChatColor.BLUE + "Current ClearLimit = " +
								ChatColor.GOLD + String.valueOf(clearLimit));
						return false;
					}
				}
			}
			else {
				p.sendMessage(ChatColor.DARK_AQUA + "Error; you do not have permission to " +
						"perform this command.");
				return true;
			}
		}
		return false;
	}
	
	/**Tests whether a player has snowball perms
	 * @param p, the Player we're testing
	 * @return boolean, true if they have permissions
	 */
	public boolean hasSnowballPerms(Player p) {
		if(p.isOp())
			return true;
		String name = p.getName();
		for(String id : iceballPerms) {
			if(name.equals(id))
				return true;
		}
		return false;
	}
	
	
	/**Displays the current snowball area
	 * @param p Player we're showing the snowball area to
	 */
	public void showSnowball(Player p) {
		p.sendMessage(ChatColor.BLUE + "Coordinates are formatted as (x, y, z)");
		p.sendMessage(ChatColor.DARK_AQUA + "Corner 1: " + ChatColor.DARK_PURPLE + 
				"(" + ChatColor.GOLD + String.valueOf(iceballArea[0]) +
					", " + String.valueOf(iceballArea[1]) + ", " + String.valueOf(iceballArea[2]) +
					ChatColor.DARK_PURPLE + ")");
		p.sendMessage(ChatColor.DARK_AQUA + "Corner 2: " + ChatColor.DARK_PURPLE + 
				"(" + ChatColor.GOLD + String.valueOf(iceballArea[3]) +
					", " + String.valueOf(iceballArea[4]) + ", " + String.valueOf(iceballArea[5]) +
					ChatColor.DARK_PURPLE + ")");
	}
	
	//########################################################################
	//                  All File Methods Are Below This Line
	//########################################################################
	
	/**Initializes the snowball area from the file*/
	public void initializeSnowballArea() {
		if(!new File("IceBallFiles").exists())
			new File("IceBallFiles").mkdir();
		File file = new File("IceBallFiles/plugins/IceBall/Snowball.txt");
		Scanner in = null;
		PrintWriter pw = null;
		try{
			if(!file.exists()) {
				pw = new PrintWriter("SnowballFiles\\Snowball.txt");
				for(int i = 0; i < 6; i++) {
					pw.println("1234567899");
					iceballArea[i] = 1234567899;
				}
			}
			else {
				in = new Scanner(file);
				for(int i = 0; i < 6; i++) {
					iceballArea[i] = Integer.parseInt(in.nextLine());
				}
			}
		}catch(IOException e) { e.printStackTrace();
		}finally {
			if(pw != null)
				pw.close();
			if(in != null)
				in.close(); 
		}
	}
	
	/**Updates the snowball area file*/
	public void updateSnowballArea() {
		PrintWriter pw = null;
		try{
			if(!new File("IceBallFiles").exists())
				new File("IceBallFiles").mkdir();
			pw = new PrintWriter("IceBallFiles/plugins/IceBall/Snowball.txt");
			for(int i = 0; i < 6; i++)
				pw.println(String.valueOf(iceballArea[i]));
		}catch(IOException e) { e.printStackTrace();
		}finally {
			if(pw != null)
				pw.close();
		}
	}
	
	/**This initializes the snowball permissions file*/
	public void initializeSnowballPerms() {
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		try{
			if(!new File("IceBallFiles").exists())
				new File("IceBallFiles").mkdir();
			File file = new File("IceBallFiles/plugins/IceBall/SnowballPerms.ser");
			if(file.exists()) {
				fis = new FileInputStream(file);
				ois = new ObjectInputStream(fis);
				iceballPerms = (ArrayList<String>)ois.readObject();
			}
		}catch(IOException e) { e.printStackTrace(); 
		}catch(ClassNotFoundException e) { e.printStackTrace();
		}finally {
			try{
				if(fis != null)
					fis.close();
				if(ois != null)
					ois.close();
			}catch(IOException e) { e.printStackTrace(); }
		}
	}
	
	/**This updates the snowball permissions file*/
	public void updatePerms() {
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		try{
			if(!new File("IceBallFiles").exists())
				new File("IceBallFiles").mkdir();
			File file = new File("IceBallFiles/plugins/IceBall/SnowballPerms.ser");
			if(file.exists())
				file.delete();
			fos = new FileOutputStream(file);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(iceballPerms);
		}catch(IOException e) { e.printStackTrace();
		}finally {
			try{
				oos.close();
				fos.close();
			}catch(IOException e) { e.printStackTrace(); }
		}
	}
	
	/**Initializes the maintenance location file*/
	public void initializeSnowballMaintenance() {
		File file = new File("IceBallFiles/plugins/IceBall/SnowballMaintenance.txt");
		if(!new File("IceBallFiles").exists())
			new File("IceBallFiles").mkdir();
		if(file.exists()) {
			Scanner in = null;
			try{
				in = new Scanner(file);
				String[] str = in.nextLine().split(",");
				for(int i = 0; i < 3; i++)
					iceballMaintenance[i] = Integer.parseInt(str[i]);
			}catch(IOException e) { e.printStackTrace();
			}finally {
				if(in != null)
					in.close();
			}
		}
		else {
			for(int i = 0; i < iceballMaintenance.length; i++)
				iceballMaintenance[i] = 9198;
		}
	}
	
	/**Initializes the snowball clearLimit file*/
	public void initializeSnowballClearLimit() {
		File file = new File("IceBallFiles/plugins/IceBall/SnowballClearLimit.txt");
		if(!new File("IceBallFiles").exists())
			new File("IceBallFiles").mkdir();
		if(file.exists()) {
			Scanner in = null;
			try{
				in = new Scanner(file);
				String str = in.nextLine().trim();
				clearLimit = Integer.parseInt(str);
			}catch(IOException e) { e.printStackTrace();
			}finally {
				if(in != null)
					in.close();
			}
		}
		else {
			clearLimit = 12;
		}
	}
	
	/**Updates the snowball maintenance file*/
	public void updateSnowballMaintenance() {
		File file = new File("IceBallFiles/plugins/IceBall/SnowballMaintenance.txt");
		PrintWriter pw = null;
		if(!new File("IceBallFiles").exists())
			new File("IceBallFiles").mkdir();
		try{
			pw = new PrintWriter(file);
			pw.println(String.valueOf(iceballMaintenance[0]) + "," +
					String.valueOf(iceballMaintenance[1]) + "," +
							String.valueOf(iceballMaintenance[2]));
		}catch(IOException e) { e.printStackTrace();
		}finally {
			if(pw != null)
				pw.close();
		}
	}
	
	/**Updates the snowball clearLimit file*/
	public void updateSnowballClearLimit() {
		File file = new File("IceBallFiles/plugins/IceBall/IceBallClearLimit.txt");
		PrintWriter pw = null;
		if(!new File("IceBallFiles").exists())
			new File("IceBallFiles").mkdir();
		try{
			pw = new PrintWriter(file);
			pw.println(String.valueOf(clearLimit));
		}catch(IOException e) { e.printStackTrace();
		}finally {
			if(pw != null)
				pw.close();
		}
	}
	
	
}
