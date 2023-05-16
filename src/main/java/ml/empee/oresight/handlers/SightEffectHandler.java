package ml.empee.oresight.handlers;

import ml.empee.ioc.Bean;
import ml.empee.ioc.RegisteredListener;
import ml.empee.ioc.ScheduledTask;
import ml.empee.oresight.model.events.SightEffectEndEvent;
import ml.empee.oresight.services.SightService;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.List;

/**
 * Handler that keep tracks of who has the sight enabled
 */

//TODO: Milk remove sight effects
//TODO: Particles
public class SightEffectHandler extends ScheduledTask implements Bean, RegisteredListener {

  private final SightService sightService;

  public SightEffectHandler(SightService sightService) {
    super(0, 20, false);
    this.sightService = sightService;
  }

  @Override
  public void run() {
    for (SightService.SightMeta meta : sightService.getSightHolders()) {
      Player player = Bukkit.getPlayer(meta.holder());
      if (player == null) {
        continue;
      }

      meta.sight().hideBlocks(player);
      meta.sight().highlightBlocksNear(player, player.getLocation());
    }
  }

  @EventHandler
  public void onSightEnd(SightEffectEndEvent event) {
    Player player = Bukkit.getPlayer(event.getPlayer());
    if (player == null) {
      return;
    }

    event.getSight().hideBlocks(player);
  }

  @EventHandler(ignoreCancelled = true)
  public void onPlayerBreak(BlockBreakEvent event) {
    Player player = event.getPlayer();
    List<SightService.SightMeta> activatedSights = sightService.getSightHolders().stream()
        .filter(h -> h.holder().equals(player.getUniqueId()))
        .toList();

    for (SightService.SightMeta meta : activatedSights) {
      meta.sight().hideBlockFrom(player, event.getBlock());
    }
  }

}
