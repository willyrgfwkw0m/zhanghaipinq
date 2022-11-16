package com.drtshock.willie.command.fun;

import java.util.ArrayList;
import java.util.Random;

import org.pircbotx.Channel;
import org.pircbotx.User;

import com.drtshock.willie.Willie;
import com.drtshock.willie.command.CommandHandler;

public class PopcornCommandHandler implements CommandHandler {

	private Random rand;
	private ArrayList<String> messages;

	public PopcornCommandHandler() {
		this.rand = new Random();
		this.messages = new ArrayList<String>();

		this.messages.add("pops some plain popcorn.");
		this.messages.add("pops some popcorn with butter.");
		this.messages.add("pops some popcorn with salt and butter!");
		this.messages.add("pops some kettle corn.");
	}

	@Override
	public void handle(Willie bot, Channel channel, User sender, String[] args) {
		if (channel.getName().equalsIgnoreCase("#hawkfalcon")) {
			return; // TODO: Add better way to disable commands per channel.
		}
		bot.sendAction(channel, this.messages.get(this.rand.nextInt(this.messages.size())));
	}
}
