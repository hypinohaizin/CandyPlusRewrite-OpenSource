package me.hypinohaizin.candyplusrewrite.command.impl;

import java.util.ArrayList;
import me.hypinohaizin.candyplusrewrite.command.Command;
import me.hypinohaizin.candyplusrewrite.command.Command.Declaration;
import me.hypinohaizin.candyplusrewrite.command.CommandManager;
import me.hypinohaizin.candyplusrewrite.utils.ChatUtil;

/**
 * @author h_ypi
 * @since 2025/08/04 15:31
 */

@Declaration(
        name = "Commands",
        syntax = "commands",
        alias = {"commands","cmds","help"}
)
public class CommandsCommand extends Command {
    @Override
    public void onCommand(String label, String[] args, boolean clientSide) {
        ArrayList<Command> cmds = CommandManager.getCommands();
        StringBuilder sb = new StringBuilder("Commands: ");
        for (Command cmd : cmds) {
            sb.append(cmd.getName()).append(", ");
        }
        if (sb.length() > 11) {
            sb.setLength(sb.length() - 2);
        }
        ChatUtil.sendMessage(sb.toString());
    }
}
