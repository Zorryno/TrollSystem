package de.zorryno.trollsystem;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import de.zorryno.trollsystem.commands.TrollCommand;
import de.zorryno.trollsystem.inventorygui.GUIPlayerSelector;
import de.zorryno.trollsystem.inventorygui.GUITrollSelector;
import de.zorryno.trollsystem.listener.GUIListener;
import de.zorryno.trollsystem.trolls.Troll;
import de.zorryno.trollsystem.trolls.TrollCreator;
import de.zorryno.trollsystem.trolls.TrollHolder;
import de.zorryno.zorrynosystems.config.Messages;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Timestamp;
import java.util.*;

public final class Trollsystem extends JavaPlugin {

    private static GUIPlayerSelector playerSelector;
    private static GUITrollSelector trollSelector;
    private static Messages messages;
    private static Economy economy;
    private static Plugin plugin;
    private static HashMap<UUID, Long> lastTrolled = new HashMap<>();

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }


    private void fillLastTrolled() {
        Bukkit.getOnlinePlayers().forEach(player -> lastTrolled.put(player.getUniqueId(), 0L));
    }

    public static void setLastTrolled(UUID uuid, long time, boolean override) {
        if(!override && lastTrolled.containsKey(uuid)) return;
        lastTrolled.put(uuid, time);
    }

    @Override
    public void onEnable() {
        plugin = this;
        messages = new Messages("messages.yml", this);
        saveDefaultConfig();
        if (!setupEconomy() ) {
            getLogger().warning("Disabled due to no Vault dependency found!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        fillLastTrolled();
        new TrollCreator(this).create();
        createGUIs(this);

        Bukkit.getPluginManager().registerEvents(new GUIListener(), this);
        TrollCommand command = new TrollCommand();
        getCommand("troll").setExecutor(command);
        getCommand("troll").setTabCompleter(command);
    }

    @Override
    public void onDisable() {
        Bukkit.getOnlinePlayers().forEach(HumanEntity::closeInventory);
        HandlerList.unregisterAll(this);
        Bukkit.getScheduler().cancelTasks(this);
    }

    public static void reload() {
        plugin.reloadConfig();
        messages.reload();

        TrollHolder.getInstance().unregisterAll();
        new TrollCreator(plugin).create();
        createGUIs(plugin);

    }

    private static final String DEFAULTSKIN = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2M2MDNjNzk1NjAzMTk5OTZkNjM5NDEyOGI0OWZlYzc2NTBjZjg2N2ExZTQ4ZmI4MGM2MDQzZTc3MGRkNzFiZCJ9fX0=";

    private static void createGUIs(Plugin plugin) {
        GUIPlayerSelector.Builder playerSelectorBuilder = GUIPlayerSelector.builder(plugin);
        playerSelectorBuilder.setRows(6);
        playerSelectorBuilder.setInventoryName(messages.getCache().get("TrollInventory.PlayerMenue.Name"));
        playerSelector = playerSelectorBuilder.build();

        GUITrollSelector.Builder trollSelectorBuilder = GUITrollSelector.builder(plugin);
        trollSelectorBuilder.setRows(6);
        trollSelectorBuilder.setInventoryName(messages.getCache().get("TrollInventory.TrollMenue.Name"));
        Iterator<Integer> slots = List.of(0, 9, 18, 27, 36, 45, 1, 10, 19, 28, 37, 46, 2, 11, 20, 29, 38, 47, 3, 12, 21, 30, 39, 48).iterator();
        for (Troll troll : TrollHolder.getInstance().getTrolls()) {
            Integer slot = slots.next();
            trollSelectorBuilder.setItem(slot, troll.getItem());
        }
        Iterator<Troll> iterator = TrollHolder.getInstance().getRandomTrolls(5).iterator();
        List.of(5, 14, 24, 34, 35).forEach(integer -> trollSelectorBuilder.setItem(integer, iterator.next().getItem()));

        ItemStack randomItem = new ItemStack(Material.valueOf(plugin.getConfig().getString("RandomItem.Item", "NETHER_STAR")));
        ItemMeta itemMeta = randomItem.getItemMeta();
        itemMeta.setDisplayName(plugin.getConfig().getString("RandomItem.Name", " "));
        itemMeta.setLore(plugin.getConfig().getStringList("RandomItem.Lore"));
        randomItem.setItemMeta(itemMeta);
        trollSelectorBuilder.setItem(53, randomItem);

        trollSelectorBuilder.setItem(16, getCustomHead(plugin.getConfig().getString("PickItem.TopRightTexture")));
        trollSelectorBuilder.setItem(32, getCustomHead(plugin.getConfig().getString("PickItem.ButtomLeftTexture")));
        trollSelector = trollSelectorBuilder.build();
    }

    private static ItemStack getCustomHead(String texture) {
        String value = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2M2MDNjNzk1NjAzMTk5OTZkNjM5NDEyOGI0OWZlYzc2NTBjZjg2N2ExZTQ4ZmI4MGM2MDQzZTc3MGRkNzFiZCJ9fX0=";
        if (texture != null) value = texture;

        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = ((SkullMeta) head.getItemMeta());
        meta.setDisplayName(" ");
        PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID());
        profile.getProperties().add(new ProfileProperty("textures", value, DEFAULTSKIN));
        meta.setPlayerProfile(profile);
        head.setItemMeta(meta);

        return head;
    }

    public static GUIPlayerSelector getPlayerSelector() {
        return playerSelector;
    }

    public static GUITrollSelector getTrollSelector() {
        return trollSelector;
    }

    public static Messages getMessages() {
        return messages;
    }

    public static Economy getEconomy() {
        return economy;
    }

    public static double getRandomTrollPrice() {
        return plugin.getConfig().getDouble("RandomPrice", 100);
    }

    public static long getTimeTillNextTroll(UUID uuid, long delay) {
        Timestamp timestamp = new Timestamp(lastTrolled.get(uuid) + delay);
        long timeTillNextTroll = timestamp.getTime() - System.currentTimeMillis();
        if(timeTillNextTroll < 0) return 0;
        return timeTillNextTroll;
    }

    private static List<UUID> getPremiumPlayers() {
        List<UUID> premiumPlayers = new ArrayList<>();
        plugin.getConfig().getStringList("TrollDelay.PremiumPlayers").forEach(uuid -> premiumPlayers.add(UUID.fromString(uuid)));
        return premiumPlayers;
    }

    public static long getTrollDelay(Player player) {
        if(Trollsystem.getPremiumPlayers().contains(player.getUniqueId()))
            return plugin.getConfig().getLong("TrollDelay.PremiumDelay", 60) * 1000;
        return plugin.getConfig().getLong("TrollDelay.NormalDelay", 120) * 1000;
    }
}
