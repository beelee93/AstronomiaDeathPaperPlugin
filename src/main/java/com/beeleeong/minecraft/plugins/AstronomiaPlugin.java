package com.beeleeong.minecraft.plugins;

import java.util.ArrayList;
import java.util.List;
import com.beeleeong.minecraft.plugins.commands.CommandSaveLocation;
import com.beeleeong.minecraft.plugins.deathSoundPlayer.Astronomia;
import com.beeleeong.minecraft.plugins.deathSoundPlayer.EmotionalDamage;
import com.beeleeong.minecraft.plugins.deathSoundPlayer.SoundPlayer;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.plugin.java.JavaPlugin;

public class AstronomiaPlugin extends JavaPlugin implements Listener {
    @Override
    public void onEnable() {
        this.saveDefaultConfig();

        String version = this.getDescription().getVersion();
        getLogger().info(String.format("AstronomiaPlugin %s enabled", version));

        getServer().getPluginManager().registerEvents(this, this);

        getCommand(CommandSaveLocation.COMMAND_NAME)
                .setExecutor(new CommandSaveLocation(getLogger(), getConfig()));
    }

    @Override
    public void onDisable() {
        String version = this.getDescription().getVersion();
        getLogger().info(String.format("AstronomiaPlugin %s disabled", version));
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        getLogger().info("entity death");
        Entity ent = event.getEntity();
        EntityDamageEvent damageEvent = ent.getLastDamageCause();
        DamageCause cause = damageEvent.getCause();

        if (ent instanceof Player) {
            getLogger().info(
                    String.format("Player %s died because of %s", ent.getName(), cause.name()));

            SoundPlayer soundPlayer = null;
            if (cause == DamageCause.LAVA || cause == DamageCause.FIRE) {
                soundPlayer = new EmotionalDamage();
            } else {
                soundPlayer = new Astronomia();
            }

            List<Player> players = new ArrayList<Player>(Bukkit.getOnlinePlayers());
            for (Player player : players) {
                soundPlayer.playFor(player);
            }
        }
    }
}
