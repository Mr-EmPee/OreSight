package ml.empee.oresight.services;

import lombok.RequiredArgsConstructor;
import ml.empee.ioc.Bean;
import ml.empee.oresight.handlers.SightEffectHandler;
import ml.empee.oresight.model.content.Sight;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Service that allows you to manage the available sights
 */

@RequiredArgsConstructor
public class SightService implements Bean {

  private final List<Sight> registeredSights = new ArrayList<>();
  private final SightEffectHandler sightEffectHandler;

  @Override
  public void onStart() {
    registerSight(
        Sight.builder()
            .targetedBlocks(List.of(Material.IRON_ORE))
            .distance(15)
            .build()
    );
  }

  public void registerSight(Sight sight) {
    registeredSights.add(sight);
  }

  public List<Sight> getAllSights() {
    return Collections.unmodifiableList(registeredSights);
  }

  public void giveSightEffectTo(Player player, Sight sight, LocalDateTime expireTime) {
    sightEffectHandler.giveSightEffect(player, sight, expireTime);
  }

}
