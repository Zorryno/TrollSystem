package de.zorryno.trollsystem.nmsapi;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Particle;
import org.bukkit.craftbukkit.v1_19_R1.CraftParticle;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.Random;

public class PacketSender {

    public static void sendPacket(Player player, Packet packet) {
        assert player != null;
        assert packet != null;
        CraftPlayer craftPlayer = (CraftPlayer) player;
        ServerPlayer serverPlayer = craftPlayer.getHandle();
        serverPlayer.connection.send(packet);
    }

    /**
     * Sends the Demo Screen to the Player
     * @param player the receiving player
     * 0: Show welcome to demo screen
     */
    public static void sendDemoScreen(Player player) {
        assert player != null;
        ClientboundGameEventPacket packet = new ClientboundGameEventPacket(ClientboundGameEventPacket.DEMO_EVENT, 0);
        sendPacket(player, packet);
    }

    /**
     * Sends the Credits Screen to the Player
     * @param player the receiving player
     */
    public static void sendEndScreen(Player player) {
        assert player != null;
        ClientboundGameEventPacket packet = new ClientboundGameEventPacket(ClientboundGameEventPacket.WIN_GAME, 1);
        sendPacket(player, packet);
    }

    /**
     * Sends so many explosion Particles so the Client Crashes
     * @param player the receiving player
     */
    public static void sendParticleExplosion(Player player) {
        assert player != null;

        double x = player.getLocation().getX();
        double y = player.getLocation().getY();
        double z = player.getLocation().getZ();
        Random random = new Random();

        for(int i = 0; i < 100; i++) {
            ClientboundLevelParticlesPacket packet = new ClientboundLevelParticlesPacket(CraftParticle.toNMS(Particle.WATER_DROP), true, x + random.nextDouble(), y + random.nextDouble(), z + random.nextDouble(), 1, 1, 1, 10, 100);
            PacketSender.sendPacket(player, packet);
        }
    }

    public static void sendExplosion(Player player, double x, double y, double z) {
        assert player != null;

        ClientboundLevelParticlesPacket packet = new ClientboundLevelParticlesPacket(CraftParticle.toNMS(Particle.EXPLOSION_HUGE), true, x, y, z, 0, 0, 0, 1, 1);
        sendPacket(player, packet);
    }

}
