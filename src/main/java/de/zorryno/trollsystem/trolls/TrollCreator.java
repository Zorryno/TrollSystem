package de.zorryno.trollsystem.trolls;

import de.zorryno.trollsystem.nmsapi.PacketSender;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;
import org.bukkit.*;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class TrollCreator {
    private Plugin plugin;

    public TrollCreator(Plugin plugin) {
        this.plugin = plugin;
    }

    public void create() {
        createDemoTroll();
        createEndScreenTroll();
        createFakeCreeperTroll();
        createParticleTroll();
        createTNTHellTroll();
        createSpeedTroll();
        createSwapTroll();
        createInventoryRandomizerTroll();

        createCommandTrolls();
    }

    private void createDemoTroll() {
        new Troll(plugin, new TrollSection(plugin, "Trolls.Demo", 0)) {
            @Override
            public void startTroll(Player player) {
                PacketSender.sendDemoScreen(player);
            }

            @Override
            public void endTroll(Player player) {

            }
        }.register();
    }

    private void createEndScreenTroll() {
        new Troll(plugin, new TrollSection(plugin, "Trolls.End", 0)) {
            @Override
            public void startTroll(Player player) {
                PacketSender.sendEndScreen(player);
            }

            @Override
            public void endTroll(Player player) {

            }
        }.register();
    }

    private void createFakeCreeperTroll() {
        new Troll(plugin, new TrollSection(plugin, "Trolls.Creeper", 0)) {
            @Override
            public void startTroll(Player player) {
                Location location = player.getLocation().add(player.getLocation().getDirection().normalize().multiply(-1));
                player.playSound(location, Sound.ENTITY_CREEPER_PRIMED, SoundCategory.MASTER, 1, 1);
            }

            @Override
            public void endTroll(Player player) {

            }
        }.register();
    }

    private void createParticleTroll() {
        new Troll(plugin, new TrollSection(plugin, "Trolls.Partikel", 100)) {
            int taskID = 0;
            @Override
            public void startTroll(Player player) {
                taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, ()-> {
                    PacketSender.sendParticleExplosion(player);
                }, 0, 1);
            }

            @Override
            public void endTroll(Player player) {
                Bukkit.getScheduler().cancelTask(taskID);
            }
        }.register();
    }

    private void createTNTHellTroll() {
        new Troll(plugin, new TrollSection(plugin, "Trolls.TNT", 100)) {

            List<UUID> spawnedEntitys = new ArrayList<>();
            int id;
            @Override
            public void startTroll(Player player) {
                Random random = new Random();
                AtomicInteger i = new AtomicInteger();
                id = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, ()-> {

                    float pitch = player.getEyeLocation().getPitch();
                    float yaw = player.getEyeLocation().getYaw();
                    double x = player.getLocation().getX() + (random.nextInt(20) - 10);
                    double y = player.getLocation().getY() + (random.nextInt(20) - 10);
                    double z = player.getLocation().getZ() + (random.nextInt(20) - 10);
                    ClientboundAddEntityPacket packet = new ClientboundAddEntityPacket(i.get(), UUID.randomUUID(), x, y, z, pitch, yaw, EntityType.TNT, 0, new Vec3(0, 0, 0), 0);
                    Location TNTLocation = new Location(player.getWorld(), x, y, z);
                    player.playSound(TNTLocation, Sound.ENTITY_TNT_PRIMED, SoundCategory.MASTER, 1, 1);
                    PacketSender.sendPacket(player, packet);
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, ()-> {
                        PacketSender.sendExplosion(player, x, y, z);
                        player.playSound(TNTLocation, Sound.ENTITY_GENERIC_EXPLODE, SoundCategory.MASTER, 1, 1);
                    }, 60);
                    i.incrementAndGet();
                }, 0, 1);
            }

            @Override
            public void endTroll(Player player) {
                Bukkit.getScheduler().cancelTask(id);
            }
        }.register();
    }

    private void createSpeedTroll() {
        new Troll(plugin, new TrollSection(plugin, "Trolls.Speed", 0)) {
            @Override
            public void startTroll(Player player) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 50));
            }

            @Override
            public void endTroll(Player player) {

            }
        }.register();
    }

    private void createSwapTroll() {
        new Troll(plugin, new TrollSection(plugin, "Trolls.SwapItem", 0)) {

            @Override
            public void startTroll(Player player) {
                ItemStack mainHand = player.getEquipment().getItemInMainHand();
                player.getEquipment().setItemInMainHand(player.getEquipment().getItemInOffHand());
                player.getEquipment().setItemInOffHand(mainHand);
                player.updateInventory();
            }

            @Override
            public void endTroll(Player player) {

            }
        }.register();
    }

    private void createInventoryRandomizerTroll() {
        new Troll(plugin, new TrollSection(plugin, "Trolls.RandomizeInventory", 100)) {
            int taskID = 0;
            ItemStack[] contentsCopy;
            @Override
            public void startTroll(Player player) {
                contentsCopy = player.getInventory().getContents().clone();

                taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
                    ItemStack[] contents = player.getInventory().getContents();
                    List<ItemStack> itemStacks = Arrays.asList(contents);
                    Collections.shuffle(itemStacks);
                    itemStacks.toArray(contents);
                    player.getInventory().setContents(contents);
                }, 0, 1);
            }

            @Override
            public void endTroll(Player player) {
                Bukkit.getScheduler().cancelTask(taskID);
                player.getInventory().setContents(contentsCopy);
            }
        }.register();
    }

    private void createCommandTrolls() {
        ConfigurationSection commandTrolls = plugin.getConfig().getConfigurationSection("CommandTrolls");
        for(String key : commandTrolls.getKeys(false)) {
            new Troll(plugin, new TrollSection(plugin, "CommandTrolls." + key, Long.MIN_VALUE)) {
                @Override
                public void startTroll(Player player) {
                    List<String> startCommands = commandTrolls.getStringList(key + ".StartCommands");
                    ConsoleCommandSender consoleSender = Bukkit.getConsoleSender();
                    startCommands.forEach(command -> Bukkit.dispatchCommand(consoleSender, command.replace("%player%", player.getName())));
                }

                @Override
                public void endTroll(Player player) {
                    List<String> stopCommands = commandTrolls.getStringList(key + ".StopCommands");
                    ConsoleCommandSender consoleSender = Bukkit.getConsoleSender();
                    stopCommands.forEach(command -> Bukkit.dispatchCommand(consoleSender, command.replace("%player%", player.getName())));

                }
            }.register();
        }
    }
    //TODO Hölle auf Erden (alle trolls auf einmal)
}
