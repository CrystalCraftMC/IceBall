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

import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class AnimatePlayer {
	private Player p;
	private double xVel, zVel, yVel;
	private Timer tim;
	private int accumulator;
	private IceBall plugin;
	public AnimatePlayer(Player p, double xVel, double zVel, IceBall plugin) {
		this.p = p;
		this.xVel = -xVel;
		yVel = 44;
		this.yVel = -zVel;
		accumulator = 0;
		this.plugin = plugin;
		tim = new Timer(10, new UpdateTim());
		tim.start();
	}
	private class UpdateTim implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			accumulator++;
			if(accumulator > 135)
				tim.stop();
			else if(accumulator > 25) {
			p.setVelocity(new Vector(xVel, yVel, zVel));
				xVel -= .004;
				yVel -= .004;
				zVel -= .004;
			}
			else {
				if(p.getLocation().getY() < plugin.Y-1) {
					p.setVelocity(new Vector(0, 1, 0));
				}
			}
		}
	}
}
