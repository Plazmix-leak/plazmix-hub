package net.plazmix.hub.utility;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.plazmix.core.PlazmixCoreApi;
import net.plazmix.coreconnector.utility.server.ServerMode;
import net.plazmix.hub.npc.ServerModeNPC;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import net.plazmix.lobby.npc.manager.ServerNPCManager;
import net.plazmix.utility.ItemUtil;
import net.plazmix.utility.mojang.MojangSkin;

import java.util.UUID;
import java.util.function.Function;

@RequiredArgsConstructor
@Getter
public enum GameServerMode {

    GunGame(ChatColor.LIGHT_PURPLE, configuration -> configuration.getString("locations.npc.gungame"),
            new ItemStack(Material.DIAMOND_AXE),

            ServerMode.GUNGAME,
            "ewogICJ0aW1lc3RhbXAiIDogMTYzOTE3MjE5ODE3NiwKICAicHJvZmlsZUlkIiA6ICIzNmMxODk4ZjlhZGE0NjZlYjk0ZDFmZWFmMjQ0MTkxMyIsCiAgInByb2ZpbGVOYW1lIiA6ICJMdW5haWFuIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzFkOWE5OGY0ODlhYzQ1MTc4YTg2ZjA0NDJjOGE1NjE2Zjk2MzkzYTEzNTAzMzQyMDJmNDNiNDVkZWVjNjM3N2YiCiAgICB9CiAgfQp9",
            "psvstgsDnixS8fBsUF1UaQ8vLESwF0BKSD7ty7UfNrMyYsCjUuiI1/klp4RFT1t+5ZAuMCdvFn57SDg4pf2E40tAuYJvD0ayD+2qgHseIXPNjWNHSgWUibua858KXyBIpWKkcDOlLo6uircyxreBkvajlIO0T449h9JE/TAU0yzj/HQ8Ezaffb/7/0pxZmlqeVRzBSM6V3s1DPRJrXd9TRfGG8RvUCARPOHWth3JeFJ3k+8vHUlg6Kmv88TveimmrcwmgGWKX38b64eG3Gyyb4rnLnNWyPzjyOm5UYeDMqdPyAisXJCNoMhYHzHil95nz3iDhd+xpwnJfIOj8nUe6PjBdP/lC7ZZoS4qHjYegrtmUyxyfWv44l/dsJE+T2LEd5+m+A1kuiAloaGNTsQBtvgQja+NYfpAj7NWFYi5yKUuhoGvf7bavAGn3IG6k1Vi5Y0kNSeTfXzEpOwKmbKCP202Ltd3dJqcWV7Ooc6h7kRZ7sQeRF9lvn9Tmxw3DBB8yKFIj+0cgxFCDRd55b+NA+qJqcCLNAgX2l4hmXvpycggm9N50+wFWx0bCkB5fG5y7DvPGApgOBBgTtKrl42SggRtiCBEglA6U1sLhoDjqt6OMksAIkhrUXG2AokklhWxnAFTEEYrbudfLFyMedENXY5CMtlI6MW51RT6BQLIecQ="),

