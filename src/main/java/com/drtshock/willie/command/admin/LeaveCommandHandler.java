package com.drtshock.willie.command.admin;

import com.drtshock.willie.Willie;
import com.drtshock.willie.command.CommandHandler;
import org.pircbotx.Channel;
import org.pircbotx.User;

public class LeaveCommandHandler implements CommandHandler {

    @Override
    public void handle(Willie bot, Channel channel, User sender, String[] args) {
        Channel leaveChannel = channel;
        if (bot.getUserBot().getChannels().size() == 1) {
            channel.sendMessage("Please don't make me leave!");
            return;
        }
        if (args.length >= 1) {
            if (!args[0].startsWith("#")) {
                args[0] = "#" + args[0];
            }
            leaveChannel = bot.getChannel(args[0]);

            if (!bot.isOnChannel(args[0])) {
                channel.sendMessage("Was I supposed to be in there?");
                return;
            } else if (channel != leaveChannel) {
                channel.sendMessage(String.format("Leaving channel %s", leaveChannel.getName()));
            }
        }
        if (!(args.length == 2 && args[1].equalsIgnoreCase("silent"))) {
            leaveChannel.sendMessage(String.format("%s says I don't belong here...", sender.getNick()));
        }
        bot.partChannel(leaveChannel);
    }
}
