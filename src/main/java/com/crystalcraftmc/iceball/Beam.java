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

import javax.swing.Timer;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Beam {
	private Player p;
	private Timer tim;
	private int accumulator = 0;
	private Location center;
	ArrayList<Player> arenaP = new ArrayList<Player>(); //significantly reduce lag
	private int type;
	private IceBall plugin;
	private ArrayList<HitStreak> al;
	public Beam(HitStreak h, int type, ArrayList<Player> onlineP, IceBall plugin, ArrayList<HitStreak> hh) {
		this.plugin = plugin;
		al=hh;
		for(int i = 0; i < onlineP.size(); i++) {
			if(!this.isOutsideArena(onlineP.get(i).getLocation(), 20, true)) {
				arenaP.add(onlineP.get(i));
			}
		}
		this.type = type;
		p = h.p;
		if(type == 4)
			p.sendMessage(ChatColor.GREEN + "Note:  This is the only purely aesthetical beam");
		center = p.getLocation();
		center = center.add(center.getDirection().multiply(2));
		center = center.add(0, 1, 0);
		tim = new Timer(25, new BeamIt());
		tim.start();
	}
	private class BeamIt implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			accumulator++;
			if(accumulator < 1140) {
				displayBeam(type);
			}
			else {
				tim.stop();
			}
		}
	}
	public void displayBeam(int index) {
		if(index != 4 || accumulator%5 == 0) {
			if(index == 0) {
				for(int i = 0; i < 200; i++) {
					center = p.getLocation();
					center = center.add(p.getLocation().getDirection().multiply(.5));
					center = center.add(0, 2.5, 0);
					center = center.add(center.getDirection().multiply(i/10));
					ParticleEffect.FLAME.display((float).01, (float).01, (float).01,
						(float)(0), (int)(1), center, 90.0);
					for(int ii = 0; ii < arenaP.size(); ii++) {
						if(this.within1BlockCube(center, arenaP.get(ii).getLocation()) &&
								!arenaP.get(ii).getName().equals(p.getName())) {
							if(arenaP.get(ii).getFireTicks() == 0) {
								arenaP.get(ii).addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE,
										100, 1));
								arenaP.get(ii).setFireTicks(90);
							}
						}
					}
				}
			}
			else if(index == 1) {
				for(int i = 0; i < 200; i++) {
					center = p.getLocation();
					center = center.add(p.getLocation().getDirection().multiply(.5));
					center = center.add(0, 2.5, 0);
					center = center.add(center.getDirection().multiply(i/10));
					ParticleEffect.WATER_WAKE.display((float).01, (float).01, (float).01,
						(float)(0), (int)(1), center, 90.0);
					for(int ii = 0; ii < arenaP.size(); ii++) {
						if(this.within1BlockCube(center, arenaP.get(ii).getLocation()) &&
								!arenaP.get(ii).getName().equals(p.getName())) {
							for(int iii = 0; iii < al.size(); iii++) {
								if(al.get(iii).getName().equals(arenaP.get(ii).getName())) {
									al.get(iii).setDouble(400); //doubles speed in which hitstreaks gotten
								}
							}
						}
					}
				}
			}
			else if(index == 2) {
				for(int i = 0; i < 200; i++) {
					center = p.getLocation();
					center = center.add(p.getLocation().getDirection().multiply(.5));
					center = center.add(0, 2.5, 0);
					center = center.add(center.getDirection().multiply(i/10));
					ParticleEffect.CLOUD.display((float).01, (float).01, (float).01,
						(float)(0), (int)(1), center, 90.0);
					for(int ii = 0; ii < arenaP.size(); ii++) {
						if(this.within1BlockCube(center, arenaP.get(ii).getLocation()) &&
								!arenaP.get(ii).getName().equals(p.getName())) {
							for(int iii = 0; iii < al.size(); iii++) {
								if(al.get(iii).getName().equals(arenaP.get(ii).getName())) {
									if(!arenaP.get(ii).isInsideVehicle())
										new Leviosa(arenaP.get(ii), plugin, false);
								}
							}
						}
					}
				}
			}
			else if(index == 3) {
				for(int i = 0; i < 200; i++) {
					center = p.getLocation();
					center = center.add(p.getLocation().getDirection().multiply(.5));
					center = center.add(0, 2.5, 0);
					center = center.add(center.getDirection().multiply(i/10));
					ParticleEffect.SPELL_MOB.display((float).01, (float).01, (float).01,
						(float)(0), (int)(1), center, 90.0);
					for(int ii = 0; ii < arenaP.size(); ii++) {
						if(this.within1BlockCube(center, arenaP.get(ii).getLocation()) &&
								!arenaP.get(ii).getName().equals(p.getName())) {
							if(!arenaP.get(ii).hasPotionEffect(PotionEffectType.BLINDNESS)) {
								arenaP.get(ii).addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS,
										100, 1));
							}
						}
					}
				}
			}
			else if(index == 4) {
				for(int i = 0; i < 200; i++) {
					center = p.getLocation();
					center = center.add(p.getLocation().getDirection().multiply(.5));
					center = center.add(0, 2.5, 0);
					center = center.add(center.getDirection().multiply(i/10));
					ParticleEffect.DRIP_LAVA.display((float).01, (float).01, (float).01,
						(float)(0), (int)(1), center, 90.0);
				}
			}
		}
	}
	public boolean within1BlockCube(Location test, Location target) {
		double xDist = Math.abs(target.getX()-test.getX());
		double zDist = Math.abs(target.getZ()-test.getZ());
		double yDist = Math.abs(target.getZ()-test.getZ());
		if(xDist < 1 && zDist < 1 && yDist < 1)
			return true;
		else
			return false;
	}
	public boolean isOutsideArena(Location loc, int constant, boolean checkY) {
		if(loc.getWorld().getEnvironment() != Environment.NORMAL)
			return true;
		int x = (int)loc.getX();
		int y = (int)loc.getY();
		int z = (int)loc.getZ();
		if(checkY) {
			if(y < (plugin.Y+5) && y > (plugin.Y-14)) { /*Bukkit.broadcastMessage("Proper y"); */}
			else
				return true;
		}
		if(x > (plugin.X-constant) && x < (plugin.X+constant) &&
				z > (plugin.Z-constant) && z < (plugin.Z+constant)) {
			return false;
		}
		else {
			return true;
		}
	}
}
