package com.solexgames.meetup.util;

import org.bukkit.ChatColor;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class CC {

	private static final Map<String, ChatColor> MAP;

	public static final String PRIMARY = ChatColor.GOLD.toString();
	public static final String SECONDARY = ChatColor.YELLOW.toString();

	public static final String B_PRIMARY = PRIMARY + ChatColor.BOLD;
	public static final String B_SECONDARY = SECONDARY + ChatColor.BOLD;

	public static final String PRI = PRIMARY;
	public static final String SEC = SECONDARY;

	public static final String B_PRI = B_PRIMARY;
	public static final String B_SEC = B_SECONDARY;

	public static final String BLUE = ChatColor.BLUE.toString();
	public static final String AQUA = ChatColor.AQUA.toString();
	public static final String YELLOW = ChatColor.YELLOW.toString();
	public static final String RED = ChatColor.RED.toString();
	public static final String GRAY = ChatColor.GRAY.toString();
	public static final String GOLD = ChatColor.GOLD.toString();
	public static final String GREEN = ChatColor.GREEN.toString();
	public static final String WHITE = ChatColor.WHITE.toString();
	public static final String BLACK = ChatColor.BLACK.toString();
	public static final String BOLD = ChatColor.BOLD.toString();
	public static final String ITALIC = ChatColor.ITALIC.toString();
	public static final String STRIKE_THROUGH = ChatColor.STRIKETHROUGH.toString();
	public static final String RESET = ChatColor.RESET.toString();
	public static final String MAGIC = ChatColor.MAGIC.toString();
	public static final String OBFUSCATED = MAGIC;
	public static final String B = BOLD;
	public static final String M = MAGIC;
	public static final String O = MAGIC;
	public static final String I = ITALIC;
	public static final String S = STRIKE_THROUGH;
	public static final String R = RESET;
	public static final String DARK_BLUE = ChatColor.DARK_BLUE.toString();
	public static final String DARK_AQUA = ChatColor.DARK_AQUA.toString();
	public static final String DARK_GRAY = ChatColor.DARK_GRAY.toString();
	public static final String DARK_GREEN = ChatColor.DARK_GREEN.toString();
	public static final String DARK_PURPLE = ChatColor.DARK_PURPLE.toString();
	public static final String DARK_RED = ChatColor.DARK_RED.toString();
	public static final String D_BLUE = DARK_BLUE;
	public static final String D_AQUA = DARK_AQUA;
	public static final String D_GRAY = DARK_GRAY;
	public static final String D_GREEN = DARK_GREEN;
	public static final String D_PURPLE = DARK_PURPLE;
	public static final String D_RED = DARK_RED;
	public static final String LIGHT_PURPLE = ChatColor.LIGHT_PURPLE.toString();
	public static final String L_PURPLE = LIGHT_PURPLE;
	public static final String PINK = L_PURPLE;
	public static final String B_BLUE = BLUE + B;
	public static final String B_AQUA = AQUA + B;
	public static final String B_YELLOW = YELLOW + B;
	public static final String B_RED = RED + B;
	public static final String B_GRAY = GRAY + B;
	public static final String B_GOLD = GOLD + B;
	public static final String B_GREEN = GREEN + B;
	public static final String B_WHITE = WHITE + B;
	public static final String B_BLACK = BLACK + B;
	public static final String BD_BLUE = D_BLUE + B;
	public static final String BD_AQUA = D_AQUA + B;
	public static final String BD_GRAY = D_GRAY + B;
	public static final String BD_GREEN = D_GREEN + B;
	public static final String BD_PURPLE = D_PURPLE + B;
	public static final String BD_RED = D_RED + B;
	public static final String BL_PURPLE = L_PURPLE + B;
	public static final String I_BLUE = BLUE + I;
	public static final String I_AQUA = AQUA + I;
	public static final String I_YELLOW = YELLOW + I;
	public static final String I_RED = RED + I;
	public static final String I_GRAY = GRAY + I;
	public static final String I_GOLD = GOLD + I;
	public static final String I_GREEN = GREEN + I;
	public static final String I_WHITE = WHITE + I;
	public static final String I_BLACK = BLACK + I;
	public static final String ID_RED = D_RED + I;
	public static final String ID_BLUE = D_BLUE + I;
	public static final String ID_AQUA = D_AQUA + I;
	public static final String ID_GRAY = D_GRAY + I;
	public static final String ID_GREEN = D_GREEN + I;
	public static final String ID_PURPLE = D_PURPLE + I;
	public static final String IL_PURPLE = L_PURPLE + I;
	public static final String VAPE = "§8 §8 §1 §3 §3 §7 §8 §r";
	public static final String BLANK_LINE = VAPE;
	public static final String BL = BLANK_LINE;
	public static final String MENU_BAR = GRAY + S + "------------------------";
	public static final String CHAT_BAR = GRAY + S + "------------------------------";
	public static final String LONGER_CHAT_BAR = GRAY + S + "------------------------------------------------";

	static {
		MAP = new HashMap<>();
		MAP.put("pink", ChatColor.LIGHT_PURPLE);
		MAP.put("orange", ChatColor.GOLD);
		MAP.put("purple", ChatColor.DARK_PURPLE);

		for (ChatColor chatColor : ChatColor.values()) {
			MAP.put(chatColor.name().toLowerCase().replace("_", ""), chatColor);
		}
	}

	public static Set<String> getColorNames() {
		return MAP.keySet();
	}

	public static ChatColor getColorFromName(String name) {
		if (MAP.containsKey(name.trim().toLowerCase())) {
			return MAP.get(name.trim().toLowerCase());
		}

		ChatColor color;

		try {
			color = ChatColor.valueOf(name.toUpperCase().replace(" ", "_"));
		} catch (Exception e) {
			return null;
		}
		return color;
	}

	private CC() {
		throw new RuntimeException("Cannot instantiate a utility class.");
	}
}
