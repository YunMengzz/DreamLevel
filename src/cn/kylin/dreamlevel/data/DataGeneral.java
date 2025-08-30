package cn.kylin.dreamlevel.data;

import cn.kylin.dreamlevel.Main;
import cn.kylin.dreamlevel.api.data.Level;
import cn.kylin.dreamlevel.api.data.PlayerData;
import cn.kylin.dreamlevel.data.database.DreamDatabase;
import cn.kylin.dreamlevel.data.yml.YmlManager;
import org.bukkit.OfflinePlayer;

import java.util.Properties;

/**
 * 汇总Database和yml操作 自动判断用哪种的api类
 */
public class DataGeneral {

    private static boolean enable;

    public static void init(Properties prop){
        enable = Main.enableMySQL;
        if (enable) {
            DreamDatabase.init(prop);
        } else {
            YmlManager.init();
        }
    }

    public static void insertPlayerData(PlayerData data){
        if (enable) {
            DreamDatabase.insertPlayerData(data);
        } else YmlManager.insertPlayerData(data);
    }

    public static void updatePlayerData(PlayerData data){
        if (enable) {
            DreamDatabase.updatePlayerData(data);
        } else YmlManager.updatePlayerData(data);
    }

    public static void deletePlayerData (PlayerData data){
        if (enable) {
            DreamDatabase.deletePlayerData(data);
        } else YmlManager.deletePlayerData(data);

    }

    public static void getPlayerData(OfflinePlayer player, Level level){
        if (enable){
            DreamDatabase.getPlayerData(player, level);
        } else YmlManager.getPlayerData(player, level);

    }

}
