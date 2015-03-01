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

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.util.Vector;

public class Turret {
	private final double SPEED = 1.5;
	private final double YSPEED = 0;
	public Turret(Villager v73, IceBall plugin, int delay) {
		final long slo = delay;
		final Villager v = v73;
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run() {
				Entity[] snowball = { v.getWorld().spawnEntity(v.getLocation().add(1, 0, 0), 
						EntityType.SNOWBALL),
				v.getWorld().spawnEntity(v.getLocation().add(-1, 0, 0), EntityType.SNOWBALL),
				v.getWorld().spawnEntity(v.getLocation().add(0, 0, 1), EntityType.SNOWBALL),
				v.getWorld().spawnEntity(v.getLocation().add(0, 0, -1), EntityType.SNOWBALL) };
				for(int i = 0; i < snowball.length; i++)
					snowball[i].setCustomName("v73warship");
				snowball[0].setVelocity(new Vector(SPEED, YSPEED, 0));
				snowball[1].setVelocity(new Vector(-SPEED, YSPEED, 0));
				snowball[2].setVelocity(new Vector(0, YSPEED, SPEED));
				snowball[3].setVelocity(new Vector(0, YSPEED, -SPEED));
			}
		}, slo);
	}
}
