package com.drtshock.willie.command;

import com.drtshock.willie.Willie;
import org.pircbotx.Channel;
import org.pircbotx.Colors;
import org.pircbotx.User;

public class RulesCommandHandler implements CommandHandler {

    @Override
    public void handle(Willie bot, Channel channel, User sender, String[] args) {
        channel.sendMessage(Colors.RED + "Don't be an annoying douche. Ask Chester if you have help");
    }

}
