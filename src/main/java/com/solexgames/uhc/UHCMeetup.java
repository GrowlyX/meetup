package com.solexgames.uhc;

import co.aikar.commands.PaperCommandManager;
import com.solexgames.uhc.handler.GameHandler;
import com.solexgames.uhc.handler.LoadoutHandler;
import com.solexgames.uhc.handler.MongoHandler;
import com.solexgames.uhc.handler.PlayerHandler;
import com.solexgames.uhc.scoreboard.ScoreboardAdapter;
import io.github.nosequel.scoreboard.ScoreboardHandler;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class UHCMeetup extends JavaPlugin {

    @Getter
    private static UHCMeetup instance;

    private GameHandler gameHandler;
    private MongoHandler mongoHandler;
    private PlayerHandler playerHandler;
    private LoadoutHandler loadoutHandler;

    @Override
    public void onEnable() {
        instance = this;

        this.gameHandler = new GameHandler();
        this.gameHandler.setupGame();

        this.mongoHandler = new MongoHandler();
        this.loadoutHandler = new LoadoutHandler();

        final PaperCommandManager manager = new PaperCommandManager(this);

        new ScoreboardHandler(this, new ScoreboardAdapter(), 5L);
    }

    @Override
    public void onDisable() {
        if (this.gameHandler.isRunning()) {
            this.gameHandler.getGame().end(null);
        }
    }
}
