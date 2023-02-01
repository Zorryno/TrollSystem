package de.zorryno.trollsystem.inventorygui;

import de.zorryno.trollsystem.inventorygui.events.GUIClickEvent;
import de.zorryno.trollsystem.inventorygui.events.GUICloseEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class GUI implements Listener {

    public static GUI.Builder builder(Plugin plugin) {
        return new GUI.Builder(plugin);
    }

    public static class Builder {
        Builder(Plugin plugin) {
            this.plugin = plugin;
        }

        protected Map<Integer, ItemStack> inventorySlots = new HashMap<>();
        protected List<Integer> interactionSlots = new ArrayList<>();
        protected String name = "";
        protected int rows = 6;
        private Plugin plugin;

        /**
         * Allows the Interaction from a Player on this Inventory Slot
         * includes removing or placing items in this slot by players
         * @param slot the slot to allow Interactions
         * @return Builder
         */
        public Builder allowSlotInteraction(int slot) {
            interactionSlots.add(slot);
            return this;
        }

        /**
         * sets an Item as default in this GUI Inventory
         * @param slot the slot in the Inventory
         * @param item the Item to set
         * @return Builder
         */
        public Builder setItem(int slot, ItemStack item) {
            inventorySlots.put(slot, item);
            return this;
        }

        /**
         * sets the Name of the Inventory
         * @param inventoryName the Name for this Inventory
         * @return Builder
         */
        public Builder setInventoryName(String inventoryName) {
            name = inventoryName;
            if (name == null) {
                name = "";
            }
            return this;
        }

        /**
         * sets the inventory size in rows (1-6)
         * @param inventoryRows the number of rows
         * @return Builder
         */
        public Builder setRows(int inventoryRows) {
            rows = Math.min(Math.max(inventoryRows, 1), 6);
            return this;
        }

        /**
         * Builds the GUI Inventory
         * @return the finished GUI
         */
        public GUI build() {
            return new GUI(name, rows, inventorySlots, interactionSlots, plugin);
        }
    }


    protected Map<Integer, ItemStack> defaultInventorySlots;
    protected List<Integer> defaultInteractionSlots;
    protected String defaultInventoryName;
    protected int defaultInventoryRows;
    private Plugin plugin;

    protected HashMap<UUID, Inventory> guiInventorys = new HashMap<>();

    protected GUI(String name, int rows, Map<Integer, ItemStack> inventorySlots, List<Integer> interactionSlots, Plugin plugin) {
        defaultInventoryName = name;
        defaultInventoryRows = rows;
        defaultInventorySlots = inventorySlots;
        defaultInteractionSlots = interactionSlots;
        this.plugin = plugin;

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    /**
     * creates a new GUI Inventory
     * @param player the Player this GUI Inventory belongs to
     * @return the new Inventory
     */
    public Inventory createInventory(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 9 * defaultInventoryRows, defaultInventoryName);
        for(int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, defaultInventorySlots.get(i));
        }

        guiInventorys.put(player.getUniqueId(), inventory);
        return inventory;
    }

    /**
     * Opens a new clear GUI Inventory or the previous opened GUI Inventory
     * @param player the Player where the Inventory opens
     * @return the Inventory
     * @see GUI#openNewInventory(Player)
     */
    public Inventory openInventory(Player player) {
        Inventory inventory = guiInventorys.get(player.getUniqueId());

        if(inventory == null) {
            inventory = createInventory(player);
        }

        player.openInventory(inventory);
        return inventory;
    }

    /**
     * Opens a new clear GUI Inventory
     * @param player the player where the Inventory opens
     * @return the new Inventory
     * @see GUI#openInventory(Player)
     */
    public Inventory openNewInventory(Player player) {
        Inventory inventory = createInventory(player);
        player.openInventory(inventory);
        guiInventorys.put(player.getUniqueId(), inventory);
        return inventory;
    }

    public void setItemGlobal(int slot, ItemStack itemStack) {
        defaultInventorySlots.put(slot, itemStack);
        guiInventorys.forEach((uuid, inventorys) -> inventorys.setItem(slot, itemStack));
    }


    //Event System
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory gui = guiInventorys.get(event.getWhoClicked().getUniqueId());
        if(gui == null || gui != event.getInventory()) return;

        if(!defaultInteractionSlots.contains(event.getSlot()))
            event.setCancelled(true);

        if(gui != event.getClickedInventory()) return;

        Player player = (Player) event.getWhoClicked();
        GUIClickEvent clickEvent = new GUIClickEvent(player, this, gui, event.getSlot());
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, clickEvent::callEvent);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Inventory gui = guiInventorys.get(event.getPlayer().getUniqueId());
        if(gui == null || gui != event.getInventory()) return;

        Player player = ((Player) event.getPlayer());
        GUICloseEvent closeEvent = new GUICloseEvent(player, this, gui);
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, closeEvent::callEvent);
    }
}
