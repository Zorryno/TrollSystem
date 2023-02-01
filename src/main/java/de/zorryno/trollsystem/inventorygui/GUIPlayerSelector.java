package de.zorryno.trollsystem.inventorygui;

import de.zorryno.trollsystem.inventorygui.events.PlayerSelectEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GUIPlayerSelector extends GUI implements Listener {

    public static GUIPlayerSelector.Builder builder(Plugin plugin) {
        return new GUIPlayerSelector.Builder(plugin);
    }

    public static final class Builder extends GUI.Builder {
        Builder(Plugin plugin) {
            super(plugin);
            this.plugin = plugin;
        }

        Plugin plugin;

        /**
         * Builds the GUI Inventory
         *
         * @return the finished GUI
         */
        @Override
        public GUIPlayerSelector build() {
            return new GUIPlayerSelector(name, rows, inventorySlots, interactionSlots, plugin);
        }
    }

    private Plugin plugin;

    protected GUIPlayerSelector(String name, int rows, Map<Integer, ItemStack> inventorySlots, List<Integer> interactionSlots, Plugin plugin) {
        super(name, rows, inventorySlots, interactionSlots, plugin);

        this.plugin = plugin;

        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this::updatePlayers, 0, 20);
    }

    private void updatePlayers() {
        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
        ItemStack air = new ItemStack(Material.AIR);

        for (int i = 0; i < defaultInventoryRows * 9; i++) {
            if(i >= players.size()) {
                setItemGlobal(i, air);
                continue;
            }

            Player player = players.get(i);
            ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = ((SkullMeta) playerHead.getItemMeta());
            meta.setOwningPlayer(player);
            meta.setDisplayName(player.getName());
            playerHead.setItemMeta(meta);

            setItemGlobal(i, playerHead);
        }
    }

    //Event System
    @Override
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory gui = super.guiInventorys.get(event.getWhoClicked().getUniqueId());
        if (gui == null || gui != event.getInventory()) return;

        if(!defaultInteractionSlots.contains(event.getSlot()))
            event.setCancelled(true);

        if(gui != event.getClickedInventory()) return;


        Player player = ((Player) event.getWhoClicked());
        ItemStack item = gui.getItem(event.getSlot());
        if(item == null) return;
        OfflinePlayer selectedPlayer = ((SkullMeta) item.getItemMeta()).getOwningPlayer();

        if (selectedPlayer == null || selectedPlayer.getPlayer() == null)
            return;

        PlayerSelectEvent clickEvent = new PlayerSelectEvent(player, selectedPlayer.getPlayer());
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, clickEvent::callEvent);
    }
}
