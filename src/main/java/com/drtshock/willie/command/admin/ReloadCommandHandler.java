package com.drtshock.willie.command.admin;

import com.drtshock.willie.Willie;
import com.drtshock.willie.command.CommandHandler;
import org.pircbotx.Channel;
import org.pircbotx.User;

/**
 * @author B2OJustin
 */
public class ReloadCommandHandler implements CommandHandler {

    @Override
    public void handle(Willie bot, Channel channel, User sender, String[] args) {
        channel.sendMessage(String.format("Yes master %s! Reloading!", sender.getNick()));
        bot.reload();
        channel.sendMessage("Configuration Reloaded.");
    }
}
