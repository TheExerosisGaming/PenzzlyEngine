package com.penzzly.engine.core.utilites.bukkit;

import com.penzzly.engine.architecture.functions.Optional;
import io.reactivex.Observable;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static com.penzzly.engine.architecture.functions.Optional.empty;
import static com.penzzly.engine.architecture.functions.Optional.of;
import static com.penzzly.engine.core.base.Events.listen;
import static com.penzzly.engine.core.utilites.functions.Functions.roundRobin;
import static java.lang.Math.round;
import static java.util.concurrent.ThreadLocalRandom.current;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.IntStream.range;
import static org.bukkit.Bukkit.createInventory;
import static org.bukkit.Material.CHEST;
import static org.bukkit.block.BlockFace.*;

/**
 * Durpped in to existence by Exerosis on 3/17/2016.
 */
public class BlockUtil {
	
	private BlockUtil() {
	}
	
	public static ItemStack toItemStack(@NotNull Block block) {
		return toItemStack(block, 1);
	}
	
	public static ItemStack toItemStack(@NotNull Block block, int amount) {
		return toMaterialData(block).toItemStack(amount);
	}
	
	public static MaterialData toMaterialData(@NotNull Block block) {
		return new MaterialData(block.getType(), block.getData());
	}
	
	public static void main(String[] args) {
		final Map<Block, Inventory> chestSpawns = new HashMap<>();
		int count = 1; //How many chests you want to spawn
		int range = 1; //How far out you want to spawn them
		Location center = player.getLocation().setY(253);
		
		while (chestSpawns.size() < count) {
			
			
			directStream(center.clone().add(new Vector(x, 0, z)).getBlock(), DOWN)
					.filter(block -> block.getType().isSolid())
					.findAny()
					.ifPresent(block -> {
						block.setType(CHEST);
						chestSpawns.put(block, createInventory(null, 27));
					});
		}
		
		Map<Block, Inventory> chests = range(0, count)
				.mapToObj($ -> {
					int x = current().nextInt(range), z = current().nextInt(range);
					return center.clone().add(x, 0, z).getBlock();
				})
				.flatMap(top -> directStream(top, DOWN)
						.filter(block -> block.getType().isSolid())
				)
				.collect(toMap(identity(), block -> {
					block.setType(Material.CHEST);
					return createInventory(null, 27); //Optionally replace null with: ((Container) block.getState())
				}));
		
		listen((PlayerInteractEvent event) -> {
			Inventory inventory = chestSpawns.get(event.getClickedBlock());
			if (inventory != null) {
				event.setCancelled(true);
				event.getPlayer().openInventory(inventory);
			}
		});
	}
	
	
	public static Stream<Vector> directStream(Vector start, Vector direction) {
		return StreamSupport.stream(directIterator(start, direction).spliterator(), false);
	}
	
	public static Iterable<Vector> directIterator(Vector start, Vector direction) {
		return () -> new Iterator<Vector>() {
			Vector current = start.clone();
			
			@Override
			public boolean hasNext() {
				return current.getY() < 256;
			}
			
			@Override
			public Vector next() {
				return current.add(direction);
			}
		};
	}
	
	private static final BlockFace[] ORDERED = {
			NORTH, NORTH_EAST,
			EAST, SOUTH_EAST,
			SOUTH, SOUTH_WEST,
			WEST, NORTH_WEST
	};
	
	public static BlockFace direction(float yaw) {
		return ORDERED[round(yaw / 45f) & 0x3];
	}
	
	public static BlockFace direction(@NotNull Entity entity) {
		return direction(entity.getLocation());
	}
	
	public static BlockFace direction(@NotNull Location location) {
		return direction(location.getYaw());
	}
	
	public static void main3(String[] args) {
		System.out.println(direction(90));
	}
	
