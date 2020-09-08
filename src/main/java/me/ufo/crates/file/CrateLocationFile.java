package me.ufo.crates.file;

import java.io.File;
import java.io.IOException;
import java.util.List;
import me.ufo.crates.CratesPlugin;
import me.ufo.crates.util.Style;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public final class CrateLocationFile {

  private final File file;
  private FileConfiguration fileConfiguration;

  public CrateLocationFile(final CratesPlugin plugin) {
    file = new File(plugin.getDataFolder().toString() + "/locations.yml");
    if (!file.exists()) {
      try {
        file.createNewFile();
      } catch (final IOException e) {
        e.printStackTrace();
        plugin.getLogger().severe("Failed to create locations.yml file.");
        plugin.getServer().getPluginManager().disablePlugin(plugin);
        return;
      }
    }
    fileConfiguration = YamlConfiguration.loadConfiguration(file);
  }

  public String getString(final String path) {
    return Style.translate(fileConfiguration.getString(path, ""));
  }

  public List<String> getStringList(final String path) {
    return Style.translate(fileConfiguration.getStringList(path));
  }

  public int getInt(final String path) {
    return fileConfiguration.getInt(path);
  }

  public void set(final String path, final Object value) {
    fileConfiguration.set(path, value);
  }

  public ConfigurationSection getConfigurationSection(final String path) {
    return fileConfiguration.getConfigurationSection(path);
  }

  public FileConfiguration getFileConfiguration() {
    if (fileConfiguration == null) {
      fileConfiguration = YamlConfiguration.loadConfiguration(file);
    }
    return fileConfiguration;
  }

  public void saveDefault(final CratesPlugin plugin) {
    if (!file.exists()) {
      plugin.saveResource(file.getName(), false);
    }
  }

  public void save() {
    if (fileConfiguration == null) {
      return;
    }

    try {
      fileConfiguration.save(file);
    } catch (final IOException e) {
      e.printStackTrace();
    }
  }

}
