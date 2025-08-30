package cn.kylin.dreamlevel.api.utils;

import cn.kylin.dreamlevel.api.data.Level;
import cn.kylin.dreamlevel.config.LevelLoader;
import org.bukkit.configuration.ConfigurationSection;

public class ConfigUtils {

    public static Level loadLevel(ConfigurationSection section){
        return LevelLoader.loadLevel(section);
    }


}
