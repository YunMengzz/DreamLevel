package cn.kylin.dreamlevel.permissions;

import cn.kylin.dreamlevel.api.data.PlayerData;
import org.bukkit.entity.Player;

import java.util.Map;

public class PerManager {

    public static boolean checkHasPermission(PlayerData data){
        Player player = data.getPlayer().getPlayer();
        if (player == null) return false;
        return hasPer(data);
    }

    private static boolean hasPer(PlayerData data){
        Map<Integer, String> pers = data.getLevel().getPermissions();
        Player p = data.getPlayer().getPlayer();

        int currentLevel = data.getCurrentLevel();
        String per = pers.get(currentLevel + 1);
        if (per == null) return true;
        if (p == null) return false;
        return p.hasPermission(per);
    }


}
