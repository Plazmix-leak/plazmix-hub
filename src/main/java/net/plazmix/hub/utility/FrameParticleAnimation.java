package net.plazmix.hub.utility;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.plazmix.utility.location.region.CuboidRegion;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.BlockFace;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

@RequiredArgsConstructor
public class FrameParticleAnimation {

    private final Particle particle;

    private final Location position1;
    private final Location position2;


    public void playAnimation(@NonNull Plugin plugin) {
        CuboidRegion cuboidRegion = new CuboidRegion(position1, position2);
        new BukkitRunnable() {

            @Override
            public void run() {
                cuboidRegion.getFace(BlockFace.DOWN).forEachBlock(block -> {

                    Location location = block.getLocation().clone()
                            .add(0.5, 0.5, 0.5)
                            .clone().subtract(0, 0, 1);

                    block.getWorld().spawnParticle(particle, location, 1, 0.03F, 0.03F, 0.03F, 0.03F);
                });
            }

        }.runTaskTimer(plugin, 0, 2);
    }

}
