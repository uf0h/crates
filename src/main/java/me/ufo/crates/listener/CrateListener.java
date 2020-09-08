package me.ufo.crates.listener;

import me.ufo.crates.crate.Crate;
import me.ufo.crates.CratesPlugin;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public final class CrateListener implements Listener {

  private final CratesPlugin plugin;

  public CrateListener(final CratesPlugin plugin) {
    this.plugin = plugin;
  }

  @EventHandler
  public void onInventoryClick(final InventoryClickEvent event) {
    if (event.getClickedInventory() == null) {
      return;
    }

    for (final Crate crate : plugin.getCrates()) {
      if (event.getClickedInventory().getName().equalsIgnoreCase(crate.getInventory().getName())) {
        event.setCancelled(true);
        return;
      }
    }
  }

  @EventHandler
  public void onPlayerInteract(final PlayerInteractEvent event) {
    if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
      for (final Crate crate : plugin.getCrates()) {
        if (crate.getLocation() == null) {
          continue;
        }

        if (event.getClickedBlock().getLocation().equals(crate.getLocation())) {
          event.setCancelled(true);
          event.getPlayer().openInventory(crate.getInventory());
          return;
        }
      }
    } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
      for (final Crate crate : plugin.getCrates()) {
        if (crate.getLocation() == null) {
          continue;
        }

        if (event.getClickedBlock().getLocation().equals(crate.getLocation())) {
          event.setCancelled(true);

          final ItemStack itemHeld = event.getItem();
          if (itemHeld == null) {
            event.getPlayer()
              .sendMessage(ChatColor.RED.toString() + "You are not holding a key for this crate.");
            return;
          }

          if (itemHeld.getType() == Material.TRIPWIRE_HOOK) {
            if (itemHeld.getAmount() == 1) {
              event.getPlayer().setItemInHand(null);
            } else {
              itemHeld.setAmount(itemHeld.getAmount() - 1);
            }
            crate.open(event.getPlayer());
          }
          return;
        }
      }

    }
  }

}
