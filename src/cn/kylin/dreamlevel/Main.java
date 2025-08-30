package cn.kylin.dreamlevel;

import cn.kylin.dreamlevel.api.DlApi;
import cn.kylin.dreamlevel.api.data.Level;
import cn.kylin.dreamlevel.api.data.PlayerData;
import cn.kylin.dreamlevel.command.CmdExecutor;
import cn.kylin.dreamlevel.config.ConfigLoader;
import cn.kylin.dreamlevel.config.LangLoader;
import cn.kylin.dreamlevel.config.LevelLoader;
import cn.kylin.dreamlevel.data.DataGeneral;
import cn.kylin.dreamlevel.listener.MythicMobsListener;
import cn.kylin.dreamlevel.listener.PlayerListener;
import cn.kylin.dreamlevel.nms.NmsUtils;
import cn.kylin.dreamlevel.papi.DreamPlaceholderExpansion;
import cn.kylin.dreamlevel.runnable.FlushNoPerMsgRunnable;
import cn.kylin.dreamlevel.runnable.TimesRunnable;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.*;

public class Main extends JavaPlugin {

    public static final String version = "2.1.3";
    public static Main plugin;

    public static Map<String, Level> levels;
    public static boolean enableActionBar;
    public static boolean enableMySQL;

    public static boolean kuaFu;

    @Override
    public void onEnable() {
        plugin = this;
        levels = new HashMap<>();
        NmsUtils.levels = new ArrayList<>();
        // register Listener  ConfigurationSerialization CommandExecutor
        // reload的时候会第二次注册 就导致执行重复多次的问题！！！！！！
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
        ConfigurationSerialization.registerClass(PlayerData.class);
        // saveConfig
        saveDefaultConfig();
        if (!new File(this.getDataFolder(), "level/default.yml").exists()) {
            saveResource("level/default.yml", false);
        }
        if (!new File(this.getDataFolder(), "lang.yml").exists()) {
            saveResource("lang.yml", false);
        }
        LevelLoader.load();
        ConfigLoader.loadConfig();
        LangLoader.init();
        Properties prop = ConfigLoader.loadMySQL(this.getConfig().getConfigurationSection("MySQL"));
        enableMySQL = Boolean.parseBoolean(prop.getProperty("enable"));
        DataGeneral.init(prop);

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new DreamPlaceholderExpansion(this).register();
            this.getLogger().info("[DreamLevel] PlaceholderAPI兼容成功！");
        }

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            Bukkit.getPluginManager().registerEvents(new MythicMobsListener(), this);
            this.getLogger().info("DreamLevel > MythicMobs兼容成功！支持mm drops");
        }

        // runnable
        TimesRunnable.runTask();
        FlushNoPerMsgRunnable.runTask();

        this.getLogger().info("DreamLevel" + version + " 加载成功！");
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);
        List<PlayerData> datas = DlApi.getAllPlayerData();
        for (PlayerData data : datas) {
            DataGeneral.updatePlayerData(data);
        }
        this.getLogger().info("DreamLevel" + version + " 卸载");
    }

    public void reload(){
        List<PlayerData> datas = DlApi.getAllPlayerData();
        for (PlayerData data : datas) {
            DataGeneral.updatePlayerData(data);
        }

        levels = new HashMap<>();
        NmsUtils.levels = new ArrayList<>();
        saveDefaultConfig();
        saveResource("level/default.yml", false);
        saveResource("lang.yml", false);

        LevelLoader.load();
        ConfigLoader.loadConfig();
        LangLoader.init();
        Properties prop = ConfigLoader.loadMySQL(this.getConfig().getConfigurationSection("MySQL"));
        enableMySQL = Boolean.parseBoolean(prop.getProperty("enable"));
        DataGeneral.init(prop);

        this.getLogger().info("DreamLevel" + version + " 加载成功！");

        // check whether online player exists new level data or not
        Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        Level level;
        for (Player player : players) {
            Set<String> keys = Main.levels.keySet();
            for (String key : keys) {
                level = Main.levels.get(key);
                PlayerData data = level.datas.get(player.getName());
                if (data == null) {
                    data = new PlayerData(level, Bukkit.getOfflinePlayer(player.getName()));
                    DataGeneral.insertPlayerData(data);
                }
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        CmdExecutor.onCommand(sender, command, label, args);
        return true;
    }
}
