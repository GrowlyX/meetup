package com.solexgames.meetup.handler;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.util.builder.ItemBuilder;
import com.solexgames.meetup.Meetup;
import com.solexgames.meetup.model.Loadout;
import com.solexgames.meetup.player.GamePlayer;
import com.solexgames.meetup.util.MeetupUtil;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Getter
public class KitHandler {

	private final Map<Integer, ItemStack> defaultInventory = new HashMap<>();

	public KitHandler() {
		this.setupDefaultInventory(this.defaultInventory);
	}

	private final Random random = CorePlugin.RANDOM;

	public void handleItems(Player player) {
		final PlayerInventory inventory = player.getInventory();
		final GamePlayer gamePlayer = Meetup.getInstance().getPlayerHandler().getByPlayer(player);

		inventory.setHelmet(new ItemBuilder(this.getRandomMaterial("helmet"))
				.setEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, this.getLevel()).create());

		inventory.setChestplate(new ItemBuilder(this.getRandomMaterial("chestplate"))
				.setEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, this.getLevel()).create());

		inventory.setLeggings(new ItemBuilder(this.getRandomMaterial("leggings"))
				.setEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, this.getLevel()).create());

		inventory.setBoots(new ItemBuilder(this.getRandomMaterial("boots"))
				.setEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, this.getLevel()).create());

		boolean hasAnythingDiamond = false;

		for (ItemStack stack : inventory.getArmorContents()) {
			if (stack.getType().name().startsWith("DIAMOND_")) {
				hasAnythingDiamond = true;
				break;
			}
		}

		if (!hasAnythingDiamond) {
			inventory.setChestplate(new ItemBuilder(getRandomMaterial("chestplate"))
					.setEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, getLevel()).create());

			inventory.setLeggings(new ItemBuilder(getRandomMaterial("leggings"))
					.setEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, getLevel()).create());
		} else {
			final boolean hasEverythingDiamond = inventory.getHelmet().getType().name().startsWith("DIAMOND")
					&& inventory.getChestplate().getType().name().startsWith("DIAMOND")
					&& inventory.getLeggings().getType().name().startsWith("DIAMOND")
					&& inventory.getBoots().getType().name().startsWith("DIAMOND");

			if (hasEverythingDiamond) {
				inventory.setHelmet(new ItemBuilder(Material.IRON_HELMET)
						.setEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, getLevel()).create());

				inventory.setBoots(new ItemBuilder(Material.IRON_BOOTS)
						.setEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, getLevel()).create());
			}
		}

		final ItemStack sword = new ItemStack(getRandomMaterial("sword"));

		sword.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, getLevel());

