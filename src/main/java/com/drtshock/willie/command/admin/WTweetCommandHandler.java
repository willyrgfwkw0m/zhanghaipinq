package com.drtshock.willie.command.admin;

import com.drtshock.willie.Willie;
import com.drtshock.willie.command.CommandHandler;
import org.pircbotx.Channel;
import org.pircbotx.Colors;
import org.pircbotx.User;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

/**
 * @author stuntguy3000
 */
public class WTweetCommandHandler implements CommandHandler {

    @Override
    public void handle(Willie bot, Channel channel, User sender, String[] args) {
        if (args.length == 0) {
            channel.sendMessage(Colors.RED + "Please provide a message " + sender.getNick() + "! !wtweet <message>");
        } else {
            ConfigurationBuilder cb = new ConfigurationBuilder();
            cb.setDebugEnabled(true)
                    .setOAuthConsumerKey(bot.getConfig().getTwitterConsumerKey())
                    .setOAuthConsumerSecret(bot.getConfig().getTwitterConsumerKeySecret())
                    .setOAuthAccessToken(bot.getConfig().getTwitterAccessToken())
                    .setOAuthAccessTokenSecret(bot.getConfig().getTwitterAccessTokenSecret());
            TwitterFactory tf = new TwitterFactory(cb.build());
            Twitter twitter = tf.getInstance();

            try {
                StringBuilder status = new StringBuilder();
                for (String arg : args) {
                    status.append(arg + " ");
                }

                twitter.updateStatus(status.toString());
                channel.sendMessage(Colors.GREEN + sender.getNick() + " your message was tweeted!");
            } catch (TwitterException e) {
                e.printStackTrace();
                channel.sendMessage(Colors.RED + "Error occurred while updating tweeting!");
            }
        }

        channel.sendMessage(Colors.CYAN + "Check out Willie on Twitter! https://twitter.com/WillieIRC");

    }
}
