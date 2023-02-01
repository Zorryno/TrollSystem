package de.zorryno.trollsystem.inventorygui.events;

import de.zorryno.trollsystem.inventorygui.GUI;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;

public class GUIClickEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    public GUIClickEvent(Player player, GUI gui, Inventory inventory, int slot) {

    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
