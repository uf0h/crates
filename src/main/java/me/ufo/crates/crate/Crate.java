package me.ufo.crates.crate;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import lombok.Getter;
import lombok.Setter;
import me.ufo.crates.CratesPlugin;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public final class Crate implements InventoryHolder {

  @Getter private final String identifier;
  @Getter private final Inventory inventory;
  @Getter private final List<CrateItem> items;

  @Getter @Setter private Location location;

  public Crate(final String identifier, final Inventory inventory, final List<CrateItem> items) {
    this.identifier = identifier;
    this.inventory = inventory;
    this.items = items;
  }

  public void open(final Player player) {
    final int random = ThreadLocalRandom.current().nextInt(1, 100);

    player.sendMessage("chance: " + random);
    player.sendMessage("size: " + items.size());

    for (final CrateItem item : items) {
      player.sendMessage("item: " + item.getItemStack().getType() + ", chance: " + item.getChance());

      if (random <= item.getChance()) {
        CratesPlugin.getInstance().getServer().dispatchCommand(
          CratesPlugin.getInstance().getServer().getConsoleSender(),
          item.getCommand().replace("{player}", player.getName()));

        if (item.getMessage() != null && item.getMessage().length > 0) {
          player.sendMessage(item.getMessage());
        }
        break;
      }
    }
  }

}
