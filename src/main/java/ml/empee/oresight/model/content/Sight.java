package ml.empee.oresight.model.content;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import ml.empee.oresight.utils.LocationUtils;
import ml.empee.oresight.utils.ProtocolUtils;
import ml.empee.oresight.utils.helpers.PluginItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Represent a sight
 */

@Builder
@AllArgsConstructor
public class Sight {

  private final Map<UUID, List<Integer>> sentBlocks = new HashMap<>();
  private List<Material> targetedBlocks;

  @Getter
  private PluginItem item;

  @Getter
  private Integer distance;

  @Getter
  private Duration duration;

  public List<Material> getTargetedBlocks() {
    return Collections.unmodifiableList(targetedBlocks);
  }

  private static int computeBlockId(Block block) {
    return (block.getX() + "X" + block.getY() + "Y" + block.getZ() + "Z").hashCode();
  }

  public void highlightBlocksNear(Player player, Location source) {
    LocationUtils.forEachBlockWithinRadius(
        source, distance, l -> highlightBlockTo(player, l.getBlock())
    );
  }

  public void highlightBlockTo(Player player, Block block) {
    if (!targetedBlocks.contains(block.getType())) {
      return;
    }

    int id = computeBlockId(block);
    ProtocolUtils.sendGlowingBlockEffect(player, id, block.getLocation());
    sentBlocks.computeIfAbsent(player.getUniqueId(), p -> new ArrayList<>()).add(id);
  }

  public void hideBlocks(Player player) {
    List<Integer> blocks = sentBlocks.get(player.getUniqueId());
    if (blocks == null) {
      return;
    }

    ProtocolUtils.removeEntities(player, blocks);
    sentBlocks.remove(player.getUniqueId());
  }

  public void hideBlockFrom(Player player, Block block) {
    if (!targetedBlocks.contains(block.getType())) {
      return;
    }

    Integer id = computeBlockId(block);
    ProtocolUtils.removeEntities(player, List.of(id));
    if (sentBlocks.containsKey(player.getUniqueId())) {
      sentBlocks.get(player.getUniqueId()).remove(id);
    }
  }

}
