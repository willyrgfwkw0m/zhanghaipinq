package com.drtshock.willie;

import com.drtshock.willie.command.Command;
import com.drtshock.willie.command.CommandManager;
import com.drtshock.willie.command.admin.*;
import com.drtshock.willie.command.fun.*;
import com.drtshock.willie.command.management.KickCommandHandler;
import com.drtshock.willie.command.minecraft.ServerCommandHandler;
import com.drtshock.willie.command.misc.DonateCommandHandler;
import com.drtshock.willie.command.misc.HelpCommandHandler;
import com.drtshock.willie.command.misc.PokeCommandHandler;
import com.drtshock.willie.command.misc.RulesCommandHandler;
import com.drtshock.willie.command.utility.*;
import com.drtshock.willie.jenkins.JenkinsServer;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import org.pircbotx.Channel;
import org.pircbotx.Colors;
import org.pircbotx.PircBotX;
import org.pircbotx.exception.IrcException;
import org.pircbotx.exception.NickAlreadyInUseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.logging.*;

public class Willie extends PircBotX {

    private static final Level LOGGING_LEVEL = Level.INFO;
    private static final Logger LOG = Logger.getLogger(Willie.class.getName());

    private static Willie instance;
    public static final Gson gson = new Gson();
    public static final JsonParser parser = new JsonParser();
    public static String GIT_AUTH;
    private static String CONFIG_FILE = "config.yml";
    public JenkinsServer jenkins;
    public CommandManager commandManager;
    private WillieConfig willieConfig;

    public static Willie getInstance() {
        return instance;
    }

    private Willie() {
        this(new WillieConfig());
    }

    private Willie(WillieConfig config) {
        super();

        try {
            // Get Root Logger
            final Logger rootLogger = Logger.getLogger("");

            // File handler
            final FileHandler handler1 = new FileHandler("Willie.log");
            handler1.setLevel(Level.ALL);

            // Console handler: re-use existing
            boolean originalConsoleHandler = false;
            ConsoleHandler handler2 = null;
            for (Handler h : rootLogger.getHandlers()) {
                if (h instanceof ConsoleHandler) {
                    handler2 = (ConsoleHandler) h;
                    originalConsoleHandler = true;
                    break;
                }
            }
            if (handler2 == null) {
                handler2 = new ConsoleHandler();
            }
            handler2.setLevel(LOGGING_LEVEL);

            // Fix FileHandler format, we don't care about the default XML shit
            handler1.setFormatter(handler2.getFormatter());

            // Register handlers
            rootLogger.addHandler(handler1);
            if (!originalConsoleHandler) {
                rootLogger.addHandler(handler2);
            }

            // Root Logger Level
            rootLogger.setLevel(LOGGING_LEVEL);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
            e.printStackTrace();
        }

        Willie.instance = this;

        LOG.info("Building Willie...");

        this.willieConfig = config;

        GIT_AUTH = "TOKEN " + willieConfig.getGitHubApiKey();

        this.jenkins = new JenkinsServer(willieConfig.getJenkinsServer());
        this.commandManager = new CommandManager(this);

        LOG.info("Registering commands...");
        this.commandManager.registerCommand(new Command("repo", "show Willie's repo", new RepoCommandHandler()));
        this.commandManager.registerCommand(new Command("latest", "<plugin_name> - Get latest file for plugin on BukkitDev", new LatestCommandHandler()));
        this.commandManager.registerCommand(new Command("plugin", "<name> - looks up a plugin on BukkitDev", new PluginCommandHandler()));
        this.commandManager.registerCommand(new Command("author", "<name> [amount] - looks up an author on BukkitDev", new AuthorCommandHandler()));
        this.commandManager.registerCommand(new Command("issues", "<job_name> [page] - check github issues for jobs on " + willieConfig.getJenkinsServer(), new IssuesCommandHandler()));
        this.commandManager.registerCommand(new Command("ci", "shows Jenkins info", new CICommandHandler()));
        this.commandManager.registerCommand(new Command("rules", "show channel rules", new RulesCommandHandler()));
        this.commandManager.registerCommand(new Command("help", "show this help info", new HelpCommandHandler()));
        this.commandManager.registerCommand(new Command("p", "pop some popcorn!", new PopcornCommandHandler()));
        this.commandManager.registerCommand(new Command("twss", "that's what she said!", new TWSSCommandHandler()));
        this.commandManager.registerCommand(new Command("donate", "shows donation info", new DonateCommandHandler()));
        this.commandManager.registerCommand(new Command("drink", "<name> - gives someone a drink!", new DrinkCommandHandler()));
        this.commandManager.registerCommand(new Command("fix", "[name] - Yell at someone to fix something", new FixCommandHandler()));
        this.commandManager.registerCommand(new Command("kick", "<name> - Kick a user", new KickCommandHandler()));
        this.commandManager.registerCommand(new Command("define", "<word|phrase> - defines a word", new DefineCommandHandler()));
        this.commandManager.registerCommand(new Command("urban", "<word|phrase> - defines a word using the urban dictionary", new UrbanCommandHandler()));
        this.commandManager.registerCommand(new Command("utime", "converts a unix timestamp to human time", new UTimeCommandHandler()));
        this.commandManager.registerCommand(new Command("shorten", "<url> shorten a url", new ShortenCommandHandler()));
        this.commandManager.registerCommand(new Command("server", "<IP> get a server's status", new ServerCommandHandler()));
        this.commandManager.registerCommand(new Command("whip", "<person> <reason> whips people", new WhipCommandHandler()));
        this.commandManager.registerCommand(new Command("poke", "<person> pokes people", new PokeCommandHandler()));
        this.commandManager.registerCommand(new Command("agree", "agree!", new AgreeDisagreeCommandHandler(true)));
        this.commandManager.registerCommand(new Command("disagree", "disagree!", new AgreeDisagreeCommandHandler(false)));

        this.commandManager.registerCommand(new Command("join", "<channel> - Joins a channel", new JoinCommandHandler(), true));
        this.commandManager.registerCommand(new Command("shutdown", "shuts the bot down", new ShutdownCommandHandler(), true));
        this.commandManager.registerCommand(new Command("leave", "<channel> - Leaves a channel", new LeaveCommandHandler(), true));
        this.commandManager.registerCommand(new Command("reload", "Reloads willie", new ReloadCommandHandler(), true));
        this.commandManager.registerCommand(new Command("save", "Saves configuration", new SaveCommandHandler(), true));
        this.commandManager.registerCommand(new Command("admin", "add <user> | del <user> | list - Modifies the bot admin list.", new AdminCommandHandler(), true));
        this.commandManager.registerCommand(new Command("prefix", "<prefix> changes command prefix for bot.", new PrefixCommandHandler(), true));

        this.setName(willieConfig.getNick());
        this.setVerbose(false);
        this.getListenerManager().addListener(this.commandManager);
    }

