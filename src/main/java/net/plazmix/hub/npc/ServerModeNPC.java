package net.plazmix.hub.npc;

import com.comphenix.protocol.wrappers.EnumWrappers;
import lombok.NonNull;
import net.plazmix.core.PlazmixCoreApi;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import net.plazmix.coreconnector.CoreConnector;
import net.plazmix.coreconnector.utility.server.ServerMode;
import net.plazmix.coreconnector.utility.server.ServerSubMode;
import net.plazmix.coreconnector.utility.server.ServerSubModeType;
import net.plazmix.holographic.ProtocolHolographic;
import net.plazmix.holographic.updater.SimpleHolographicUpdater;
import net.plazmix.lobby.npc.ServerPlayerNPC;
import net.plazmix.hub.utility.GameServerMode;
import net.plazmix.protocollib.entity.impl.FakePlayer;
import net.plazmix.utility.NumberUtil;
import net.plazmix.utility.PlayerUtil;

public class ServerModeNPC extends ServerPlayerNPC {

    private final GameServerMode gameServerMode;

    public ServerModeNPC(GameServerMode gameServerMode, Location location) {
        super(gameServerMode.getMojangSkin(), location);

        this.gameServerMode = gameServerMode;
    }

    @Override
    protected void onReceive(@NonNull FakePlayer fakePlayer) {
        String gameServerModeName = gameServerMode.getChatColor() + ChatColor.BOLD.toString() + gameServerMode.name().replace("_", " ");

        addLocalizedLine("HOLOGRAM_HUB_GAME_SELECT_NPC_NO_SERVERS_LINE_1");
        addLocalizedLine("");
        addLocalizedLine("HOLOGRAM_HUB_GAME_SELECT_NPC_NO_SERVERS_LINE_3");
        addLocalizedLine("HOLOGRAM_HUB_GAME_SELECT_NPC_NO_SERVERS_LINE_4");

        holographic.setHolographicUpdater(20, new SimpleHolographicUpdater(holographic) {

            @Override
            public void accept(ProtocolHolographic protocolHolographic) {
                holographic.setLangHolographicLine(0, localizationPlayer -> localizationPlayer.getMessage("HOLOGRAM_HUB_GAME_SELECT_NPC_NO_SERVERS_LINE_1")
                        .replace("%game_name%", gameServerModeName)
                        .toText());

                if (gameServerMode.getConnectedServers() > 0) {
                    holographic.setLangHolographicLine(2, localizationPlayer -> localizationPlayer.getMessageText("HOLOGRAM_HUB_GAME_SELECT_NPC_AVAILABLE_LINE_3"));

                } else {

                    holographic.setLangHolographicLine(2, localizationPlayer -> localizationPlayer.getMessageText("HOLOGRAM_HUB_GAME_SELECT_NPC_NO_SERVERS_LINE_3"));
                }

                holographic.setLangHolographicLine(3, localizationPlayer -> localizationPlayer.getMessage("HOLOGRAM_HUB_GAME_SELECT_NPC_NO_SERVERS_LINE_4")
                        .replace("%players%", NumberUtil.spaced(gameServerMode.getModeOnline()))
                        .toText());
            }
        });

       // fakePlayer.setGlowingColor(gameServerMode.getChatColor());

       fakePlayer.getEntityEquipment().setEquipment(EnumWrappers.ItemSlot.MAINHAND, gameServerMode.getItemInMainHand());

        addTeleportAction(fakePlayer);
        enableAutoLooking(10);
    }

    private void addTeleportAction(FakePlayer fakePlayer) {
        fakePlayer.setClickAction(player -> {

            if (gameServerMode.getConnectedServers() <= 0) {
                player.sendMessage("§d§lPlazmix §8:: §cНа данный момент нет свободных серверов " + gameServerMode.getChatColor() + gameServerMode.name() + " §cнедоступны!");
                return;
            }

            // Detect Lobby server.
            ServerSubMode lobbyServer = gameServerMode.getServerMode()
                    .getSubModes(ServerSubModeType.GAME_LOBBY)
                    .stream()
                    .findAny()
                    .orElse(null);

            if (lobbyServer != null) {
                String lobbyServerName = CoreConnector.getNetworkInstance().getBestServer(false, lobbyServer);

                if (lobbyServerName == null) {
                    player.sendMessage("§d§lPlazmix §8:: §cОшибка, fallback сервер для режима " + gameServerMode.name() + " не найден");
                    return;
                }

                PlayerUtil.redirect(player, lobbyServerName);
                return;
            }

            // Detect available servers.
            String serverName = PlazmixCoreApi.getConnectedServers(gameServerMode.getServerMode().getServersPrefix())
                    .stream()
                    .findAny()
                    .orElse(null);

            if (serverName == null) {
                player.sendMessage("§d§lPlazmix §8:: §cОшибка, нет свободных серверов для режима " + gameServerMode.getChatColor() + gameServerMode.name());
                return;
            }

            PlayerUtil.redirect(player, serverName);
        });
    }

}
