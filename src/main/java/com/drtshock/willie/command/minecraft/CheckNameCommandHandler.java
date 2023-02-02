package com.drtshock.willie.command.minecraft;

import com.drtshock.willie.Willie;
import com.drtshock.willie.command.CommandHandler;
import org.pircbotx.Channel;
import org.pircbotx.Colors;
import org.pircbotx.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author stuntguy3000
 */
public class CheckNameCommandHandler implements CommandHandler {

    @Override
    public void handle(Willie bot, Channel channel, User sender, String[] args) throws Exception {
        if (args.length != 1) {
            channel.sendMessage("Check if a Minecraft username is available " + bot.getConfig().getCommandPrefix() + "checkname <username>");
            return;
        }

        String user = args[0];

        if (user.length() > 16) {
            user = user.substring(0, 16);
        }

        if (!user.matches("^[a-zA-Z0-9_]*$")) {
            channel.sendMessage("Only usernames with letters, numbers and underscores are valid!");
            return;
        }

        String result = getPage("https://account.minecraft.net/buy/frame/checkName/" + user);

        if (result.equalsIgnoreCase("OK")) {
            channel.sendMessage(Colors.GREEN + Colors.BOLD + user + Colors.NORMAL + Colors.GREEN + " is available!");
        } else if (result.equalsIgnoreCase("TAKEN")) {
            channel.sendMessage(Colors.RED + Colors.BOLD + user + Colors.NORMAL + Colors.RED + " is NOT available!");
        } else {
            throw new Exception("Page Output: " + result);
        }
    }

    private String getPage(String urlString) throws IOException {
        URL url = new URL(urlString);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setConnectTimeout(10000);
        connection.setReadTimeout(10000);
        connection.setUseCaches(false);

        BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream()));

        StringBuilder buffer = new StringBuilder();
        String line;

        while ((line = input.readLine()) != null) {
            buffer.append(line);
        }

        String page = buffer.toString();

        input.close();

        return page;
    }
}
