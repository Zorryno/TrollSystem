package de.zorryno.trollsystem.inventorygui.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerSelectEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private Player player;
    private Player selectedPlayer;

    public PlayerSelectEvent(Player player, Player selectedPlayer) {
        this.player = player;
        this.selectedPlayer = selectedPlayer;
    }

    public Player getPlayer() {
        return player;
    }

    public Player getSelectedPlayer() {
        return selectedPlayer;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
