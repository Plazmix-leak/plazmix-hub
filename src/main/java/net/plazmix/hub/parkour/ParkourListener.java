package net.plazmix.hub.parkour;

import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.plazmix.utility.NumberUtil;
import net.plazmix.utility.player.PlazmixUser;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;

@RequiredArgsConstructor
public final class ParkourListener implements Listener {
    private final ParkourController controller;

    private Location normalize(Location target, BlockFace direction) {
        return target.getBlock().getRelative(direction).getLocation();
    }

    private Location normalize(Location target) {
        return this.normalize(target, BlockFace.SELF);
    }

    @SuppressWarnings("deprecated")
    private void onSuccessJump(Player player) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§8[ §c§l" + NumberUtil.formattingSpaced(
                controller.getPoints(player), "§cочко", "§cочка", "§cочков") + " §8]"));

        controller.addPoint(player, 1);
        controller.getBlockGenerator().newForwardedPoint(player);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (controller.isGamer(player)) {
            Location forwardedPoint = controller.getBlockGenerator().getForwardedPoint(player);

            if (forwardedPoint.getBlockY() - event.getTo().getBlockY() > 5) {
                controller.cancelListeningFor(player, ParkourCancelReason.FALLING);
            }
            else if (this.normalize(event.getTo(), BlockFace.DOWN).equals(this.normalize(forwardedPoint))) {
                this.onSuccessJump(player);
            }
        }
    }

    @EventHandler
    public void onToggleFlight(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();

        if (controller.isGamer(player)) {
            controller.cancelListeningFor(player, ParkourCancelReason.TOGGLE_FLIGHT);
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (controller.isGamer(player)) {
            controller.cancelListeningFor(player, ParkourCancelReason.LEAVE_GAME);
        }
    }

}
