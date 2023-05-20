package ml.empee.oresight;

import lombok.Getter;
import ml.empee.ioc.SimpleIoC;
import ml.empee.notifier.Notifier;
import ml.empee.oresight.utils.Logger;
import ml.empee.oresight.utils.Metrics;
import ml.empee.oresight.utils.Translator;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Boot class of this plugin.
 **/

public final class OreSight extends JavaPlugin {

  private static final String SPIGOT_PLUGIN_ID = "109956";
  private static final Integer METRICS_PLUGIN_ID = 18498;

  @Getter
  private final SimpleIoC iocContainer = new SimpleIoC(this);

  /**
   * Called when enabling the plugin
   */
  public void onEnable() {
    Translator.init(this);
    Logger.setPrefix(Translator.translate("prefix"));

    Metrics.of(this, METRICS_PLUGIN_ID);
    Notifier.listenForUpdates(this, SPIGOT_PLUGIN_ID);

    iocContainer.initialize("relocations");
  }

  public void onDisable() {
    iocContainer.removeAllBeans(true);
  }
}
