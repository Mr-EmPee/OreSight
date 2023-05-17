package ml.empee.oresight.controllers;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.parsers.Parser;
import cloud.commandframework.annotations.suggestions.Suggestions;
import cloud.commandframework.context.CommandContext;
import lombok.RequiredArgsConstructor;
import ml.empee.ioc.Bean;
import ml.empee.oresight.OreSight;
import ml.empee.oresight.config.CommandsConfig;
import ml.empee.oresight.constants.Permissions;
import ml.empee.oresight.model.content.Sight;
import ml.empee.oresight.services.SightService;
import ml.empee.oresight.utils.Logger;
import ml.empee.oresight.utils.Translator;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.Queue;

/**
 * Plugin related commands
 */

@RequiredArgsConstructor
public class PluginController implements Bean {

  private final CommandsConfig commandsConfig;
  private final SightService sightService;

  @Override
  public void onStart() {
    commandsConfig.register(this);
  }

  /**
   * Give a sight potion to the target
   */
  @CommandMethod("os give <type> [target]")
  @CommandPermission(Permissions.ADMIN)
  public void giveSightPotion(CommandSender sender, @Argument("type") Sight sight, @Argument Player target) {
    if (target == null) {
      if (sender instanceof Player) {
        target = (Player) sender;
      } else {
        Logger.log(sender, "&cMissing target player!");
        return;
      }
    }

    boolean success = target.getInventory().addItem(sight.getItem().build()).isEmpty();
    if (success) {
      Logger.log(sender, "&7The sight has been given");
    } else {
      Logger.log(sender, "&cThe target inventory was full");
    }
  }

  @CommandMethod("os reload")
  @CommandPermission(Permissions.ADMIN)
  public void reload(CommandSender sender) {
    Translator.reload();
    sightService.reload();
    Logger.log(sender, "&7The plugin has been reloaded");
  }

  /**
   * Parse a sight from a command input
   */
  @Parser(suggestions = "sightsProvider")
  public Sight parseSight(CommandContext<CommandSender> context, Queue<String> input) {
    String sightID = input.peek();
    if (sightID == null) {
      throw new IllegalArgumentException("Missing sight name");
    }

    Optional<Sight> sight = sightService.findById(sightID);
    if (sight.isPresent()) {
      input.remove();
      return sight.get();
    }

    throw new IllegalArgumentException("Unable to find the sight");
  }

  /**
   * Find all matchable sights based on a command input
   */
  @Suggestions("sightsProvider")
  public List<String> getSightsSuggestions(
      CommandContext<CommandSender> context, String input
  ) {
    return sightService.getAllSights().stream()
        .map(Sight::getId)
        .toList();
  }

}
