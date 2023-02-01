package de.zorryno.trollsystem.listener;

import de.zorryno.trollsystem.Trollsystem;
import de.zorryno.trollsystem.inventorygui.events.PlayerSelectEvent;
import de.zorryno.trollsystem.inventorygui.events.TrollSelectEvent;
import de.zorryno.trollsystem.trolls.Troll;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class GUIListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Trollsystem.getPlayerSelector().createInventory(event.getPlayer());
        Trollsystem.setLastTrolled(event.getPlayer().getUniqueId(), 0, false);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Trollsystem.getTrollSelector().closeTrollInventorys(event.getPlayer());
    }

    @EventHandler
    public void onGUISelection(PlayerSelectEvent event) {
        Trollsystem.getTrollSelector().openNewInventory(event.getPlayer());
        Trollsystem.getTrollSelector().setTarget(event.getPlayer(), event.getSelectedPlayer());
    }

    @EventHandler
    public void onTrollSelection(TrollSelectEvent event) {
        Troll troll = event.getTroll();
        if(!troll.withdraw(event.getPlayer())) {
            event.getPlayer().sendMessage(Trollsystem.getMessages().getCache().get("NotEnoughMoney"));
            return;
        }

        event.getPlayer().sendMessage(Trollsystem.getMessages().getCache().get("WithdrawMoney").replace("%price%", troll.getPrice() + ""));
        troll.trollPlayer(event.getTargetPlayer());
        event.getTargetPlayer().sendMessage(Trollsystem.getMessages().getCache().get("TrolledBy").replace("%player%", event.getPlayer().getName()));
    }
}
