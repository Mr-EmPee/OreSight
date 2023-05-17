package ml.empee.oresight.config;

import ml.empee.ioc.Bean;
import ml.empee.itembuilder.ItemBuilder;
import ml.empee.oresight.model.content.Sight;
import ml.empee.oresight.utils.helpers.PluginItem;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Load all the available sights
 */

public class SightsConfig extends AbstractConfig implements Bean {

  private final JavaPlugin plugin;

  public SightsConfig(JavaPlugin plugin) {
    super(plugin, "sights.yml", 1);

    this.plugin = plugin;
  }

  @Override
  protected void update(int from) {
  }

  public List<Sight> loadSights() {
    List<Sight> sights = new ArrayList<>();

    for (String key : config.getKeys(false)) {
      sights.add(parseSight(key, config.getConfigurationSection(key)));
    }

    return sights;
  }

  private Sight parseSight(String id, ConfigurationSection config) {
    var sight = Sight.builder();

    sight.duration(Duration.of(config.getInt("duration", 60), ChronoUnit.SECONDS));
    sight.distance(config.getInt("max-distance", 15));
    sight.targetedBlocks(
        config.getStringList("targeted-blocks").stream().map(Material::valueOf).toList()
    );

    var item = ItemBuilder.potion(new ItemStack(Material.POTION));
    item.flags(ItemFlag.values());
    item.color(Color.fromRGB(Integer.parseInt(config.getString("color", "FFFF00"), 16)));
    item.setName(config.getString("display-name", "&eSight Potion"));
    item.setLore(config.getStringList("lore"));

    sight.item(new PluginItem(plugin, id, "1", item));
    return sight.build();
  }

  private String colorize(String input) {
    return ChatColor.translateAlternateColorCodes('&', input);
  }

}
