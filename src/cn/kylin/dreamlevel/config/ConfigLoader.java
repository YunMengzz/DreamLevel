package cn.kylin.dreamlevel.config;

import cn.kylin.dreamlevel.Main;
import cn.kylin.dreamlevel.covervanilla.CoverManager;
import cn.kylin.dreamlevel.nms.NmsUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.*;

public class ConfigLoader {

    public static String getVersion(){
        return Main.plugin.getConfig().getString("version") == null ? Main.version : Main.plugin.getConfig().getString("version") ;
    }


    public static void loadConfig(){
        checkVersion();
        FileConfiguration config = Main.plugin.getConfig();
        config.set("version", Main.version);
        Main.enableActionBar = config.getBoolean("actionBar.enable");
        NmsUtils.levels = config.getStringList("actionBar.level");
        CoverManager.coverVanilla = config.getBoolean("coverVanilla.enable");
        CoverManager.coverLevel = config.getString("coverVanilla.level");

        Main.kuaFu = config.getBoolean("kuafu");
    }

    public static Properties loadMySQL(ConfigurationSection section){
        Properties prop = new Properties();
        prop.setProperty("enable", section.getBoolean("enable") + "");
        prop.setProperty("host", section.getString("host"));
        prop.setProperty("port", section.getString("port"));
        prop.setProperty("username", section.getString("username"));
        prop.setProperty("password", section.getString("password"));
        prop.setProperty("database", section.getString("database"));
        return prop;
    }

    public static void checkVersion(){
        FileConfiguration config = Main.plugin.getConfig();
        // 2.1与2.1.1更新到2.1.2
        if ("V2.1".equalsIgnoreCase(config.getString("version")) || "V2.1.1".equalsIgnoreCase(config.getString("version"))) {
            // 自动更新
            config.set("version", "2.1.2");
            config.set("kuafu", false);
            File langFile = new File(Main.plugin.getDataFolder(), "lang.yml");
            YamlConfiguration lang = YamlConfiguration.loadConfiguration(langFile);
            lang.set("kuaFuStart", "[DreamLevel]正在同步数据...");
            lang.set("kuaFuEnd", "[DreamLevel]同步数据成功！");
            Main.plugin.getLogger().info("DreamLevel > 成功自动更新至2.1.2！");
        }

    }

}
