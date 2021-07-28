package com.drtshock.willie.command;

import com.drtshock.willie.Willie;
import org.pircbotx.Channel;
import org.pircbotx.Colors;
import org.pircbotx.User;

public class AdminCommandHandler implements CommandHandler {
    @Override
    public void handle(Willie bot, Channel channel, User sender, String[] args) {
        switch(args[0].toLowerCase()) {
            case "add":
                if(args.length != 2) return;
                else if(sender.getNick().toLowerCase().equals(args[1].toLowerCase())) {
                    channel.sendMessage(Colors.CYAN + "Huh?");
                    return;
                }
                else if(bot.getConfig().getAdmins().contains(args[1])) {
                    channel.sendMessage(Colors.GREEN + String.format("%s is already an admin.", args[1]));
                }
                else {
                    bot.getConfig().addAdmin(args[1]);
                    channel.sendMessage(Colors.GREEN + String.format("Added %s as an admin!", args[1]));
                    bot.sendMessage(args[1], Colors.GREEN + "You've been added as an admin!");

                }
                break;
            case "remove":
            case "del":
            case "delete":
                if(args.length != 2) return;
                else if(sender.getNick().toLowerCase().equals(args[1].toLowerCase())) {
                    channel.sendMessage(Colors.CYAN + "Huh?");
                    return;
                }
                else if(!bot.getConfig().getAdmins().contains(args[1])) {
                    channel.sendMessage(Colors.RED + String.format("%s is not an admin.", args[1]));
                }
                else {
                    bot.getConfig().removeAdmin(args[1]);
                    channel.sendMessage(Colors.RED + String.format("Removed %s from the admin list.", args[1]));
                    bot.sendMessage(args[1], Colors.RED + "Sorry, you're no longer an admin.");
                }
                break;
            case "list":
                channel.sendMessage("The admins are: " + bot.getConfig().getAdmins());
                break;
        }
    }
}
