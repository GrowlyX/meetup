package com.solexgames.meetup;

import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.PaperCommandManager;
import com.solexgames.core.CorePlugin;
import com.solexgames.lib.commons.redis.JedisBuilder;
import com.solexgames.lib.commons.redis.JedisManager;
import com.solexgames.meetup.chat.UHCMeetupSpectatorChatCheckImpl;
import com.solexgames.meetup.command.*;
import com.solexgames.meetup.listener.GameListener;
import com.solexgames.meetup.handler.KitHandler;
import com.solexgames.meetup.handler.*;
import com.solexgames.meetup.listener.PlayerListener;
import com.solexgames.meetup.provider.ScoreboardProvider;
import com.solexgames.meetup.scenario.Scenario;
import com.solexgames.meetup.task.game.GameCheckTask;
import com.solexgames.meetup.task.ServerUpdateTask;
import com.solexgames.meetup.util.CC;
import com.solexgames.meetup.util.JedisUtil;
import com.solexgames.meetup.util.MeetupUtil;
import io.github.nosequel.scoreboard.ScoreboardHandler;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

@Getter
public final class Meetup extends JavaPlugin {

    @Getter
    private static Meetup instance;

    private GameHandler gameHandler;
    private MongoHandler mongoHandler;
    private PlayerHandler playerHandler;
    private LoadoutHandler loadoutHandler;
    private SpectatorHandler spectatorHandler;
    private ScenarioHandler scenarioHandler;
    private KitHandler kitHandler;
    private BorderHandler borderHandler;
    private DeathMessageHandler deathMessageHandler;
    private ScoreboardHandler scoreboardHandler;

    private JedisManager jedisManager;

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
        this.kitHandler = new KitHandler();
        this.borderHandler = new BorderHandler();
        this.deathMessageHandler = new DeathMessageHandler();
        this.scoreboardHandler = new ScoreboardHandler(this, new ScoreboardProvider(), 5L);

        this.setupJedis();

        this.registerCommands();
        this.registerListeners();

        CorePlugin.getInstance().getChatCheckList().add(new UHCMeetupSpectatorChatCheckImpl());
    }

    @Override
    public void onDisable() {
        this.jedisManager.publish(JedisUtil.getServerOfflineJson());

        MeetupUtil.deleteWorld();
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
