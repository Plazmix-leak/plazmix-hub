package net.plazmix.hub.parkour;

import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.plazmix.coreconnector.CoreConnector;
import net.plazmix.coreconnector.module.type.NetworkModule;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class ParkourDatabaseManager {

    private static final String LOAD_QUERY = "SELECT * FROM `HubParkour` WHERE `Id`=?";
    private static final String STORE_QUERY = "INSERT INTO `HubParkour` VALUES (?, ?)";

    @Getter
    private final TIntIntMap databasePointsCache = new TIntIntHashMap();

    private final ParkourController controller;

    public void store(String name) {
        Player bukkitGamer = Bukkit.getPlayer(name);
        if (bukkitGamer == null) {
            return;
        }

        int ingamePoints = controller.getPoints(bukkitGamer);
        int loadedPoints = this.load(name);

        if (ingamePoints > loadedPoints) {

            int playerID = NetworkModule.getInstance().getPlayerId(name);
            CoreConnector.getInstance().getMysqlConnection().execute(true, STORE_QUERY, playerID, ingamePoints);

            databasePointsCache.put(playerID, ingamePoints);
        }
    }

    public int load(String name) {
        int playerID = NetworkModule.getInstance().getPlayerId(name);

        int result;
        if (!databasePointsCache.containsKey(playerID)) {
            result = CoreConnector.getInstance().getMysqlConnection().executeQuery(false, LOAD_QUERY,
                    response -> !response.next() ? 0 : response.getInt("MaxPoints"), playerID);

            databasePointsCache.put(playerID, result);
        }
        else {
            result = databasePointsCache.get(playerID);
        }

        return result;
    }


    // -----------------------------------------------------------------------------------------------
    //
    // Весь нижний кусок кода я спиздил из старого
    // паркура, ибо в падлу писать с нуля...

    private final long maxTopUpdateTimeMillis = TimeUnit.MINUTES.toMillis(10);
    private long previousTopUpdateTimeMillis = System.currentTimeMillis();

    private TIntIntMap previousLoadedPlayerPointsMap;

    public TIntIntMap loadPlayerPoints(int limit) {
        TIntIntMap loadedPlayerPointsMap = previousLoadedPlayerPointsMap == null ? new TIntIntHashMap(limit) : previousLoadedPlayerPointsMap;

        if (previousLoadedPlayerPointsMap != null && System.currentTimeMillis() - previousTopUpdateTimeMillis < maxTopUpdateTimeMillis) {
            return loadedPlayerPointsMap;
        }

        previousTopUpdateTimeMillis = System.currentTimeMillis();

        CoreConnector.getInstance().getMysqlConnection().executeQuery(false, "SELECT * FROM `HubParkour` ORDER BY `MaxPoints` DESC LIMIT " + limit,
                resultSet -> {

                    while (resultSet.next()) {
                        loadedPlayerPointsMap.put(resultSet.getInt("Id"), resultSet.getInt("MaxPoints"));
                    }

                    return null;
                });

        databasePointsCache.putAll(loadedPlayerPointsMap);
        return previousLoadedPlayerPointsMap = loadedPlayerPointsMap;
    }

}
