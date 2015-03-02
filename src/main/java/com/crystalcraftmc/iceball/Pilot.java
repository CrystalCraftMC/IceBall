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

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class Pilot {
	private int index;
	private ArrayList<Location> spawns = new ArrayList<Location>();
	private Timer tim;
	private Location spawn;
	private Blaze bomber;
	private int accumulator = 0;
	private Random rand = new Random();
	private IceBall ib;
	private Location target;
	private boolean hasLitBomb = false;
	
	private enum Direction { NORTH, EAST, SOUTH, WEST }
	Direction dir;

	public Pilot(int ind, ArrayList<Location> spa, IceBall plugin, Location targetArea) {
		ib = plugin;
		target = targetArea;
		this.index = ind;
		this.spawns = spa;
		this.spawnEntity();
		this.showyEntrance();
		tim = new Timer(50, new Fly());
		tim.start();
	}
	
	private class Fly implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			accumulator++;
			if(dir == Direction.NORTH) {
				bomber.setVelocity(new Vector(0, 0, -.5));
				ParticleEffect.SMOKE_NORMAL.display((float).3, (float).1, (float).3,
						(float)(.1), (int)(100), new Location(bomber.getWorld(),
								bomber.getLocation().getX(), bomber.getLocation().getY()+.5,
								bomber.getLocation().getZ()+2), 90.0);
			}
			else if(dir == Direction.EAST) {
				bomber.setVelocity(new Vector(.5, 0, 0));
				ParticleEffect.SMOKE_NORMAL.display((float).3, (float).1, (float).3,
						(float)(.1), (int)(100), new Location(bomber.getWorld(),
								bomber.getLocation().getX()-2, bomber.getLocation().getY()+.5,
								bomber.getLocation().getZ()), 90.0);
			}
			else if(dir == Direction.SOUTH) {
				bomber.setVelocity(new Vector(0, 0, .5));
				ParticleEffect.SMOKE_NORMAL.display((float).3, (float).1, (float).3,
						(float)(.1), (int)(100), new Location(bomber.getWorld(),
								bomber.getLocation().getX(), bomber.getLocation().getY()+.5,
								bomber.getLocation().getZ()-2), 90.0);
			}
			else if(dir == Direction.WEST) {
				bomber.setVelocity(new Vector(-.5, 0, 0));
				ParticleEffect.SMOKE_NORMAL.display((float).3, (float).1, (float).3,
						(float)(.1), (int)(100), new Location(bomber.getWorld(),
								bomber.getLocation().getX()+2, bomber.getLocation().getY()+.5,
								bomber.getLocation().getZ()), 90.0);
			}
			
			if(!hasLitBomb) {
				if(within7BlockCube(bomber.getLocation())) {
					hasLitBomb = true;
					for(int i = 0; i < 3; i++)
						carpetBomb(i);
				}
			}
			
			if(accumulator > 65) {
				bomber.setHealth(0);
				if(rand.nextInt(2) == 1) { //50% chance drop blazerod
					int backX=0;
					int backZ=0;
					if(dir == Direction.NORTH)
						backZ = 2;
					if(dir == Direction.EAST)
						backX = -2;
					if(dir == Direction.SOUTH)
						backZ = -2;
					if(dir == Direction.WEST)
						backX = 2;
					final int backX9 = backX;
					final int backZ9 = backZ;
					ib.getServer().getScheduler().scheduleSyncDelayedTask(ib, new Runnable() {
						public void run() {	
							bomber.getWorld().dropItem(new Location(bomber.getWorld(), 
								bomber.getLocation().getX()+backX9, bomber.getLocation().getY(),
								bomber.getLocation().getZ()+backZ9),new ItemStack(Material.BLAZE_ROD, 1));
						}
					}, 1L);
				}
				tim.stop();
			}
		}
	}
	
	public boolean within7BlockCube(Location test) {
		double xDist = Math.abs(target.getX()-test.getX());
		double zDist = Math.abs(target.getZ()-test.getZ());
		if(xDist < 7 && zDist < 7)
			return true;
		else
			return false;
	}
	
	public void spawnEntity() {
		if(index == 18 || index == 19 || index == 27 || index == 28) {
			dir = Direction.SOUTH;
			if(index == 18 || index == 19)
				spawn = spawns.get(6);
			else if(index == 27 || index == 28)
				spawn = spawns.get(8);
		}
		else if(index == 25 || index == 26 || index == 34 || index == 35) {
			dir = Direction.NORTH;
			if(index == 26 || index == 25)
				spawn = spawns.get(5);
			else if(index == 35 || index == 34)
				spawn = spawns.get(7);
		}
		else if(index < 26) {
			dir = Direction.WEST;
			spawn = spawns.get((index%9)+7);
		}
		else if(index > 27) {
			dir = Direction.EAST;
			spawn = spawns.get((index%9)-2);
		}
		bomber = (Blaze)spawn.getWorld().spawnEntity(spawn, EntityType.BLAZE);
	}
	public void showyEntrance() {
		ParticleEffect.EXPLOSION_HUGE.display((float).7, (float)3, (float).7,
				(float)(.3), (int)(1), bomber.getLocation(), 90.0);
	}
	public void carpetBomb(final int iterator) {
		
		ib.getServer().getScheduler().scheduleSyncDelayedTask(ib, new Runnable() {
			public void run() {
				Location mid = new Location(bomber.getWorld(), bomber.getLocation().getX(),
						bomber.getLocation().getY()-.4, bomber.getLocation().getZ());
				Entity sn[] = new Entity[9];
				for(int i = 0; i < 9; i++) {
					Location adjust = mid;
					switch(i) {
					case 0:
						sn[i] = bomber.getWorld().spawnEntity(adjust.add(1, 0, 1), EntityType.SNOWBALL);
						break;
					case 1:
						sn[i] = bomber.getWorld().spawnEntity(adjust.add(0, 0, 1), EntityType.SNOWBALL);
						break;
					case 2:
						sn[i] = bomber.getWorld().spawnEntity(adjust.add(1, 0, 0), EntityType.SNOWBALL);
						break;
					case 3:
						sn[i] = bomber.getWorld().spawnEntity(adjust.add(0, 0, 0), EntityType.SNOWBALL);
						break;
					case 4:
						sn[i] = bomber.getWorld().spawnEntity(adjust.add(-1, 0, -1), EntityType.SNOWBALL);
						break;
					case 5:
						sn[i] = bomber.getWorld().spawnEntity(adjust.add(1, 0, -1), EntityType.SNOWBALL);
						break;
					case 6:
						sn[i] = bomber.getWorld().spawnEntity(adjust.add(-1, 0, 1), EntityType.SNOWBALL);
						break;
					case 7:
						sn[i] = bomber.getWorld().spawnEntity(adjust.add(-1, 0, 0), EntityType.SNOWBALL);
						break;
					case 8:
						sn[i] = bomber.getWorld().spawnEntity(adjust.add(0, 0, -1), EntityType.SNOWBALL);
						break;
					}
				}
				final double DOWN_S = -.4; //S == speed
				final double FORWARD_S = .4;
				for(int i = 0; i < 9; i++) {
					if(dir == Direction.NORTH) {
						sn[i].setVelocity(new Vector(0, DOWN_S, -FORWARD_S));
					}
					else if(dir == Direction.EAST) {
						sn[i].setVelocity(new Vector(FORWARD_S, DOWN_S, 0));
					}
					else if(dir == Direction.SOUTH) {
						sn[i].setVelocity(new Vector(0, DOWN_S, FORWARD_S));
					}
					else if(dir == Direction.WEST) {
						sn[i].setVelocity(new Vector(-FORWARD_S, DOWN_S, 0));
					}
				}
			}
		}, (long)(iterator*5));
	}
}
