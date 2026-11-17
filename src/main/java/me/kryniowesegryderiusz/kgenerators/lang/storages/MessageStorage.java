package me.kryniowesegryderiusz.kgenerators.lang.storages;

import me.kryniowesegryderiusz.kgenerators.utils.Text;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.dependencies.hooks.PlaceholderAPIHook;
import me.kryniowesegryderiusz.kgenerators.lang.enums.Message;
import me.kryniowesegryderiusz.kgenerators.lang.interfaces.IMessage;
import me.kryniowesegryderiusz.kgenerators.logger.Logger;
import me.kryniowesegryderiusz.kgenerators.utils.immutable.Config;

public class MessageStorage {

	private final HashMap<String, String> lang = new HashMap<>();
	private final Config config;

	public MessageStorage(Config config) {
		this.config = config;
	}

	public <T extends Enum<T> & IMessage> void register(Class<T> c) {
		for (T en : c.getEnumConstants()) {
			checkLine(en.getKey(), en.getMessage());
		}
	}

	public void reload() {
		for (Entry<String, String> e : lang.entrySet()) {
			checkLine(e.getKey(), e.getValue());
		}
	}

	private void checkLine(String key, String value) {
		String keyAdjusted = "messages." + key;
		if (config.getString(keyAdjusted) != null) {
			lang.put(key, Text.colorize(config.getString(keyAdjusted)));
		} else {
			try {
				lang.put(key, Text.colorize(value));
				config.set(keyAdjusted, value);
				config.saveConfig();
			} catch (IOException ex) {
				Logger.error("Lang: Cant save lang file!");
				Logger.error(ex);
			}
		}
	}

	public <T extends Enum<T> & IMessage> String get(T m, String... replacables) {
		return get(m, false, replacables);
	}

	public <T extends Enum<T> & IMessage> String get(T m, boolean prefix, String... replacables) {

		String message = lang.get(m.getKey());
		ArrayList<String> reps = new ArrayList<>(Arrays.asList(replacables));

		if (message != null && !message.isEmpty()) {

			for (int i = 0; i < reps.size(); i = i + 2) {
				message = message.replace(reps.get(i), reps.get(i + 1));
			}

			if (prefix)
				message = lang.get(Message.PREFIX.getKey()) + message;

			return message;
		}
		return null;
	}

	public <T extends Enum<T> & IMessage> void send(CommandSender sender, T m, String... replacables) {
		send(sender, m, false, true, replacables);
	}

	public <T extends Enum<T> & IMessage> void send(CommandSender sender, T m, boolean forceChat, boolean prefix,
			String... replacables) {

		String message = this.get(m, prefix, replacables);

		if (message != null) {
			String[] splittedMessage = message.split("\n");
			for (String s : splittedMessage) {
				
				String mess = s;
				
				if (sender instanceof Player player) mess = PlaceholderAPIHook.translatePlaceholders(player, mess);

				// Only players have an action bar; anyone else (the console) gets it as chat
				if (sender instanceof Player player && m.getKey().split("\\.")[0].contains("generators")
						&& Main.getSettings().isActionbarMessages() && !forceChat) {
					player.sendActionBar(Text.component(mess));
				} else {
					sender.sendMessage(Text.component(mess));
				}
			}
		}
	}

	public void sendHelp(CommandSender sender) {

		sender.sendMessage(Text.component(lang.get("commands.help.label")));

		for (Map.Entry<String, String> entry : lang.entrySet()) {
			String path = entry.getKey();
			String[] spath = path.split("\\.");

			if (spath.length == 3 && spath[0].equals("commands") && spath[2].equals("help")
					&& sender.hasPermission("kgenerators." + spath[1])) {
				sender.sendMessage(Text.component(lang.get("commands.help.format").replace("<subcommand>", spath[1]).replace("<help>",
						entry.getValue())));
			}
		}
	}

	/**
	 * Gets command list
	 * 
	 * @param sender sender whose permissions filter the list
	 * @return names of commands that have help text and the sender has permission for
	 */
	public ArrayList<String> getCommands(CommandSender sender) {

		ArrayList<String> commands = new ArrayList<>();

		for (Map.Entry<String, String> entry : lang.entrySet()) {
			String path = entry.getKey();
			String[] spath = path.split("\\.");

			if (spath.length == 3 && spath[0].equals("commands") && spath[2].equals("help")
					&& sender.hasPermission("kgenerators." + spath[1])) {
				commands.add(spath[1]);
			}
		}
		return commands;
	}
}
