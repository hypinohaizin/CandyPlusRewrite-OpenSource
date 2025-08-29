package me.hypinohaizin.candyplusrewrite.command.impl;

import me.hypinohaizin.candyplusrewrite.command.Command;
import me.hypinohaizin.candyplusrewrite.command.Command.Declaration;
import me.hypinohaizin.candyplusrewrite.command.CommandManager;
import me.hypinohaizin.candyplusrewrite.utils.ChatUtil;

/**
 * @author h_ypi
 * @since 2025/08/04 15:31
 */
@Declaration(name = "Prefix", syntax = "prefix <value> (no letters or numbers)", alias = {"prefix", "setprefix", "cmdprefix", "commandprefix"})
public class PrefixCommand extends Command {

    @Override
    public void onCommand(String label, String[] args, boolean clientSide) {
        if (args.length < 1) {
            ChatUtil.sendMessage(getSyntax());
            return;
        }
        String val = args[0];
        if (val.length() != 1 || Character.isLetterOrDigit(val.charAt(0))) {
            ChatUtil.sendMessage(getSyntax());
        } else {
            CommandManager.setCommandPrefix(val);
            ChatUtil.sendMessage("Prefix set: \"" + val + "\"!");
        }
    }
}
