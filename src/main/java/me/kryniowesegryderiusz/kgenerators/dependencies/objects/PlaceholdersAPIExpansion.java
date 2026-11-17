package me.kryniowesegryderiusz.kgenerators.dependencies.objects;

import java.util.StringJoiner;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.generators.generator.objects.Generator;
import me.kryniowesegryderiusz.kgenerators.generators.players.limits.objects.Limit;
import me.kryniowesegryderiusz.kgenerators.generators.players.limits.objects.PlayerLimits;
import me.kryniowesegryderiusz.kgenerators.generators.players.objects.GeneratorPlayer;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlaceholdersAPIExpansion extends PlaceholderExpansion {

    @SuppressWarnings("unused")
    private final Main plugin; // The instance is created in the constructor and won't be modified, so it can
    // be final

    public PlaceholdersAPIExpansion() {
        this.plugin = Main.getInstance();
    }

    @Override
    public @NotNull String getIdentifier() {
        return "kgenerators";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Krynio";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true; // This is required or else PlaceholderAPI will unregister the Expansion on
        // reload
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {

        Player p = player.getPlayer();

        if (p == null) return "KGen player not online";

        GeneratorPlayer gp = Main.getPlayers().getPlayer(p);

        if (gp == null) return "KGen GeneratorPlayer doesn't exist";

        String[] args = params.split("_");

        /*
         * kgenerators_generators_placed_<generator>/all
         * kgenerators_limits_placed_<limit>
         * kgenerators_limits_limit_<limit>
         * kgenerators_limits_left_<limit>
         */

        if (args.length <= 2) return "KGen placeholder doesn't exist";

        switch (args[0].toLowerCase()) {
            case "generators":
                if (args[1].equalsIgnoreCase("placed")) {

                    StringJoiner joiner = new StringJoiner("_");
                    for (int i = 2; i < args.length; i++) joiner.add(args[i]);

                    if (args[2].equalsIgnoreCase("all")) {
                        return gp.getAllGeneratorsCount() + "";
                    } else {
                        Generator g = Main.getGenerators().get(joiner.toString());
                        if (g == null) return "KGen generator " + joiner + " doesn't exist";
                        else return gp.getGeneratorsCount(g) + "";
                    }

                } else return "Not valid KGen placeholder";

            case "limits":
                StringJoiner joiner = new StringJoiner("_");
                for (int i = 2; i < args.length; i++) joiner.add(args[i]);

                Limit limit = Main.getLimits().get(joiner.toString());
                if (limit == null) return "KGen Limit " + joiner + " doesn't exist";

                switch (args[1].toLowerCase()) {
                    case "placed" -> {
                        return limit.getPlacedGenerators(gp) + "";
                    }
                    case "limit" -> {
                        PlayerLimits pl = new PlayerLimits(p);
                        return pl.getLimit(limit) + "";
                    }
                    case "left" -> {
                        int playerLimit = new PlayerLimits(p).getLimit(limit);
                        if (playerLimit == -1) return "-1";
                        return Math.max(0, playerLimit - limit.getPlacedGenerators(gp)) + "";
                    }
                    default -> {
                        return "Not valid KGen placeholder";
                    }
                }
        }

        return "Not valid KGen placeholder"; // null = Placeholder is unknown by the Expansion
    }
}
