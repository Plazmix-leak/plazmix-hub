package net.plazmix.hub.parkour;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.plazmix.coreconnector.module.type.NetworkModule;
import net.plazmix.hub.PlazmixHubPlugin;
import net.plazmix.utility.location.LocationUtil;
import net.plazmix.utility.player.PlazmixUser;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ParkourController {

    public static final ParkourController INSTANCE = new ParkourController();

    @Getter
    private Location startPoint;

    private TIntList gamersList;
    private TIntIntMap pointsMap;

    @Getter
    private ParkourBlockGenerator blockGenerator;

    @Getter
    private ParkourDatabaseManager database;

    public void init(Plugin plugin) {
        this.startPoint = LocationUtil.stringToLocation(plugin.getConfig().getString("locations.parkour_block"))
                .clone()
                .subtract(0, 1, 0);

        this.gamersList = new TIntArrayList();
        this.pointsMap = new TIntIntHashMap();

        this.blockGenerator = new ParkourBlockGenerator(this);
        this.database = new ParkourDatabaseManager(this);

        plugin.getServer().getPluginManager().registerEvents(new ParkourListener(this), plugin);
    }

    @SuppressWarnings("deprecated")
    private void postGamerAdd(int playerID, Player gamer) {
        pointsMap.put(playerID, 0);

        for (Player serverPlayer : Bukkit.getOnlinePlayers()) {
            serverPlayer.hidePlayer(gamer);

            // Скрываем текущих игроков паркура.
            if (isGamer(serverPlayer)) {
                gamer.hidePlayer(serverPlayer);
            }
            else {
                gamer.showPlayer(serverPlayer);
            }
        }
    }

    @SuppressWarnings("deprecated")
    private void postGamerRemove(int playerID, Player gamer) {
        if (database != null) {
            database.store(gamer.getName());
        }

        pointsMap.remove(playerID);

        for (Player serverPlayer : Bukkit.getOnlinePlayers()) {

            if (this.isGamer(serverPlayer)) {
                serverPlayer.showPlayer(gamer);

                continue;
            }

            gamer.showPlayer(serverPlayer);
            serverPlayer.showPlayer(gamer);
        }
    }

    public boolean isGamer(Player gamer) {
        int playerID = NetworkModule.getInstance().getPlayerId(gamer.getName());

        return gamersList.contains(playerID);
    }

    public void addGamer(Player gamer) {
        int playerID = NetworkModule.getInstance().getPlayerId(gamer.getName());

        this.postGamerAdd(playerID, gamer);
        gamersList.add(playerID);
    }

    public void removeGamer(Player gamer) {
        int playerID = NetworkModule.getInstance().getPlayerId(gamer.getName());

        this.postGamerRemove(playerID, gamer);
        gamersList.remove(playerID);
    }

    public int getPoints(Player gamer) {
        int playerID = NetworkModule.getInstance().getPlayerId(gamer.getName());
        return pointsMap.get(playerID);
    }

    public void addPoint(Player gamer, int value) {
        int playerID = NetworkModule.getInstance().getPlayerId(gamer.getName());
        int previousPoints = pointsMap.get(playerID);

        pointsMap.put(playerID, previousPoints + value);

        PlazmixUser.of(gamer).addCoins(1);
    }

    public void startListeningFor(Player gamer) {
        if (startPoint == null) {
            throw new IllegalArgumentException("parkour location is`nt initialized");
        }

        this.addGamer(gamer);

        gamer.teleport(startPoint.clone().add(0, 3, 0));

        gamer.setAllowFlight(true);
        gamer.setFlying(false);

        gamer.setWalkSpeed(0.2f);

        gamer.playSound(gamer.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
        gamer.sendTitle("§6§lПАРКУР НАЧАЛСЯ", "§fПрыгайте по блокам, которые стоят перед Вами", 0, 70, 20);

        if (blockGenerator != null) {
            blockGenerator.newForwardedPoint(gamer);
        }
    }

    public void cancelListeningFor(Player gamer, ParkourCancelReason cancelReason) {
        this.removeGamer(gamer);

        gamer.setWalkSpeed(0.45f);

        Bukkit.dispatchCommand(gamer, "spawn");

        gamer.playSound(gamer.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
        gamer.sendTitle("§c§lПАРКУР ЗАВЕРШЕН", cancelReason.getReason(), 0, 70, 20);

        if (blockGenerator != null) {
            blockGenerator.remove(gamer);
        }
    }

}