    public void connect() {
        LOG.info("Connecting...");
        try {
            this.connect(willieConfig.getServer(), this.willieConfig.getPort(), this.willieConfig.getPassword());
            this.setAutoReconnectChannels(true);
            LOG.log(Level.INFO, "Connected to ''{0}''", willieConfig.getServer());

            if (!willieConfig.getAccountPass().isEmpty()) {
                identify(willieConfig.getAccountPass());
            }

            for (String channel : willieConfig.getChannels()) {
                this.joinChannel(channel);
                LOG.log(Level.INFO, "Joined channel ''{0}''", channel);
            }

            //(new Timer()).schedule(new IssueNotifierTask(this), 300000, 300000); // 5 minutes
        } catch (NickAlreadyInUseException e) {
            LOG.severe("That nickname is already in use!");
        } catch (IrcException | IOException ignored) {
        }
        LOG.info("Connected!");
    }

    @Override
    public Channel getChannel(String string) {
        for (Channel channel : getUserBot().getChannels()) {
            if (channel.getName().equalsIgnoreCase(string)) {
                return channel;
            }
        }
        return super.getChannel(string);
    }

    public boolean isOnChannel(String channel) {
        for (Channel chan : getUserBot().getChannels()) {
            if (chan.getName().equalsIgnoreCase(channel)) {
                return true;
            }
        }
        return false;
    }

    public void reload() {
        LOG.info("Reloading...");
        willieConfig = WillieConfig.loadFromFile(CONFIG_FILE);

        // Update command prefix
        commandManager.setCommandPrefix(willieConfig.getCommandPrefix());

        // Nick
        if (!willieConfig.getNick().equals(getNick())) {
            changeNick(willieConfig.getNick());
            LOG.info("Nick updated.");
        }

        // Server
        if (!willieConfig.getServer().equals(getServer())) {
            for (Channel channel : getChannels()) {
                channel.sendMessage("Uh oh...looks like I'm on the wrong server.");
            }
            LOG.info("Bot seems to be on the wrong server! Reconnecting...");
            disconnect();
            connect();
        }

        // Channels
        ArrayList<String> newChannels = willieConfig.getChannels();
        Set<String> oldChannels = getChannelsNames();
        for (String channel : willieConfig.getChannels()) {
            if (!oldChannels.contains(channel)) {
                joinChannel(channel);
                LOG.log(Level.INFO, "Joined new channel {0}", channel);
                getChannel(channel).sendMessage(Colors.GREEN + "Someone told me I belong here.");
            }
        }
        for (String channel : oldChannels) {
            if (!newChannels.contains(channel)) {
                getChannel(channel).sendMessage(Colors.RED + "Looks like I don't belong here...");
                LOG.log(Level.INFO, "Leaving channel {0}", channel);
                partChannel(getChannel(channel));
            }
        }
        LOG.info("Reloaded!");
    }

    public void save() {
        LOG.info("Saving...");
        // Save channels
        willieConfig.getChannels().clear();
        for (Channel channel : getUserBot().getChannels()) {
            willieConfig.getChannels().add(channel.getName());
        }

        willieConfig.update();
        willieConfig.save(CONFIG_FILE);
        LOG.info("Saved!");
    }

    public WillieConfig getConfig() {
        return willieConfig;
    }

    public static void main(String[] args) {
        LOG.info("Starting Willie...");
        Willie willie = new Willie(WillieConfig.loadFromFile("config.yml"));
        willie.connect();
        LOG.info("Willie started!");
    }
}
