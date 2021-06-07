package com.solexgames.meetup;

import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.PaperCommandManager;
import com.solexgames.core.CorePlugin;
import com.solexgames.lib.commons.redis.JedisBuilder;
import com.solexgames.lib.commons.redis.JedisManager;
import com.solexgames.meetup.board.BoardManager;
import com.solexgames.meetup.command.*;
import com.solexgames.meetup.game.GameListener;
import com.solexgames.meetup.game.kit.KitManager;
import com.solexgames.meetup.handler.*;
import com.solexgames.meetup.listener.PlayerListener;
import com.solexgames.meetup.scenario.Scenario;
import com.solexgames.meetup.scoreboard.ScoreboardAdapter;
import com.solexgames.meetup.task.game.GameCheckTask;
import com.solexgames.meetup.task.ServerUpdateTask;
import com.solexgames.meetup.util.CC;
import com.solexgames.meetup.util.JedisUtil;
import com.solexgames.meetup.util.MeetupUtils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.stream.Collectors;

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
        this.jedisManager.publish(JedisUtil.getServerOfflineJson());

        MeetupUtils.deleteWorld();
    }

    private void registerCommands() {
        final PaperCommandManager manager = new PaperCommandManager(this);

        manager.registerCommand(new ForceStartCommand());
        manager.registerCommand(new LoadoutCommand());
        manager.registerCommand(new ResetLoadoutCommand());
        manager.registerCommand(new SpectateCommand());
        manager.registerCommand(new AnnounceCommand());
        manager.registerCommand(new ReRollCommand());

        manager.getCommandContexts().registerContext(Integer.class, bukkitCommandExecutionContext -> {
            try {
                return Integer.parseInt(bukkitCommandExecutionContext.getFirstArg());
            } catch (Exception ignored) {
                throw new InvalidCommandArgument(CC.RED + bukkitCommandExecutionContext.getFirstArg() + " is not a valid integer.");
            }
        });

        manager.getCommandCompletions().registerAsyncCompletion("purchasable", context ->
                Arrays.asList("1", "2", "3", "4", "5", "10", "20", "30", "50")
        );
    }

    private void setupJedis() {
        this.jedisManager = new JedisBuilder()
                .withChannel("meetup")
                .withSettings(CorePlugin.getInstance().getDefaultJedisSettings())
                .build();

        new ServerUpdateTask().runTaskTimerAsynchronously(this, 0L, 100L);
        new GameCheckTask().runTaskTimerAsynchronously(this, 0L, 100L);
    }

    private void registerListeners() {
        final PluginManager pluginManager = Bukkit.getPluginManager();

        pluginManager.registerEvents(new GameListener(), this);
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
