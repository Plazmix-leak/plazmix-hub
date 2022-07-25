package net.plazmix.hub.npc;

import lombok.NonNull;
import net.plazmix.hub.parkour.ParkourMenu;
import net.plazmix.lobby.npc.ServerNPC;
import net.plazmix.protocollib.entity.impl.FakeCreeper;
import org.bukkit.Location;

public class ParkourNPC extends ServerNPC<FakeCreeper> {

    public ParkourNPC(@NonNull Location location) {
        super(location);

        setHandle(new FakeCreeper(location));
    }

    @Override
    protected void onReceive(@NonNull FakeCreeper fakeCreeper) {
        enableAutoLooking(7);

        fakeCreeper.setCharged(true);
        fakeCreeper.setClickAction(player -> new ParkourMenu().openInventory(player));

        addHolographicLine("§a§lПаркур в лобби");
        addHolographicLine("§f§oНажмите, чтобы открыть!");

        getHolographic().teleport(getHolographic().getLocation().clone().subtract(0, 0.5, 0));
    }

}
