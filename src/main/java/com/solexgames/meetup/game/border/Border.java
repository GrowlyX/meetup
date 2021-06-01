package com.solexgames.meetup.game.border;

import com.solexgames.meetup.UHCMeetup;
import com.solexgames.meetup.game.Game;
import org.bukkit.World;

public class Border {

	public Border(World world, int border) {
		final Game game = UHCMeetup.getInstance().getGameHandler().getGame();

		game.setBorder(border);
		BorderHelper.addBedrockBorder(world.getName(), border, 5);
		// todo: set world border

		if (border == 10) {
			game.setBorderTime(-1);
		}
	}
}
