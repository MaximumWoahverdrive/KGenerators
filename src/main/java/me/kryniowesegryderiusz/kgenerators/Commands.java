package me.kryniowesegryderiusz.kgenerators;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import me.kryniowesegryderiusz.kgenerators.addons.Addons;
import me.kryniowesegryderiusz.kgenerators.addons.objects.Addon;
import me.kryniowesegryderiusz.kgenerators.data.enums.DatabaseType;
import me.kryniowesegryderiusz.kgenerators.dependencies.hooks.WorldEditHook;
import me.kryniowesegryderiusz.kgenerators.generators.generator.objects.Generator;
import me.kryniowesegryderiusz.kgenerators.generators.generator.objects.GeneratorAction;
import me.kryniowesegryderiusz.kgenerators.generators.locations.handlers.enums.ActionType;
import me.kryniowesegryderiusz.kgenerators.generators.locations.handlers.enums.InteractionType;
import me.kryniowesegryderiusz.kgenerators.generators.locations.objects.GeneratorLocation;
import me.kryniowesegryderiusz.kgenerators.lang.Lang;
import me.kryniowesegryderiusz.kgenerators.lang.enums.Message;
import me.kryniowesegryderiusz.kgenerators.logger.Logger;
import me.kryniowesegryderiusz.kgenerators.utils.Sched;
import me.kryniowesegryderiusz.kgenerators.utils.Text;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;

public class Commands implements CommandExecutor {

