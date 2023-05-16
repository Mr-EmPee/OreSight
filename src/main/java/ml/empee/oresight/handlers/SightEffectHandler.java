package ml.empee.oresight.handlers;

import ml.empee.ioc.Bean;
import ml.empee.ioc.RegisteredListener;
import ml.empee.ioc.ScheduledTask;
import ml.empee.oresight.model.content.Sight;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Handler that keep tracks of who has the sight enabled
 */

//TODO: Milk remove sight effects
//TODO: Particles
public class SightEffectHandler extends ScheduledTask implements Bean, RegisteredListener {

  private final Map<UUID, List<SightMeta>> sightEffectHolders = new HashMap<>();

  private record SightMeta(LocalDateTime expireTime, Sight sight) {}

  public SightEffectHandler() {
    super(0, 20, false);
  }

  public void giveSightEffect(Player target, Sight sight, LocalDateTime expireTime) {
    sightEffectHolders.computeIfAbsent(target.getUniqueId(), u -> new ArrayList<>()).add(
        new SightMeta(expireTime, sight)
    );
  }

  @Override
  public void run() {
    LocalDateTime now = LocalDateTime.now();
    var iterator = sightEffectHolders.entrySet().iterator();
    while (iterator.hasNext()) {
      var entry = iterator.next();
      Player player = Bukkit.getPlayer(entry.getKey());
      List<SightMeta> playerSights = entry.getValue();
      var sightIterator = entry.getValue().iterator();
      while (sightIterator.hasNext()) {
        SightMeta meta = sightIterator.next();
        if (now.isAfter(meta.expireTime)) {
          sightIterator.remove();

          if (player != null) {
            meta.sight.hideBlocks(player);
          }
        } else if (player != null) {
          meta.sight.hideBlocks(player);
          meta.sight.highlightBlocksNear(player, player.getLocation());
        }
      }

      if (playerSights.isEmpty()) {
        iterator.remove();
      }
    }
  }

  @EventHandler(ignoreCancelled = true)
  public void onPlayerBreak(BlockBreakEvent event) {
    Player player = event.getPlayer();
    List<SightMeta> activatedSights = sightEffectHolders.get(player.getUniqueId());
    if (activatedSights == null || activatedSights.isEmpty()) {
      return;
    }

    for (SightMeta meta : activatedSights) {
      meta.sight.hideBlockFrom(player, event.getBlock());
    }
  }

}
