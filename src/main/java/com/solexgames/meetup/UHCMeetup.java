package com.solexgames.meetup;

import co.aikar.commands.PaperCommandManager;
import com.solexgames.meetup.board.BoardManager;
import com.solexgames.meetup.handler.*;
import com.solexgames.meetup.scoreboard.ScoreboardAdapter;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class UHCMeetup extends JavaPlugin {

    @Getter
    private static UHCMeetup instance;

    private GameHandler gameHandler;
    private MongoHandler mongoHandler;
    private PlayerHandler playerHandler;
    private LoadoutHandler loadoutHandler;
    private SpectatorHandler spectatorHandler;

    private BoardManager boardManager;

    @Override
    public void onEnable() {
        instance = this;

        this.gameHandler = new GameHandler();
        this.gameHandler.setupGame();

        this.mongoHandler = new MongoHandler();
        this.loadoutHandler = new LoadoutHandler();
        this.spectatorHandler = new SpectatorHandler();

        final PaperCommandManager manager = new PaperCommandManager(this);

//        new ScoreboardHandler(this, new ScoreboardAdapter(), 5L);

        setBoardManager(new BoardManager(new ScoreboardAdapter()));
    }

    @Override
    public void onDisable() {
        if (this.gameHandler.isRunning()) {
            this.gameHandler.getGame().end(null);
        }
    }

    public void setBoardManager(BoardManager boardManager) {
        this.boardManager = boardManager;

        long interval = this.boardManager.getAdapter().getInterval();

        this.getServer().getScheduler().runTaskTimerAsynchronously(this, this.boardManager, 0L, interval);
    }

    public void setWorldProperties() {
        Bukkit.getWorlds().forEach(world -> world.getEntities().forEach(Entity::remove));

        World meetupWorld = Bukkit.getWorld("meetupworld");
        meetupWorld.setGameRuleValue("doMobSpawning", "false");
        meetupWorld.setGameRuleValue("doDaylightCycle", "false");
        meetupWorld.setGameRuleValue("naturalRegeneration", "false");
        meetupWorld.setGameRuleValue("doFireTick", "false");
        meetupWorld.setGameRuleValue("difficulty", "0");
        meetupWorld.setTime(0);
    }
}
