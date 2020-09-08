package me.ufo.crates.commands;

import java.util.Set;
import me.ufo.crates.crate.Crate;
import me.ufo.crates.CratesPlugin;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class CrateCommand implements CommandExecutor {

  private final CratesPlugin plugin;

  public CrateCommand(final CratesPlugin plugin) {
    this.plugin = plugin;
  }

  @Override
  public boolean onCommand(final CommandSender sender, final Command command, final String label,
                           final String[] args) {

    if (args.length == 0) {
      // Usage Message
      return false;
    }

    switch (args[0].toLowerCase()) {
      default:
        // Usage Message
        return false;

      case "gui": {
        if (args.length != 2) {
          // Usage: /crate gui <name>
          return false;
        }

        final Crate crate = plugin.getCrate(args[1]);
        if (crate == null) {
          sender.sendMessage("That is not a valid crate.");
          return false;
        }

        ((Player) sender).openInventory(crate.getInventory());
        return true;
      }

      case "open": {
        if (args.length != 2) {
          // Usage: /crate open <name>
          return false;
        }

        final Crate crate = plugin.getCrate(args[1]);
        if (crate == null) {
          sender.sendMessage("That is not a valid crate.");
          return false;
        }

        crate.open((Player) sender);
        return true;
      }

      case "set": {
        if (!(sender instanceof Player)) {
          return false;
        }
        if (args.length != 2) {
          // Usage: /crate open <name>
          return false;
        }

        final Crate crate = plugin.getCrate(args[1]);
        if (crate == null) {
          sender.sendMessage("That is not a valid crate.");
          return false;
        }

        if (crate.getLocation() != null) {
          sender.sendMessage(crate.getIdentifier() + " Crate already has a location set");
          return false;
        }

        plugin.setCrateLocation(crate,
                                ((Player) sender).getTargetBlock((Set<Material>) null, 5).getLocation());
        sender.sendMessage(crate.getIdentifier() + " Crate has been set.");
        return true;
      }

      case "unset": {
        if (args.length == 1) {
          if (sender instanceof Player) {
            final Player player = (Player) sender;
            final Location location = ((Player) sender).getTargetBlock((Set<Material>) null, 5).getLocation();
            for (final Crate crate : plugin.getCrates()) {
              if (location.equals(crate.getLocation())) {
                crate.setLocation(null);
                player.sendMessage(crate.getIdentifier() + " Crate location has been unset.");
                return true;
              }
            }
            player.sendMessage("That is not a valid crate.");
          }
          return false;
        }

        if (args.length == 2) {
          final Crate crate = plugin.getCrate(args[1]);
          if (crate == null) {
            sender.sendMessage("That is not a valid crate.");
            return false;
          }

          crate.setLocation(null);
          sender.sendMessage(crate.getIdentifier() + " Crate location has been unset.");
        }

        return true;
      }
    }
  }

}
