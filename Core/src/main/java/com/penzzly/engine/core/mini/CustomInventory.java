package com.penzzly.engine.core.mini;

import net.minecraft.server.v1_12_R1.EntityHuman;
import net.minecraft.server.v1_12_R1.IChatBaseComponent;
import net.minecraft.server.v1_12_R1.IInventory;
import net.minecraft.server.v1_12_R1.ItemStack;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class CustomInventory implements MiniWindow.Page, IInventory {
	private final Map<Integer, MiniWindow.Item> elements = new IdentityHashMap<>();
	private String title = "Menu";
	
	@Override
	public int getSize() {
		return size();
	}
	
	@Override
	public net.minecraft.server.v1_12_R1.ItemStack getItem(int i) {
		return null;
	}
	
	@Override
	public void setItem(int i, net.minecraft.server.v1_12_R1.ItemStack itemStack) {
	
	}
	
	@Override
	public int getMaxStackSize() {
		return 127;
	}
	
	@Override
	public boolean x_() {
		return false;
	}
	
	@Override
	public void update() {
	
	}
	
	@Override
	public net.minecraft.server.v1_12_R1.ItemStack splitWithoutUpdate(int i) {
		return null;
	}
	
	@Override
	public net.minecraft.server.v1_12_R1.ItemStack splitStack(int i, int i1) {
		return null;
	}
	
	@Override
	public boolean a(EntityHuman entityHuman) {
		return false;
	}
	
	@Override
	public void startOpen(EntityHuman entityHuman) {
	
	}
	
	@Override
	public void closeContainer(EntityHuman entityHuman) {
	
	}
	
	@Override
	public boolean b(int i, net.minecraft.server.v1_12_R1.ItemStack itemStack) {
		return false;
	}
	
	@Override
	public int getProperty(int i) {
		return 0;
	}
	
	@Override
	public void setProperty(int i, int i1) {
	
	}
	
	@Override
	public int h() {
		return 0;
	}
	
	@Override
	public void clear() {
	
	}
	
	@Override
	public List<ItemStack> getContents() {
		return null;
	}
	
	@Override
	public void onOpen(CraftHumanEntity craftHumanEntity) {
	
	}
	
	@Override
	public void onClose(CraftHumanEntity craftHumanEntity) {
	
	}
	
	@Override
	public List<HumanEntity> getViewers() {
		return null;
	}
	
	@Override
	public InventoryHolder getOwner() {
		return null;
	}
	
	@Override
	public void setMaxStackSize(int i) {
	
	}
	
	@Override
	public Location getLocation() {
		return null;
	}
	
	
	@Override
	public MiniWindow.Page title(Object title) {
		this.title = title.toString();
		return this;
	}
	
	@Override
	public Map<Integer, MiniWindow.Item> items() {
		return elements;
	}
	
	@Override
	public Inventory toInventory() {
		return null;
	}
	
	@Override
	public MiniWindow.Item item(Number index) {
		return null;
	}
	
	@Override
	public List<Consumer<Player>> getCloseListeners() {
		return null;
	}
	
	@Override
	public List<Consumer<Player>> getOpenListeners() {
		return null;
	}
	
	@Override
	public String getName() {
		return null;
	}
	
	@Override
	public boolean hasCustomName() {
		return false;
	}
	
	@Override
	public IChatBaseComponent getScoreboardDisplayName() {
		return null;
	}
	
	@Override
	public Inventory getInventory() {
		return null;
	}
}