/*
package com.penzzly.engine.core.components.player;

import com.penzzly.engine.architecture.base.Component;
import com.penzzly.engine.architecture.holder.mutable.CachedHolder;
import com.penzzly.engine.architecture.holder.mutable.MutableHolder;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.function.Consumer;

import static com.penzzly.engine.core.base.Disable.*;
import static com.penzzly.engine.core.base.Events.listen;
import static com.penzzly.engine.core.utilites.MathUtil.bound;
import static com.penzzly.engine.core.utilites.RandomUtil.randomElement;
import static org.bukkit.GameMode.ADVENTURE;
import static org.bukkit.GameMode.SURVIVAL;
import static org.bukkit.Material.ARROW;
import static org.bukkit.Sound.UI_BUTTON_CLICK;
import static org.bukkit.potion.PotionEffectType.INVISIBILITY;

public class SpectatorComponent extends Component implements MutableHolder<Player> {
    private static final float SPEED_INCREMENT = 0.1f;
    private final Set<Player> spectators = new HashSet<>();
    private final List<Consumer<Player>> removeListeners = new ArrayList<>();
    private final List<Consumer<Player>> addListeners = new ArrayList<>();
    private final List<Consumer<Player>> removedListeners = new ArrayList<>();
    private final List<Consumer<Player>> addedListeners = new ArrayList<>();
    private final List<Vector> spawns;

    public SpectatorComponent(List<Vector> spawns) {
        this.spawns = spawns;
        addChild(pvp(this),
                damage(this),
                hunger(this),
                dropItem(this),
                itemPickup(this),
                listen(PlayerItemHeldEvent.class, event -> {
                    Player player = event.getPlayer();
                    if (!test(player))
                        return;

                    event.setCancelled(true);
                    if (event.getNewSlot() == 0)
                        return;

                    float speed = player.getFlySpeed() + (event.getNewSlot() < 5 ? -SPEED_INCREMENT : SPEED_INCREMENT);
                    if (speed > 1.05f || speed < SPEED_INCREMENT + 0.05f) {
                        player.playSound(player.getLocation(), UI_BUTTON_CLICK, 10, 2);
                        return;
                    }

                    player.getInventory().getItemInOffHand().setAmount((int) (speed * 10));
                    player.setFlySpeed(bound(speed, 1f, 0.1f));
                }));

        onAdd(this::addListener);
        onRemove(this::removeListener);
        onDisable(this::clear);
    }

    private void addListener(Player player) {
        player.setGameMode(ADVENTURE);
        player.setAllowFlight(true);
        player.setFlying(true);
        player.setVelocity(new Vector());
        player.getInventory().setHeldItemSlot(0);
        for (PotionEffect potionEffect : player.getActivePotionEffects())
            player.removePotionEffect(potionEffect.getType());
        player.addPotionEffect(new PotionEffect(INVISIBILITY, Integer.MAX_VALUE, 10, true, true));

        player.teleport(player.getLocation().add(0, 10, 0));
        player.getInventory().clear();

        player.getInventory().setItemInOffHand(new ItemStack(ARROW, 1));
        player.setFlySpeed(0.1f);
    }

    private void removeListener(Player player) {
        player.setGameMode(SURVIVAL);
        player.setVelocity(new Vector());
        player.setAllowFlight(false);
        player.setFlying(false);
        player.teleport(randomElement(spawns).toLocation(player.getWorld()));
        for (PotionEffect potionEffect : player.getActivePotionEffects())
            player.removePotionEffect(potionEffect.getType());


        player.setFlySpeed(0.1f);
        player.getInventory().clear();
    }

    @Override
    public List<Consumer<Player>> getRemoveListeners() {
        return removeListeners;
    }

    @Override
    public List<Consumer<Player>> getRemovedListeners() {
        return removedListeners;
    }

    @Override
    public List<Consumer<Player>> getAddListeners() {
        return addListeners;
    }

    @Override
    public List<Consumer<Player>> getAddedListeners() {
        return addedListeners;
    }

    @Override
    public Collection<Player> getContents() {
        return spectators;
    }
}*/
