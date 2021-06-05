package com.solexgames.meetup;

import co.aikar.commands.PaperCommandManager;
import com.solexgames.core.CorePlugin;
import com.solexgames.lib.commons.redis.JedisBuilder;
import com.solexgames.lib.commons.redis.JedisManager;
import com.solexgames.lib.commons.redis.JedisSettings;
import com.solexgames.meetup.board.BoardManager;
import com.solexgames.meetup.command.LoadoutCommand;
import com.solexgames.meetup.command.ResetLoadoutCommand;
import com.solexgames.meetup.command.SpectateCommand;
import com.solexgames.meetup.game.GameListener;
import com.solexgames.meetup.game.kit.KitManager;
import com.solexgames.meetup.handler.*;
import com.solexgames.meetup.listener.BorderListener;
import com.solexgames.meetup.listener.PlayerListener;
import com.solexgames.meetup.scenario.Scenario;
import com.solexgames.meetup.handler.ScenarioHandler;
import com.solexgames.meetup.scoreboard.ScoreboardAdapter;
import com.solexgames.meetup.task.ServerUpdateTask;
import com.solexgames.meetup.util.JedisUtil;
import com.solexgames.meetup.util.MeetupUtils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.PluginManager;
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
    private ScenarioHandler scenarioHandler;

    private BoardManager boardManager;
    private JedisManager jedisManager;
    private KitManager kitManager;

    @Override
    public void onEnable() {
        instance = this;

        this.gameHandler = new GameHandler();
        this.gameHandler.setupGame();

        this.mongoHandler = new MongoHandler();
        this.playerHandler = new PlayerHandler();
        this.loadoutHandler = new LoadoutHandler();
        this.spectatorHandler = new SpectatorHandler();
        this.scenarioHandler = new ScenarioHandler();

        this.kitManager = new KitManager();

        this.setupJedis();
        this.registerCommands();
        this.registerListeners();
        this.setBoardManager(new BoardManager(new ScoreboardAdapter()));
    }

    @Override
    public void onDisable() {
        MeetupUtils.deleteWorld();
    }

    private void registerCommands() {
        final PaperCommandManager manager = new PaperCommandManager(this);

        manager.registerCommand(new LoadoutCommand());
        manager.registerCommand(new ResetLoadoutCommand());
        manager.registerCommand(new SpectateCommand());
    }

    private void setupJedis() {
//        this.jedisManager = new JedisBuilder()
//                .withChannel("meetup")
//                .withSettings(new JedisSettings(
//                        CorePlugin.getInstance().getDatabaseConfig().getString("redis.host"),
//                        CorePlugin.getInstance().getDatabaseConfig().getInt("redis.port"),
//                        CorePlugin.getInstance().getDatabaseConfig().getBoolean("redis.authentication.enabled"),
//                        CorePlugin.getInstance().getDatabaseConfig().getString("redis.authentication.password")
//                ))
//                .build();
//
//        new ServerUpdateTask().runTaskTimerAsynchronously(this, 20L, TimeUnit.SECONDS.toMillis(5L));
    }

    private void registerListeners() {
        final PluginManager pluginManager = Bukkit.getPluginManager();

        pluginManager.registerEvents(new GameListener(), this);
        pluginManager.registerEvents(new BorderListener(), this);
        pluginManager.registerEvents(new PlayerListener(), this);
    }

    public <T extends Scenario> T getScenario(Class<T> scenarioClass) {
        return this.scenarioHandler.getScenario(scenarioClass);
    }

    public void setBoardManager(BoardManager boardManager) {
        this.boardManager = boardManager;

        final long interval = this.boardManager.getAdapter().getInterval();

        this.getServer().getScheduler().runTaskTimerAsynchronously(this, this.boardManager, 0L, interval);
    }

    public void setWorldProperties() {
        Bukkit.getWorlds().forEach(world -> world.getEntities().forEach(Entity::remove));

        final World world = Bukkit.getWorld("meetup_game");

        world.setGameRuleValue("doMobSpawning", "false");
        world.setGameRuleValue("doDaylightCycle", "false");
        world.setGameRuleValue("naturalRegeneration", "false");
        world.setGameRuleValue("doFireTick", "false");
        world.setGameRuleValue("difficulty", "0");
        world.setTime(0);
    }
}
