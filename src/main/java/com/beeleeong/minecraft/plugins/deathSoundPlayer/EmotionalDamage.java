package com.beeleeong.minecraft.plugins.deathSoundPlayer;

import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class EmotionalDamage implements SoundPlayer {
    private final String MUSIC_NAME = "music.emotional_damage";

    @Override
    public void playFor(Player p) {
        World w = p.getWorld();
        w.playSound(p.getLocation(), MUSIC_NAME, SoundCategory.AMBIENT, 10, 1);
    }
}
