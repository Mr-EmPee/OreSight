package ml.empee.oresight.handlers;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import ml.empee.ioc.Bean;
import ml.empee.ioc.RegisteredListener;
import ml.empee.ioc.ScheduledTask;
import ml.empee.oresight.model.content.Sight;
import ml.empee.oresight.model.events.SightEffectEndEvent;
import ml.empee.oresight.model.events.SightEffectStartEvent;
import ml.empee.oresight.services.SightService;
import ml.empee.oresight.utils.LocationUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Handler that keep tracks of who has the sight enabled
 */

//TODO: Particles
public class SightEffectHandler extends ScheduledTask implements Bean, RegisteredListener {

  private final SightService sightService;
  private final Cache<UUID, Location> lastLocations = CacheBuilder.newBuilder()
      .expireAfterAccess(2, TimeUnit.SECONDS)
      .build();

  public SightEffectHandler(SightService sightService) {
    super(0, 20, false);
    this.sightService = sightService;
  }

  @Override
  public void run() {
    for (SightService.SightMeta meta : sightService.getSightHolders()) {
      Player player = Bukkit.getPlayer(meta.getHolder());
      if (player == null) {
        continue;
      }

      Location lastLocation = lastLocations.getIfPresent(player.getUniqueId());
      Location currentLocation = player.getLocation();

      if (lastLocation != null && !LocationUtils.isSameBlock(lastLocation, currentLocation)) {
        meta.getSight().refreshHighlightedBlocksFor(player, lastLocation, currentLocation);
      }

      lastLocations.put(player.getUniqueId(), currentLocation);
    }
  }

  @EventHandler
  public void onSightStart(SightEffectStartEvent event) {
    Player player = event.getPlayer();
    event.getSight().highlightBlocksNear(player, player.getLocation());
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
  public void onMilkConsume(PlayerItemConsumeEvent event) {
    if (event.getItem().getType() != Material.MILK_BUCKET) {
      return;
    }

    sightService.clearSightEffectsTo(event.getPlayer());
  }

  @EventHandler(ignoreCancelled = true)
  public void onPlayerBreak(BlockBreakEvent event) {
    List<SightService.SightMeta> activatedSights = sightService.getSightHolders();

    for (SightService.SightMeta meta : activatedSights) {
      Player player = Bukkit.getPlayer(meta.getHolder());
      if (player == null) {
        continue;
      }

      meta.getSight().hideBlockFrom(player, event.getBlock());
    }
  }

  @EventHandler(ignoreCancelled = true)
  public void onPlayerPlace(BlockPlaceEvent event) {
    List<SightService.SightMeta> activatedSights = sightService.getSightHolders();

    for (SightService.SightMeta meta : activatedSights) {
      Player player = Bukkit.getPlayer(meta.getHolder());
      if (player == null) {
        continue;
      }

      meta.getSight().highlightBlockTo(player, event.getBlock());
    }
  }

  @EventHandler(ignoreCancelled = true)
  public void onPotionConsume(PlayerItemConsumeEvent event) {
    List<Sight> sights = sightService.getAllSights();

    for (Sight sight : sights) {
      if (sight.getItem().isPluginItem(event.getItem())) {
        sightService.giveSightEffectTo(
            event.getPlayer(), sight, LocalDateTime.now().plus(sight.getDuration())
        );

        return;
      }
    }
  }

}
