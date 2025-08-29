package me.hypinohaizin.candyplusrewrite.command;

import me.hypinohaizin.candyplusrewrite.command.impl.*;
import me.hypinohaizin.candyplusrewrite.utils.ChatUtil;

import java.util.ArrayList;

/**
 * @author h_ypi
 * @since 2025/08/04 15:25
 */
public class CommandManager {
    private static String commandPrefix = "-";
    public static final ArrayList<Command> commands = new ArrayList<>();
    public static boolean isValidCommand = false;

    public static void init() {
        registerCommand(new PrefixCommand());
        registerCommand(new FriendCommand());
    }

    public static void registerCommand(final Command command) {
        commands.add(command);
    }

    public static ArrayList<Command> getCommands() {
        return commands;
    }

    public static String getCommandPrefix() {
        return commandPrefix;
    }

    public static void setCommandPrefix(final String prefix) {
        commandPrefix = prefix;
    }

    public static void callCommand(final String input, final boolean clientSide) {
        final String[] split = input.split(" (?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
        final String commandLabel = split[0];
        final String argsString = input.substring(commandLabel.length()).trim();
        isValidCommand = false;
        for (Command command : commands) {
            for (String alias : command.getAlias()) {
                if (alias.equalsIgnoreCase(commandLabel)) {
                    isValidCommand = true;
                    try {
                        command.onCommand(argsString, argsString.split(" (?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"), clientSide);
                    } catch (Exception e) {
                        ChatUtil.sendMessage(command.getSyntax());
                    }
                    break;
                }
            }
            if (isValidCommand) break;
        }
        if (!isValidCommand) {
            ChatUtil.sendMessage("Error! Invalid command!");
        }
    }
}