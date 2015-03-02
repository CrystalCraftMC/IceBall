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


import org.bukkit.Location;

public class Inbound {
	private ArrayList<Integer> indecis;
	private final IceBall plugin;
	private final ArrayList<Location> spawns;
	private ArrayList<Location> target = new ArrayList<Location>();
	private Location gridC;
	public Inbound(ArrayList<Integer> index, ArrayList<Location> spa, IceBall plu, Location gridC) {
		this.gridC = gridC;
		indecis = index;
		spawns = spa;
		plugin = plu;
		this.setTargets();
		for(int i = 0; i < indecis.size(); i++) {
			this.delayedCall(i, index.get(i));
		}
	}
	public void delayedCall(int iterate, int index5) {
		final int index = index5;
		final int iteration = iterate;
		final Location targetA = target.get(iterate);
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run() {
				new Pilot(index, spawns, plugin, targetA);
			}
		}, (long)(iteration*20 + 50));
	}
	public void setTargets() {
		for(int i = 0; i < indecis.size(); i++) {
			double locX = gridC.getX() + g2b((8.0/5)*((int)(indecis.get(i)/9)));
			double locZ = gridC.getZ() - g2b(indecis.get(i)%9);
			target.add(new Location(spawns.get(0).getWorld(), locX, gridC.getY()-1, 
					locZ));
		}
	}
	public double g2b(double in) {
		return ((double)(in*3.444444));
	}
	public double b2g(double in) {
		return ((double)(in/3.444444));
	}
}
