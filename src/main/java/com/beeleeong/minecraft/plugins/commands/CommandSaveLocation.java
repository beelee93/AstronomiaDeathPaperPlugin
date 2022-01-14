package com.beeleeong.minecraft.plugins.commands;

import java.util.logging.Logger;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class CommandSaveLocation implements CommandExecutor {
    public static final String COMMAND_NAME = "save-loc";

    private Logger logger;
    private CloseableHttpClient client;

    private String discordWebhook;
    private ConfigurationSection playerMapping;

    public CommandSaveLocation(Logger logger, Configuration config) {
        this.logger = logger;
        this.client = HttpClients.createDefault();

        this.discordWebhook = config.getString("webhook_url");
        this.playerMapping = config.getConfigurationSection("player_mapping");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            this.logger.warning("A non-player entity attempted to save location.");
            return false;
        }
        Player player = (Player) sender;
        String ID = player.getUniqueId().toString();
        String userID = playerMapping.getString(ID);

        if (userID == null || userID == "") {
            this.logger
                    .warning(String.format("Player %s [%s] is not mapped", ID, player.getName()));
            player.sendMessage("Your player ID has not been whitelisted for this feature.");
            return true;
        }

        String description = String.join(" ", args);
        Location loc = player.getLocation();
        String locString =
                String.format("%d %d %d", loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());

        this.logger.info(String.format("Player %s saves location %s", ID, locString));

        if (sendLocation(userID, locString, description)) {
            Bukkit.broadcastMessage(
                    String.format("%s saved location %s", player.getName(), locString));
        } else {
            player.sendMessage("Failed to save your location.");
        }
        return true;
    }

    private boolean sendLocation(String userID, String loc, String desc) {
        String content =
                String.format("{\"content\": \":bulb: <@%s> at **%s**\\n%s\"}", userID, loc, desc);

        HttpPost post = new HttpPost(this.discordWebhook);
        post.setHeader("Content-Type", "application/json");

        CloseableHttpResponse response = null;
        try {
            StringEntity entity = new StringEntity(content);
            post.setEntity(entity);

            this.logger.info("Sending content: " + content);
            response = this.client.execute(post);
        } catch (Exception ex) {
            this.logger.warning(
                    String.format("Error while sending message to discord: %s", ex.getMessage()));
        }

        if (response == null) {
            return false;
        }

        if (response.getStatusLine().getStatusCode() < 200
                || response.getStatusLine().getStatusCode() > 299) {
            this.logger.warning(String.format("%d response while sending message to discord",
                    response.getStatusLine().getStatusCode()));
            return false;
        }

        return true;
    }
}