    ArcadeGames(ChatColor.DARK_GREEN, configuration -> configuration.getString("locations.npc.arcadegames"),
            ItemUtil.newBuilder(Material.SKULL_ITEM)
                    .setDurability(3)
                    .setTextureValue("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGY5MjMzYzEyNDdlMDNlOWZkMjc3NDI3MzdlNzllNGNjZWJkMjI1YTliMDU5ZDU5NmQ1Y2QzNGUyNmYyMTY1In19fQ==")
                    .build(),

            ServerMode.ARCADE,
            "ewogICJ0aW1lc3RhbXAiIDogMTU5MTU4ODc0NTcwMSwKICAicHJvZmlsZUlkIiA6ICIxOTI1MjFiNGVmZGI0MjVjODkzMWYwMmE4NDk2ZTExYiIsCiAgInByb2ZpbGVOYW1lIiA6ICJTZXJpYWxpemFibGUiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWM4NTgyZTM3ZmJmZWMzMTdmMzkzMzVjYTU4MTYwMTRmMzIyNjJlMjZhYWEyY2I2YjcwYzllOThjM2MyMzk5MSIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9",
            "Z4j6sDkN0lqRzWBbE5a1V7o9YG0JTMmyVOZT6Xe6OhehSmAq8R+rDX88Kug1Cpb6qx20UR0znuVRRs7IwmOlZA1z1Ma3w9Fr1IIVZJ1r3kt2RgBMMdfNX+L6AfHKo3I/tvUf4G3qdOQ5suZ6aFf9AwHcUl0ol15pg09qqOOhwHuLwoTllfov4clOhCDwCFVPrnc/VWb1znKilpL3icIoZFxgqjDFXwc9xQ/r2HI9auMgNnKpMnVJBH7vNdBzRViZt4ZcC7K6fQeDEv6Y70zdx3b+P2Aqh8tDvynUm3vvk7cCborPsG7Zu0rgSTbfBc3tj3dZYGZjgopI0UjGTdThKoI+6gOVc3kESRzzK4Cyqf+tzjZ9SsLOCXZOU1wc6h1+nyTGkKO/qifSnCN6NywhzdWopKnrwFE3yCwSPrY1SfcWZWYtY8SdOXdTNrcoYNpfdvfabKi3ynXVN7qHzkVYcejXLDcpAN8iy/0sx6F0klMQY3jzS4wYydRxhgCJ35/uYelLFAHnQo1ZRQ5i4e6sjwAj4Jh4nOrXPEstcPyJr3SHLAqLVJuqQnGY5FSlAotsCBtVzZApVuuYZv4Ac491mV+wrS/J2fbnfb0VEupOGSfSpFBgIlgSLlC5x7LZhT3Y6g3xW+xRi8BU2xkEiX1fMpH0yruYglYwz1KY4gv0WOg="),

    SkyWars(ChatColor.AQUA, configuration -> configuration.getString("locations.npc.skywars"),
            ItemUtil.newBuilder(Material.BOW)
                    .setGlowing(true)
                    .build(),

            ServerMode.SKYWARS,
            "ewogICJ0aW1lc3RhbXAiIDogMTYzOTE2NzAwMDg2NywKICAicHJvZmlsZUlkIiA6ICJkMGI4MjE1OThmMTE0NzI1ODBmNmNiZTliOGUxYmU3MCIsCiAgInByb2ZpbGVOYW1lIiA6ICJqYmFydHl5IiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2RhZDNkNGMxNzViMTgxMDA1MDFhMDEzMGU0NzU5YTQzMDNhOGI4ODJjNjczYjNiOWZhZDEwZjhkZjFkYmMxNGIiCiAgICB9CiAgfQp9",
            "v73F89wrBWqUm+YGnXV8BxkMb0Z8ZBtFURrfUip5lbAWyndZ3b49xVSajYZ0m6N0caAOLNnkZePn+j5yP0X0y/9XzAfnNLxi6KyyoJ4aWLRjyNwL3B+hybZ527rF7QUZm0vQw3BD2zBTlsVomS0RW9f0SzKZ6Lx7R8/a+vUSSHpzwg1idZIJxEsx3TzEoyTjiW6N8QalGQ6UMoniU2Oy4gNdmhzcXuIXmI0vitCjxs7MTPEuRH9KxJXELpAHFgSEjb+9p5SvMG68/INYXs4LyIkgZL9/AKATeCqq2rlA9EirJG2ejQyMnepMXgI+rqiI0ucYxpNQVi/9iGbX2Y6TO3+K2hE/T+V97QwA5fbzFIy7VFr6alkZf+ToOdLwrD+IytFEXzjiGB9Ej0UG104fTewgAxEVRDJ4O1kQ7Rbsb+w7nPbU0+/BEYnS3SnGR5anBzt8WIh4buJzn6JTRxg4kqleKqo2zpuhvai0oLGcVPDSboai93Lvv4N4+FsiaXpBB1EUgBo9b6zaY6FQBXHrVre1XF/LIgoUGk++f9RdX3OJLzaWtDfnc7xu2whebnORYG63Tkh6Wi9apiZAR5CfO8CD6PZPpwqT+VPRNc+44k5TNY6V5vNO5n6KeFUeG/lKRpMKjvwIVBuV7WkAZUKX2h/D4N6H/yiejpnX7kTUCbU="),

