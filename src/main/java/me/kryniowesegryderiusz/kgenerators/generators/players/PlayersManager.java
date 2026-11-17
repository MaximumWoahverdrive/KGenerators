package me.kryniowesegryderiusz.kgenerators.generators.players;

import java.util.concurrent.ConcurrentHashMap;
import me.kryniowesegryderiusz.kgenerators.generators.players.objects.GeneratorPlayer;
import org.bukkit.entity.Player;

public class PlayersManager {

    private final GeneratorPlayer nullPlayer = new GeneratorPlayer(null, true);

    // Concurrent: players are created from the async chunk loader as well as from region threads
    private final ConcurrentHashMap<String, GeneratorPlayer> players = new ConcurrentHashMap<>();

    public GeneratorPlayer getPlayer(String nick) {
        if (nick == null) return nullPlayer;

        GeneratorPlayer player = this.createPlayer(nick);
        player.loadIfNeeded();
        return player;
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

        return players.computeIfAbsent(nick, GeneratorPlayer::new);
    }
}
