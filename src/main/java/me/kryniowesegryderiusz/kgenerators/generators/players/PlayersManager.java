package me.kryniowesegryderiusz.kgenerators.generators.players;

import java.util.HashMap;
import me.kryniowesegryderiusz.kgenerators.generators.players.objects.GeneratorPlayer;
import org.bukkit.entity.Player;

public class PlayersManager {

    private final GeneratorPlayer nullPlayer = new GeneratorPlayer(null, true);

    private final HashMap<String, GeneratorPlayer> players = new HashMap<>();

    public GeneratorPlayer getPlayer(String nick) {
        if (nick == null) return nullPlayer;

        this.createPlayer(nick);

        if (!players.get(nick).isLoaded()) players.get(nick).loadPlayer();

        return players.get(nick);
    }

    public GeneratorPlayer getPlayer(Player p) {
        return getPlayer(p.getName());
    }

    /**
     * Creates player and does not load it. Player will be loaded if plugin will
     * need that (getPlayer()); This method should be used specifically in loading
     * generators from database
     *
     * @param nick player name, or null for the shared "no player" instance
     * @return GeneratorPlayer: not loaded player if he is not loaded yet, loaded player otherwise
     */
    public GeneratorPlayer createPlayer(String nick) {
        if (nick == null) return this.nullPlayer;

        if (!players.containsKey(nick)) players.put(nick, new GeneratorPlayer(nick));

        return players.get(nick);
    }

    public void clear() {
        this.players.clear();
    }
}
