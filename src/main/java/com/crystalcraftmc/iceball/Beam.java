package com.crystalcraftmc.iceball;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Beam {
	private HitStreak hs;
	private Player p;
	private Timer tim;
	private int accumulator = 0;
	private Location center;
	public Beam(HitStreak h) {
		hs = h;
		p = hs.p;
		center = p.getLocation();
		tim = new Timer(50, new BeamIt());
		tim.start();
	}
	private class BeamIt implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			accumulator++;
			if(accumulator < 27) {
				//winners=CLOUD&&FLAME&&SUSPEND_DEPTH(with tight height/width)
				ParticleEffect.SUSPENDED_DEPTH.display((float)40, (float).01, (float).01,
						(float)(0), (int)(500), new Location(center.getWorld(),
								center.getX(), center.getY(), center.getZ()), 90.0);
			}
			else if(accumulator < 200) {
				ParticleEffect.FLAME.display((float)40, (float)1.0, (float)1.0,
						(float)(0), (int)(1000), new Location(center.getWorld(),
								center.getX(), center.getY(), center.getZ()), 90.0);
			}
			if(accumulator > 200) {
				tim.stop();
				p.sendMessage("stopped tim");
			}
		}
	}
}
