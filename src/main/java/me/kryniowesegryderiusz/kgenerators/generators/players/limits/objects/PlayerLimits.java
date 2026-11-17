package me.kryniowesegryderiusz.kgenerators.generators.players.limits.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map.Entry;
import me.kryniowesegryderiusz.kgenerators.Main;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

public class PlayerLimits {

    private final HashMap<Limit, Integer> adjustedPlaceLimits = new HashMap<>();

    /**
     * Creates limit map with limits adjusted by permissions
     *
     * @param player the player whose permissions adjust the limits
     */
    public PlayerLimits(Player player) {

        /* Filling variables */
        for (Entry<String, Limit> e : Main.getLimits().getEntrySet()) {
            this.adjustedPlaceLimits.put(e.getValue(), e.getValue().getPlaceLimit());
        }

        ArrayList<Limit> bypassedLimits = new ArrayList<>();

        /* Checking limits: kgenerators.placelimit.<limit id>.<amount> or .bypass */
        player.recalculatePermissions();

        for (PermissionAttachmentInfo pai : player.getEffectivePermissions()) {
            if (!pai.getValue()) continue;
            String perm = pai.getPermission().toLowerCase(Locale.ROOT);

            for (Limit limit : Main.getLimits().getValues()) {
                String prefix = "kgenerators.placelimit." + limit.getId().toLowerCase(Locale.ROOT) + ".";
                if (!perm.startsWith(prefix)) continue;
                String suffix = perm.substring(prefix.length());

                if (suffix.equals("bypass")) {
                    bypassedLimits.add(limit);
                    continue;
                }

                int current = this.adjustedPlaceLimits.get(limit);
                if (current == -1) continue; // already unlimited
                try {
                    int amount = Integer.parseInt(suffix);
                    if (amount > current) this.adjustedPlaceLimits.put(limit, amount);
                } catch (NumberFormatException ignored) {
                    // Not a limit permission, e.g. a longer limit id sharing this prefix
                }
            }
        }

        /* Adjusting limits basing on bypass permissions */

        for (Limit limit : bypassedLimits) {
            this.adjustedPlaceLimits.put(limit, -1);
        }
    }

    public int getLimit(Limit limit) {
        return this.adjustedPlaceLimits.get(limit);
    }
}
