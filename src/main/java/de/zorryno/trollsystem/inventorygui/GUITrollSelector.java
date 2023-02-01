package de.zorryno.trollsystem.inventorygui;

import com.mysql.cj.util.TimeUtil;
import de.zorryno.trollsystem.Trollsystem;
import de.zorryno.trollsystem.inventorygui.events.TrollSelectEvent;
import de.zorryno.trollsystem.trolls.Troll;
import de.zorryno.trollsystem.trolls.TrollHolder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class GUITrollSelector extends GUI{

    public static GUITrollSelector.Builder builder(Plugin plugin) {
        return new GUITrollSelector.Builder(plugin);
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
        public GUITrollSelector build() {
            return new GUITrollSelector(name, rows, inventorySlots, interactionSlots, plugin);
        }
    }

    private Plugin plugin;

    protected GUITrollSelector(String name, int rows, Map<Integer, ItemStack> inventorySlots, List<Integer> interactionSlots, Plugin plugin) {
        super(name, rows, inventorySlots, interactionSlots, plugin);

        this.plugin = plugin;
    }

    public void setTarget(Player player, Player target) {
        ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = ((SkullMeta) playerHead.getItemMeta());
        meta.setOwningPlayer(target);
        meta.setDisplayName(target.getName());
        playerHead.setItemMeta(meta);
        guiInventorys.get(player.getUniqueId()).setItem(8, playerHead);
    }

    public OfflinePlayer getTarget(UUID uuid) {
        Inventory inventory = guiInventorys.get(uuid);
        if(inventory == null) return null;

        ItemStack item = inventory.getItem(8);
        if(item == null) return null;

        SkullMeta meta = (SkullMeta) item.getItemMeta();
        return meta.getOwningPlayer();
    }

    public void closeTrollInventorys(Player targetPlayer) {
        guiInventorys.forEach((uuid, inventory) -> {
            if(getTarget(uuid).getUniqueId() == targetPlayer.getUniqueId())
                inventory.getViewers().forEach(humanEntity -> humanEntity.sendMessage(Trollsystem.getMessages().getCache().get("PlayerWentOffline")));
                inventory.close();
        });
    }

    @Override
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory gui = super.guiInventorys.get(event.getWhoClicked().getUniqueId());
        if (gui == null || gui != event.getInventory()) return;

        if(!defaultInteractionSlots.contains(event.getSlot()))
            event.setCancelled(true);


        if(gui != event.getClickedInventory()) return;

        Player player = ((Player) event.getWhoClicked());
        Player targetPlayer = null;
        for(ItemStack head : gui.getContents()) {
            if(head == null) continue;
            if(!(head.getItemMeta() instanceof SkullMeta meta))
                continue;

            OfflinePlayer owningPlayer = meta.getOwningPlayer();
            if(owningPlayer == null || owningPlayer.getPlayer() == null) continue;
            targetPlayer = owningPlayer.getPlayer();
        }

        ItemStack item = gui.getItem(event.getSlot());
        if(item == null) return;

        List<Integer> slots = List.of(0, 9, 18, 27, 36, 45, 1, 10, 19, 28, 37, 46, 2, 11, 20, 29, 38, 47, 3, 12, 21, 30, 39, 48, 53);
        if(!slots.contains(event.getSlot()))
            return;

        long delay = Trollsystem.getTrollDelay(player);
        long timeTillNextTroll = Trollsystem.getTimeTillNextTroll(targetPlayer.getUniqueId(), delay);
        if(timeTillNextTroll > 0) {
            long convertMin = TimeUnit.MINUTES.convert(timeTillNextTroll, TimeUnit.MILLISECONDS);
            long convertSec = TimeUnit.SECONDS.convert(((timeTillNextTroll / 1000) % 60) * 1000, TimeUnit.MILLISECONDS);
            String message = Trollsystem.getMessages().getCache().get("TrollDelay").
                    replace("%timeMin%", convertMin + "").
                    replace("%timeSec%", convertSec + "");
            player.sendMessage(message);
            return;
        }


        if(event.getSlot() == 53) {
            if (Trollsystem.getEconomy().has(player, Trollsystem.getRandomTrollPrice())) {
                TrollRandomizer.randomize(plugin, player, targetPlayer, gui);
                player.sendMessage(Trollsystem.getMessages().getCache().get("WithdrawMoney").replace("%price%", Trollsystem.getRandomTrollPrice() + ""));
            } else {
                player.sendMessage(Trollsystem.getMessages().getCache().get("NotEnoughMoney"));
            }
            return;
        }

        Troll troll = TrollHolder.getInstance().getTroll(item);

        if(targetPlayer == null || troll == null) return;

        TrollSelectEvent clickEvent = new TrollSelectEvent(player, targetPlayer, troll);
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, clickEvent::callEvent);
    }

}
