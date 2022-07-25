package net.plazmix.hub.listener;

import com.comphenix.protocol.utility.MinecraftReflection;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.plazmix.core.PlazmixCoreApi;
import net.plazmix.event.PlazmixExperienceChangeEvent;
import net.plazmix.hub.PlazmixHubPlugin;
import net.plazmix.utility.BukkitPotionUtil;
import net.plazmix.utility.ChatUtil;
import net.plazmix.utility.PlayerUtil;
import net.plazmix.utility.book.BookUtil;
import net.plazmix.utility.cooldown.PlayerCooldownUtil;
import net.plazmix.utility.leveling.LevelingUtil;
import net.plazmix.utility.player.PlazmixUser;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.Vector;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class SpawnListener implements Listener {

    //private static final long JOIN_PLAYER_UPDATE_DELAY_TICK_TIME = 20L;
    //private static final Supplier<Boolean> IGNORE_LOGIC_FALSE = () -> false;

    private final Scoreboard levelingBellownameScoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
    private final Objective levelingBellownameObjective = levelingBellownameScoreboard.registerNewObjective("level", "dummy");

    private void updateLevelingBellowName(@NonNull Player player) {
        PlazmixUser plazmixUser = PlazmixUser.of(player);

        levelingBellownameObjective.setDisplaySlot(DisplaySlot.BELOW_NAME);
        levelingBellownameObjective.setDisplayName("§7★");

        Score score = levelingBellownameObjective.getScore(player.getName());
        score.setScore(plazmixUser.getLevel());

        player.setScoreboard(levelingBellownameScoreboard);
    }

    @EventHandler
    public void onBlockFromTo(BlockFromToEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockIgnite(BlockIgniteEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockSpread(BlockSpreadEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBurn(BlockBurnEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPhysicsEvent(BlockPhysicsEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onArmorStandEdit(PlayerArmorStandManipulateEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityDeath(PlayerDeathEvent event) {
        event.setDeathMessage(null);
    }

    @EventHandler
    public void onLeavesDecay(LeavesDecayEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityCombust(EntityCombustEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityBreed(EntityBreedEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityBlockForm(EntityBlockFormEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() != null && event.getClickedBlock().getType().equals(Material.ENDER_CHEST)) {
            return;
        }

        if (event.hasItem() && event.getItem().getType().equals(Material.BOW)) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.CUSTOM)) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {

        if (!(event.getDamager() instanceof Projectile)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {

        if (!event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        PlazmixUser plazmixUser = PlazmixUser.of(event.getPlayer());
        plazmixUser.handle().setGameMode(GameMode.ADVENTURE);

        event.setJoinMessage(null);

        // Update for player...
        this.updatePlayerOnJoin(plazmixUser);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {

        if (event.getTo().getY() <= 0) {
            Bukkit.dispatchCommand(event.getPlayer(), "spawn");
        }
    }

    @EventHandler
    public void onDoubleJump(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();

        if (!PlazmixCoreApi.GROUP_API.isDefault(player.getName())) {
            return;
        }

        event.setCancelled(true);

        if (PlayerCooldownUtil.hasCooldown("double_jump", player)) {
            return;
        }

        PlayerCooldownUtil.putCooldown("double_jump", player, TimeUnit.SECONDS.toMillis(5));

        Vector velocity = player.getLocation().getDirection().clone().multiply(2.5).setY(2);
        player.setVelocity(velocity);

        player.playSound(player.getLocation(), Sound.ENTITY_WITHER_SHOOT, 1, 1);
        player.sendTitle("", "§b§lWHOOSH!", 0, 50, 10);
    }


    @EventHandler
    public void onPlayerFood(FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onExpChange(PlazmixExperienceChangeEvent event) {
        PlazmixUser plazmixUser = PlazmixUser.of(event.getPlayer());

        int currentExp = event.getCurrentExp();
        int level = LevelingUtil.getLevel(currentExp);

        plazmixUser.handle().setLevel(level);
        plazmixUser.handle().setExp((float) ((level > 1 ? currentExp - LevelingUtil.getTotalExpToLevel(level) : currentExp) / LevelingUtil.getExpFromLevelToNext(level)));

        updateLevelingBellowName(plazmixUser.handle());
    }

    private void updatePlayerOnJoin(PlazmixUser plazmixUser) {
        if (plazmixUser.handle() == null) {
            return;
        }

        Bukkit.dispatchCommand(plazmixUser.handle(), "spawn");
        plazmixUser.handle().setAllowFlight(true);

        // Player leveling
        updateLevelingBellowName(plazmixUser.handle());
        plazmixUser.handle().setLevel(plazmixUser.getLevel());
        plazmixUser.handle().setExp((float) plazmixUser.getExperience() / plazmixUser.getMaxExperience());

        // Add potion effects.
        plazmixUser.handle().getActivePotionEffects()
                .forEach(potionEffect -> plazmixUser.handle().removePotionEffect(potionEffect.getType()));

        plazmixUser.handle().addPotionEffect(BukkitPotionUtil.getInfinityPotion(PotionEffectType.JUMP, 1));

        plazmixUser.handle().setFoodLevel(20);
        plazmixUser.handle().setHealth(2);
        plazmixUser.handle().setMaxHealth(2);

        plazmixUser.handle().setWalkSpeed(0.45f);
        plazmixUser.handle().setFlySpeed(0.35f);

        try {
            MinecraftReflection.getCraftPlayerClass().getMethod("updateScaledHealth")
                    .invoke(plazmixUser.handle());
        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException ignored) {
        }


        // Donate features.
        if (!PlazmixCoreApi.GROUP_API.isDefault(plazmixUser.getName())) {
            plazmixUser.handle().playSound(plazmixUser.handle().getLocation(), Sound.ENTITY_ITEM_PICKUP, 1, 1);

            Firework firework = plazmixUser.handle().getWorld().spawn(plazmixUser.handle().getLocation(), Firework.class);
            FireworkMeta fireworkMeta = firework.getFireworkMeta();

            fireworkMeta.setPower(3);
            fireworkMeta.addEffect(FireworkEffect.builder()
                    .with(FireworkEffect.Type.STAR)

                    .withColor(Color.PURPLE)
                    .withColor(Color.WHITE)

                    .build());

            firework.setFireworkMeta(fireworkMeta);
        }

        // Chat announce.
        plazmixUser.localization().sendMessage("WELCOME_MESSAGE");

        // Title announce.
        plazmixUser.handle().playSound(plazmixUser.handle().getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
        plazmixUser.handle().sendTitle("§d§lPlazmix Network", "Онлайн-магазин: §ewww.plazmix.net");

        if (plazmixUser.handle().getStatistic(Statistic.PLAY_ONE_TICK) <= 100) {
            BookUtil.openPlayer(plazmixUser.handle(), BookUtil.writtenBook()

                    .title("Welcome")
                    .author("Plazmix Network")

                    .pages(ChatUtil.newBuilder("§6§lДОБРО ПОЖАЛОВАТЬ!\n" +
                            "\n" +
                            "§8На данный момент сервер находится\n" +
                            "§8в бета-тестировании!\n" +
                            "§8Если Вы найдёте баги\n" +
                            "§8просим §cВас §8сообщить нам\n" +
                            "\n" +
                            "§f       §6§l§nНАЖМИ, СЮДА!\n")

                            .setHoverEvent(HoverEvent.Action.SHOW_TEXT, "§eНажмите, чтобы связаться с технической поддержкой")
                            .setClickEvent(ClickEvent.Action.OPEN_URL, "https://vk.me/plazmixnetwork")

                            .build())

                    .generation(BookMeta.Generation.ORIGINAL)
                    .build());
        }
    }

}
