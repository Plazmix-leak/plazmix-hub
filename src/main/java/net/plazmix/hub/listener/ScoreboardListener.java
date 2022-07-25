package net.plazmix.hub.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import net.plazmix.hub.scoreboard.HubScoreboard;

public class ScoreboardListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        new HubScoreboard(event.getPlayer());
    }

}
