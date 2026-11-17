package me.kryniowesegryderiusz.kgenerators;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import me.kryniowesegryderiusz.kgenerators.generators.generator.objects.Generator;
import me.kryniowesegryderiusz.kgenerators.lang.Lang;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CommandTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(
            @NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String @NotNull [] args) {

        if (args.length == 1) return filter(Lang.getMessageStorage().getCommands(sender), args[0]);

        String sub = args[0].toLowerCase(Locale.ROOT);
        String last = args[args.length - 1];
        switch (sub) {
            case "give":
                if (args.length == 2) return getPlayers(last);
                if (args.length == 3) return getGenerators(last);
                break;
            case "menu":
                if (args.length == 2) return getPlayers(last);
                if (args.length == 3) return filter(List.of("chances", "recipe", "upgrade"), last);
                if (args.length == 4) return getGenerators(last);
                break;
            case "remove":
                if (args.length == 2) return filter(List.of("worldedit", "owner"), last);
                if (args.length == 3 && args[1].equalsIgnoreCase("owner")) return getPlayers(last);
                break;
            case "convertdbto":
                if (args.length == 2) return filter(List.of("SQLITE", "MYSQL"), last);
                break;
            case "spawn":
                if (args.length == 2) {
                    List<String> c = new ArrayList<>();
                    c.add("worldedit");
                    for (World w : Main.getInstance().getServer().getWorlds()) c.add(w.getName());
                    return filter(c, last);
                }
                if (args[1].equalsIgnoreCase("worldedit")) {
                    if (args.length == 3) return getGenerators(last);
                    if (args.length == 4) return getPlayers(last);
                } else {
                    // spawn <world> <x> <y> <z> <generator> [owner]
                    if (args.length == 6) return getGenerators(last);
                    if (args.length == 7) return getPlayers(last);
                }
                break;
            default:
                break;
        }
        return new ArrayList<>();
    }

    private List<String> filter(List<String> options, String arg) {
        List<String> result = new ArrayList<>();
        for (String o : options)
            if (isBlank(arg) || o.toLowerCase(Locale.ROOT).contains(arg.toLowerCase(Locale.ROOT))) result.add(o);
        return result;
    }

    ArrayList<String> getPlayers(String arg) {
        ArrayList<String> players = new ArrayList<>();
        if (isBlank(arg)) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                players.add(p.getName());
            }
        } else {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.getName().contains(arg)) players.add(p.getName());
            }
        }
        return players;
    }

    ArrayList<String> getGenerators(String arg) {
        ArrayList<String> generators = new ArrayList<>();
        if (isBlank(arg)) {
            for (Generator g : Main.getGenerators().getAll()) {
                generators.add(g.getId());
            }
        } else {
            for (Generator g : Main.getGenerators().getAll()) {
                if (g.getId().contains(arg)) generators.add(g.getId());
            }
        }
        return generators;
    }

    boolean isBlank(String s) {
        return s.isEmpty() || s.equals(" ");
    }
}
