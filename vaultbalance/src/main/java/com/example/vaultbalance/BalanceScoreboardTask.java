package com.example.vaultbalance;

import io.papermc.paper.scoreboard.numbers.NumberFormat;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.plugin.Plugin;

public class BalanceScoreboardTask extends BukkitRunnable {

    private static final String OBJECTIVE_NAME = "vaultbal";
    private final Plugin plugin;
    private final Economy economy;

    public BalanceScoreboardTask(Plugin plugin, Economy economy) {
        this.plugin = plugin;
        this.economy = economy;
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            updatePlayer(player);
        }
    }

    private void updatePlayer(Player player) {
        // Jeder Spieler bekommt sein eigenes Scoreboard-Objekt mit "Below Name".
        Scoreboard board = player.getScoreboard();

        // Falls der Spieler noch das Hauptboard des Servers nutzt, eigenes anlegen
        if (board == null || board == Bukkit.getScoreboardManager().getMainScoreboard()) {
            board = Bukkit.getScoreboardManager().getNewScoreboard();
            player.setScoreboard(board);
        }

        Objective objective = board.getObjective(OBJECTIVE_NAME);
        if (objective == null) {
            // "$" grün einfärben
            objective = board.registerNewObjective(OBJECTIVE_NAME, "dummy", ChatColor.GREEN + "$");
            objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
        }

        // Die Zahl selbst weiß einfärben (Paper-API, unabhängig vom Objective-Namen)
        NumberFormat whiteNumber = NumberFormat.styled(Style.style(NamedTextColor.WHITE));

        for (Player target : Bukkit.getOnlinePlayers()) {
            double balance = economy.getBalance(target);
            int rounded = (int) Math.round(balance);
            Score score = objective.getScore(target.getName());
            score.setScore(rounded);
            score.numberFormat(whiteNumber);
        }
    }

    public void cleanupAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            Scoreboard board = player.getScoreboard();
            if (board != null) {
                Objective objective = board.getObjective(OBJECTIVE_NAME);
                if (objective != null) {
                    objective.unregister();
                }
            }
        }
    }
}
