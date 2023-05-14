package ml.empee.oresight.model.content;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import ml.empee.oresight.utils.LocationUtils;
import ml.empee.oresight.utils.ProtocolUtils;
import ml.empee.oresight.utils.helpers.PluginItem;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
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

  private PluginItem item;
  private List<Material> targetedBlocks;

  @Getter
  private Integer sightDistance;

  public ItemStack getSightItem() {
    return item.build();
  }

  public List<Material> getTargetedBlocks() {
    return Collections.unmodifiableList(targetedBlocks);
  }

  private static int computeBlockId(Block block) {
    return (block.getX() + "X" + block.getY() + "Y" + block.getZ() + "Z").hashCode();
  }

  public static void enableHighlightBlock(Player player, Block block) {
    ProtocolUtils.sendGlowingBlockEffect(player, computeBlockId(block));
  }

  public void hideTargetedBlocks(Player player) {
    List<Integer> sentBlocks = sightHolderSentBlocks.get(player.getUniqueId());
    if (sentBlocks != null) {
      ProtocolUtils.removeEntities(player, sentBlocks);
      sightHolderSentBlocks.remove(player.getUniqueId());
    }
  }

}
