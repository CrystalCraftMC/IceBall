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

import org.bukkit.Location;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class RingOfFire {
	
	private Timer tim;
	private Player p;
	private Location loc;
	private int accumulator = 0;
	private int small;
	private int large;
	private boolean updateY;
	private ArrayList<ROFMemory> al = new ArrayList<ROFMemory>();
	private double R = 1.5;
	private boolean created = false;
	private Creeper doc;
	private boolean doccc;
	private Location docL;

	public RingOfFire(Player z) {
		p=z;
		updateY=false;
		small = 81;
		large = 480;
		loc = p.getLocation();
		tim = new Timer(25, new UpdateROF());
		tim.start();
	}
	public RingOfFire(Location locs, double rad, boolean isSpiral) {
		updateY=isSpiral;
		
		this.loc = locs.add(.5, 0, .5);
		small = isSpiral ? 300 : 240;
		large = isSpiral ? 800 : 1500;
		if(isSpiral) {
			for(int i = 0; i < 500; i++) {
				double lookout = 360.0/(i%50);
				double angle = (double)((double)i*lookout) * Math.PI / 180.0;
				double x = Math.cos(angle) * R + loc.getX();
				double z = Math.sin(angle) * R + loc.getZ();
				double yV = i/20.0;
				al.add(new ROFMemory(x, z, loc, yV));
			}
		}
		
		R=rad;
		int framerate = 25;
		tim = new Timer(framerate, new UpdateROF());
		tim.start();
	}
	public RingOfFire(Location locs, double rad, boolean isSpiral, boolean isCrystal, Player z, boolean docc) {
		updateY=isSpiral;
		p=z;
		doccc = docc;
		this.loc = locs.add(.5, 0, .5);
		small = 67;
		large = 151;
		
		R=rad;
		int framerate = 100;
		tim = new Timer(framerate, new UpdateROF2());
		tim.start();
	}
	private class UpdateROF implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			accumulator++;
			if(accumulator < small) {
				if(updateY) {
					for(int i = 0; i < al.size(); i++) {
						al.get(i).rekindle();
						if(updateY)
							al.get(i).animate();
					}
				}
				else {
					double lookout = (360.0/(small-1));
					double angle = (double)(accumulator*lookout) * Math.PI / 180.0;
					double x = Math.cos(angle) * R + loc.getX();
					double z = Math.sin(angle) * R + loc.getZ();
					for(int i = 0; i < al.size(); i++) {
						al.get(i).rekindle();
					}
					double yV = updateY ? accumulator/20.0 : 0;
					al.add(new ROFMemory(x, z, loc, yV));
				}
			}
			else if(accumulator < large) {
				
				for(int i = 0; i < al.size(); i++) {
					al.get(i).rekindle();
					if(updateY)
						al.get(i).animate();
				}
			}
			else {
				eliminateROFAL();
				tim.stop();
			}
		}
	}
	private class UpdateROF2 implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			accumulator++;
			if(doccc) {
				if(created) {
					double x$, z$, y$;
					x$ = doc.getLocation().getX() < docL.getX() ? .1 : -.1;
					y$ = doc.getLocation().getY() < docL.getY() ? .1 : -.1;
					z$ = doc.getLocation().getZ() < docL.getZ() ? .1 : -.1;
					doc.setVelocity(new Vector(x$, y$, z$));
				}
				else {
					docL = new Location(loc.getWorld(), loc.getX(), loc.getY()+2, loc.getZ());
					doc = (Creeper)p.getWorld().spawnEntity(docL, EntityType.CREEPER);
					doc.setPowered(true);
					doc.setMaxHealth(11);
					created = true;
				}
			}
			if(accumulator < small) {
				
					double lookout = (360.0/(small-1));
					double angle = (double)(accumulator*lookout) * Math.PI / 180.0;
					double x = Math.cos(angle) * R + loc.getX();
					double z = Math.sin(angle) * R + loc.getZ();
					for(int i = 0; i < al.size(); i++) {
						al.get(i).rekindle();
					}
					double yV = updateY ? accumulator/20.0 : 0;
					al.add(new ROFMemory(x, z, loc, yV));
				
			}
			else if(accumulator < large) {
				
				for(int i = 0; i < al.size(); i++) {
					al.get(i).rekindle();
				}
			}
			else {
				eliminateROFAL();
				doc.setHealth(0);
				tim.stop();
			}
		}
	}
	public void eliminateROFAL() {
		for(int i = 0; i < al.size(); i++)
			al.remove(0);
	}
}