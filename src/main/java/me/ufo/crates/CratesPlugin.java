package me.ufo.crates;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import me.ufo.crates.commands.CrateCommand;
import me.ufo.crates.crate.Crate;
import me.ufo.crates.crate.CrateItem;
import me.ufo.crates.file.CrateFile;
import me.ufo.crates.file.CrateLocationFile;
import me.ufo.crates.listener.CrateListener;
import me.ufo.crates.util.Item;
import me.ufo.crates.util.Style;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class CratesPlugin extends JavaPlugin {

  @Getter private static CratesPlugin instance;

  @Getter private CrateLocationFile locationFile;

  @Getter private List<Crate> crates;

  @Override
  public void onLoad() {
    this.crates = new ArrayList<>(5);
    this.saveDefaultConfig();
    this.locationFile = new CrateLocationFile(this);
  }

  @Override
  public void onEnable() {
    instance = this;

    this.loadCrateFiles();
    this.loadCrateLocations();

    final PluginManager pm = this.getServer().getPluginManager();
    pm.registerEvents(new CrateListener(this), this);

    this.getCommand("crate").setExecutor(new CrateCommand(this));
  }

  private void loadCrateLocations() {
    final ConfigurationSection locationSection = this.locationFile.getConfigurationSection("locations");
    if (locationSection == null) {
      this.locationFile.getFileConfiguration().createSection("locations");
      return;
    }

    for (final String identifier : locationSection.getKeys(false)) {
      final Crate crate = this.getCrate(identifier);
      if (crate == null) {
        // Crate not found
        continue;
      }
      final String[] split = locationSection.getString(identifier).split(",");
      final World world = Bukkit.getWorld(split[0]);
      final Location location = new Location(world,
                                             Double.parseDouble(split[1]),
                                             Double.parseDouble(split[2]),
                                             Double.parseDouble(split[3]));
      crate.setLocation(location);
    }
  }


  public void setCrateLocation(final Crate crate, final Location location) {
    crate.setLocation(location);

    this.locationFile.getConfigurationSection("locations")
      .set(crate.getIdentifier(), this.serializeBlockLocation(location));
    this.locationFile.save();
  }

  public String serializeBlockLocation(final Location location) {
    return location.getWorld().getName() + "," +
           location.getBlockX() + "," +
           location.getBlockY() + "," +
           location.getBlockZ();
  }

  private void loadCrateFiles() {
    final Path path = Paths.get(this.getDataFolder().toString() + "/crates/");
    try {
      // Create crate directory
      Files.createDirectories(path);
    } catch (final IOException e) {
      e.printStackTrace();
      this.getServer().getPluginManager().disablePlugin(this);
      return;
    }

    final File dir = new File(path.toString());
    final File[] crateFiles = dir.listFiles();
    if (crateFiles == null || crateFiles.length == 0) {
      this.getLogger().info("No crates found, creating default crate.");
      this.crates.add(this.loadCrate(new CrateFile(this, "default.yml")));
      return;
    }

    // Load crate files in directory
    for (final File file : crateFiles) {
      this.getLogger().info("Loading crate file: " + file.getName());
      this.crates.add(this.loadCrate(new CrateFile(this, file.getName())));
    }
  }

  private Crate loadCrate(final CrateFile crateFile) {
    // Crate Identifier
    final String identifier = crateFile.getString("crate-identifier");
    if (identifier.isEmpty()) {
      return null;
    }

    // Crate Inventory
    final ConfigurationSection guiConfig = crateFile.getConfigurationSection("crate-gui");
    if (guiConfig == null) {
      return null;
    }
    final String guiName = guiConfig.getString("name");
    final int guiSize = guiConfig.getInt("size");
    final Inventory inventory = Bukkit.createInventory(null, guiSize, guiName);

    // Placeholder Item
    final ConfigurationSection placeholdersSection = guiConfig.getConfigurationSection("placeholders");
    final boolean placeholders = placeholdersSection.getBoolean("enabled", true);
    if (placeholders) {
      for (int i = 0; i < guiSize; i++) {
        inventory.setItem(i, Item.fromConfig(placeholdersSection.getConfigurationSection("item")));
      }
    }

    // Inventory Items
    final ConfigurationSection guiItemConfig = guiConfig.getConfigurationSection("items");
    for (final String slot : guiItemConfig.getKeys(false)) {
      inventory.setItem(Integer.parseInt(slot), Item.fromConfig(guiItemConfig.getConfigurationSection(slot)));
    }

    // Crate Items
    final ConfigurationSection itemsSection = crateFile.getConfigurationSection("crate-rewards");
    final Set<String> slots = itemsSection.getKeys(false);
    final List<CrateItem> crateItems = new ArrayList<>(slots.size());
    for (final String slot : slots) {
      final ConfigurationSection itemSection = itemsSection.getConfigurationSection(slot);

      final ItemStack guiItem = Item.fromConfig(itemSection);
      final CrateItem crateItem = new CrateItem(itemSection.getInt("chance"),
                                                itemSection.getString("command"),
                                                guiItem);

      final List<String> message = itemSection.getStringList("message");
      if (message != null && !message.isEmpty()) {
        crateItem.setMessage(Style.translate(message.toArray(new String[0])));
      }

      inventory.setItem(Integer.parseInt(slot), guiItem);
      crateItems.add(crateItem);
    }

    crateItems.sort(CrateItem::compareTo);

    this.getLogger().info("CRATE ITEMS:");
    for (final CrateItem crateItem : crateItems) {
      this.getLogger().info(crateItem.getChance() + "," + crateItem.getItemStack().getType());
    }

    return new Crate(identifier, inventory, crateItems);
  }

  public Crate getCrate(final String identifier) {
    for (final Crate crate : this.crates) {
      if (identifier.equalsIgnoreCase(crate.getIdentifier())) {
        return crate;
      }
    }

    return null;
  }

  public Crate getCrate(final Crate compare) {
    for (final Crate compareTo : this.crates) {
      if (compare.equals(compareTo)) {
        return compareTo;
      }
    }

    return null;
  }

}
