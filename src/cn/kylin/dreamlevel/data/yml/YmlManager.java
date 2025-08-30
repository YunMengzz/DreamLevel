package cn.kylin.dreamlevel.data.yml;

import cn.kylin.dreamlevel.Main;
import cn.kylin.dreamlevel.api.data.Level;
import cn.kylin.dreamlevel.api.data.PlayerData;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

/**
 * get insert update delete getall
 */
public class YmlManager {

    public static final File root = new File(Main.plugin.getDataFolder(), "data");

    public static void init(){
        if (!root.exists()) {
            root.mkdirs();
        }
        getAllPlayerData();
    }

    public static PlayerData getPlayerData(OfflinePlayer player, Level level){
        if (player == null || level == null) return null;
        File file = new File(root, level.getLevelName() + "/" + player.getName() + ".yml");
        if (!file.exists()) return null;
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        PlayerData data = (PlayerData) config.get("data");
        return data;
    }

    public static void insertPlayerData(PlayerData data){
        if (data == null) return;
        File file = new File(root, data.getLevel().getLevelName() + "/" + data.getPlayer().getName() + ".yml");
        if (file.exists()) file.delete();
        file.getParentFile().mkdirs();
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        config.set("data", data);
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void updatePlayerData(PlayerData data){
        insertPlayerData(data);
    }

    public static void deletePlayerData(PlayerData data){
        if (data == null) return;
        File file = new File(root, data.getLevel().getLevelName() + "/" + data.getPlayer().getName() + ".yml");
        if (!file.exists()) return;
        file.delete();
    }

    public static void getAllPlayerData(){
        if (!root.exists()) return;
        forFolder(root);
    }

    private static void forFolder(File file){
        if (file == null) return;
        File[] files = file.listFiles();
        if (files == null) return;
        for (int i = 0; i < files.length; i++) {
            File f = files[i];
            if (f.isDirectory()) {
                forFolder(f);
            } else if(f.getName().endsWith(".yml")) {
                YamlConfiguration config = YamlConfiguration.loadConfiguration(f);
                PlayerData data = (PlayerData) config.get("data");
            }
        }
    }

}
