package de.zorryno.trollsystem.commands;

import de.zorryno.trollsystem.Trollsystem;
import de.zorryno.trollsystem.nmsapi.PacketSender;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TrollCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player player))
            return true;

        if(args.length == 1) {
            Player targetPlayer = Bukkit.getPlayer(args[0]);
            if(targetPlayer == null) {
                player.sendMessage(Trollsystem.getMessages().getCache().get("PlayerNotFound"));
                return true;
            }

            Trollsystem.getTrollSelector().openNewInventory(player);
            Trollsystem.getTrollSelector().setTarget(player, targetPlayer);
            return true;
        }

        if(args.length == 2 && args[0].equalsIgnoreCase("settings")) {
            switch (args[1].toLowerCase()) {
                case "reload":
                    Trollsystem.reload();
                    player.sendMessage("§aReloaded!");
                    break;
            }
            return true;
        }

        Trollsystem.getPlayerSelector().openInventory(player);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player player))
            return Collections.emptyList();

        List<String> commands = new ArrayList<>();
        List<String> completions = new ArrayList<>();

        if(args.length == 1) {
            Bukkit.getOnlinePlayers().forEach(target -> commands.add(target.getName()));
            if(sender.isOp())
                commands.add("settings");
        }

        if(args.length == 2 && sender.isOp() && args[0].equalsIgnoreCase("settings")) {
            commands.add("reload");
        }

        StringUtil.copyPartialMatches(args[args.length - 1] , commands, completions);
        Collections.sort(completions);
        return completions;
    }
}
