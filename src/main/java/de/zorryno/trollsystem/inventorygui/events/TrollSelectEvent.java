package de.zorryno.trollsystem.inventorygui.events;

import de.zorryno.trollsystem.trolls.Troll;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TrollSelectEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private Player player;
    private Player targetPlayer;
    private Troll troll;

    public TrollSelectEvent(Player player, Player targetPlayer, Troll troll) {
        this.player = player;
        this.targetPlayer = targetPlayer;
        this.troll = troll;
    }

    public Player getPlayer() {
        return player;
    }

    public Player getTargetPlayer() {
        return targetPlayer;
    }

    public Troll getTroll() {
        return troll;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
