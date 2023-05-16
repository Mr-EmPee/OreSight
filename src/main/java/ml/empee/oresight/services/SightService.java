package ml.empee.oresight.services;

import lombok.RequiredArgsConstructor;
import ml.empee.ioc.Bean;
import ml.empee.oresight.config.SightsConfig;
import ml.empee.oresight.model.content.Sight;
import ml.empee.oresight.model.events.SightEffectEndEvent;
import ml.empee.oresight.model.events.SightEffectStartEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Service that allows you to manage the available sights
 */

@RequiredArgsConstructor
public class SightService implements Bean {

  private final List<SightMeta> sightEffectHolders = new ArrayList<>();
  private final List<Sight> registeredSights = new ArrayList<>();
  private final SightsConfig sightsConfig;

  /**
   * Contains metadata of an activated sight
   */
  public record SightMeta(UUID holder, Sight sight, LocalDateTime expireTime) {
  }

  @Override
  public void onStart() {
    sightsConfig.loadSights().forEach(this::registerSight);
  }

  public void registerSight(Sight sight) {
    registeredSights.add(sight);
  }

  public List<Sight> getAllSights() {
    return Collections.unmodifiableList(registeredSights);
  }

  public List<SightMeta> getSightHolders() {
    refreshSightHolders();

    return Collections.unmodifiableList(sightEffectHolders);
  }

  private void refreshSightHolders() {
    LocalDateTime now = LocalDateTime.now();
    sightEffectHolders.removeIf(m -> {
      if (now.isAfter(m.expireTime)) {
        Bukkit.getPluginManager().callEvent(SightEffectEndEvent.of(m.holder, m.sight, m.expireTime));
        return true;
      }

      return false;
    });
  }

  public void giveSightEffectTo(Player player, Sight sight, LocalDateTime expireTime) {
    Bukkit.getPluginManager().callEvent(SightEffectStartEvent.of(player, sight, expireTime));
    sightEffectHolders.add(new SightMeta(player.getUniqueId(), sight, expireTime));
  }

}
