package com.penzzly.engine.core.components.world;

import com.penzzly.engine.architecture.base.Component;

public class DigSpeedComponent extends Component {
//	private ScheduledExecutorService executor;
//	private final Map<Player, ScheduledFuture> tasks = new HashMap<>();
//
//	public DigSpeedComponent(TimeUnit unit, @NotNull BiFunction<Player, Block, Integer> digSpeed) {
//		this(digSpeed.andThen(time -> Pair.with(time, unit)));
//	}
//
//	public DigSpeedComponent(TimeUnit unit, @NotNull BiFunction<Player, Block, Integer> digSpeed, @NotNull BiFunction<Player, Block, Collection<ItemStack>> drops) {
//		this(digSpeed.andThen(time -> Pair.with(time, unit)), drops);
//	}
//
//	public DigSpeedComponent(@NotNull BiFunction<Player, Block, Pair<Integer, TimeUnit>> digSpeed) {
//		this(digSpeed, (player, block) -> null);
//	}
//
//	public DigSpeedComponent(@NotNull BiFunction<Player, Block, Pair<Integer, TimeUnit>> digSpeed, @NotNull BiFunction<Player, Block, Collection<ItemStack>> drops) {
//		onEnable(() -> {
//			executor = new ScheduledThreadPoolExecutor(1);
//			getProtocolManager().addPacketListener(new PacketAdapter(getPlugin(), PacketType.Play.Client.BLOCK_DIG) {
//				@Override
//				public void onPacketReceiving(@NotNull PacketEvent event) {
//					Player player = event.getPlayer();
//					BlockPosition position = event.getPacket().getBlockPositionModifier().read(0);
//					Block block = position.toLocation(player.getWorld()).getBlock();
//					switch (event.getPacket().getPlayerDigTypes().getValues().get(0)) {
//						case STOP_DESTROY_BLOCK: {
//							stopDestroying(player);
//							break;
//						}
//						case ABORT_DESTROY_BLOCK: {
//							stopDestroying(player);
//							sendBlockDamage(position, 10);
//							break;
//						}
//						case START_DESTROY_BLOCK: {
//							if (tasks.containsKey(player) || !player.getGameMode().equals(GameMode.SURVIVAL)) {
//								return;
//							}
//							//TODO display massive times.
//							Pair<Integer, TimeUnit> time = digSpeed.apply(player, block);
//							long period = time.getValue1().toMillis(time.getValue0()) / 10;
//							tasks.put(player, executor.scheduleAtFixedRate(new Runnable() {
//								private int state = 0;
//
//								@Override
//								public void run() {
//									if (state >= 10) {
//										Bukkit.getScheduler().runTask(plugin, () -> {
//											BlockBreakEvent event = new BlockBreakEvent(block, player);
//											Bukkit.getPluginManager().callEvent(event);
//											if (event.isCancelled()) {
//												return;
//											}
//											Location location = block.getLocation();
//											player.getWorld().playEffect(location, Effect.STEP_SOUND, block.getTypeId());
//											Collection<ItemStack> items = drops.apply(player, block);
//											if (items == null || items.isEmpty()) {
//												block.breakNaturally(player.toInventory().getItemInHand());
//											}
//										});
//										stopDestroying(player);
//									} else {
//										sendBlockDamage(position, state++);
//									}
//								}
//							}, period, period, TimeUnit.MILLISECONDS));
//							Bukkit.getScheduler().runTask(getPlugin(), () -> {
//								player.removePotionEffect(SLOW_DIGGING);
//								player.addPotionEffect(new PotionEffect(SLOW_DIGGING, Integer.MAX_INDEX, -1, true));
//							});
//						}
//					}
//				}
//			});
//		});
//
//		onDisable(() -> {
//			executor.shutdown();
//			tasks.clear();
//		});
//
//		addChild(listen(BlockBreakEvent.class, HIGHEST)).subscribe(event -> {
//			Player player = event.getPlayer();
//			Block block = event.getBlock();
//			Collection<ItemStack> items = drops.apply(player, block);
//			if (items == null || items.isEmpty()) {
//				return;
//			}
//			block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, block.getTypeId());
//			block.setType(Material.AIR);
//			Bukkit.getScheduler().runTaskLater(ServerUtil.getPlugin(), () -> items.forEach(item ->
//					player.getWorld().dropItemNaturally(player.getLocation(), item)), 1);
//		});
//	}
//
//	private void stopDestroying(@NotNull Player player) {
//		Bukkit.getScheduler().runTask(getPlugin(), () -> player.removePotionEffect(SLOW_DIGGING));
//		if (tasks.containsKey(player)) {
//			tasks.remove(player).cancel(true);
//		}
//	}
//
//	private void sendBlockDamage(BlockPosition position, int state) {
//		PacketContainer packet = new PacketContainer(BLOCK_BREAK_ANIMATION);
//		packet.getIntegers().write(0, 0);
//		packet.getBlockPositionModifier().write(0, position);
//		packet.getIntegers().write(1, state);
//		getProtocolManager().broadcastServerPacket(packet);
//	}
}