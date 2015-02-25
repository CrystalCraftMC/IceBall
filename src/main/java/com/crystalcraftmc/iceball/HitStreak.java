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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.Timer;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class HitStreak {
	private Random rand = new Random();
	private IceBallListener ibl;
	private String name;
	private int hitStreak;
	private Player p;
	public boolean isImmune;
	private IceBall plugin;
	private ItemStack witchBomb;
	private ItemStack hungerRune;
	private ItemStack sorcerorBomb;
	private Timer tim;
	private int immuneAcc = 0;
	public boolean isOnTank = false;
	
	private enum Team { PURPLE, RED, BLUE, GREEN };
	private Team team;
	public HitStreak(Player p, IceBall plugin, String team, IceBallListener ibl) {
		this.plugin = plugin;
		this.ibl = ibl;
		name = p.getName();
		this.p = p;
		hitStreak = 0;
		isImmune = false;
		if(team.equalsIgnoreCase("purple"))
			this.team = Team.PURPLE;
		else if(team.equalsIgnoreCase("blue"))
			this.team = Team.BLUE;
		else if(team.equalsIgnoreCase("green"))
			this.team = Team.GREEN;
		else
			this.team = Team.RED;
		this.createWitchBomb();
		this.createSorcerorBomb();
		hungerRune = new ItemStack(Material.FLINT_AND_STEEL, 1);
	}
	public String getName() {
		return name;
	}
	public void anotherSnipe() { //only called if shooter is inside snowball arena
		hitStreak++;
		if(hitStreak == 5) {
			this.reward(5);
		}
		if(hitStreak == 10) {
			this.reward(10);
		}
		if(hitStreak == 15) {
			this.reward(15);
		}
	}
	public void reward(int streak) {
		if(streak == 5) {
			if(team == Team.RED) {
				p.sendMessage(ChatColor.LIGHT_PURPLE + "5 Hit Streak! Here's a couple " + ChatColor.GREEN +
						"EnderPearls.");
				p.getInventory().addItem(new ItemStack(Material.ENDER_PEARL, 2));
			}
			else if(team == Team.PURPLE) {
				p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 600, 1));
				p.sendMessage(ChatColor.LIGHT_PURPLE + "5 Hit Streak! You have " + ChatColor.RED +
						"Speed " + ChatColor.BLUE + "for 30 seconds.");
			}
			else if(team == Team.BLUE) {
				p.sendMessage(ChatColor.GOLD + "5 Hit Streak " + ChatColor.BLUE + "Wizard." +
						ChatColor.AQUA + " You summon a hell-hound - he will nauseate & make hungry your opponents and " +
						ChatColor.DARK_RED + " annoy " + ChatColor.AQUA + "them.");
				Wolf wo = (Wolf)p.getWorld().spawnEntity(p.getLocation(), EntityType.WOLF);
				wo.setOwner(p);
				wo.setCustomName(p.getName() + " Wizard Jr.");
				wo.setCustomNameVisible(true);
				wo.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 2400, 1));
				wo.setFireTicks(2400);
				wo.setHealth(3);
			}
			else if(team == Team.GREEN) {
				p.sendMessage(ChatColor.GOLD + "5 Hit Streak " + ChatColor.GREEN + "Clown." +
						ChatColor.AQUA + " You are rewarded with 3 fireworks with randomized attributes! " +
						ChatColor.DARK_RED + " No tactical advantage- this one is for giggles.");
				this.giveTheClownHisExplosives();
			}
		}
		else if(streak == 10) {
			if(team == Team.RED) {
				p.sendMessage(ChatColor.LIGHT_PURPLE + "10 Hit Streak!!  You're Immune from Snowballs" +
						" for 7 seconds");
				isImmune = true;
				tim = new Timer(50, new ImmunityFlash());
				tim.start();
				p.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					public void run() {
						isImmune = false;
					}
				}, (long)140);
			}
			else if(team == Team.PURPLE) {
				p.sendMessage(ChatColor.LIGHT_PURPLE + "10 Hit Streak!! You have " + ChatColor.RED +
						"Jump-Boost " + ChatColor.BLUE + "for 20 seconds.");
				p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 400, 2));
			}
			else if(team == Team.BLUE) {
				p.sendMessage(ChatColor.GOLD + "10 Hit Streak " + ChatColor.BLUE + "Wizard." +
						ChatColor.AQUA + " You are bestowed with a magic hunger rune." +
						ChatColor.DARK_RED + " Right-Click on any block to strike anyone on the same " +
						"x/z coord (give/take 1 block) with serious " + ChatColor.AQUA + "Hunger.");
				p.getInventory().addItem(hungerRune);
			}
			else if(team == Team.GREEN) {
				p.sendMessage(ChatColor.GOLD + "10 Hit Streak " + ChatColor.GREEN + "Clown." +
						ChatColor.AQUA + " You are rewarded with a levatation stick! " +
						ChatColor.BOLD + " #opFloat&Fire.");
				p.sendMessage(ChatColor.GREEN + "Right-Click While Holding Stick To Use");
				this.clownLevitation();
			}
		}
		else if(streak == 15) {
			if(team == Team.RED) {
				p.sendMessage(ChatColor.YELLOW + "15 Hit Streak, Rogue!!!  You gain 4 legendary " +
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
			else if(team == Team.PURPLE) {
				p.sendMessage(ChatColor.LIGHT_PURPLE + "15 Hit-Streak!!! You gain a " +
						"blindness bomb.  Place to blind all enemies in a 10 block cube " +
						"for" + ChatColor.GOLD + " 10 seconds.");
				p.getInventory().addItem(new ItemStack(witchBomb));
			}
			else if(team == Team.BLUE) {
				p.sendMessage(ChatColor.RED + "15 Hit-Streak!!! You gain a " +
						"slow-mo bomb.  Place to slow all enemies in a 10 block cube " +
						"for" + ChatColor.GOLD + " 12 seconds.");
				p.getInventory().addItem(new ItemStack(sorcerorBomb));
			}
			else if(team == Team.GREEN) {
				p.sendMessage(ChatColor.GOLD + "15 Hit Streak " + ChatColor.GREEN + "Clown." +
						ChatColor.AQUA + " You are rewarded with " + ChatColor.BOLD + "TANK");
				p.sendMessage(ChatColor.RED + "Docking will commence in " + ChatColor.AQUA +
						"t" + ChatColor.RED + "-" + ChatColor.AQUA + "10 " + ChatColor.RED + "seconds.");
				this.initiateTank();
			}
		}
		
	}
	public int getHitStreak() {
		return hitStreak;
	}
	public void setHitStreak(int hs) {
		hitStreak = hs;
	}
	public void setTeam(String t) {
		if(t.equalsIgnoreCase("purple"))
			this.team = Team.PURPLE;
		else if(t.equalsIgnoreCase("blue"))
			this.team = Team.BLUE;
		else if(t.equalsIgnoreCase("green"))
			this.team = Team.GREEN;
		else
			this.team = Team.RED;
		this.setHitStreak(0);
	}
	public void reset() { //DEPRECATED
		hitStreak = 0;
		isImmune = false;
	}
	public void createWitchBomb() {
		witchBomb = new ItemStack(Material.FIREWORK, 1);
		FireworkMeta fm = (FireworkMeta) witchBomb.getItemMeta();
		ArrayList<Color> alColor = new ArrayList<Color>();
		alColor.add(Color.PURPLE);
		alColor.add(Color.WHITE);
		alColor.add(Color.MAROON);
		ArrayList<Color> alFade = new ArrayList<Color>();
		alFade.add(Color.SILVER);
		fm.addEffects(FireworkEffect.builder().trail(true).withColor(alColor).withFade(alFade).with(Type.BALL_LARGE).build());
		fm.setPower(0);
		fm.setDisplayName(ChatColor.LIGHT_PURPLE + "Headless-Chicken Bomb");
//####################################################################
		ArrayList<String> lore = new ArrayList<String>();
		lore.add(ChatColor.BLUE + "Made By a Cult Of Witches In a Cave");
		lore.add(ChatColor.RED + "They Call It The" + ChatColor.LIGHT_PURPLE + " Headless-Chicken Bomb");
		fm.setLore(lore);
//####################################################################
		fm.addEnchant(Enchantment.LURE, 1, false);
		witchBomb.setItemMeta(fm);
	}
	public void createSorcerorBomb() {
		sorcerorBomb = new ItemStack(Material.FIREWORK, 1);
		FireworkMeta fm = (FireworkMeta) sorcerorBomb.getItemMeta();
		ArrayList<Color> alColor = new ArrayList<Color>();
		alColor.add(Color.NAVY);
		alColor.add(Color.BLACK);
		alColor.add(Color.ORANGE);
		ArrayList<Color> alFade = new ArrayList<Color>();
		alFade.add(Color.RED);
		fm.addEffects(FireworkEffect.builder().trail(true).withColor(alColor).withFade(alFade).with(Type.STAR).build());
		fm.setPower(0);
		fm.setDisplayName(ChatColor.AQUA + "The_Lag");
//####################################################################
		ArrayList<String> lore = new ArrayList<String>();
		lore.add(ChatColor.RED + "Sss-ll-oo-wwwww  mm-oooo");
		lore.add(ChatColor.GOLD + "Sorcery");
		fm.setLore(lore);
//####################################################################
		fm.addEnchant(Enchantment.ARROW_FIRE, 1, false);
		sorcerorBomb.setItemMeta(fm);
	}
	public void giveTheClownHisExplosives() {
		ItemStack clownBomb = new ItemStack(Material.FIREWORK, 3);
		FireworkMeta fm = (FireworkMeta) clownBomb.getItemMeta();
		ArrayList<Color> alColor = new ArrayList<Color>();
		if(rand.nextInt(2) == 1) {
			alColor.add(this.randomColor());
			alColor.add(this.randomColor());
			alColor.add(this.randomColor());
		}
		else
			alColor.add(this.randomColor());
		ArrayList<Color> alFade = new ArrayList<Color>();
		alFade.add(this.randomColor());
		alFade.add(this.randomColor());
		fm.addEffects(FireworkEffect.builder().trail(true).withColor(alColor).withFade(alFade).with(this.randomType()).build());
		fm.setPower(0);
		fm.setDisplayName(ChatColor.AQUA + this.randomName());
//####################################################################
		ArrayList<String> lore = new ArrayList<String>();
		lore.add(ChatColor.ITALIC + "Schenanigans " + ChatColor.MAGIC + "In");
		lore.add(ChatColor.GOLD + "Clown-Paradise");
		fm.setLore(lore);
//####################################################################
		clownBomb.setItemMeta(fm);
		p.getInventory().addItem(clownBomb);
	}
	public Color randomColor() {
		return Color.fromBGR(rand.nextInt(256), 
				rand.nextInt(256), rand.nextInt(256));
		
		/*switch(rand.nextInt(16)) {
		case 0:
			return Color.AQUA;
		case 1:
			return Color.BLACK;
		case 2:
			return Color.BLUE;
		case 3:
			return Color.FUCHSIA;
		case 4:
			return Color.GREEN;
		case 5:
			return Color.LIME;
		case 6:
			return Color.MAROON;
		case 7:
			return Color.NAVY;
		case 8:
			return Color.OLIVE;
		case 9:
			return Color.ORANGE;
		case 10:
			return Color.PURPLE;
		case 11:
			return Color.RED;
		case 12:
			return Color.SILVER;
		case 13:
			return Color.TEAL;
		case 14:
			return Color.WHITE;
		case 15:
			return Color.YELLOW;
		default:
			return Color.fromBGR(rand.nextInt(0xFFFFFF+1), rand.nextInt(0xFFFFFF+1), rand.nextInt(0xFFFFFF+1));
		}*/
	}
	public Type randomType() {
		switch(rand.nextInt(5)) {
		case 0:
			return Type.BALL;
		case 1:
			return Type.BALL_LARGE;
		case 2:
			return Type.BURST;
		case 3:
			return Type.CREEPER;
		default:
			return Type.STAR;
		}
	}
	public String randomName() {
		String[] strand = { "jwood9198", "todd5747", "tethtibis", "jacc734", "jflory7",
				"tehelee", "puzzlem00n", "fredeux", "xtylorx", "romulus1997", "dextile",
				"mcminingcaveman", "staroki", "Infinitecorners", "wasthisme", "_stevoism_",
				"ultimateafs", "mittykitten", "nether_god97", "fortification45",
				"echophox", "kalibj", "mcmorkey", "codebaka", "mattdude234", "ruggedKingz",
				"El_pengi", "twootton", "ajs", "raeophox", "xxthesilent18xx", "gluumba",
				"nightling3", "321mcblaster", "thunder_rain", "blackdiamond31", "crisscrisis",
				"jestercopperpot", "dellman135", "crystalcraftmc", p.getName()};
		//1 in 5 chance that it's the person getting the firework's name
		int index = rand.nextInt(5) == 1 ? strand.length-1 : rand.nextInt(strand.length);
		if(rand.nextInt(5) == 1) //1 in 5 chance it's me unscrambled :D
			return "jwood9198";
		StringBuilder ascend = new StringBuilder(strand[index]);
		String randStr = "";
		for(int i = 0; i < strand[index].length(); i++) {
			int pickR = rand.nextInt(ascend.length());
			if(i == 0)
				randStr = randStr.concat(String.format("%C", ascend.charAt(pickR)));
			else
				randStr = randStr.concat(String.valueOf(ascend.charAt(pickR)).toLowerCase());
			ascend.deleteCharAt(pickR);
		}
		return randStr;
	}
	public void initiateTank() {
		String[] $clownNames = { "Bobby", "Lucky", "Chuckle",
				"Sunshine", "Doctor", "Jedson", "Jwood The Clown" };
		final HitStreak shallow = this;
		final String clownName1 =  rand.nextInt(2) == 0 ? this.randomName() : 
			$clownNames[rand.nextInt($clownNames.length)];
		final String clownName2 = rand.nextInt(2) == 0 ? this.randomName() :
			$clownNames[rand.nextInt($clownNames.length)];
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run() {
				if(!ibl.isOutsideArena(p, 20, true) && p.getLocation().getY() < (plugin.Y-1)) {
					new Tank(p, clownName1, clownName2, shallow, plugin);
					isOnTank = true;
				}
				else {
					p.sendMessage(ChatColor.RED + "Error; you must be inside the snowball-arena to" +
							" use the tank. " + ChatColor.GOLD + "-J");
				}
			}
		}, 200L);
		
	}
	public void clownLevitation() {
		ItemStack wand = new ItemStack(Material.STICK, 1);
		ItemMeta im = wand.getItemMeta();
		ArrayList<String> lore = new ArrayList<String>();
		lore.add("Jwood's Stick Of");
		lore.add("Levatation");
		im.setLore(lore);
		im.setDisplayName("Floatation Device");
		wand.setItemMeta(im);
		p.getInventory().addItem(wand);
	}
	private class ImmunityFlash implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			immuneAcc++;
			Location loc = p.getLocation();
			ParticleEffect.FIREWORKS_SPARK.display((float).5, (float).0, (float).5,
					(float)(.2), (int)(2), new Location(loc.getWorld(), 
							loc.getX(),loc.getY(), loc.getZ()), 90.0);
			
			if(immuneAcc > 140) {
				tim.stop();
				immuneAcc = 0;
			}
		}
	}
}
