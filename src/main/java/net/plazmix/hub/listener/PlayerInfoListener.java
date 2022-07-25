package net.plazmix.hub.listener;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.plazmix.core.PlazmixCoreApi;
import net.plazmix.holographic.impl.SimpleHolographic;
import net.plazmix.protocollib.entity.impl.FakePlayer;
import net.plazmix.utility.NumberUtil;
import net.plazmix.utility.cooldown.PlayerCooldownUtil;
import net.plazmix.utility.player.PlazmixUser;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class PlayerInfoListener implements Listener {

    private final Map<Player, PlayerInfoData> playersInfoCache
            = new HashMap<>();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // Reshow online players.
        for (Player hiddenPlayer : player.spigot().getHiddenPlayers()) {

            player.showPlayer(hiddenPlayer);
            hiddenPlayer.showPlayer(player);
        }
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractAtEntityEvent event) {
        Player receiver = event.getPlayer();
        Entity rightClicked = event.getRightClicked();

        if (rightClicked instanceof Player) {
            Player target = ((Player) rightClicked);

            // Check cooldowns.
            if (PlayerCooldownUtil.hasCooldown("player_info_check", receiver)) {
                return;
            }

            PlayerCooldownUtil.putCooldown("player_info_check", receiver, 1000);

            // Create player info.
            if (PlazmixCoreApi.GROUP_API.isDefault(receiver.getName())) {
                receiver.sendMessage("§cОшибка, просматривать информацию об игроке можно от статуса §eStar §cи выше");
                return;
            }

            if (receiver.isFlying() || target.isFlying()) {
                receiver.sendMessage("§cОшибка, нельзя смотреть информацию об игроке, пока один из Вас в режиме полета!");
                return;
            }

            removePlayerInfo(receiver);
            createPlayerInfo(receiver, target);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        PlayerInfoData playerInfoData = playersInfoCache.get(player);

        if (playerInfoData == null) {
            return;
        }

        if (player.getWorld().equals(playerInfoData.fakePlayer.getWorld()) && player.getLocation().distance(playerInfoData.fakePlayer.getLocation()) >= 10) {
            playerInfoData.cancel();
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        removePlayerInfo(event.getPlayer());
    }

    private void createPlayerInfo(@NonNull Player receiver,
                                  @NonNull Player player) {

        PlayerInfoData playerInfoData = new PlayerInfoData(receiver, player);
        playerInfoData.create();

        playersInfoCache.put(receiver, playerInfoData);
    }

    private void removePlayerInfo(@NonNull Player receiver) {
        PlayerInfoData playerInfoData = playersInfoCache.remove(receiver);

        if (playerInfoData != null) {
            playerInfoData.cancel();
        }
    }


    @RequiredArgsConstructor
    @Getter
    private class PlayerInfoData {

        private final Player receiverPlayer;
        private final Player targetPlayer;


        private FakePlayer fakePlayer;
        private final Collection<SimpleHolographic> holographicCollection = new ArrayList<>();


        private void addHolographic(@NonNull Location location,
                                    String... holoLines) {

            SimpleHolographic simpleHolographic = new SimpleHolographic(location);

            for (String holographicLine : holoLines) {
                simpleHolographic.addOriginalHolographicLine(holographicLine);
            }

            holographicCollection.add(simpleHolographic);
            simpleHolographic.addReceivers(receiverPlayer);
        }

        public void create() {
            receiverPlayer.spawnParticle(Particle.VILLAGER_HAPPY, targetPlayer.getLocation().clone().add(0, 2, 0), 2, 0.05F, 0.05F, 0.05F, 0.05F);

            receiverPlayer.playSound(receiverPlayer.getLocation(), Sound.ENTITY_CAT_AMBIENT, 1, 1);
            receiverPlayer.playSound(receiverPlayer.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 2);

            receiverPlayer.hidePlayer(targetPlayer);

            // Create NPC
            fakePlayer = new FakePlayer(targetPlayer.getName(), targetPlayer.getLocation());
            fakePlayer.setGlowingColor(ChatColor.getByChar(PlazmixCoreApi.GROUP_API.getGroupPrefix(targetPlayer.getName()).charAt(1)));

            fakePlayer.setClickAction(player -> cancel());
            fakePlayer.setAttackAction(player -> cancel());

            fakePlayer.addReceivers(receiverPlayer);
            fakePlayer.look(targetPlayer.getLocation().getYaw(), targetPlayer.getLocation().getPitch());

            // Create holographics
            // Create player information holographic
            PlazmixUser tynixPlayer = PlazmixUser.of(targetPlayer);

            addHolographic(fakePlayer.getLocation().clone().add(0, 2.25, 0),
                    "§7Статус: " + PlazmixCoreApi.GROUP_API.getGroupColouredName(tynixPlayer.getName()),
                    "§7Серверный язык: §cN/A",
                    "§r",
                    "§7Монет: §e" + NumberUtil.spaced(tynixPlayer.getCoins()),
                    "§7Плазмы: §6" + NumberUtil.spaced(tynixPlayer.getGolds()),
                    "§r",
                    "§7Игровой уровень: §d" + tynixPlayer.getLevel(),
                    "§7До §e" + (tynixPlayer.getLevel() + 1) + " §7уровня осталось §b" + NumberUtil.spaced(tynixPlayer.getMaxExperience() - tynixPlayer.getExperience()) + " EXP §f(" + NumberUtil.getIntPercent(tynixPlayer.getExperience(), tynixPlayer.getMaxExperience()) + "%)",
                    "§r",
                    "§e§oНажмите, чтобы скрыть информацию!");
        }

        public void cancel() {
            receiverPlayer.spawnParticle(Particle.VILLAGER_HAPPY, targetPlayer.getLocation().clone().add(0, 2, 0), 10, 0.05F, 0.05F, 0.05F, 0.05F);
            receiverPlayer.playSound(receiverPlayer.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1, 1);

            if (targetPlayer.isOnline()) {
                receiverPlayer.showPlayer(targetPlayer);
            }

            // Hide packet objects.
            fakePlayer.remove();

            for (SimpleHolographic simpleHolographic : holographicCollection) {
                simpleHolographic.remove();
            }

            // Remove from cache.
            playersInfoCache.remove(receiverPlayer);
        }

    }

}
