package ml.empee.oresight.controllers;

import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import lombok.RequiredArgsConstructor;
import ml.empee.ioc.Bean;
import ml.empee.oresight.config.CommandsConfig;
import ml.empee.oresight.constants.Permissions;
import ml.empee.oresight.utils.Logger;
import ml.empee.oresight.utils.Translator;
import org.bukkit.command.CommandSender;

/**
 * Plugin related commands
 */

@RequiredArgsConstructor
public class PluginController implements Bean {

  private final CommandsConfig commandsConfig;

  @Override
  public void onStart() {
    commandsConfig.register(this);
  }

  @CommandMethod("os reload")
  @CommandPermission(Permissions.ADMIN)
  public void reload(CommandSender sender) {
    Translator.reload();

    Logger.log(sender, "&7The plugin has been reloaded");
  }

}
