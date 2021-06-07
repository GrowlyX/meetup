package com.solexgames.meetup.game.kit;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.util.builder.ItemBuilder;
import com.solexgames.meetup.UHCMeetup;
import com.solexgames.meetup.player.GamePlayer;
import com.solexgames.meetup.util.MeetupUtils;
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
public class KitManager {

	private final Map<Integer, ItemStack> defaultInventory = new HashMap<>();

	public KitManager() {
		this.setupDefaultInventory(this.defaultInventory);
	}

	private final Random random = CorePlugin.RANDOM;

	public void handleItems(Player player) {
		final PlayerInventory inventory = player.getInventory();
		final GamePlayer gamePlayer = UHCMeetup.getInstance().getPlayerHandler().getByPlayer(player);

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

		if (this.random.nextInt(4) == 2) {
			sword.addUnsafeEnchantment(Enchantment.FIRE_ASPECT, 1);
		}

		final ItemStack bow = new ItemStack(Material.BOW);

		bow.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, this.random.nextInt(3) + 1);

		if (this.random.nextInt(10) == 7) {
			bow.addUnsafeEnchantment(Enchantment.ARROW_FIRE, 1);
		}

		if (this.random.nextInt(10) == 7) {
			bow.addUnsafeEnchantment(Enchantment.ARROW_KNOCKBACK, 1);
		}

		if (this.random.nextInt(100) >= 95) {
			bow.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1);
		}

		final ItemStack head = MeetupUtils.getGoldenHead();
		head.setAmount(this.random.nextInt(3) + 1);

		inventory.setItem(0, sword);
		inventory.setItem(1, new ItemStack(Material.FISHING_ROD));
		inventory.setItem(2, bow);
		inventory.setItem(3, new ItemStack(Material.COOKED_BEEF, 64));
		inventory.setItem(4, new ItemStack(Material.GOLDEN_APPLE, this.random.nextInt(7) + 1));
		inventory.setItem(5, head);
		inventory.setItem(6, new ItemStack(Material.DIAMOND_AXE));
		inventory.setItem(7, new ItemStack(Material.FLINT_AND_STEEL));
		inventory.setItem(8, new ItemStack(Material.COBBLESTONE, 64));

		inventory.setItem(9, new ItemStack(Material.ARROW, 64));
		inventory.setItem(10, new ItemStack(Material.LAVA_BUCKET));
		inventory.setItem(11, new ItemStack(Material.LAVA_BUCKET));
		inventory.setItem(12, new ItemStack(Material.WATER_BUCKET));
		inventory.setItem(13, new ItemStack(Material.WATER_BUCKET));
		inventory.setItem(14, new ItemStack(Material.DIAMOND_PICKAXE));
		inventory.setItem(15, new ItemStack(Material.ENCHANTMENT_TABLE));
		inventory.setItem(16, new ItemStack(Material.ANVIL, this.random.nextInt(2) + 1));
		inventory.setItem(17, new ItemStack(Material.EXP_BOTTLE, 64));

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
		defaultInventory.put(0, new ItemStack(Material.GOLD_SWORD));
		defaultInventory.put(1, new ItemStack(Material.FISHING_ROD));
		defaultInventory.put(2, new ItemStack(Material.BOW));
		defaultInventory.put(3, new ItemStack(Material.COOKED_BEEF));
		defaultInventory.put(4 ,new ItemStack(Material.GOLDEN_APPLE));
		defaultInventory.put(5, new ItemStack(Material.APPLE));
		defaultInventory.put( 6,new ItemStack(Material.DIAMOND_AXE));
		defaultInventory.put(7, new ItemStack(Material.FLINT_AND_STEEL));
		defaultInventory.put(8, new ItemStack(Material.COBBLESTONE));
		defaultInventory.put(9, new ItemStack(Material.ARROW));
		defaultInventory.put(10, new ItemStack(Material.LAVA_BUCKET));
		defaultInventory.put(11, new ItemStack(Material.LAVA_BUCKET));
		defaultInventory.put(12, new ItemStack(Material.WATER_BUCKET));
		defaultInventory.put(13, new ItemStack(Material.WATER_BUCKET));
		defaultInventory.put(14, new ItemStack(Material.DIAMOND_PICKAXE));
		defaultInventory.put(15, new ItemStack(Material.ENCHANTMENT_TABLE));
		defaultInventory.put(16, new ItemStack(Material.ANVIL));
		defaultInventory.put(17, new ItemStack(Material.EXP_BOTTLE));
	}
}
