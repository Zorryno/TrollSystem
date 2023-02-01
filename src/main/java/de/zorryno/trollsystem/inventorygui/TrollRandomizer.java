package de.zorryno.trollsystem.inventorygui;

import de.zorryno.trollsystem.Trollsystem;
import de.zorryno.trollsystem.trolls.Troll;
import de.zorryno.trollsystem.trolls.TrollHolder;
import de.zorryno.zorrynosystems.config.Messages;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.Iterator;
import java.util.List;

public class TrollRandomizer {

    private static final List<Integer> randomizerSlots = List.of(35, 34, 24, 14, 5);

    public static void randomize(Plugin plugin, Player player, Player targetPlayer, Inventory gui) {
        ItemStack startItem = gui.getItem(53);
        gui.setItem(53, new ItemStack(Material.AIR));
        int shifts = 25;
        long ticks = 3;
        Iterator<Troll> iterator = TrollHolder.getInstance().getRandomTrolls(shifts).iterator();

        for (int i = 0; i < shifts; i++) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                shift(gui, iterator.next());
                player.playSound(player, Sound.ENTITY_ARROW_HIT_PLAYER, SoundCategory.MASTER, 1, 1);
            }, i * ticks + i);


            if (i == (shifts - 1)) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                    Troll troll = TrollHolder.getInstance().getTroll(gui.getItem(24));
                    if(!targetPlayer.isOnline()) {
                        player.sendMessage(Trollsystem.getMessages().getCache().get("PlayerWentOffline"));
                        return;
                    }

                    if (!Trollsystem.getEconomy().has(player, Trollsystem.getRandomTrollPrice()) ||
                            !Trollsystem.getEconomy().withdrawPlayer(player, Trollsystem.getRandomTrollPrice()).transactionSuccess()) {
                            player.sendMessage(Trollsystem.getMessages().getCache().get("NotEnoughMoney"));
                        return;
                    }
                    troll.trollPlayer(targetPlayer);
                    targetPlayer.sendMessage(Trollsystem.getMessages().getCache().get("TrolledBy").replace("%player%", player.getName()));

                    player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, SoundCategory.MASTER, 1, 1);
                    gui.setItem(53, startItem);
                }, (i + 1) * ticks + i);
            }
        }

    }

    private static void shift(Inventory gui, Troll newTroll) {
        Iterator<Integer> iterator = randomizerSlots.iterator();
        int slot = iterator.next();

        while (iterator.hasNext()) {
            Integer next = iterator.next();
            gui.setItem(slot, gui.getItem(next));
            slot = next;
        }

        gui.setItem(slot, newTroll.getItem());
    }
}
