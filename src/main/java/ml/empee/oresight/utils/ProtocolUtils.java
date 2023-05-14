package ml.empee.oresight.utils;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataValue;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

/**
 * Sending per player packets
 */

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProtocolUtils {

  private static final ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();

  /**
   * Spawn an invisible glowing shulker to the player
   */
  public static void sendGlowingBlockEffect(Player target, Integer entityID, Location location) {
    var packet = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY);

    packet.getUUIDs().write(0, UUID.randomUUID());
    packet.getIntegers().write(0, entityID);
    packet.getEntityTypeModifier().write(0, EntityType.SHULKER);
    packet.getDoubles().write(0, location.getX());
    packet.getDoubles().write(1, location.getY());
    packet.getDoubles().write(2, location.getZ());

    var metadata = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);
    metadata.getIntegers().write(0, entityID);

    // 0x40 == Glowing
    // 0x20 == Invisibility
    Byte mask = 0x40 | 0x20;
    metadata.getDataValueCollectionModifier().write(0, List.of(
        new WrappedDataValue(0, WrappedDataWatcher.Registry.get(Byte.class), mask)
    ));

    protocolManager.sendServerPacket(target, packet);
    protocolManager.sendServerPacket(target, metadata);
  }

  public static void removeEntities(Player target, List<Integer> entitiesID) {
    var packet = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);
    packet.getIntLists().write(0, entitiesID);

    protocolManager.sendServerPacket(target, packet);
  }

}
