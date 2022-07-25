package net.plazmix.hub.parkour;

import gnu.trove.map.TIntIntMap;
import net.plazmix.inventory.impl.BaseSimpleInventory;
import net.plazmix.utility.ItemUtil;
import net.plazmix.utility.NumberUtil;
import net.plazmix.utility.player.PlazmixUser;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ParkourMenu extends BaseSimpleInventory {

    public ParkourMenu() {
        super("Мини-Паркур", 3);
    }

    @Override
    public void drawInventory(Player player) {
        ParkourDatabaseManager parkourDatabase = ParkourController.INSTANCE.getDatabase();

        setClickItem(12, ItemUtil.newBuilder(Material.SKULL_ITEM)
                        .setDurability(SkullType.CREEPER.ordinal())
                        .setName("§a§oНачать паркур")

                        .addLore("")
                        .addLore("§7За каждый прыжок на правильный")
                        .addLore("§7блок Вам будет начисляться")
                        .addLore("§e1 монета §7в качестве")
                        .addLore("§7вознаграждения за прохождение")
                        .addLore("")
                        .addLore("§fМаксимум Вы получали:")
                        .addLore(" §b" + NumberUtil.formattingSpaced(parkourDatabase.load(player.getName()), "очко", "очка", "очков"))
                        .addLore("")
                        .addLore("§a▸ Нажмите, чтобы начать прохождение!")

                        .setGlowing(true)
                        .build(),

                (player1, inventoryClickEvent) -> {

                    player1.closeInventory();
                    player1.sendTitle("", "§cЗагрузка локаций для паркура...", 0, 100, 20);

                    ParkourController.INSTANCE.startListeningFor(player);
                });

        TIntIntMap parkourPointsTopMap = parkourDatabase.loadPlayerPoints(10);
        List<Integer> playerIds = Arrays.stream(parkourPointsTopMap.keySet().toArray()).boxed().collect(Collectors.toList());

        setOriginalItem(14, ItemUtil.newBuilder(Material.BREWING_STAND_ITEM)

                .setName("§b§lТОП ПО МИНИ-ПАРКУРУ")
                .addLore("")
                .addLore("§fЗдесь собран топ §a10 лучших §fигроков,")
                .addLore("§fнабравших наибольшее количество очков в паркуре:")
                .addLore("")

                .ifPresent(parkourPointsTopMap.isEmpty(), itemBuilder -> {

                    itemBuilder.addLore(" §7На данный момент этот список является пустым,");
                    itemBuilder.addLore(" §7но Вы можете пополнить его попыткой прохождения");
                    itemBuilder.addLore(" §7этого паркура!");
                })

                .ifPresent(!parkourPointsTopMap.isEmpty(), itemBuilder -> {
                    int selfPlace = Arrays.stream(parkourDatabase.getDatabasePointsCache().keySet().toArray())
                            .boxed()
                            .sorted(Comparator.comparingInt(playerId -> parkourDatabase.getDatabasePointsCache().get((int)playerId)).reversed())
                            .collect(Collectors.toList())
                            .indexOf(PlazmixUser.of(player).getPlayerId());

                    itemBuilder.addLoreArray(Arrays.stream(parkourPointsTopMap.keys())
                            .boxed()
                            .sorted(Comparator.comparingInt(playerId -> parkourDatabase.getDatabasePointsCache().get((int)playerId)).reversed())
                            .map(playerId -> " §f" + (Arrays.stream(parkourDatabase.getDatabasePointsCache().keySet().toArray())
                                    .boxed()
                                    .sorted(Comparator.comparingInt(playerId1 -> parkourDatabase.getDatabasePointsCache().get((int)playerId1)).reversed())
                                    .collect(Collectors.toList()).indexOf(playerId) + 1) + ". " + PlazmixUser.of(playerId).getDisplayName() + " §7- §e" + NumberUtil.formattingSpaced(parkourPointsTopMap.get(playerId), "очко", "очка", "очков"))
                            .toArray(String[]::new));

                    itemBuilder.addLore(" §7(Список обновляется каждые §c10 минут§7)");
                    itemBuilder.addLore("");

                    if (selfPlace >= 0) {
                        itemBuilder.addLore(" §d▸ §eВаше место в топе: §b" + (selfPlace + 1) + " §7(" + NumberUtil.formattingSpaced(parkourDatabase.load(player.getName()), "очко", "очка", "очков") + ")");
                    }
                })

                .build());

        setClickItem(16, ItemUtil.newBuilder(Material.BARRIER)
                        .setName("§cЗакрыть меню")
                        .addLore("§7▸ Нажмите здесь, чтобы закрыть меню")
                        .build(),

                (player1, inventoryClickEvent) -> player1.closeInventory());
    }

}
