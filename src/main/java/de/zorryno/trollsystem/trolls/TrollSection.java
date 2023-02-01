package de.zorryno.trollsystem.trolls;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class TrollSection {
    private ConfigurationSection section;
    private long duration;

    public TrollSection(Plugin plugin, String path, long duration) {
        this.section = plugin.getConfig().getConfigurationSection(path);
        this.duration = duration;
    }

    public String getName() {
        return ChatColor.translateAlternateColorCodes('&', section.getString("Name", " "));
    }

    public List<String> getLore() {
        List<String> lore = new ArrayList<>();
        section.getStringList("Lore").forEach(string -> lore.add(ChatColor.translateAlternateColorCodes('&', string)));
        return lore;
    }

    public double getPrice() {
        return section.getDouble("Price", 0);
    }

    public long getDuration() {
        return duration == Long.MIN_VALUE ? section.getLong("Duration", 0) : duration;
    }

    public ItemStack getItem() {
        return new ItemStack(Material.valueOf(section.getString("Item", "BARRIER")));
    }
}
