package ml.jordie.vatsimnotify;

import ml.jordie.vatsimnotify.storage.ConfigFile;
import ml.jordie.vatsimnotify.vatsim.ControllerManager;
import ml.jordie.vatsimnotify.vatsim.Parser;
import ml.jordie.vatsimnotify.vatsim.model.Controller;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.TextChannel;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Bot {

    private JDA api;
    public static Bot instance;
    // This is used to calculate time between start, and current (preventing spam from "newController" Alerts).
    public long startTime;
    // List of beginning of callsigns (ie MEM, JFK, EWR, if a callsign begins with an entry in this list and filtering is enabled, it will send a message).
    ArrayList<String> filteredCallsign = null;

    /**
     * @description Application entry method.
     * @param args
     */
    public static void main(String[] args) {
        new Bot().runBot();
    }

    public static Bot getInstance() {
        return instance;
    }

    /**
     * @description "run" method for the Bot. Should not be called more than once, as this is the method that
     *              logs the Bot into Discord.
     */
    private void runBot() {
        try {
            instance = this;
            new ConfigFile();

            if (ConfigFile.getInstance().getProperty("FILTER_POSITIONS").equalsIgnoreCase("yes")) {
                filteredCallsign = new ArrayList<>();
                String[] splitPositions = ConfigFile.getInstance().getProperty("POSITIONS").split(",");
                filteredCallsign.addAll(Arrays.asList(splitPositions));
            }

            api = new JDABuilder(AccountType.BOT).setToken(ConfigFile.getInstance().getProperty("DISCORD_TOKEN")).build().awaitReady();
            api.getPresence().setGame(Game.watching("for new controllers!"));

            api.setAutoReconnect(true);

            startTime = System.currentTimeMillis();

            new ControllerManager();

            refreshData();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     * @description TimerTask used to call the Parser.parse method every 2 minutes (when Vatsim's Data Server updates).
     *              eventually I will improve this.
     */
    public void refreshData() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    System.out.println("Running Sync!");
                    new Parser().parse();
                    ControllerManager.getInstance().purgeControllers();

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }, 0, 1000 * 60 * 2);
    }

    /**
     * @description Method used to send login notifications. This will also filter out callsigns if that is enabled.
     * @param c Controller that has gone online/offline.
     * @param offlineNotification boolean used to indicate wether this is an online controller, or offline controller notification (t = online|f = offline).
     */
    public void newControllerAlert(Controller c, boolean offlineNotification) {
        if (System.currentTimeMillis() - startTime > 10000) {
            if (filteredCallsign != null) {
                for (String s : filteredCallsign)
                    if (c.getCallsign().startsWith(s))
                        if(offlineNotification)
                            sendLogoutNotification(c);
                        else
                            sendNotification(c);
            } else
                if(offlineNotification)
                    sendLogoutNotification(c);
                else
                    sendNotification(c);
        }
    }

    /**
     * @description Send an online controller notification to the designated channel.
     * @param c Controler who has gone online.
     */
    private void sendNotification(Controller c) {
        try {
            TextChannel notifyChannel = api.getTextChannelById(ConfigFile.getInstance().getProperty("TEXT_CHANNEL"));
            if (notifyChannel != null) {
                EmbedBuilder eb = new EmbedBuilder();
                eb.setTitle("" + c.getCallsign());
                eb.setDescription(ConfigFile.getInstance().getProperty("POSITION_OPEN_NOTIFICATION")
                        .replace("%name%", c.getRealName())
                        .replace("%position%", c.getCallsign())
                        .replace("%frequency%", c.getFrequency()));
                eb.setFooter("VATSIM Notify", api.getSelfUser().getEffectiveAvatarUrl());
                eb.setColor(Color.GREEN);
                notifyChannel.sendMessage(eb.build()).queue();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     * @description Send an offline controller notification to the designated channel.
     * @param c Controller who has gone offline.
     */
    private void sendLogoutNotification(Controller c){
        try {
            TextChannel notifyChannel = api.getTextChannelById(ConfigFile.getInstance().getProperty("TEXT_CHANNEL"));
            if (ConfigFile.getInstance().getProperty("CLOSING_NOTIFICATIONS").equalsIgnoreCase("yes")) {
                EmbedBuilder eb = new EmbedBuilder();
                eb.setTitle("" + c.getCallsign());
                eb.setDescription(ConfigFile.getInstance().getProperty("POSITION_CLOSED_NOTIFICATION")
                        .replace("%name%", c.getRealName())
                        .replace("%position%", c.getCallsign())
                        .replace("%frequency%", c.getFrequency()));
                eb.setFooter("VATSIM Notify", api.getSelfUser().getEffectiveAvatarUrl());
                eb.setColor(Color.RED);
                notifyChannel.sendMessage(eb.build()).queue();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(-1);
        }
    }


}