    @SuppressWarnings("deprecation")
    @Override
    public boolean onCommand(
            final @NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String @NotNull [] args) {
        if (sender.hasPermission("kgenerators.commands") || sender instanceof ConsoleCommandSender) {

            if (args.length == 0) {
                Lang.getMessageStorage().sendHelp(sender);
                return false;
            }

            switch (args[0].toLowerCase()) {
                case "reload":
                    if (sender.hasPermission("kgenerators.reload") || sender instanceof ConsoleCommandSender) {
                        Main.getInstance().reload();
                        Lang.getMessageStorage().send(sender, Message.COMMANDS_RELOAD_DONE);
                    } else {
                        Lang.getMessageStorage()
                                .send(
                                        sender,
                                        Message.COMMANDS_RELOAD_NO_PERMISSION,
                                        "<permission>",
                                        "kgenerators.reload");
                    }
                    break;
                case "getall":
                    if (sender instanceof Player player) {
                        if (sender.hasPermission("kgenerators.getall")) {
                            for (Entry<String, Generator> generatorhmap :
                                    Main.getGenerators().getEntrySet()) {
                                Generator generator = generatorhmap.getValue();
                                player.getInventory().addItem(generator.getGeneratorItem());
                            }
                            Lang.getMessageStorage().send(sender, Message.COMMANDS_GET_ALL_RECIEVED);
                        } else {
                            Lang.getMessageStorage()
                                    .send(
                                            sender,
                                            Message.COMMANDS_GET_ALL_NO_PERMISSION,
                                            "<permission>",
                                            "kgenerators.getall");
                        }
                    } else {
                        Lang.getMessageStorage().send(sender, Message.COMMANDS_ANY_ONLY_PLAYER);
                    }
                    break;
                case "list":
                    if (sender.hasPermission("kgenerators.list") || sender instanceof ConsoleCommandSender) {
                        Lang.getMessageStorage().send(sender, Message.COMMANDS_LIST_HEADER);
                        for (Entry<String, Generator> e : Main.getGenerators().getEntrySet()) {
                            Lang.getMessageStorage()
                                    .send(
                                            sender,
                                            Message.COMMANDS_LIST_LIST,
                                            false,
                                            false,
                                            "<generator>",
                                            Text.name(e.getValue()
                                                    .getGeneratorItem()
                                                    .getItemMeta()),
                                            "<generatorID>",
                                            e.getKey());
                        }
                    } else {
                        Lang.getMessageStorage()
                                .send(sender, Message.COMMANDS_LIST_NO_PERMISSION, "<permission>", "kgenerators.list");
                    }
                    break;
                case "limits":
                    if (sender.hasPermission("kgenerators.limits") || sender instanceof ConsoleCommandSender) {
                        if (sender instanceof Player player) {
                            Main.getMenus().openLimitsMenu(player);
                        } else {
                            Lang.getMessageStorage().send(sender, Message.COMMANDS_ANY_ONLY_PLAYER);
                        }
                    } else {
                        Lang.getMessageStorage()
                                .send(
                                        sender,
                                        Message.COMMANDS_LIMITS_NO_PERMISSION,
                                        "<permission>",
                                        "kgenerators.limits");
                    }
                    break;
                case "actions":
                    if (sender.hasPermission("kgenerators.actions") || sender instanceof ConsoleCommandSender) {
                        Lang.getMessageStorage().send(sender, Message.COMMANDS_ACTIONS_HEADER);
                        for (Entry<ActionType, GeneratorAction> e :
                                Main.getSettings().getActions().getEntrySet()) {
                            if (e.getValue().getInteraction() != InteractionType.NONE) {
                                String action = "";
                                String mode = "";
                                String sneak = "";
                                String item = "";

                                if (e.getKey() == ActionType.PICKUP)
                                    action = Lang.getMessageStorage().get(Message.COMMANDS_ACTIONS_PICK_UP, false);
                                else if (e.getKey() == ActionType.OPENGUI)
                                    action = Lang.getMessageStorage().get(Message.COMMANDS_ACTIONS_OPEN_GUI, false);
                                else if (e.getKey() == ActionType.TIMELEFT)
                                    action = Lang.getMessageStorage().get(Message.COMMANDS_ACTIONS_TIME_LEFT, false);
                                else if (e.getKey() == ActionType.UPGRADE)
                                    action = Lang.getMessageStorage().get(Message.COMMANDS_ACTIONS_UPGRADE, false);

                                if (e.getValue().isSneak())
                                    sneak = Lang.getMessageStorage().get(Message.COMMANDS_ACTIONS_SNEAK, false);

                                if (e.getValue().getInteraction() == InteractionType.BREAK)
                                    mode = Lang.getMessageStorage().get(Message.COMMANDS_ACTIONS_BREAK, false);
                                else if (e.getValue().getInteraction() == InteractionType.LEFT_CLICK)
                                    mode = Lang.getMessageStorage().get(Message.COMMANDS_ACTIONS_LEFT_CLICK, false);
                                else if (e.getValue().getInteraction() == InteractionType.RIGHT_CLICK)
                                    mode = Lang.getMessageStorage().get(Message.COMMANDS_ACTIONS_RIGHT_CLICK, false);

                                if (e.getValue().getItem() != null) {
                                    item = Lang.getMessageStorage()
                                            .get(
                                                    Message.COMMANDS_ACTIONS_ITEM,
                                                    false,
                                                    "<itemname>",
                                                    Lang.getCustomNamesStorage()
                                                            .getItemTypeName(
                                                                    e.getValue().getItem()));
                                }

                                Lang.getMessageStorage()
                                        .send(
                                                sender,
                                                Message.COMMANDS_ACTIONS_LIST,
                                                "<action>",
                                                action,
                                                "<sneak>",
                                                sneak,
                                                "<mode>",
                                                mode,
                                                "<item>",
                                                item);
                            }
                        }
                    } else {
                        Lang.getMessageStorage()
                                .send(
                                        sender,
                                        Message.COMMANDS_ACTIONS_NO_PERMISSION,
                                        "<permission>",
                                        "kgenerators.actions");
                    }
                    break;
                case "give":
                    if (sender.hasPermission("kgenerators.give") || sender instanceof ConsoleCommandSender) {
                        if (args.length >= 3) {
                            Player player = Bukkit.getPlayer(args[1]);
                            if (player == null) {
                                Lang.getMessageStorage().send(sender, Message.COMMANDS_ANY_PLAYER_NOT_ONLINE);
                            } else {
                                String generatorID = args[2];
                                if (!Main.getGenerators().exists(generatorID)) {
                                    Lang.getMessageStorage().send(sender, Message.COMMANDS_ANY_GENERATOR_DOESNT_EXIST);
                                    break;
                                }

                                ItemStack item =
                                        Main.getGenerators().get(generatorID).getGeneratorItem();

                                int amount = 1;

                                if (args.length >= 4) {
                                    try {
                                        amount = Integer.parseInt(args[3]);
                                        if (amount < 1) throw new NumberFormatException();
                                        item.setAmount(amount);
                                    } catch (NumberFormatException e) {
                                        Lang.getMessageStorage()
                                                .send(
                                                        sender,
                                                        Message.COMMANDS_ANY_NOT_A_INTEGER,
                                                        "<variable>",
                                                        args[3]);
                                        return false;
                                    }
                                }

                                // The player's own thread (Folia), as this may run from the console
                                Sched.entity(player, () -> player.getInventory().addItem(item));

                                Lang.getMessageStorage()
                                        .send(
                                                sender,
                                                Message.COMMANDS_GIVE_GENERATOR_GIVEN,
                                                "<generator>",
                                                Text.name(item.getItemMeta()),
                                                "<player>",
                                                Text.legacy(player.displayName()),
                                                "<amount>",
                                                String.valueOf(amount));

                                Lang.getMessageStorage()
                                        .send(
                                                player,
                                                Message.COMMANDS_GIVE_GENERATOR_RECIEVED,
                                                "<generator>",
                                                Text.name(item.getItemMeta()),
                                                "<amount>",
                                                String.valueOf(amount));
                            }
                        } else {
                            Lang.getMessageStorage().send(sender, Message.COMMANDS_GIVE_USAGE);
                        }
                    } else {
                        Lang.getMessageStorage()
                                .send(sender, Message.COMMANDS_GIVE_NO_PERMISSION, "<permission>", "kgenerators.give");
                    }
                    break;
                case "debug":
                    if (sender.hasPermission("kgenerators.debug") || sender instanceof ConsoleCommandSender) {
                        Logger.debugPaste(sender);
                    } else {
                        Lang.getMessageStorage()
                                .send(
                                        sender,
                                        Message.COMMANDS_DEBUG_NO_PERMISSION,
                                        "<permission>",
                                        "kgenerators.debug");
                    }
                    break;
                case "timeleft":
                    if (sender instanceof Player p) {
                        if (sender.hasPermission("kgenerators.timeleft")) {
                            Location l = null;
                            if (p.getTargetBlockExact(5) != null)
                                l = p.getTargetBlockExact(5).getLocation();
                            if (l != null
                                    && Main.getPlacedGenerators().isLoaded(l)
                                    && Main.getSchedules()
                                                    .timeLeft(Main.getPlacedGenerators()
                                                            .getLoaded(l))
                                            >= 0) {
                                Lang.getMessageStorage()
                                        .send(
                                                sender,
                                                Message.GENERATORS_TIME_LEFT_OUTPUT,
                                                "<time>",
                                                Main.getSchedules()
                                                        .timeLeftFormatted(Main.getPlacedGenerators()
                                                                .getLoaded(l)));
                            } else Lang.getMessageStorage().send(sender, Message.COMMANDS_TIME_LEFT_NO_GENERATOR);
                        } else {
                            Lang.getMessageStorage()
                                    .send(
                                            sender,
                                            Message.COMMANDS_TIME_LEFT_NO_PERMISSION,
                                            "<permission>",
                                            "kgenerators.timeleft");
                        }
                    } else {
                        Lang.getMessageStorage().send(sender, Message.COMMANDS_ANY_ONLY_PLAYER);
                    }
                    break;
                case "upgrade":
                    if (sender instanceof Player p) {

                        if (Main.getUpgrades().getUpgrade(p.getItemInHand()) != null) {
                            Main.getUpgrades().getUpgrade(p.getItemInHand()).handUpgrade(p);
                        } else {
                            Lang.getMessageStorage().send(p, Message.UPGRADES_NO_NEXT_LEVEL);
                        }
                    } else {
                        Logger.textToConsole("Use that command as player!");
                    }
                    break;
                case "menu":
                    if (sender.hasPermission("kgenerators.menu") || sender instanceof ConsoleCommandSender) {
                        if (args.length == 1) {
                            if (sender instanceof Player player) Main.getMenus().openMainMenu(player);
                            else Lang.getMessageStorage().send(sender, Message.COMMANDS_ANY_ONLY_PLAYER);
                        } else if (sender.hasPermission("kgenerators.menu.others")) {

                            Player player = Bukkit.getPlayer(args[1]);

                            if (player != null) {
                                if (args.length == 2) {
                                    Main.getMenus().openMainMenu(player);
                                } else if (!args[2].equalsIgnoreCase("chances")
                                        && !args[2].equalsIgnoreCase("recipe")
                                        && !args[2].equalsIgnoreCase("upgrade")) {
                                    Lang.getMessageStorage().send(sender, Message.COMMANDS_ANY_MENU_DOESNT_EXIST);
                                } else if (args.length < 4
                                        || Main.getGenerators().get(args[3]) == null) {
                                    Lang.getMessageStorage().send(sender, Message.COMMANDS_ANY_GENERATOR_DOESNT_EXIST);
                                } else {
                                    Generator generator = Main.getGenerators().get(args[3]);

                                    if (args[2].equalsIgnoreCase("chances")) {
                                        Main.getMenus().openChancesMenu(player, generator);
                                        openedOthers(sender, player, generator, args[2]);
                                    } else if (args[2].equalsIgnoreCase("recipe")) {
                                        List<Recipe> recipe = Main.getInstance()
                                                .getServer()
                                                .getRecipesFor(generator.getGeneratorItem());
                                        if (!recipe.isEmpty()
                                                && recipe.get(0).getResult().equals(generator.getGeneratorItem())) {
                                            Main.getMenus().openRecipeMenu(player, generator);
                                            openedOthers(sender, player, generator, args[2]);
                                        } else
                                            Lang.getMessageStorage()
                                                    .send(sender, Message.COMMANDS_ANY_MENU_DOESNT_EXIST);
                                    } else if (args[2].equalsIgnoreCase("upgrade")) {
                                        if (Main.getUpgrades().couldBeObtained(generator.getId())) {
                                            Main.getMenus().openUpgradeMenu(player, generator);
                                            openedOthers(sender, player, generator, args[2]);
                                        } else
                                            Lang.getMessageStorage()
                                                    .send(sender, Message.COMMANDS_ANY_MENU_DOESNT_EXIST);
                                    }
                                }

                            } else {
                                Lang.getMessageStorage().send(sender, Message.COMMANDS_ANY_PLAYER_NOT_ONLINE);
                            }
                        } else {
                            Lang.getMessageStorage()
                                    .send(
                                            sender,
                                            Message.COMMANDS_MENU_NO_PERMISSION_OTHERS,
                                            "<permission>",
                                            "kgenerators.menu.others");
                        }
                    } else {
                        Lang.getMessageStorage()
                                .send(sender, Message.COMMANDS_MENU_NO_PERMISSION, "<permission>", "kgenerators.menu");
                    }
                    break;
                case "spawn":
                    if (!sender.hasPermission("kgenerators.spawn") && !(sender instanceof ConsoleCommandSender)) {
                        Lang.getMessageStorage()
                                .send(
                                        sender,
                                        Message.COMMANDS_SPAWN_NO_PERMISSION,
                                        "<permission>",
                                        "kgenerators.spawn");
                        return false;
                    }

                    if (args.length < 3) {
                        Lang.getMessageStorage().send(sender, Message.COMMANDS_SPAWN_USAGE_COORDINATES);
                        Lang.getMessageStorage().send(sender, Message.COMMANDS_SPAWN_USAGE_WORLDEDIT);
                        return false;
                    }

                    if (args[1].equalsIgnoreCase("worldedit")) {

                        if (!(sender instanceof Player selectingPlayer)) {
                            Lang.getMessageStorage().send(sender, Message.COMMANDS_ANY_ONLY_PLAYER);
                            return false;
                        }

                        if (!Main.getGenerators().exists(args[2])) {
                            Lang.getMessageStorage().send(sender, Message.COMMANDS_ANY_GENERATOR_DOESNT_EXIST);
                            return false;
                        }

                        ArrayList<Location> playerSelection = WorldEditHook.getPlayerSelection(selectingPlayer);

                        if (playerSelection == null || playerSelection.isEmpty()) {
                            Lang.getMessageStorage()
                                    .send(sender, Message.COMMANDS_SPAWN_DONE_WORLDEDIT, "<amount>", "0");
                            return false;
                        }

                        Generator weGenerator = Main.getGenerators().get(args[2]);
                        String weOwner = args.length >= 4 ? args[3] : null;
                        // Each block is placed on the thread owning its region (Folia); report once all are done
                        AtomicInteger placed = new AtomicInteger();
                        AtomicInteger remaining = new AtomicInteger(playerSelection.size());
                        for (Location l : playerSelection) {
                            Sched.atNow(l, () -> {
                                GeneratorLocation gl = new GeneratorLocation(
                                        -1,
                                        weGenerator,
                                        l,
                                        Main.getPlacedGenerators().new ChunkInfo(l.getChunk()),
                                        Main.getPlayers().getPlayer(weOwner),
                                        null);
                                if (gl.placeGenerator(sender, true)) placed.incrementAndGet();
                                if (remaining.decrementAndGet() == 0)
                                    Lang.getMessageStorage()
                                            .send(
                                                    sender,
                                                    Message.COMMANDS_SPAWN_DONE_WORLDEDIT,
                                                    "<amount>",
                                                    placed.get() + "");
                            });
                        }

                        return false;

                    } else {

                        if (args.length < 6) {
                            Lang.getMessageStorage().send(sender, Message.COMMANDS_SPAWN_USAGE_COORDINATES);
                            return false;
                        }

                        if (Bukkit.getWorld(args[1]) == null) {
                            Lang.getMessageStorage()
                                    .send(sender, Message.COMMANDS_ANY_WORLD_DOESNT_EXIST, "<world>", args[1]);
                            return false;
                        }

                        try {
                            Integer.valueOf(args[2]);
                            Integer.valueOf(args[3]);
                            Integer.valueOf(args[4]);

                            Generator g = Main.getGenerators().get(args[5]);

                            if (g != null) {
                                String owner = null;
                                if (args.length >= 7) owner = args[6];

                                Location l = Main.getPlacedGenerators()
                                        .stringToLocation(args[1] + "," + args[2] + "," + args[3] + "," + args[4]);
                                String coordOwner = owner;

                                // Placed on the thread owning the location's region (Folia)
                                Sched.atNow(l, () -> {
                                    GeneratorLocation gl = new GeneratorLocation(
                                            -1,
                                            g,
                                            l,
                                            Main.getPlacedGenerators().new ChunkInfo(l.getChunk()),
                                            Main.getPlayers().getPlayer(coordOwner),
                                            null);
                                    if (gl.placeGenerator(sender, true))
                                        Lang.getMessageStorage()
                                                .send(
                                                        sender,
                                                        Message.COMMANDS_SPAWN_DONE_COORDINATES,
                                                        "<location_info>",
                                                        gl.toString());
                                });

                            } else Lang.getMessageStorage().send(sender, Message.COMMANDS_ANY_GENERATOR_DOESNT_EXIST);

                        } catch (NumberFormatException e) {
                            Lang.getMessageStorage()
                                    .send(
                                            sender,
                                            Message.COMMANDS_ANY_NOT_A_INTEGER,
                                            "<variable>",
                                            args[2] + "," + args[3] + "," + args[4]);
                            return false;
                        }
                    }

                    break;
                case "remove":
                    if (!sender.hasPermission("kgenerators.remove")) {
                        Lang.getMessageStorage()
                                .send(
                                        sender,
                                        Message.COMMANDS_REMOVE_NO_PERMISSION,
                                        "<permission>",
                                        "kgenerators.remove");
                        return false;
                    }

                    if (args.length < 2) {
                        Lang.getMessageStorage().send(sender, Message.COMMANDS_REMOVE_USAGE_WORLDEDIT);
                        Lang.getMessageStorage().send(sender, Message.COMMANDS_REMOVE_USAGE_OWNER);
                        return false;
                    }

                    if (args[1].equalsIgnoreCase("worldedit")) {

                        if (!(sender instanceof Player p)) {
                            Lang.getMessageStorage().send(sender, Message.COMMANDS_ANY_ONLY_PLAYER);
                            return false;
                        }

                        ArrayList<GeneratorLocation> gls = WorldEditHook.getGeneratorsInRange(p);
                        if (gls != null) {
                            int amount = gls.size();
                            // Each on the thread owning its region (Folia)
                            for (GeneratorLocation gl : gls)
                                Sched.atNow(gl.getLocation(), () -> gl.removeGenerator(false, null));
                            Lang.getMessageStorage()
                                    .send(sender, Message.COMMANDS_REMOVE_DONE, "<amount>", String.valueOf(amount));
                        }
                    } else if (args[1].equalsIgnoreCase("owner")) {
                        if (args.length < 3) {
                            Lang.getMessageStorage().send(sender, Message.COMMANDS_REMOVE_USAGE_OWNER);
                            return false;
                        }

                        int amount = 0;
                        // Removals may run later on Folia, so skip database rows already removed as loaded ones
                        java.util.Set<Location> removed = new java.util.HashSet<>();

                        for (GeneratorLocation gl : Main.getPlacedGenerators().getAll()) {
                            if (gl.getOwner() != null && gl.getOwner().getName().equalsIgnoreCase(args[2])) {
                                removed.add(gl.getLocation());
                                Sched.atNow(gl.getLocation(), () -> gl.removeGenerator(false, null));
                                amount++;
                            }
                        }

                        ArrayList<GeneratorLocation> gls =
                                Main.getDatabases().getDb().getGenerators(args[2]);
                        if (gls != null) {
                            for (GeneratorLocation gl : gls) {
                                if (removed.contains(gl.getLocation())) continue;
                                Sched.atNow(gl.getLocation(), () -> gl.removeGenerator(false, null));
                                amount++;
                            }
                        }

                        Lang.getMessageStorage()
                                .send(sender, Message.COMMANDS_REMOVE_DONE, "<amount>", String.valueOf(amount));
                    }

                    break;
                case "convertdbto":
                    if (sender instanceof ConsoleCommandSender) {
                        if (args.length >= 2) {
                            DatabaseType newDbType = null;
                            for (DatabaseType type : new DatabaseType[] {DatabaseType.SQLITE, DatabaseType.MYSQL})
                                if (type.name().equalsIgnoreCase(args[1])) newDbType = type;

                            if (newDbType == null)
                                Logger.textToConsole("Unknown database type " + args[1] + "! Use SQLITE or MYSQL.");
                            else if (Main.getDatabases().isMigratorRunning())
                                Logger.textToConsole("A database conversion is already running!");
                            else if (newDbType != Main.getSettings().getDbType()) {
                                Main.getDatabases().changeTo(newDbType);
                            } else Logger.textToConsole("You already have this database type!");
                        } else Logger.textToConsole("You have to choose database you want to convert to!");
                    } else Lang.getMessageStorage().send(sender, Message.COMMANDS_ANY_ONLY_CONSOLE);
                    break;
                default:
                    for (Addon addon : Addons.getAddons()) {
                        if (addon.getCommands().containsKey(args[0].toLowerCase())) {
                            addon.getCommands().get(args[0].toLowerCase()).onCommand(sender, args);
                            return false;
                        }
                    }
                    Lang.getMessageStorage().send(sender, Message.COMMANDS_ANY_WRONG);
                    break;
            }
        } else
            Lang.getMessageStorage()
                    .send(sender, Message.COMMANDS_ANY_NO_PERMISSION, "<permission>", "kgenerators.commands");

        return false;
    }

    private static void openedOthers(CommandSender sender, Player player, Generator generator, String menu) {
        Lang.getMessageStorage()
                .send(
                        sender,
                        Message.COMMANDS_MENU_OPENED_OTHERS,
                        "<generator>",
                        generator.getGeneratorItemName(),
                        "<menu>",
                        menu.toLowerCase(java.util.Locale.ROOT),
                        "<player>",
                        player.getName());
    }
}
