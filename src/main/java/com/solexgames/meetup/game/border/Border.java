package com.solexgames.meetup.game.border;

import com.solexgames.meetup.UHCMeetup;
import com.solexgames.meetup.game.Game;
import org.bukkit.Bukkit;
import org.bukkit.World;

public class Border {

	public Border(World world, int border) {
		final Game game = UHCMeetup.getInstance().getGameHandler().getGame();

		game.setBorder(border);
		BorderHelper.addBedrockBorder(world.getName(), border, 5);

		world.getWorldBorder().setCenter(0, 0);
		world.getWorldBorder().setSize(border * 2);

		if (border == 10) {
			game.setBorderTime(-1);
		}
	}
}
