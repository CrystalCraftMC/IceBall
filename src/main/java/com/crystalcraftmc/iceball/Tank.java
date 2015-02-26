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

import org.bukkit.ChatColor;
import org.bukkit.entity.Bat;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

public class Tank implements Listener {
	private final Player p;
	private final double TANKSPEED = .25;
	private final int BATHOVER = 6; //default = -6
	private HitStreak globalHs;
	private Villager v1;
	private IceBall plugin;
	private Villager v2;
	private Bat b;
	private final Timer tim;
	private int accumulator = 0;
	public Tank(Player player, String v1Name, String v2Name, HitStreak hs, IceBall plugin) {
		this.plugin = plugin;
		p=player;
		globalHs=hs;
		v1 = (Villager)p.getWorld().spawnEntity(p.getLocation().add(0, 1, 0), EntityType.VILLAGER);
		v2 = (Villager)p.getWorld().spawnEntity(p.getLocation().add(0, 1, 0), EntityType.VILLAGER);
		b = (Bat)p.getWorld().spawnEntity(p.getLocation().add(0, 1, 0), EntityType.BAT);
		v1.setHealth(8);
		v2.setHealth(8);
		v1.setCustomName(ChatColor.GREEN +"Tanker: "+ChatColor.AQUA+v1Name);
		v2.setCustomName(ChatColor.GREEN +"Tanker: "+ChatColor.AQUA+v2Name);
		v1.setCustomNameVisible(true);
		v2.setCustomNameVisible(true);
		v1.setProfession(Profession.LIBRARIAN);
		v2.setProfession(Profession.LIBRARIAN);
		b.setPassenger(v1);
		v1.setPassenger(v2);
		v2.setPassenger(p);
		tim = new Timer(50, new TankUpdate());
		tim.start();
	}
	private class TankUpdate implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			accumulator++;
			if(accumulator < 5){}
			else {
			if(!p.isInsideVehicle() || !v2.isInsideVehicle() ||
					v2.isDead() || v1.isDead() || b.isDead()) {
				if(!v1.isDead())
					v1.setHealth(0);
				if(!v2.isDead())
					v2.setHealth(0);
				if(!b.isDead())
					b.setHealth(0);
				globalHs.isOnTank = false;
				tim.stop();
			}
			
			int yaw = (int)p.getLocation().getYaw();
			double xVel = 0, zVel = 0;
			if(yaw >= 0 && yaw < 90) {
				xVel = -(yaw/90.0);
				zVel = 1-(yaw/90.0);
			}
			else if(yaw >= 90 && yaw < 180) {
				xVel = -(1-((yaw-90)/90.0));
				zVel = -((yaw-90)/90.0);
			}
			else if(yaw >= 180 && yaw < 270) {
				xVel = (yaw-180)/90.0;
				zVel = -(1-((yaw-180)/90.0));
			}
			else if(yaw >= 270 && yaw < 361) {
				xVel = 1-((yaw-270)/90.0);
				zVel = (yaw-270)/90.0;
			}
			if(accumulator%16 == 0) {
				new Turret(v1, plugin, 4);
			}
			else if(accumulator%8 == 0) {
				new Turret(v2, plugin, 4);
			}
			if(b.getLocation().getY() > (plugin.Y+BATHOVER))
				b.setVelocity(new Vector(xVel*TANKSPEED, -.2, zVel*TANKSPEED));
			else
				b.setVelocity(new Vector(xVel*TANKSPEED, .2, zVel*TANKSPEED));
			}
		}
		
	}
}
