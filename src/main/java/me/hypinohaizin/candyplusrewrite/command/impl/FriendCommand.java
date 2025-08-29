package me.hypinohaizin.candyplusrewrite.command.impl;

import me.hypinohaizin.candyplusrewrite.command.Command;
import me.hypinohaizin.candyplusrewrite.command.Command.Declaration;
import me.hypinohaizin.candyplusrewrite.managers.FriendManager;
import me.hypinohaizin.candyplusrewrite.utils.ChatUtil;

/**
 * @author h_ypi
 * @since 2025/08/04 15:31
 */

@Declaration(name = "Friend", syntax = "friend list/add/del [player]", alias = {"friend","friends","f"})
public class FriendCommand extends Command {
    @Override
    public void onCommand(String label, String[] args, boolean clientSide) {
        if (args.length < 1) {
            ChatUtil.sendMessage(getSyntax());
            return;
        }
        String action = args[0].toLowerCase();
        String msg;
        switch (action) {
            case "list":
                msg = "Friends: " + FriendManager.getFriendsByName() + ".";
                break;
            case "add":
                if (args.length < 2) {
                    msg = getSyntax();
                } else {
                    String name = args[1];
                    if (FriendManager.isFriend(name)) {
                        msg = name + " is already your friend.";
                    } else {
                        FriendManager.addFriend(name);
                        msg = "Added friend: " + name + ".";
                    }
                }
                break;
            case "del":
                if (args.length < 2) {
                    msg = getSyntax();
                } else {
                    String name = args[1];
                    if (FriendManager.isFriend(name)) {
                        FriendManager.delFriend(name);
                        msg = "Deleted friend: " + name + ".";
                    } else {
                        msg = name + " isn't your friend.";
                    }
                }
                break;
            default:
                msg = getSyntax();
        }
        ChatUtil.sendMessage(msg);
    }
}