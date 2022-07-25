package net.plazmix.hub.npc;

import lombok.NonNull;
import org.bukkit.Sound;
import net.plazmix.lobby.npc.ServerNPC;
import net.plazmix.protocollib.entity.animation.FakeEntityAnimation;
import net.plazmix.protocollib.entity.impl.FakePlayer;
import net.plazmix.protocollib.entity.impl.FakeVillager;

public class SecretVillagerNPC extends ServerNPC<FakeVillager> {

    private final FakePlayer fakePlayer;

    public SecretVillagerNPC(FakePlayer fakePlayer) {
        super(fakePlayer.getLocation());
        this.fakePlayer = fakePlayer;

        setHandle(new FakeVillager(location));
    }

    @Override
    protected void onReceive(@NonNull FakeVillager fakeVillager) {
        fakeVillager.setClickAction(fakePlayer.getClickAction());
        fakeVillager.setAttackAction(player1 -> {

            player1.playSound(fakeVillager.getLocation(), Sound.ENTITY_VILLAGER_DEATH, 1, 1);
            fakeVillager.playAnimationAll(FakeEntityAnimation.TAKE_DAMAGE);
        });

        enableAutoLooking(10);
    }

}
