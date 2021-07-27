package com.drtshock.willie.command;

import com.drtshock.willie.Willie;
import org.pircbotx.Channel;
import org.pircbotx.Colors;
import org.pircbotx.User;

public class JoinCommandHandler implements CommandHandler {
    @Override
    public void handle(Willie bot, Channel channel, User sender, String[] args) {
        channel.sendMessage(String.format("See you in %s!", args[0]));
        bot.joinChannel(args[0]);
        bot.getChannel(args[0]).sendMessage(Colors.GREEN + String.format("%s told me I belong here!", sender.getNick()));
    }
}
