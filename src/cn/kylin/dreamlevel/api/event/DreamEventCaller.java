package cn.kylin.dreamlevel.api.event;


import cn.kylin.dreamlevel.api.data.PlayerData;
import cn.kylin.dreamlevel.covervanilla.CoverManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class DreamEventCaller {
    // Player Online：update Outside   Player Offline：update Inside and Outside
    // PlayerQuit saveData
    public static void playerDataUpdate(PlayerData data){
        OfflinePlayer p = data.getPlayer();
        if (p != null) {
            // coverVanilla
            CoverManager.updateExp(data);
        }
    }
    public static DreamPlayerLevelUpEvent playerLevelUp(PlayerData data){
        DreamPlayerLevelUpEvent event = new DreamPlayerLevelUpEvent();
        event.setPlayerData(data);
        Bukkit.getPluginManager().callEvent(event);
        return event;
    }

    public static DreamPlayerGetExpEvent playerGetExp(PlayerData data, long exp){
        DreamPlayerGetExpEvent event = new DreamPlayerGetExpEvent();
        event.setPlayerData(data);
        event.setGetExp(exp);
        Bukkit.getPluginManager().callEvent(event);
        return event;
    }
}
