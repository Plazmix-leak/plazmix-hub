package net.plazmix.hub.npc;

import lombok.NonNull;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import net.plazmix.hub.PlazmixHubPlugin;
import net.plazmix.lobby.npc.ServerPlayerNPC;
import net.plazmix.protocollib.entity.impl.FakePlayer;
import net.plazmix.utility.cooldown.PlayerCooldownUtil;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class FunPlayerNPC extends ServerPlayerNPC {

    public FunPlayerNPC(String playerSkin, Location location) {
        super(playerSkin, location);
    }

    @Override
    protected void onReceive(@NonNull FakePlayer fakePlayer) {
        addVillagerAction(fakePlayer);

        enableAutoLooking();
    }

    private void addVillagerAction(FakePlayer fakePlayer) {
        addHolographicLine("§cРежим ****");
        addHolographicLine("");
        addHolographicLine("§dЭтот режим скоро появится в свет!");

        Consumer<Player> playerConsumer = player -> {

            // Check cooldown
            if (PlayerCooldownUtil.hasCooldown("npc_spawn", player)) {

                player.sendMessage("§cПотайной NPC боится, и пока что не хочет выходить,");
                player.sendMessage("§cПопробуйте разбудить его немного поздее!");
                return;
            }

            PlayerCooldownUtil.putCooldown("npc_spawn", player, TimeUnit.SECONDS.toMillis(25));

            // Temp remove NPC.
            fakePlayer.removeReceivers(player);

            // Create temp villager NPC.
            player.sendMessage(ChatColor.GREEN + "Вы разбудили потайного NPC!");

            SecretVillagerNPC secretVillagerNPC = new SecretVillagerNPC(fakePlayer);
            secretVillagerNPC.addReceivers(player);


            // Play spawn sounds & effects
            player.spigot().playEffect(fakePlayer.getLocation().clone(), Effect.CLOUD, 0, 0, 0.1F, 0.1F, 0.1F, 0.25F, 5, 50);
            player.spigot().playEffect(fakePlayer.getLocation().clone().add(0, 1, 0), Effect.CLOUD, 0, 0, 0.1F, 0.25F, 0.1F, 0.5F, 5, 50);

            player.playSound(fakePlayer.getLocation(), Sound.ENTITY_VILLAGER_YES, 1, 1);
            player.playSound(fakePlayer.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);

            new BukkitRunnable() {
                @Override
                public void run() {
                    secretVillagerNPC.getHandle().remove();
                    fakePlayer.addReceivers(player);

                    // Play hide sounds & effects
                    player.spigot().playEffect(fakePlayer.getLocation().clone(), Effect.LARGE_SMOKE, 0, 0, 0.1F, 0.1F, 0.25F, 0.5F, 5, 50);
                    player.spigot().playEffect(fakePlayer.getLocation().clone().add(0, 1, 0), Effect.LARGE_SMOKE, 0, 0, 0.25F, 0.1F, 0.1F, 0.5F, 5, 50);

                    player.playSound(fakePlayer.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                    player.playSound(fakePlayer.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1, 1);

                    player.sendMessage(ChatColor.YELLOW + "Потайной NPC вновь уснул на 25 секунд, вот же соня!");
                }

            }.runTaskLater(PlazmixHubPlugin.getPlugin(PlazmixHubPlugin.class), 20 * 2);
        };

        fakePlayer.setClickAction(playerConsumer);
        fakePlayer.setAttackAction(playerConsumer);
    }

}
