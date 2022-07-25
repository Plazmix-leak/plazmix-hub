package net.plazmix.hub.parkour;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class ParkourBlockGenerator {

    private static final ThreadLocalRandom RANDOMIZE = ThreadLocalRandom.current();

    private static final int MAX_POINTS_DISTANCE_X = 5;
    private static final int MAX_POINTS_DISTANCE_Z = 4;
    private static final int MAX_POINTS_DISTANCE_Y = 1;

    private final ParkourController controller;

    private final Map<String, Location> playerForwardJumpsMap   = new HashMap<>();
    private final Map<String, Location> playerPreviousJumpsMap  = new HashMap<>();

    public void remove(Player gamer) {
        String lowername = gamer.getName().toLowerCase();

        Location forward = playerForwardJumpsMap.remove(lowername);
        Location previous = playerPreviousJumpsMap.remove(lowername);

        if (forward != null) {
            gamer.sendBlockChange(forward, Material.AIR, (byte) 0);
        }

        if (previous != null) {
            gamer.sendBlockChange(previous, Material.AIR, (byte) 0);
        }
    }

    private void update(Player gamer, Location value) {
        String lowername = gamer.getName().toLowerCase();

        Location previous = playerForwardJumpsMap.put(lowername, value);
        playerPreviousJumpsMap.put(lowername, (previous != null ? previous : controller.getStartPoint()));
    }

    private Location generateBlockLocation(Player gamer) {
        int randomX = RANDOMIZE.nextInt(-MAX_POINTS_DISTANCE_X, MAX_POINTS_DISTANCE_X);
        int randomY = RANDOMIZE.nextInt(-MAX_POINTS_DISTANCE_Y, MAX_POINTS_DISTANCE_Y);
        int randomZ = RANDOMIZE.nextInt(-MAX_POINTS_DISTANCE_Z, MAX_POINTS_DISTANCE_Z);

        Location gamerLocation = gamer.getLocation();
        Location newPoint = gamerLocation.clone().subtract(0, 1, 0).add(randomX, randomY, randomZ);

        if (controller.getPoints(gamer) > 0) {

            // Check Y distance.
            if (Math.abs(newPoint.getY() - gamerLocation.getY()) > 2) {
                return this.generateBlockLocation(gamer);
            }

            // Check XZ distance.
            boolean xzEquals = (newPoint.getX() == gamerLocation.getX() && newPoint.getZ() == gamerLocation.getZ());
            double distance = newPoint.distance(gamerLocation);

            if ((distance > 5 || distance < 2) || xzEquals) {
                return this.generateBlockLocation(gamer);
            }
        }

        return newPoint;
    }

    public Location getForwardedPoint(Player gamer) {
        return playerForwardJumpsMap.get(gamer.getName().toLowerCase());
    }

    public void newForwardedPoint(Player gamer) {

        Location previousPoint = playerPreviousJumpsMap.get(gamer.getName().toLowerCase());
        if (previousPoint == null) {
            previousPoint = controller.getStartPoint();
        }

        // Update new player forward point to jump.
        Location forwardLocation = this.generateBlockLocation(gamer);
        this.update(gamer, forwardLocation);

        // Update previous block for that player.
        if (controller.getPoints(gamer) > 0) {
            gamer.sendBlockChange(previousPoint, Material.AIR, (byte) 0);
        }
        // Update start point block for that player.
        else {
            gamer.sendBlockChange(previousPoint, Material.WOOL, (byte) RANDOMIZE.nextInt(0, 16));
        }

        // Place a block for that player.
        gamer.sendBlockChange(forwardLocation, Material.WOOL, (byte) RANDOMIZE.nextInt(0, 16));

        gamer.playSound(forwardLocation, Sound.BLOCK_NOTE_PLING, 1, 2);
        gamer.spawnParticle(Particle.CLOUD, forwardLocation.clone().add(0.5, 0.5, 0.5), 10, 0.1f, 0.1f, 0.1f, 0.1f);
    }

}
