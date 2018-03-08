package com.penzzly.engine.core.mini.partial;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

//https://gist.github.com/Exerosis/46ceff88d8efd6cd1caebf8ae0e5abd1
public class Particles {
	private final Particle type;
	private int count = 0;
	private double x, y, z = 0;
	private Object data;
	
	public Particles(Particle type) {
		this.type = type;
	}
	
	public static Particles create(Particle type) {
		return new Particles(type);
	}
	
	public Particles color(Color color) {
		return color(color.getRed(), color.getGreen(), color.getBlue());
	}
	
	public Particles color(Number red, Number green, Number blue) {
		x = red.intValue() / 255f;
		y = green.intValue() / 255f;
		z = blue.intValue() / 255f;
		return this;
	}
	
	public Particles offset(Vector vector) {
		x = (float) vector.getX();
		y = (float) vector.getY();
		z = (float) vector.getZ();
		return this;
	}
	
	public Particles offset(Number randSeed) {
		return offset(randSeed, randSeed, randSeed);
	}
	
	public Particles offset(Number x, Number y, Number z) {
		this.x = x.floatValue();
		this.y = y.floatValue();
		this.z = z.floatValue();
		return this;
	}
	
	public Particles block(Block type) {
		return block(new MaterialData(type.getType(), type.getData()));
	}
	
	public Particles block(Material type) {
		return block(type.getNewData((byte) 0));
	}
	
	public Particles block(MaterialData type) {
		data = type;
		return this;
	}
	
	public Particles item(Material type) {
		return item(new ItemStack(type));
	}
	
	public Particles item(ItemStack stack) {
		data = stack;
		return this;
	}
	
	public Particles count(Number count) {
		this.count = count.intValue();
		return this;
	}
	
	public static void spawn(Location location, Particles particle) {
		location.getWorld().spawnParticle(
				particle.type,
				location,
				particle.count,
				particle.x,
				particle.y,
				particle.z,
				particle.data
		);
	}
	
	public static void show(Player player, Vector location, Particles particle) {
		show(player, location.toLocation(player.getWorld()), particle);
	}
	
	public static void show(Player player, Location location, Particles particle) {
		player.spawnParticle(
				particle.type,
				location,
				particle.count,
				particle.x,
				particle.y,
				particle.z,
				particle.data
		);
	}
}
