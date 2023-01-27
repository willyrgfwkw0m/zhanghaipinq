package com.drtshock.willie.command.admin;

import com.drtshock.willie.Willie;
import com.drtshock.willie.command.CommandHandler;
import org.pircbotx.Channel;
import org.pircbotx.User;

public class BlacklistCommandHandler implements CommandHandler {

    @Override
    public void handle(Willie bot, Channel channel, User sender, String[] args) {
        StringBuilder sb = new StringBuilder();
        if (args.length > 1) {
            for (String s : args) {
                if (Willie.getInstance().getConfig().blacklistWord(s)) {
                    sb.append("+" + s + " ");
                } else {
                    sb.append("-" + s + " ");
                }
            }
            String message = sb.toString().trim();
            Willie.getInstance().save();
            channel.sendMessage("Updated word blacklist: " + message);
        } else {
            for (String s : Willie.getInstance().getConfig().getBlacklistedWords()) {
                sb.append(s + " ");
            }
            String list = sb.toString().trim();
            channel.sendMessage("Blacklisted words: " + list);
        }
    }
}
