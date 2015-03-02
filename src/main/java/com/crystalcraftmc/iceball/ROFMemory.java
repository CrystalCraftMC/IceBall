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

import org.bukkit.Location;

public class ROFMemory {
	private double x;
	private double z;
	private double y;
	private Location loc;
	public ROFMemory(double x, double z, Location loc, double y) {
		this.x = x;
		this.z = z;
		this.y = y; 
		this.loc = loc;
	}
	public void rekindle() {
		ParticleEffect.FLAME.display((float).01, (float).01, (float).01,
				(float)(0), (int)(1), new Location(loc.getWorld(), 
						x, loc.getY()+.2+y, z), 90.0);
	}
	public void animate() {
		loc = new Location(loc.getWorld(), loc.getX(), loc.getY()-.05, loc.getZ());
	}
}
