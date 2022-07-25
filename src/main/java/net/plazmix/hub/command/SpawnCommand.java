package net.plazmix.hub.command;

import net.plazmix.hub.PlazmixHubPlugin;
import net.plazmix.utility.location.LocationUtil;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import net.plazmix.command.BaseCommand;

public class SpawnCommand extends BaseCommand<Player> {

    private final PlazmixHubPlugin plugin;

    public SpawnCommand(PlazmixHubPlugin plugin) {
        super("spawn", "ызфцт", "спаун", "спавн");
        this.plugin = plugin;
    }

    @Override
    protected void onExecute(Player player, String[] args) {
        player.teleport(LocationUtil.stringToLocation(plugin.getConfig().getString("locations.spawn")));
    }
}
