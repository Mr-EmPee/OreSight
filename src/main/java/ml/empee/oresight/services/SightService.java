package ml.empee.oresight.services;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
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
import java.util.Optional;
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
  @Getter @Setter
  @AllArgsConstructor
  public static class SightMeta {
    private final UUID holder;
    private final Sight sight;
    private LocalDateTime expireTime;
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

  public void reload() {
    sightEffectHolders.forEach(m ->
        Bukkit.getPluginManager().callEvent(SightEffectEndEvent.of(m.holder, m.sight, m.expireTime))
    );

    sightEffectHolders.clear();
    registeredSights.clear();

    sightsConfig.reload();
    sightsConfig.loadSights().forEach(this::registerSight);
  }

  public Optional<Sight> findById(String id) {
    return getAllSights().stream().filter(s -> s.getId().equals(id)).findFirst();
  }

  public List<SightMeta> getSightHolders() {
    refreshSightHolders();

    return Collections.unmodifiableList(sightEffectHolders);
  }

  public void clearSightEffectsTo(Player player) {
    sightEffectHolders.removeIf(m -> m.getHolder().equals(player.getUniqueId()));
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

  /**
   * Give a sight effect or update the expire time of an existing one
   */
  public void giveSightEffectTo(Player player, Sight sight, LocalDateTime expireTime) {
    Bukkit.getPluginManager().callEvent(SightEffectStartEvent.of(player, sight, expireTime));
    UUID uuid = player.getUniqueId();

    for (SightMeta meta : sightEffectHolders) {
      if (meta.getHolder().equals(uuid) && meta.getSight().equals(sight)) {
        meta.setExpireTime(expireTime);
        return;
      }
    }

    sightEffectHolders.add(new SightMeta(player.getUniqueId(), sight, expireTime));
  }

}
