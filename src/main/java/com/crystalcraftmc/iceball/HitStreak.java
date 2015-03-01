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
	public Player p;
	public boolean isImmune;
	public boolean hasDog;
	private IceBall plugin;
	private ItemStack witchBomb;
	private ItemStack hungerRune;
	private ItemStack sorcerorBomb;
	private Timer tim;
	private Timer gappleTim;
	private int immuneAcc = 0;
	private boolean isDoubled; //a perk of being hit by Beam of type water
	public boolean isOnTank = false;
	public int gapple = 0;
	
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
		gappleTim = new Timer(50, new AteGapple());
	}
	public void setDouble(int time8) {
		isDoubled = true;
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run() {
				switchOffDouble();
			}
		}, (long)time8);
	}
	public void switchOffDouble() {
		isDoubled = false;
	}
	public String getName() {
		return name;
	}
	public String getTeam() {
		if(team == Team.RED)
			return "red";
		if(team == Team.PURPLE)
			return "purple";
		if(team == Team.BLUE)
			return "blue";
		return "green";
	}
	public ChatColor getTeamColor() {
		if(team == Team.RED)
			return ChatColor.DARK_RED;
		if(team == Team.PURPLE)
			return ChatColor.LIGHT_PURPLE;
		if(team == Team.BLUE)
			return ChatColor.AQUA;
		return ChatColor.GREEN;
	}
	public void anotherSnipe() { //only called if shooter is inside snowball arena
		int iterate8 = isDoubled ? 2 : 1;
		for(int i = 0; i < iterate8; i++) {
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
			if(hitStreak == 21) {
				//applies for all teams
				p.sendMessage(ChatColor.GOLD + "21 HitStreak- Here is a piece of white-wool " +
					"and some dyes.");
				p.sendMessage(ChatColor.AQUA + "Place the wool to " + ChatColor.RED + "change teams" +
					ChatColor.AQUA + " to the wool's respective " + ChatColor.DARK_PURPLE + "color.");
				delayedGive(new ItemStack(Material.WOOL, 1));
				delayedGive(new ItemStack(Material.INK_SACK, 1, (short)1));
				delayedGive(new ItemStack(Material.INK_SACK, 1, (short)5));
				delayedGive(new ItemStack(Material.INK_SACK, 1, (short)12));
				delayedGive(new ItemStack(Material.INK_SACK, 1, (short)10));
			}
		}
	}
	public void reward(int streak) {
		if(streak == 5) {
			if(rand.nextInt(5) == 2)
				this.giveWoolSet(true);
			if(team == Team.RED) {
				p.sendMessage(ChatColor.LIGHT_PURPLE + "5 Hit Streak! Here's a couple " + ChatColor.GREEN +
						"EnderPearls.");
				delayedGive(new ItemStack(Material.ENDER_PEARL, 2));
				if(rand.nextInt(2) == 0)
					delayedGive(new ItemStack(Material.COAL, 1));
			}
			else if(team == Team.PURPLE) {
				p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 600, 1));
				p.sendMessage(ChatColor.LIGHT_PURPLE + "5 Hit Streak! You have " + ChatColor.RED +
						"Speed " + ChatColor.BLUE + "for 30 seconds.");
				if(rand.nextInt(2) == 0)
					delayedGive(new ItemStack(Material.SLIME_BALL, 1));
			}
			else if(team == Team.BLUE) {
				p.sendMessage(ChatColor.GOLD + "5 Hit Streak " + ChatColor.BLUE + "Wizard." +
						ChatColor.AQUA + " You summon a hell-hound - he will nauseate & make hungry your opponents and " +
						ChatColor.DARK_RED + " annoy " + ChatColor.AQUA + "them.");
				Wolf wo = (Wolf)p.getWorld().spawnEntity(p.getLocation(), EntityType.WOLF);
				wo.setOwner(p);
				wo.setCustomName(p.getName() + " Wizard Jr.");
				wo.setCustomNameVisible(true);
				wo.setHealth(2);
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
				delayedGive(new ItemStack(Material.FEATHER, 1));
				if(rand.nextInt(2) == 0)
					delayedGive(new ItemStack(Material.COAL, 1));
			}
			else if(team == Team.PURPLE) {
				p.sendMessage(ChatColor.LIGHT_PURPLE + "10 Hit Streak!! You have " + ChatColor.RED +
						"Jump-Boost " + ChatColor.BLUE + "for 20 seconds.");
				p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 400, 2));
				delayedGive(new ItemStack(Material.STRING, 1));
				giveWoolSet(false);
			}
			else if(team == Team.BLUE) {
				p.sendMessage(ChatColor.GOLD + "10 Hit Streak " + ChatColor.BLUE + "Wizard." +
						ChatColor.AQUA + " You are bestowed with a magic hunger rune." +
						ChatColor.DARK_RED + " Right-Click on any block to strike anyone on the same " +
						"x/z coord (give/take 1 block) with serious " + ChatColor.AQUA + "Hunger.");
				delayedGive(hungerRune);
				delayedGive(new ItemStack(Material.FLINT, 1));
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
				im.setDisplayName(ChatColor.BLUE + "" + ChatColor.BOLD  + "Ice_Ball");
				ArrayList<String> alLore = new ArrayList<String>();
				alLore.add(ChatColor.RED + "Snow Dug From");
				alLore.add(ChatColor.GOLD + "Plastic Beach");
				im.setLore(alLore);
				is.setItemMeta(im);
				delayedGive(is);
				if(rand.nextInt(2) == 0)
					delayedGive(new ItemStack(Material.COAL, 1));
			}
			else if(team == Team.PURPLE) {
				p.sendMessage(ChatColor.LIGHT_PURPLE + "15 Hit-Streak!!! You gain a " +
						"blindness bomb.  Place to blind all enemies in a 10 block cube " +
						"for" + ChatColor.GOLD + " 10 seconds.");
				delayedGive(witchBomb);
			}
			else if(team == Team.BLUE) {
				p.sendMessage(ChatColor.RED + "15 Hit-Streak!!! You gain a " +
						"slow-mo bomb.  Place to slow all enemies in a 10 block cube " +
						"for" + ChatColor.GOLD + " 12 seconds.");
				delayedGive(sorcerorBomb);
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
	public void giveWoolSet(boolean luck) {
		if(luck)
			p.sendMessage(ChatColor.GOLD + "Lucky Break!  Here's a wool & " +
				"and some dyes.");
		p.sendMessage(ChatColor.AQUA + "Place the wool to " + ChatColor.RED + "change teams" +
				ChatColor.AQUA + " to the wool's respective " + ChatColor.DARK_PURPLE + "color.");
		delayedGive(new ItemStack(Material.WOOL, 1));
		delayedGive(new ItemStack(Material.INK_SACK, 1, (short)1));
		delayedGive(new ItemStack(Material.INK_SACK, 1, (short)5));
		delayedGive(new ItemStack(Material.INK_SACK, 1, (short)12));
		delayedGive(new ItemStack(Material.INK_SACK, 1, (short)10));
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
		delayedGive(clownBomb);
	}
	public Color randomColor() {
		return Color.fromBGR(rand.nextInt(256), 
				rand.nextInt(256), rand.nextInt(256));
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
		String[] strand = { "jwood9198", "todd5747", "tethtibis", "jacc734", "jflory7", "dev10k",
				"tehelee", "puzzlem00n", "fredeux", "xtylorx", "romulus1997", "dextile",
				"mcminingcaveman", "staroki", "Infinitecorners", "wasthisme", "_stevoism_",
				"ultimateafs", "mittykitten", "nether_god97", "fortification45",
				"echophox", "kalibj", "mcmorkey", "codebaka", "mattdude234", "ruggedKingz",
				"El_pengi", "twootton", "ajs", "raeophox", "xxthesilent18xx", "gluumba",
				"nightling3", "321mcblaster", "thunder_rain", "blackdiamond31", "crisscrisis",
				"jestercopperpot", "dellman135", "crystalcraftmc", p.getName()};
		//1 in 5 chance that it's the person getting the firework's name
		int index = rand.nextInt(5) == 1 ? strand.length-1 : rand.nextInt(strand.length);
		if(rand.nextInt(9) == 1) //1 in 9 chance it's me unscrambled :D
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
		delayedGive(wand);
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
	public void randomizeTeam() {
		Team newTeam = team;
		while(newTeam == team) {
			switch(rand.nextInt(4)) {
			case 0:
				newTeam = Team.RED;
				break;
			case 1:
				newTeam = Team.PURPLE;
				break;
			case 2:
				newTeam = Team.BLUE;
				break;
			case 3:
				newTeam = Team.GREEN;
				break;
			}
		}
		team = newTeam;
	}
	public void ateGapple() {
		gapple++;
		gappleTim.start();
	}
	private class AteGapple implements ActionListener {
		private int accumulator = 0;
		private float spread = 0;
		private float gammount = 20;
		private final int GSPEED = 2;
		public void actionPerformed(ActionEvent e) {
			accumulator++;
			if(accumulator < 27) {
				ParticleEffect.REDSTONE.display((float).5, (float)0, (float).5,
						(float)(GSPEED), (int)(gammount), p.getLocation().add(0, 3, 0), 90.0);
			}
			else if(accumulator < 50) {
				spread += .05;
				gammount += 3;
				ParticleEffect.REDSTONE.display((float).5, (float)spread, (float).5,
						(float)(GSPEED), (int)(gammount), p.getLocation().add(0, 3-(spread/2), 0), 90.0);
			}
			else if(accumulator < 100) {
				spread -= .8;
				ParticleEffect.REDSTONE.display((float)(.5), (float)spread, (float)(.5),
						(float)(GSPEED), (int)(gammount), p.getLocation().add(0, 3-(spread/2), 0), 90.0);
			}
			else {
				accumulator = 0;
				spread = 0;
				gammount = 20;
				gappleTim.stop();
			}
		}
	}
	public void delayedGive(ItemStack zis) {
		//the purpose of this method is to fix the bug where an item is given to you,
		//but it doesn't appear in your inventory
		final Player zz=p;
		final ItemStack is = zis;
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run() {
				zz.getInventory().addItem(is);
			}
		}, 5L);
	}
	
	public void blazeSequence() {
		String $ = getRName();
		p.sendMessage(ChatColor.LIGHT_PURPLE + $ + ChatColor.DARK_AQUA +
				" = " + ChatColor.GOLD + this.randomizeR($));
	}
	public String getRName() {
		String[] strand = { "jwood9198", "todd5747", "tethtibis", "jacc734", "jflory7", "dev10k",
				"tehelee", "puzzlem00n", "fredeux", "xtylorx", "romulus1997", "dextile",
				"mcminingcaveman", "staroki", "Infinitecorners", "wasthisme", "_stevoism_",
				"ultimateafs", "mittykitten", "nether_god97", "fortification45",
				"echophox", "kalibj", "mcmorkey", "codebaka", "mattdude234", "ruggedKingz",
				"El_pengi", "twootton", "ajs", "raeophox", "xxthesilent18xx", "gluumba",
				"nightling3", "321mcblaster", "thunder_rain", "blackdiamond31", "crisscrisis",
				"jestercopperpot", "dellman135", "crystalcraftmc", p.getName()};
		return strand[rand.nextInt(42)];
	}
	public String randomizeR(String toR) {
		StringBuilder ascend = new StringBuilder(toR);
		String randStr = "";
		for(int i = 0; i < toR.length(); i++) {
			int pickR = rand.nextInt(ascend.length());
			if(i == 0)
				randStr = randStr.concat(String.format("%C", ascend.charAt(pickR)));
			else
				randStr = randStr.concat(String.valueOf(ascend.charAt(pickR)).toLowerCase());
			ascend.deleteCharAt(pickR);
		}
		return randStr;
	}
}
