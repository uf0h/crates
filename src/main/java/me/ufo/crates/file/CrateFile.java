package me.ufo.crates.file;

import java.io.File;
import java.io.IOException;
import java.util.List;
import me.ufo.crates.CratesPlugin;
import me.ufo.crates.util.Style;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public final class CrateFile {

  private final File file;
  private FileConfiguration fileConfiguration;

  public CrateFile(final CratesPlugin plugin, final String fileName) {
    file = new File(plugin.getDataFolder().toString() + "/crates/" + fileName);
    if (!file.exists()) {
      plugin.saveResource("crates/" + fileName, true);
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