    BedWars(ChatColor.RED, configuration -> configuration.getString("locations.npc.bedwars"),
            new ItemStack(Material.BED, 1, (byte) 14),

            ServerMode.BEDWARS,
            "ewogICJ0aW1lc3RhbXAiIDogMTYzOTE2NzEzMzAwOSwKICAicHJvZmlsZUlkIiA6ICIxN2Q0ODA1ZDRmMTA0YTA5OWRiYzJmNzYzMDNjYmRkZiIsCiAgInByb2ZpbGVOYW1lIiA6ICJnaWZ0bWV0b25uZXMiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGE5N2M5YjFlMzNmMWFhZjkwNWUwOTI0M2Q4ODdlN2UyMTU0MGQ1YjIxMGUxM2ZkMGRiZTUyMjViY2YzMTA0NSIKICAgIH0KICB9Cn0=",
            "np02prZJbhAi5EKwA75aSig3SQnHbhI2w1WEHaKy3RrqvqSIfWlZnbNbNpDSqKFma5IPx0L5GPAU2OPeOCHq2in1pN2sRDgpLhVyQTlJHnhxFOdomec4CFw2Y6Q0zpIOOCGpSOzdei0Df3dIbOMw48rZjtbq6NcVYFy0mdruuk/sCHOOmq4tElxyf34ZbC6ZyKLQz826px7IQ/o5mObQKgDlVOkiqTBLKJr7jLuR42STxDiR3UQoPgSAi+F1ywLIkQvERV7LlKEmkiH/9kdlBjqvCvb7uVBeATeXCdh76wF9LeJlnfvgm1KaUfgxKRRfWWSeXK5UeP29tVYCH6fBtuGfzuc7a1W+pPQ41vcmPc+v0l7vZS5y09ZBQE22JkFSfOXfcU3gBlSCxFv91Qi8T7bEXjA/+CzbnP+a1yQQfCl7U8zLxNggQZnUBx4SBlTcP226uFpatPlLXHRkOD2jwmkqczkKJlIJTWgPlvPGNRUFOxTr4Xd3DRZVeUS8EQQ7e/XejW+yxB1x++/CYbLkY//rNFxxWWUcGZfo/gqxfh/FDnv7D+RSmUsjH3LJG6PHEdIdw1trhnkJjxBPlTAtYNJWL7qL0FoRoBxCLdeIX4SrEc3yTzmBvd+MYisuEbq6hZau1Z+2o4AyV5V8ZE5U8oPJKkKi1rZaK3QauRvD/+Q="),

    Duels(ChatColor.YELLOW, configuration -> configuration.getString("locations.npc.duels"),
            new ItemStack(Material.WOOD_SWORD),

            ServerMode.DUELS,
            "ewogICJ0aW1lc3RhbXAiIDogMTY0NTQ2MjMzMDM4MiwKICAicHJvZmlsZUlkIiA6ICJmMjc0YzRkNjI1MDQ0ZTQxOGVmYmYwNmM3NWIyMDIxMyIsCiAgInByb2ZpbGVOYW1lIiA6ICJIeXBpZ3NlbCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS85ZDkzODI5ZjAwYjQ5YWZlNDRkMGJkMGZhYWYyYzQwNTA4MTY0NGIyMmNhYThlZDg5ODRhYTNkODBjYzYxM2NkIgogICAgfQogIH0KfQ==",
            "DbHjn7v68bqMGiLIwFTY3kkM2A+p1PNd7gYBW322RhQpfcDcVlxr3TcAtMSt0aE6xFSO0/YtZ9PZW5iQ+hxSM2MFTBf/Z+9nmOZYDzKSVD8uaNy91/G5LJRJdp0TRxGa7wNElsC9dD4i0a1VR5fu0lW1JrYwcIc/x7Wxug4vQP7vB9sG+XnS5mig+q6p3SUNnwe7226TsFynliqJhMaaHFoq/lJ3SiLkyqUZtbtCNs6t7BuyvahbgKIgfZB+hd+5hsjY7Z3jnEguvuEbvgseYuMrmC8Si88LlsJhuOXAmL9WjombWISLxEsOSn5fb5QMZuVi146o9/O5Rqf3zgxdc61nrNn51fDuNzXgsG/ggPT2TEcrnVms6X3q2MXvzgLZIfXqostGrphRKhoqPI6OHdSc0gGD0xolLnLfOitzvlxJr5vJImW06RasyqVxjIzmAPfywIYQuImfyiRKtSpkX8Ikkm3CqJRSndyhePWsoBfxa+V7nbfcPxYc612maQNJ4dDTzKKadCJuyF4EWpVG8bn1I8DOnm8IKXlAtVE84ii8rYWlgEAYo0DpnRc+Cbob7XqIxyg74X64JKGQQQaxM0k7frYQvjpKGQiTikXPzE4zgfLbfSIMeQPnBt7DNPSimnRMrIyZ9JBoDlVdoOalI+eiVW9tcBVdmSG2pmracKA="),

