package com.solexgames.meetup.board;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.board.ScoreBoard;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
public class Board {

	private final BoardAdapter adapter;
	private final Player player;
	private final List<BoardEntry> entries = new ArrayList<>();
	private final Set<String> keys = new HashSet<>();
	private Scoreboard scoreboard;
	private Objective objective;

	public Board(Player player, BoardAdapter adapter) {
		this.adapter = adapter;
		this.player = player;

		this.init();
	}

	private void init() {
		if (!this.player.getScoreboard().equals(CorePlugin.getInstance().getServer().getScoreboardManager().getMainScoreboard())) {
			this.scoreboard = this.player.getScoreboard();
		} else {
			this.scoreboard = CorePlugin.getInstance().getServer().getScoreboardManager().getNewScoreboard();
		}

		this.objective = this.scoreboard.registerNewObjective("Default", "dummy");
		this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);

		this.enableBelowNameTagHearts();
		this.enableTabListHearts();

		this.objective.setDisplayName(this.adapter.getTitle(this.player));
	}

	public void enableBelowNameTagHearts() {
		final Objective objective = this.scoreboard.registerNewObjective("name", "health");

		objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
		objective.setDisplayName("§4❤");
	}

	public void enableTabListHearts() {
		final Objective tab = this.scoreboard.registerNewObjective("tab", "health");
		tab.setDisplaySlot(DisplaySlot.PLAYER_LIST);
	}

	public String getNewKey(BoardEntry entry) {
		for (ChatColor color : ChatColor.values()) {
			String colorText = color + "" + ChatColor.WHITE;

			if (entry.getText().length() > 16) {
				String sub = entry.getText().substring(0, 16);
				colorText = colorText + ChatColor.getLastColors(sub);
			}

			if (!keys.contains(colorText)) {
				keys.add(colorText);
				return colorText;
			}
		}
		throw new IndexOutOfBoundsException("No more keys available!");
	}

	public List<String> getBoardEntriesFormatted() {
		List<String> toReturn = new ArrayList<>();

		for (BoardEntry entry : new ArrayList<>(entries)) {
			toReturn.add(entry.getText());
		}
		return toReturn;
	}

	public BoardEntry getByPosition(int position) {
		for (int i = 0; i < this.entries.size(); i++) {
			if (i == position) {
				return this.entries.get(i);
			}
		}
		return null;
	}
}