	public static Stream<Block> multiDirectStream(@NotNull Block block, BlockFace... faces) {
		List<Iterator<Block>> iterators = new ArrayList<>();
		for (BlockFace face : faces)
			iterators.add(directIterator(block, face));
		
		return StreamSupport.stream(((Iterable<Block>) () -> new Iterator<Block>() {
			int index = 0;
			Block next = getNext();
			
			@Override
			public boolean hasNext() {
				return next != null;
			}
			
			@Override
			public Block next() {
				try {
					return next;
				} finally {
					next = getNext();
				}
			}
			
			@SuppressWarnings("unchecked")
			private Block getNext() {
				while (iterators.size() > 0) {
					if (index >= iterators.size()) {
						index = 0;
					}
					Iterator<Block> iterator = iterators.get(index++);
					if (!iterator.hasNext()) {
						return iterator.next();
					}
					iterators.remove(iterator);
				}
				return null;
			}
		}).spliterator(), false);
	}
	
	private static Iterator<Block> directIterator(@NotNull Block block, BlockFace direction) {
		return new Iterator<Block>() {
			@Nullable Block next = getNext();
			
			@Override
			public boolean hasNext() {
				return next != null;
			}
			
			@Nullable
			@Override
			public Block next() {
				try {
					return next;
				} finally {
					next = getNext();
				}
			}
			
			private Block getNext() {
				if (block.getY() <= 0 || block.getY() >= 256) {
					return null;
				} else {
					return next.getRelative(direction);
				}
			}
		};
	}
	
	public static Stream<Block> directStream(@NotNull Block block, BlockFace direction) {
		return StreamSupport.stream(((Iterable<Block>) () ->
				directIterator(block, direction)).spliterator(), false);
	}
	
	//--Location--
	@NotNull
	public static Optional<Block> directSearch(@NotNull Location location, BlockFace
			direction, @NotNull Predicate<Block> target) {
		return directSearch(location, () -> direction, target);
	}
	
	@NotNull
	public static Optional<Block> directSearch(@NotNull Location location, @NotNull Supplier<BlockFace> direction, @NotNull Predicate<Block> target) {
		return directSearch(location.getBlock(), direction, target);
	}
	
	@NotNull
	public static Optional<Block> directSearch(@NotNull Location location, BlockFace first, BlockFace
			second, @NotNull Predicate<Block> target) {
		return directSearch(location.getBlock(), first, second, target);
	}
	
	
	//Block--
	@NotNull
	public static Optional<Block> directSearch(@NotNull Block block, BlockFace first, BlockFace
			second, @NotNull Predicate<Block> target) {
		return directSearch(block, roundRobin(first, second), target);
	}
	
	@NotNull
	public static Optional<Block> directSearch(@NotNull Block
			                                           block, @NotNull Supplier<BlockFace> direction, @NotNull Predicate<Block> target) {
		do
			if (target.test(block)) {
				return of(block);
			}
		while ((block = block.getRelative(direction.get())) != null);
		return empty();
	}
	
	@NotNull
	public static List<Location> sphere(@NotNull Block center, int radius, boolean hollow) {
		return sphere(center.getLocation(), radius, hollow);
	}
	
	@NotNull
	public static List<Location> sphere(@NotNull Location center, int radius, boolean hollow) {
		List<Location> blocks = new ArrayList<Location>();
		int bx = center.getBlockX();
		int by = center.getBlockY();
		int bz = center.getBlockZ();
		Observable.fromIterable()
		for (int x = bx - radius; x <= bx + radius; x++) {
			for (int y = by - radius; y <= by + radius; y++) {
				for (int z = bz - radius; z <= bz + radius; z++) {
					
					double distance = ((bx - x) * (bx - x) + ((bz - z) * (bz - z)) + ((by - y) * (by - y)));
					
					if (distance < radius * radius && !(hollow && distance < ((radius - 1) * (radius - 1)))) {
						
						Location l = new Location(center.getWorld(), x, y, z);
						
						blocks.add(l);
						
					}
					
				}
			}
		}
		return blocks;
	}
}
