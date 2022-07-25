package net.plazmix.hub.listener;

import net.plazmix.hub.PlazmixHubPlugin;
import net.plazmix.utility.NumberUtil;
import net.plazmix.utility.cooldown.PlayerCooldownUtil;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

public class LobbySheepsListener implements Listener {

    private static final BukkitTask ENTITY_DISPLAY_CHANGE_TASK = new BukkitRunnable() {

        private int animationCounter = 0;

        private final String[] animation = {
                "§c§lУДАРЬ МЕНЯ",
                "§f§lУДАРЬ МЕНЯ",
        };

        @Override
        public void run() {
            animationCounter++;

            if (animationCounter >= animation.length) {
                animationCounter = 0;
            }

            for (World world : Bukkit.getWorlds()) {
                for (Sheep sheep : world.getEntitiesByClass(Sheep.class)) {

                    sheep.setCustomNameVisible(true);
                    sheep.setCustomName(sheep.isOnGround() ? animation[animationCounter] : "§b§lУИИИ");
                }
            }
        }

    }.runTaskTimer(PlazmixHubPlugin.getPlugin(PlazmixHubPlugin.class), 20, 20);

    @EventHandler
    public void onClick(EntityDamageByEntityEvent event) {
        Player player = (Player) event.getDamager();
        Entity rightClicked = event.getEntity();

        if (!(rightClicked instanceof Sheep)) {
            return;
        }

        if (PlayerCooldownUtil.hasCooldown("sheep_attack" + rightClicked.getEntityId(), player)) {
            event.setCancelled(true);
            return;
        }

        handleEntityClick((Sheep) rightClicked);
        PlayerCooldownUtil.putCooldown("sheep_attack" + rightClicked.getEntityId(), player, 2650);
    }

    private void handleEntityClick(Sheep sheep) {
        sheep.getWorld().playSound(sheep.getLocation(), Sound.ENTITY_WITHER_SHOOT, 1, 2);

        sheep.getWorld().spigot().playEffect(sheep.getLocation().clone().add(0, 0.5, 0), Effect.CLOUD,
                0, 0, 0.1F, 0.1F, 0.1F, 0.5F, 10, 50);

        sheep.setColor(DyeColor.getByWoolData((byte) NumberUtil.randomInt(0, 16)));
        sheep.setVelocity(new Vector(0, NumberUtil.randomDouble(1.5, 5.25), 0));
    }

}
