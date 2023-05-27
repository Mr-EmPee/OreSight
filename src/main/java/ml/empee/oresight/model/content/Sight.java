package ml.empee.oresight.model.content;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import ml.empee.oresight.utils.LocationUtils;
import ml.empee.oresight.utils.ProtocolUtils;
import ml.empee.oresight.utils.helpers.PluginItem;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

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
  private String id;

  @Getter
  private PluginItem item;

  @Getter
  private Integer distance;

  @Getter
  private Duration duration;

  @Getter
  private Color color;

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
    ProtocolUtils.sendGlowingBlockEffect(player, id, block.getLocation(), block.getBlockData());
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

  /**
   * Highlight <b>new</b> blocks that are in-range and hide the ones that are out of range
   */
  public void refreshHighlightedBlocksFor(Player player, Location lastLocation, Location currentLocation) {
    List<Block> lastSentBlocks = new ArrayList<>();

    LocationUtils.forEachBlockWithinRadius(lastLocation, distance, l -> {
      Block block = l.getBlock();
      if (targetedBlocks.contains(block.getType())) {
        lastSentBlocks.add(block);
      }
    });

    LocationUtils.forEachBlockWithinRadius(currentLocation, distance, l -> {
      Block block = l.getBlock();
      if (targetedBlocks.contains(block.getType())) {
        if (!lastSentBlocks.remove(block)) {
          highlightBlockTo(player, block);
        }
      }
    });

    hideBlocksFrom(player, lastSentBlocks);
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

  public void hideBlocksFrom(Player player, List<Block> block) {
    List<Integer> blockIds = block.stream()
        .filter(b -> targetedBlocks.contains(b.getType()))
        .map(Sight::computeBlockId)
        .toList();

    ProtocolUtils.removeEntities(player, blockIds);
    if (sentBlocks.containsKey(player.getUniqueId())) {
      sentBlocks.get(player.getUniqueId()).removeAll(blockIds);
    }
  }

}
