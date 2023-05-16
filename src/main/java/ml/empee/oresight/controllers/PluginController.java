package ml.empee.oresight.controllers;

import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BukkitConverters;
import com.comphenix.protocol.wrappers.WrappedDataValue;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import lombok.RequiredArgsConstructor;
import ml.empee.ioc.Bean;
import ml.empee.oresight.config.CommandsConfig;
import ml.empee.oresight.constants.Permissions;
import ml.empee.oresight.services.SightService;
import ml.empee.oresight.utils.Logger;
import ml.empee.oresight.utils.Translator;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.inventory.ItemStack;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

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

  @CommandMethod("os test")
  @CommandPermission(Permissions.ADMIN)
  public void test(Player sender) {
    sightService.giveSightEffectTo(
        sender, sightService.getAllSights().get(0), LocalDateTime.MAX
    );

    Logger.log(sender, "&7Sight given");
  }

  @CommandMethod("os reload")
  @CommandPermission(Permissions.ADMIN)
  public void reload(CommandSender sender) {
    Translator.reload();

    Logger.log(sender, "&7The plugin has been reloaded");
  }

}
