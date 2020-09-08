package me.ufo.crates.crate;

import org.bukkit.inventory.ItemStack;

public final class CrateItem implements Comparable<CrateItem> {

  private final int chance;
  private final String command;

  private final ItemStack itemStack;
  private String[] message;

  public CrateItem(final int chance, final String command, final ItemStack itemStack) {
    this.chance = chance;
    this.command = command;
    this.itemStack = itemStack;
  }

  public int getChance() {
    return chance;
  }

  public String getCommand() {
    return command;
  }

  public ItemStack getItemStack() {
    return itemStack;
  }

  public String[] getMessage() {
    return message;
  }

  public void setMessage(final String[] message) {
    this.message = message;
  }

  @Override
  public int compareTo(final CrateItem o) {
    return Integer.compare(o.getChance(), this.getChance());
  }

}
