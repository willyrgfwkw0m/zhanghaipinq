package com.drtshock.willie.command.misc;

import com.drtshock.willie.Willie;
import com.drtshock.willie.auth.Auth;
import com.drtshock.willie.command.Command;
import com.drtshock.willie.command.CommandHandler;
import org.pircbotx.Channel;
import org.pircbotx.User;

public class HelpCommandHandler implements CommandHandler {

    @Override
    public void handle(Willie bot, Channel channel, User sender, String[] args) {
        String cmdPrefix = bot.getConfig().getCommandPrefix();
        for (Command command : bot.commandManager.getCommands()) {
            if (command.isAdminOnly()) {
                if (Auth.checkAuth(sender)) {
                    sender.sendMessage(cmdPrefix + command.getName() + " - " + command.getHelp());
                }
            } else {
                sender.sendMessage(cmdPrefix + command.getName() + " - " + command.getHelp());
            }
        }
    }
}
