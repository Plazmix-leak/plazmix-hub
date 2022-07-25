package net.plazmix.hub;

import lombok.Getter;
import net.plazmix.PlazmixApi;
import net.plazmix.hub.command.SpawnCommand;
import net.plazmix.hub.listener.*;
import net.plazmix.hub.parkour.ParkourController;
import net.plazmix.hub.utility.GameServerMode;
import net.plazmix.hub.npc.FunPlayerNPC;
import net.plazmix.hub.npc.ParkourNPC;
import net.plazmix.hub.npc.RewardsNPC;
import net.plazmix.lobby.npc.manager.ServerNPCManager;
import net.plazmix.lobby.playertop.PlayerTopsStorage;
import net.plazmix.lobby.playertop.database.type.PlayerTopsMysqlConvertibleDatabase;
import net.plazmix.lobby.playertop.database.type.PlayerTopsMysqlDatabase;
import net.plazmix.lobby.playertop.pagination.PlayerTopsPaginationChanger;
import net.plazmix.utility.RotatingHead;
import net.plazmix.utility.leveling.LevelingUtil;
import net.plazmix.utility.location.LocationUtil;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Sheep;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/*  Leaked by https://t.me/leak_mine
    - Все слитые материалы вы используете на свой страх и риск.

    - Мы настоятельно рекомендуем проверять код плагинов на хаки!
    - Список софта для декопиляции плагинов:
    1. Luyten (последнюю версию можно скачать можно тут https://github.com/deathmarine/Luyten/releases);
    2. Bytecode-Viewer (последнюю версию можно скачать можно тут https://github.com/Konloch/bytecode-viewer/releases);
    3. Онлайн декомпиляторы https://jdec.app или http://www.javadecompilers.com/

    - Предложить свой слив вы можете по ссылке @leakmine_send_bot или https://t.me/leakmine_send_bot
*/

