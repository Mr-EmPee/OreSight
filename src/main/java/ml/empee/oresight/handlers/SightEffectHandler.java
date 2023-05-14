package ml.empee.oresight.handlers;

import ml.empee.ioc.Bean;
import ml.empee.ioc.RegisteredListener;
import ml.empee.ioc.ScheduledTask;
import ml.empee.oresight.model.content.Sight;
import ml.empee.oresight.services.SightService;
import ml.empee.oresight.utils.ProtocolUtils;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.List;
import java.util.UUID;

/**
 * Handler that keep tracks of who has the sight enabled
 */

public class SightEffectHandler extends ScheduledTask implements Bean, RegisteredListener {

  private final SightService sightService;

  public SightEffectHandler(SightService sightService) {
    super(0, 20, false);

    this.sightService = sightService;
  }

  @Override
  public void run() {
    for (Sight sight : sightService.getAllSights()) {
      List<Player> sightHolders = sight.getSightHolders().stream().map(Bukkit::getPlayer).toList();
      for (Player player : sightHolders) {
        sight.hideTargetedBlocks(player);
        sight.showTargetedBlocks(player);
      }
    }
  }

  @EventHandler(ignoreCancelled = true)
  public void onPlayerBreak(BlockBreakEvent event) {
    Player player = event.getPlayer();
    for (Sight sight : sightService.getAllSights()) {
      if (sight.hasSightEnabled(player)) {
        Block block = event.getBlock();
        if (sight.getTargetedBlocks().contains(block.getType())) {
          ProtocolUtils.removeEntities(player, List.of((block.getX() + "X" + block.getY() + "Y" + block.getZ() + "Z").hashCode()));
        }
      }
    }
  }

}
