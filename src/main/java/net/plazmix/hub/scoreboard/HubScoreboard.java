package net.plazmix.hub.scoreboard;

import lombok.NonNull;
import net.plazmix.core.PlazmixCoreApi;
import net.plazmix.scoreboard.BaseScoreboardBuilder;
import net.plazmix.scoreboard.BaseScoreboardScope;
import net.plazmix.scoreboard.animation.ScoreboardDisplayFlickAnimation;
import net.plazmix.utility.NumberUtil;
import net.plazmix.utility.ProgressBar;
import net.plazmix.utility.player.PlazmixUser;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.List;

public class HubScoreboard {

    public HubScoreboard(@NonNull Player player) {
        PlazmixUser plazmixUser = PlazmixUser.of(player);

        BaseScoreboardBuilder scoreboardBuilder = BaseScoreboardBuilder.newScoreboardBuilder();
        scoreboardBuilder.scoreboardScope(BaseScoreboardScope.PROTOTYPE);

        ScoreboardDisplayFlickAnimation displayFlickAnimation = new ScoreboardDisplayFlickAnimation();

        displayFlickAnimation.addColor(ChatColor.LIGHT_PURPLE);
        displayFlickAnimation.addColor(ChatColor.DARK_PURPLE);
        displayFlickAnimation.addColor(ChatColor.WHITE);
        displayFlickAnimation.addColor(ChatColor.DARK_PURPLE);

        displayFlickAnimation.addTextToAnimation(plazmixUser.localization().getMessageText("HUB_BOARD_TITLE"));

        scoreboardBuilder.scoreboardDisplay(displayFlickAnimation);
        scoreboardBuilder.scoreboardUpdater((baseScoreboard, player1) -> {

            baseScoreboard.setScoreboardDisplay(displayFlickAnimation);

            List<String> scoreboardLineList = getScoreboardLines(plazmixUser);

            for (String scoreboardLine : scoreboardLineList) {
                baseScoreboard.setScoreboardLine(scoreboardLineList.size() - scoreboardLineList.indexOf(scoreboardLine), player, scoreboardLine);
            }

        }, 20);

        scoreboardBuilder.build().setScoreboardToPlayer(player);
    }

    private List<String> getScoreboardLines(@NonNull PlazmixUser plazmixUser) {

        List<String> scoreboardLineList = new LinkedList<>();

        ProgressBar levelProgressBar = new ProgressBar(plazmixUser.getExperience(), plazmixUser.getMaxExperience(),
                10, "§e", "§7", "―");

        for (String scoreboardLine : plazmixUser.localization().getMessageList("HUB_BOARD_LINES")) {
            scoreboardLine = scoreboardLine
                    .replace("%online%", NumberUtil.spaced(PlazmixCoreApi.getGlobalOnline()))

                    .replace("%lobby%", "#" + PlazmixCoreApi.getCurrentServerName().split("\\-")[1])
                    .replace("%level%", NumberUtil.spaced(plazmixUser.getLevel()))
                    .replace("%level_exp%", NumberUtil.spaced(plazmixUser.getExperience()))
                    .replace("%level_max_exp%", NumberUtil.spaced(plazmixUser.getMaxExperience()))
                    .replace("%level_exp_percent%", levelProgressBar.getPercent())
                    .replace("%level_bar%", levelProgressBar.getProgressBar())

                    .replace("%plazma%", NumberUtil.spaced(plazmixUser.getGolds()))
                    .replace("%money%", NumberUtil.spaced(plazmixUser.getCoins()))

                    .replace("%status%", PlazmixCoreApi.GROUP_API.getGroupColouredName(plazmixUser.getName()));

            scoreboardLineList.add(ChatColor.translateAlternateColorCodes('&', scoreboardLine));
        }

        return scoreboardLineList;
    }
}