public final class PlazmixHubPlugin
        extends JavaPlugin {

    @Getter
    private RewardsNPC rewardsNPC;

    @Override
    public void onEnable() {
        // CoreConnector.getInstance().getMysqlConnection().createTable("HubParkour", "`Id` INT NOT NULL, `MaxPoints` INT NOT NULL");

        saveDefaultConfig();

        addNpcs();
        addTops();

        createSheepSpawner();

        registerRotatingHeads();
        registerCommands();
        registerListeners();

        enableWorldTicker();

        ParkourController.INSTANCE.init(this);
        // ParkourNPC.MiniParkourSystem.registerParkourEvents(this);
    }

    private void registerRotatingHeads() {
        new RotatingHead(this, LocationUtil.stringToLocation(getConfig().getString("locations.heads.vk")),
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmJkNzNkNjE2ZDIxYzE5MzAxZjJmMDc2Y2JjNTQ3YzdjMWI1MWJkNWUxYTQ1ZDdjNTlkNWFkYjgyODA4ZSJ9fX0=")

                .addTextLine("§b§lМы в ВКонтакте")
                .addTextLine("§fhttps://plzm.xyz/vk")

                .setRotateDirection(RotatingHead.RotateDirection.TO_LEFT)

                .setClickAction(player -> {

                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 2);
                    player.sendMessage("§b§lVKontakte §8:: §fПрисоединяйся: §ehttps://plzm.xyz/vk");
                })

                .register();

        new RotatingHead(this, LocationUtil.stringToLocation(getConfig().getString("locations.heads.ds")),
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTNiMTgzYjE0OGI5YjRlMmIxNTgzMzRhZmYzYjViYjZjMmMyZGJiYzRkNjdmNzZhN2JlODU2Njg3YTJiNjIzIn19fQ==")

                .addTextLine("§9§lНаш дискорд сервер")
                .addTextLine("§fhttps://plzm.xyz/discord")

                .setRotateDirection(RotatingHead.RotateDirection.TO_RIGHT)
                
                .setClickAction(player -> {
                    
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 2);
                    player.sendMessage("§9§lDiscord §8:: §fПрисоединяйся: §ehttps://plzm.xyz/discord");
                })

                .register();
    }

    private void registerCommands() {
        PlazmixApi.registerCommand(new SpawnCommand(this));
    }

    private void addNpcs() {
        ServerNPCManager.INSTANCE.register(rewardsNPC = new RewardsNPC(LocationUtil.stringToLocation(getConfig().getString("locations.npc.rewards"))));
        ServerNPCManager.INSTANCE.register(new ParkourNPC(LocationUtil.stringToLocation(getConfig().getString("locations.npc.parkour"))));

        // Selecting server mode.
        for (GameServerMode gameServerMode : GameServerMode.values()) {
            String stringLocation = gameServerMode.getNpcLocation().apply(getConfig());

            if (stringLocation != null) {
                gameServerMode.addNpc(LocationUtil.stringToLocation(stringLocation));
            }
        }

        // Add npcs.
        ServerNPCManager.INSTANCE.register(new FunPlayerNPC("texxst", LocationUtil.stringToLocation(getConfig().getString("locations.npc.wait1"))));
        ServerNPCManager.INSTANCE.register(new FunPlayerNPC("texxst", LocationUtil.stringToLocation(getConfig().getString("locations.npc.wait2"))));
        ServerNPCManager.INSTANCE.register(new FunPlayerNPC("texxst", LocationUtil.stringToLocation(getConfig().getString("locations.npc.wait3"))));
    }

    private void createSheepSpawner() {

        ThreadLocalRandom random = ThreadLocalRandom.current();
        List<Location> locationList = getConfig().getStringList("locations.sheeps")
                .stream()
                .map(LocationUtil::stringToLocation)
                .collect(Collectors.toList());

        Location selectLocation = locationList.stream()
                .findFirst()
                .orElse(null);

        if (selectLocation == null) {
            return;
        }

        World world = selectLocation.getWorld();
        world.getLivingEntities().stream()
                .filter(entity -> entity.getType() == EntityType.SHEEP)
                .forEach(Entity::remove);

        new BukkitRunnable() {
            private final int entityLimit = 26;
            private int entityCurrentAmount;

            @Override
            public void run() {
                if (entityCurrentAmount >= entityLimit) {
                    this.cancel();
                    return;
                }

                Sheep sheep = world.spawn(locationList.get(random.nextInt(0, locationList.size())), Sheep.class);
                sheep.setColor(random.nextBoolean() ? DyeColor.PINK : DyeColor.LIGHT_BLUE);

                entityCurrentAmount++;
            }

        }.runTaskTimer(this, 0, 20 * 60 * 5); // каждые 5 минут
    }

    private void enableWorldTicker() {

        for (World world : getServer().getWorlds()) {
            world.setPVP(false);
            world.setDifficulty(Difficulty.NORMAL);

            new BukkitRunnable() {

                @Override
                public void run() {

                    world.setTime(18000);

                    // Set world settings.
                    world.setThundering(false);
                    world.setStorm(false);

                    world.setGameRuleValue("randomTickSpeed", "0");

                    world.setWeatherDuration(0);

                }

            }.runTaskTimer(this, 0, 1);
        }
    }

    private void addTops() {
        Location location = LocationUtil.stringToLocation(getConfig().getString("locations.tops"));
        PlayerTopsPaginationChanger paginationChanger = PlayerTopsPaginationChanger.create();

        // Plazma
        paginationChanger.addPlayerTops(PlayerTopsStorage.newBuilder()
                .setDatabaseManager(new PlayerTopsMysqlDatabase("PlazmaEconomyService", "Value"))

                .setLocation(location)
                .setSkullParticle(Particle.TOTEM)

                .setLimit(10)
                .setUpdater(60)

                .setSkullTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTQ1ZjQ3ZmViNGQ3NWNiMzMzOTE0YmZkYjk5OWE0ODljOWQwZTMyMGQ1NDhmMzEwNDE5YWQ3MzhkMWUyNGI5In19fQ==")

                .setStatsName("Плазма")
                .setDescription("Топ 10 игроков, набравшие наибольшее",
                        "количество платной валюты")

                .setValueSuffix("плазмы"));

        // Leveling
        paginationChanger.addPlayerTops(PlayerTopsStorage.newBuilder()
                .setDatabaseManager(new PlayerTopsMysqlConvertibleDatabase("PlayerLeveling", "Experience", (user, value) -> LevelingUtil.getLevel(value)))

                .setLocation(location)
                .setSkullParticle(Particle.SPIT)

                .setSkullTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTMxYTRmYWIyZjg3ZGI1NDMzMDEzNjUxN2I0NTNhYWNiOWQ3YzBmZTc4NDMwMDcwOWU5YjEwOWNiYzUxNGYwMCJ9fX0=")

                .setLimit(10)
                .setUpdater(60)

                .setStatsName("Уровень")
                .setDescription("Топ 10 игроков, набравшие наибольшее",
                        "количество игрового уровня")

                .setValueSuffix("уровень"));

        // Spawn player-tops
        paginationChanger.spawn();
    }

    private void registerListeners() {

        // Default player actions.
        getServer().getPluginManager().registerEvents(new RewardsListener(this), this);

        // getServer().getPluginManager().registerEvents(new PlayerInfoListener(), this);
        getServer().getPluginManager().registerEvents(new ScoreboardListener(), this);
        getServer().getPluginManager().registerEvents(new SpawnListener(), this);

        // Protocol lib.
        getServer().getPluginManager().registerEvents(new LobbySheepsListener(), this);
    }

}
