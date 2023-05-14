package ml.empee.oresight.services;

import ml.empee.ioc.Bean;
import ml.empee.oresight.model.content.Sight;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Service that allows you to manage the available sights
 */

public class SightService implements Bean {

  private final List<Sight> registeredSights = new ArrayList<>();

  @Override
  public void onStart() {
    registerSight(
        Sight.builder()
            .targetedBlocks(List.of(Material.IRON_ORE))
            .sightDistance(15)
            .maxDetectableBlocks(10)
            .build()
    );
  }

  public void registerSight(Sight sight) {
    registeredSights.add(sight);
  }

  public List<Sight> getAllSights() {
    return Collections.unmodifiableList(registeredSights);
  }

}
