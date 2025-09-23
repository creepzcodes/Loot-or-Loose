package de.creepz.command;

import de.creepz.Core;
import de.creepz.util.Timer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.mineacademy.fo.annotation.AutoRegister;
import org.mineacademy.fo.command.SimpleCommand;

import java.util.List;

@AutoRegister
public final class TimerCommand extends SimpleCommand {


    public TimerCommand() {
        super("timer");
    }

    @Override
    protected void onCommand() {
        checkConsole();
        Player player = getPlayer();
        String param = args[0];


        if (param.isEmpty()) {
            sendUsage(player);
            return;
        }

        switch (param.toLowerCase()) {
            case "resume": {
                Timer timer = Core.getInstance().getTimer();

                if (timer.isRunning()) {
                    player.sendMessage(ChatColor.RED + "Der Timer läuft bereits.");
                    break;
                }
                timer.setRunning(true);
                player.sendMessage(ChatColor.GRAY + "Der Timer wurde gestartet.");

                break;
            }
            case "pause": {
                Timer timer = Core.getInstance().getTimer();

                if (!timer.isRunning()) {
                    player.sendMessage(ChatColor.RED + "Der Timer läuft nicht.");
                    break;
                }
                timer.setRunning(false);
                player.sendMessage(ChatColor.GRAY + "Der Timer wurde gestoppt.");
                break;
            }
            case "time": {

                if (args.length != 2) {
                    player.sendMessage(ChatColor.GRAY + "Verwendung" + ChatColor.DARK_GRAY + ": " + ChatColor.BLUE + "/timer time <Zeit>");
                    return;
                }

                try {
                    Timer timer = Core.getInstance().getTimer();

                    timer.setRunning(false);
                    timer.setTime(Integer.parseInt(args[1]));
                    player.sendMessage(ChatColor.GRAY + "Deine Zeit wurde auf " + args[1] + " gesetzt");
                } catch (NumberFormatException e) {
                    player.sendMessage(ChatColor.GRAY + "Dein Parameter 2 muss eine Zahl sein.");
                }

                break;
            }
            case "reset": {
                Timer timer = Core.getInstance().getTimer();

                timer.setRunning(false);
                timer.setTime(0);

                player.sendMessage(ChatColor.GRAY + "Der Timer und die Border wurden zurückgesetzt.");
                break;
            }
            default:
                sendUsage(player);
                break;
        }

    }

    private void sendUsage(Player player) {
        player.sendMessage(ChatColor.GRAY + "Verwendung" + ChatColor.DARK_GRAY + ": " + ChatColor.BLUE + "/timer resume, /timer pause, /timer time <Zeit>, /timer reset");
    }

    @Override
    protected List<String> tabComplete() {
        if (args.length == 1) {
            return completeLastWord("resume", "pause", "time", "reset");
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("resume")) {
            return completeLastWord("<Zeit>");
        }

        return NO_COMPLETE;
    }
}
