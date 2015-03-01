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
import org.bukkit.Location;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class JwoodSpecial {
	private Random rand = new Random();
	private Player p;
	private IceBallListener ibl;
	private Location loc;
	private IceBall plugin;
	private Timer verticleBeam;
	private int verticleBeamAccumulator = 0;
	private Timer theCloud;
	private int theCloudAccumulator = 0;
	private float breadth;
	private Timer ambiance;
	private Creeper creep;
	private Bat bat;
	private int randX;
	private int randZ;
	private int ambianceAccumulator = 0;
	private Location center;
	public JwoodSpecial(Player z, IceBall pl, IceBallListener ibls) {
		p=z;
		ibl = ibls;
		plugin = pl;
		loc = new Location(p.getWorld(), plugin.X, plugin.Y-7, plugin.Z);
		new RingOfFire(loc, 5.8, false);
		Location throne = new Location(p.getWorld(), plugin.X+.6, plugin.Y-4.7, plugin.Z+.6);
		throne.setPitch((float)22);
		throne.setYaw((float)270);
		p.teleport(throne);
		verticleBeam = new Timer(50, new VertBeam());
		verticleBeam.start();
		theCloud = new Timer(100, new CloudGrow());
		theCloud.start();
		ambiance = new Timer(50, new Ambiance());
		ambiance.start();
	}
	private class VertBeam implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			verticleBeamAccumulator++;
			ParticleEffect.SUSPENDED_DEPTH.display((float).01, (float)3.0, (float).01,
					(float)0, 100, new Location(p.getWorld(), plugin.X+.4,
							plugin.Y-4.5, plugin.Z+.5), 90.0);
			if(verticleBeamAccumulator > 400)
				verticleBeam.stop();
		}
	}
	private class CloudGrow implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			theCloudAccumulator++;
			if(theCloudAccumulator <= 60) {
				ParticleEffect.VILLAGER_HAPPY.display((float).5, (float).2, (float).5,
						(float)0, 100, new Location(p.getWorld(), plugin.X+.4,
								plugin.Y-.8, plugin.Z+.5), 90.0);
			}
			else if(theCloudAccumulator < 300) {
				breadth = (float)((theCloudAccumulator-40)/40.0);
				ParticleEffect.VILLAGER_HAPPY.display(breadth, (float).2, breadth,
						(float).04, theCloudAccumulator*2, new Location(p.getWorld(), plugin.X+.4,
								plugin.Y-.8, plugin.Z+.5), 90.0);
			}
			else if(theCloudAccumulator < 450) {
				ParticleEffect.VILLAGER_HAPPY.display(breadth, (float).2, breadth,
						(float).04, 600, new Location(p.getWorld(), plugin.X+.4,
								plugin.Y-.8, plugin.Z+.5), 90.0);
			}
			else {
				theCloud.stop();
			}
		}
	}
	private class Ambiance implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			ambianceAccumulator++;
			ParticleEffect.FOOTSTEP.display((float)30, (float)15, (float)30,
					(float)0, 100, new Location(p.getWorld(), plugin.X+.4,
							plugin.Y-4.5, plugin.Z+.5), 90.0);
			if(ambianceAccumulator == 100) {
				randX=rand.nextInt(6);
				randZ=rand.nextInt(6);
				new RingOfFire(new Location(p.getWorld(), plugin.X + randX, 
						plugin.Y+5, plugin.Z+randZ), 1.5, true);
			}
			if(ambianceAccumulator == 300) {
				plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					public void run() {
						creep = (Creeper)p.getWorld().spawnEntity(p.getLocation(),
						EntityType.CREEPER);
						creep.setCustomName(ChatColor.AQUA + "Crystal-Dragon");
						creep.setCustomNameVisible(true);
						bat = (Bat)p.getWorld().spawnEntity(new Location(p.getLocation().getWorld(),
						randX+plugin.X, plugin.Y-3.3, randZ+plugin.Z), EntityType.BAT);
						center = bat.getLocation();
						bat.setPassenger(creep);
						ParticleEffect.MOB_APPEARANCE.display((float).5, (float)1, (float).5,
							(float).5, 15, bat.getLocation(), 90.0);
					}
				}, 1L);
			}
			if(ambianceAccumulator > 309) {
				if(!bat.isDead()) {
					double xSet = bat.getLocation().getX() > center.getX() ? -.1 : .1;
					double zSet = bat.getLocation().getZ() > center.getZ() ? -.1 : .1;
					bat.setVelocity(new Vector(xSet, -.07, zSet));
				}
				if(bat.getLocation().getY() < plugin.Y-5 && !bat.isDead()) {
					bat.setHealth(0);
					creep.setPowered(true);
					ArrayList<Player> players = new ArrayList<Player>(plugin.getServer().getOnlinePlayers());
					ArrayList<Player> candidates = new ArrayList<Player>();
					for(int i = 0; i < players.size(); i++) {
						if(!ibl.isOutsideArena(players.get(i), 20, true)) {
							if(players.get(i).getLocation().getY() < plugin.Y-2)
								candidates.add(players.get(i));
						}
					}
					Player target = candidates.size() > 0 ?
						candidates.get(rand.nextInt(candidates.size())) : p;
					creep.setTarget(target);
					creep.setMaxHealth(10);
					creep.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 500, 2));
				}
			}
			if(ambianceAccumulator > 900)
				ambiance.stop();
		}
	}
}
