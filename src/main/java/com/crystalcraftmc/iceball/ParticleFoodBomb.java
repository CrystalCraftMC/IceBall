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

import javax.swing.Timer;

import org.bukkit.Location;

public class ParticleFoodBomb {
	private Timer tim;
	private int accumulator = 0;
	private final int BLOCK_REACH = 50;
	private Location ground0;
	private final float XOS = 1F;
	private final float YOS = 0F;
	private final float ZOS = 1F;
	private final float SPEED = 0F;
	private final int AMOUNT = 1;
	
	public ParticleFoodBomb(Location ground0) {
		this.ground0 = ground0;
		tim = new Timer(150, new UpdateParticles());
		tim.start();
	}
	private class UpdateParticles implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			accumulator++;
			if(accumulator >= BLOCK_REACH)
				tim.stop();
			else {
				//north
				ParticleEffect.EXPLOSION_LARGE.display(XOS, YOS, ZOS,
						SPEED, AMOUNT, new Location(ground0.getWorld(), ground0.getX(),
								ground0.getY(), ground0.getZ()-accumulator), 90.0);
				ParticleEffect.NOTE.display(XOS+1, YOS+3, ZOS+1,
						SPEED+3, AMOUNT+8, new Location(ground0.getWorld(), ground0.getX(),
								ground0.getY(), ground0.getZ()-accumulator), 90.0);
				ParticleEffect.VILLAGER_ANGRY.display(XOS, YOS, ZOS,
						SPEED, AMOUNT+22, new Location(ground0.getWorld(), ground0.getX(),
								ground0.getY()+1, ground0.getZ()-accumulator), 90.0);
				//east
				ParticleEffect.EXPLOSION_LARGE.display(XOS, YOS, ZOS,
						SPEED, AMOUNT, new Location(ground0.getWorld(), ground0.getX()+accumulator,
								ground0.getY(), ground0.getZ()), 90.0);
				ParticleEffect.NOTE.display(XOS+1, YOS+3, ZOS+1,
						SPEED+3, AMOUNT+8, new Location(ground0.getWorld(), ground0.getX()+accumulator,
								ground0.getY(), ground0.getZ()), 90.0);
				ParticleEffect.VILLAGER_ANGRY.display(XOS, YOS, ZOS,
						SPEED, AMOUNT+22, new Location(ground0.getWorld(), ground0.getX()+accumulator,
								ground0.getY()+1, ground0.getZ()), 90.0);
				//south
				ParticleEffect.EXPLOSION_LARGE.display(XOS, YOS, ZOS,
						SPEED, AMOUNT, new Location(ground0.getWorld(), ground0.getX(),
								ground0.getY(), ground0.getZ()+accumulator), 90.0);
				ParticleEffect.NOTE.display(XOS+1, YOS+3, ZOS+1,
						SPEED+3, AMOUNT+8, new Location(ground0.getWorld(), ground0.getX(),
								ground0.getY(), ground0.getZ()+accumulator), 90.0);
				ParticleEffect.VILLAGER_ANGRY.display(XOS, YOS, ZOS,
						SPEED, AMOUNT+22, new Location(ground0.getWorld(), ground0.getX(),
								ground0.getY()+1, ground0.getZ()+accumulator), 90.0);
				//west
				ParticleEffect.EXPLOSION_LARGE.display(XOS, YOS, ZOS,
						SPEED, AMOUNT, new Location(ground0.getWorld(), ground0.getX()-accumulator,
								ground0.getY(), ground0.getZ()), 90.0);
				ParticleEffect.NOTE.display(XOS+1, YOS+3, ZOS+1,
						SPEED+3, AMOUNT+8, new Location(ground0.getWorld(), ground0.getX()-accumulator,
								ground0.getY(), ground0.getZ()), 90.0);
				ParticleEffect.VILLAGER_ANGRY.display(XOS, YOS, ZOS,
						SPEED, AMOUNT+22, new Location(ground0.getWorld(), ground0.getX()-accumulator,
								ground0.getY()+1, ground0.getZ()), 90.0);
				/*
				//north
				ParticleEffect.PORTAL.display(new Vector(0, 3, -3), 2, ground0.add(0, 0, -accumulator));
				ParticleEffect.LAVA.display(new Vector(0, 3, -3), 2, ground0.add(0, 1, -accumulator));
				//east
				ParticleEffect.PORTAL.display(new Vector(3, 3, 0), 2, ground0.add(accumulator, 0, 0));
				ParticleEffect.LAVA.display(new Vector(3, 3, 0), 2, ground0.add(accumulator, 1, 0));
				//south
				ParticleEffect.PORTAL.display(new Vector(0, 3, 3), 2, ground0.add(0, 0, accumulator));
				ParticleEffect.LAVA.display(new Vector(0, 3, 3), 2, ground0.add(0, 1, accumulator));
				//west
				ParticleEffect.PORTAL.display(new Vector(-3, 3, 0), 2, ground0.add(-accumulator, 0, 0));
				ParticleEffect.LAVA.display(new Vector(0, 3, -3), 2, ground0.add(-accumulator, 1, 0));*/
			}
		}
	}
}
