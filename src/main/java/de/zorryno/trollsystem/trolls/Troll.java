package de.zorryno.trollsystem.trolls;

import de.zorryno.trollsystem.Trollsystem;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.List;

public abstract class Troll {
    private Plugin plugin;
    private String name;
    private double price;
    private long duration;
    private ItemStack itemStack;

    public Troll(Plugin plugin, TrollSection section) {
        this(plugin, section.getName(), section.getLore(), section.getPrice(), section.getDuration(), section.getItem());
    }

    public Troll(Plugin plugin, String name, List<String> lore, double price, long durationTicks, ItemStack displayItem) {
        this.plugin = plugin;
        this.name = name;
        this.price = price;
        this.duration = durationTicks;
        this.itemStack = displayItem;

        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(name);
        itemMeta.setLore(lore);
        itemStack.setItemMeta( itemMeta);
    }

    public void register() {
        TrollHolder.getInstance().registerTroll(this);
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public long getDuration() {
        return duration;
    }

    public ItemStack getItem() {
        return itemStack;
    }

    public void trollPlayer(Player targetPlayer) {
        Player tPlayer = Bukkit.getPlayer(targetPlayer.getUniqueId());
        Trollsystem.setLastTrolled(targetPlayer.getUniqueId(), System.currentTimeMillis(), true);
        startTroll(tPlayer);
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, ()-> endTroll(tPlayer), getDuration());
    }

    public boolean withdraw(Player player) {
        if(!Trollsystem.getEconomy().has(player, getPrice()))
            return false;
        EconomyResponse economyResponse = Trollsystem.getEconomy().withdrawPlayer(player, getPrice());
        return economyResponse.transactionSuccess();
    }
    public abstract void startTroll(Player player);
    public abstract void endTroll(Player player);
}
