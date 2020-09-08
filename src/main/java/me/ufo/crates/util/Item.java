package me.ufo.crates.util;

import java.util.List;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class Item {

  private Item() {
    throw new UnsupportedOperationException("This class cannot be instantiated");
  }

  public static ItemStack fromConfig(final ConfigurationSection section) {
    final Builder builder = new Builder(Material.valueOf(section.getString("material")));

    builder.amount(section.getInt("amount", 1));
    builder.data(section.getInt("data", 0));

    final boolean glow = section.getBoolean("glow", false);
    if (glow) {
      builder.glow();
    }

    final String name = section.getString("name");
    if (name != null && !name.isEmpty()) {
      builder.name(Style.translate(name));
    }

    final List<String> lore = section.getStringList("lore");
    if (lore != null && !lore.isEmpty()) {
      builder.lore(Style.translate(lore));
    }

    final List<String> enchants = section.getStringList("enchantments");
    if (enchants != null && !enchants.isEmpty()) {
      for (final String enchant : enchants) {
        final String[] split = enchant.split(":");

        final Enchantment enchantment = EnchantmentFromString(split[0]);
        if (enchantment == null) {
          continue;
        }

        final int level;
        try {
          level = Integer.parseInt(split[1]);
        } catch (final NumberFormatException e) {
          continue;
        }

        builder.enchantment(enchantment, level);
      }
    }

    return builder.build();
  }

  public static Enchantment EnchantmentFromString(final String enchant) {
    switch (enchant.toUpperCase()) {
      default:
        return null;

      case "PROTECTION":
        return Enchantment.PROTECTION_ENVIRONMENTAL;
      case "FIRE_PROTECTION":
        return Enchantment.PROTECTION_FIRE;
      case "BLAST_PROTECTION":
        return Enchantment.PROTECTION_EXPLOSIONS;
      case "PROJECTILE_PROTECTION":
        return Enchantment.PROTECTION_PROJECTILE;
      case "FEATHER_FALL":
        return Enchantment.PROTECTION_FALL;
      case "DEPTH_STRIDER":
        return Enchantment.DEPTH_STRIDER;
      case "UNBREAKING":
      case "DURABILITY":
        return Enchantment.DURABILITY;
      case "SHARPNESS":
        return Enchantment.DAMAGE_ALL;
      case "LOOTING":
        return Enchantment.LOOT_BONUS_MOBS;
      case "FIRE_ASPECT":
        return Enchantment.FIRE_ASPECT;
      case "KNOCKBACK":
        return Enchantment.KNOCKBACK;
      case "SMITE":
        return Enchantment.DAMAGE_UNDEAD;
      case "BANE":
      case "BANE_OF_ARTHROPODS":
        return Enchantment.DAMAGE_ARTHROPODS;
      case "PUNCH":
        return Enchantment.ARROW_KNOCKBACK;
      case "POWER":
        return Enchantment.ARROW_DAMAGE;
      case "FLAME":
        return Enchantment.ARROW_FIRE;
      case "INFINITY":
        return Enchantment.ARROW_INFINITE;
      case "EFFICIENCY":
        return Enchantment.DIG_SPEED;
      case "SILK_TOUCH":
        return Enchantment.SILK_TOUCH;
      case "FORTUNE":
        return Enchantment.LOOT_BONUS_BLOCKS;
      case "LUCK":
        return Enchantment.LUCK;
      case "HASTE":
        return Enchantment.OXYGEN;
    }
  }

  public final static class Builder {

    private final ItemStack itemStack;
    private final ItemMeta itemMeta;

    public Builder(final Material material) {
      this.itemStack = new ItemStack(material);
      this.itemMeta = itemStack.getItemMeta();
    }

    public Builder data(final int data) {
      itemStack.setDurability((short) data);
      return this;
    }

    public Builder name(final String name) {
      itemMeta.setDisplayName(name);
      return this;
    }

    public Builder lore(final List<String> lore) {
      itemMeta.setLore(lore);
      return this;
    }

    public Builder amount(final int amount) {
      this.itemStack.setAmount(amount);
      return this;
    }

    public Builder glow() {
      itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
      itemMeta.addEnchant(Enchantment.DURABILITY, 1, true);
      return this;
    }

    public Builder enchantment(final Enchantment enchantment, final int level) {
      itemMeta.addEnchant(enchantment, level, true);
      return this;
    }

    public ItemStack build() {
      itemStack.setItemMeta(itemMeta);
      return itemStack;
    }

  }

}