    //Prison(ChatColor.AQUA, configuration -> configuration.getString("locations.npc.eggwars"),
    //        new ItemStack(Material.APPLE),
    //
    //        ServerMode.PRISON,
    //        "eyJ0aW1lc3RhbXAiOjE1MjAyNzkzOTM3ODksInByb2ZpbGVJZCI6IjNkOWNmOTZiN2MyNzRiZWVhZDFiOWQ0NTM3NTRjYjc2IiwicHJvZmlsZU5hbWUiOiJOaWtha2EiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2ExNWEyMzBiZDJjZDMxNjFjNjdlYmY3Yjk3NWY5Mjc1YWY1ZGRlNjg2NDZlNTQ2NTgxMmU0OTdhNTJiZjZlIn19fQ==",
    //        "SRakq8xPMfh17Uh+1t3+9Rs8PbL4Yn1uqfC1xmcmbZLs9M7S8/i/qTPBS02g8eaNhKIbDw2LvWv2t6M8UYxXeKP+8O7nRoit1Nh3v5tHjseOXnIrxTLNDbdEpYVyZ0KDykPm+7D5Hpymc5e0k+mQsQGCYbtK9XgLkqjzP76iJVCFQWdP8DNQi8R6NeOqDNSfWhxjmWXzE+0cuWLpPtuqZqq99FUVTIzNa0gSu/T5PSXoR2zPvVqm3/Ia/tS+38Yj91910qwfa3ABq35G/u3o3FsZVJ8tbyZwQO2nHiOBuyiu5+rcxGViT55gnx3OsWOKeCIo1rdgvl55vzcAtnaEQW8E1UPszEkS0WYPpD3UOj7jzrvTQAFdNkN0R+lhiIS5gnBfuKhhbtL3wFre3ay2Ofw1jaS/bRWXUiK9IyMf1RuOBdBWsoRt0MNL//L7n2SKvZQdS7oy4boZPA7wq5xCIKHLIUfgz3ZeY+fEgCVPeCpWkjm2j1emnp2qofEJGhtZ/4gXs0/uhAVNk7W4xClKEO8XV44HkrHJJqdpjNe6fuMEVKCESHyu4d/zByZDGCh7ymC9puP0RqhEevws0xlsnvYlbjMDnWWugKVWQRUZ8MahxZinFAAq27ucFWDUfoc/AYE1aQQCweGpNDh+lbIaZ++Y3kzwjoy0a7EmMq3FC6k="),
    ;

    private final ChatColor chatColor;
    private final Function<FileConfiguration, String> npcLocation;

    private final ItemStack itemInMainHand;

    private final ServerMode serverMode;
    private final String textureData;
    private final String textureSignature;


    public int getModeOnline() {
        return PlazmixCoreApi.getOnlineByServersPrefixes(serverMode.getServersPrefix());
    }

    public int getConnectedServers() {
        return PlazmixCoreApi.getConnectedServersCount(serverMode.getServersPrefix());
    }


    public MojangSkin getMojangSkin() {
        return new MojangSkin(name(), UUID.randomUUID().toString(), textureData, textureSignature, System.currentTimeMillis());
    }

    public void addNpc(@NonNull Location location) {
        ServerNPCManager.INSTANCE.register(new ServerModeNPC(this, location));
    }

}