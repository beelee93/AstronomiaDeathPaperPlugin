package com.beeleeong.minecraft.plugins;

import java.util.ArrayList;
import java.util.List;

import com.beeleeong.minecraft.plugins.deathSoundPlayer.Astronomia;
import com.beeleeong.minecraft.plugins.deathSoundPlayer.Ohnono;
import com.beeleeong.minecraft.plugins.deathSoundPlayer.SoundPlayer;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.plugin.java.JavaPlugin;

public class AstronomiaPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        String version = this.getDescription().getAPIVersion();
        getLogger().info(String.format("AstronomiaPlugin %s enabled", version));
    }

    @Override
    public void onDisable() {
        String version = this.getDescription().getAPIVersion();
        getLogger().info(String.format("AstronomiaPlugin %s disabled", version));
    }

    @EventHandler
    public void onEntityDeath(final EntityDeathEvent event) {
        Entity ent = event.getEntity();
        EntityDamageEvent damageEvent = ent.getLastDamageCause();
        DamageCause cause = damageEvent.getCause();

        if (ent instanceof Player) {
            SoundPlayer soundPlayer = null;
            if (cause == DamageCause.FALL) {
                soundPlayer = new Ohnono();
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