//		if (this.random.nextInt(4) == 2) {
//			sword.addUnsafeEnchantment(Enchantment.FIRE_ASPECT, 1);
//		}

		final ItemStack bow = new ItemStack(Material.BOW);

		bow.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, this.random.nextInt(3) + 1);

		if (this.random.nextInt(10) == 7) {
			bow.addUnsafeEnchantment(Enchantment.ARROW_KNOCKBACK, 1);
		}

		if (this.random.nextInt(100) >= 95) {
			bow.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1);
		}

		final ItemStack head = MeetupUtil.getGoldenHead();
		final Loadout loadout = gamePlayer.getLoadout();

		head.setAmount(this.random.nextInt(3) + 2);

		inventory.setItem(loadout.getLocationOf(Material.DIAMOND_SWORD), sword);
		inventory.setItem(loadout.getLocationOf(Material.FISHING_ROD), new ItemStack(Material.FISHING_ROD));
		inventory.setItem(loadout.getLocationOf(Material.BOW), bow);
		inventory.setItem(loadout.getLocationOf(Material.COOKED_BEEF), new ItemStack(Material.COOKED_BEEF, 64));
		inventory.setItem(loadout.getLocationOf(Material.GOLDEN_APPLE), new ItemStack(Material.GOLDEN_APPLE, this.random.nextInt(4) + 4));
		inventory.setItem(loadout.getLocationOf(Material.APPLE), head);
		inventory.setItem(loadout.getLocationOf(Material.DIAMOND_AXE), new ItemStack(Material.DIAMOND_AXE));
		inventory.setItem(loadout.getLocationOf(Material.FLINT_AND_STEEL), new ItemStack(Material.FLINT_AND_STEEL));
		inventory.setItem(loadout.getLocationOf(Material.COBBLESTONE), new ItemStack(Material.COBBLESTONE, 64));

		inventory.setItem(loadout.getLocationOf(Material.ARROW), new ItemStack(Material.ARROW, 64));
		inventory.setItem(loadout.getLocationOf(Material.LAVA_BUCKET), new ItemBuilder(Material.LAVA_BUCKET).setAmount(1).create());
		inventory.setItem(loadout.getLocationOf(Material.WATER_BUCKET), new ItemBuilder(Material.WATER_BUCKET).setAmount(1).create());

		inventory.setItem(loadout.getLocationOf(Material.DIAMOND_PICKAXE), new ItemStack(Material.DIAMOND_PICKAXE));
		inventory.setItem(loadout.getLocationOf(Material.ENCHANTMENT_TABLE), new ItemStack(Material.ENCHANTMENT_TABLE));
		inventory.setItem(loadout.getLocationOf(Material.ANVIL), new ItemStack(Material.ANVIL, this.random.nextInt(2) + 1));
		inventory.setItem(loadout.getLocationOf(Material.EXP_BOTTLE), new ItemStack(Material.EXP_BOTTLE, 64));

		inventory.setItem(inventory.firstEmpty(), new ItemBuilder(Material.LAVA_BUCKET).setAmount(1).create());
		inventory.setItem(inventory.firstEmpty(), new ItemBuilder(Material.WATER_BUCKET).setAmount(1).create());

		player.updateInventory();
	}

	private Material getRandomMaterial(String type) {
		final int random = this.random.nextInt(100);

		switch (type) {
			case "helmet":
				return random >= 50 ? Material.DIAMOND_HELMET : Material.IRON_HELMET;
			case "chestplate":
				return random >= 60 ? Material.DIAMOND_CHESTPLATE : Material.IRON_CHESTPLATE;
			case "leggings":
				return random >= 60 ? Material.DIAMOND_LEGGINGS : Material.IRON_LEGGINGS;
			case "boots":
				return random >= 50 ? Material.DIAMOND_BOOTS : Material.IRON_BOOTS;
			case "sword":
				return random >= 50 ? Material.DIAMOND_SWORD : Material.IRON_SWORD;
			default:
				return Material.GRASS;
		}
	}

	private int getLevel() {
		final int r = this.random.nextInt(100);
		return r >= 65 ? 3 : r >= 35 ? 2 : 1;
	}

	public void setupDefaultInventory(Map<Integer, ItemStack> defaultInventory) {
		defaultInventory.clear();

		defaultInventory.put(0, new ItemStack(Material.DIAMOND_SWORD));
		defaultInventory.put(1, new ItemStack(Material.FISHING_ROD));
		defaultInventory.put(2, new ItemStack(Material.BOW));
		defaultInventory.put(3, new ItemStack(Material.COOKED_BEEF));
		defaultInventory.put(4, new ItemStack(Material.GOLDEN_APPLE));
		defaultInventory.put(5, new ItemStack(Material.APPLE));
		defaultInventory.put(6, new ItemStack(Material.DIAMOND_AXE));
		defaultInventory.put(7, new ItemStack(Material.FLINT_AND_STEEL));
		defaultInventory.put(8, new ItemStack(Material.COBBLESTONE));
		defaultInventory.put(9, new ItemStack(Material.ARROW));
		defaultInventory.put(10, new ItemStack(Material.LAVA_BUCKET));
		defaultInventory.put(11, new ItemStack(Material.WATER_BUCKET));
		defaultInventory.put(14, new ItemStack(Material.DIAMOND_PICKAXE));
		defaultInventory.put(15, new ItemStack(Material.ENCHANTMENT_TABLE));
		defaultInventory.put(16, new ItemStack(Material.ANVIL));
		defaultInventory.put(17, new ItemStack(Material.EXP_BOTTLE));
	}
}
