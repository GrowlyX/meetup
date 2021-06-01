package com.solexgames.meetup;

import co.aikar.commands.PaperCommandManager;
import com.solexgames.core.CorePlugin;
import com.solexgames.lib.commons.redis.JedisBuilder;
import com.solexgames.lib.commons.redis.JedisManager;
import com.solexgames.meetup.board.BoardManager;
import com.solexgames.meetup.handler.*;
import com.solexgames.meetup.scoreboard.ScoreboardAdapter;
import com.solexgames.meetup.task.ServerUpdateTask;
import com.solexgames.meetup.util.JedisUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.TimeUnit;

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
    private JedisManager jedisManager;

    @Override
    public void onEnable() {
        instance = this;

        this.gameHandler = new GameHandler();
        this.gameHandler.setupGame();

        this.mongoHandler = new MongoHandler();
        this.loadoutHandler = new LoadoutHandler();
        this.spectatorHandler = new SpectatorHandler();

        this.jedisManager = new JedisBuilder()
                .withChannel("meetup:bukkit")
                .withSettings(CorePlugin.getInstance().getDefaultJedisSettings())
                .build();

        this.jedisManager.publish(JedisUtil.getServerUpdateJson());

        new ServerUpdateTask().runTaskTimerAsynchronously(this, 20L, TimeUnit.SECONDS.toMillis(5L));

        final PaperCommandManager manager = new PaperCommandManager(this);

        this.setBoardManager(new BoardManager(new ScoreboardAdapter()));
    }

    @Override
    public void onDisable() {
        if (this.gameHandler.isRunning()) {
            this.gameHandler.getGame().end(null);
        }
    }

    public void setBoardManager(BoardManager boardManager) {
        this.boardManager = boardManager;

        final long interval = this.boardManager.getAdapter().getInterval();

        this.getServer().getScheduler().runTaskTimerAsynchronously(this, this.boardManager, 0L, interval);
    }

    public void setWorldProperties() {
        Bukkit.getWorlds().forEach(world -> world.getEntities().forEach(Entity::remove));

        final World meetup_game = Bukkit.getWorld("meetup_game");

        meetup_game.setGameRuleValue("doMobSpawning", "false");
        meetup_game.setGameRuleValue("doDaylightCycle", "false");
        meetup_game.setGameRuleValue("naturalRegeneration", "false");
        meetup_game.setGameRuleValue("doFireTick", "false");
        meetup_game.setGameRuleValue("difficulty", "0");
        meetup_game.setTime(0);
    }
}
