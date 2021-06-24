package com.solexgames.meetup.util;

import com.solexgames.core.util.Color;
import org.bukkit.ChatColor;

/**
 * @author puugz
 * @since 18/06/2021 23:00
 */
public class CC {

	public static final String PRI = Color.MAIN_COLOR;
	public static final String SEC = Color.SECONDARY_COLOR;

	public static final String B_PRI = PRI + ChatColor.BOLD;

	public static final String WHITE = ChatColor.WHITE.toString();
	public static final String RED = ChatColor.RED.toString();
	public static final String GRAY = ChatColor.GRAY.toString();
	public static final String GREEN = ChatColor.GREEN.toString();
	public static final String YELLOW = ChatColor.YELLOW.toString();
	public static final String AQUA = ChatColor.AQUA.toString();

	public static final String BOLD = ChatColor.BOLD.toString();
	public static final String S = ChatColor.STRIKETHROUGH.toString();

	public static final String B_GREEN = GREEN + BOLD;
	public static final String B_RED = RED + BOLD;
	public static final String B_GRAY = GRAY + BOLD;

	public static final String D_RED = ChatColor.DARK_RED.toString();

	public static final String I_GRAY = ChatColor.ITALIC + GRAY;
	public static final String I_YELLOW = ChatColor.ITALIC + YELLOW;
}
