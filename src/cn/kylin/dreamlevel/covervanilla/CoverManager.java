package cn.kylin.dreamlevel.covervanilla;

import cn.kylin.dreamlevel.api.data.PlayerData;
import org.bukkit.entity.Player;

public class CoverManager {

    public static boolean coverVanilla;
    public static String coverLevel;

    public static boolean setLevel(Player player, int level){
        if (player == null) {
            return false;
        }
        player.setLevel(level);
        return true;
    }

    public static boolean setExp(PlayerData data){
        Player player = data.getPlayer().getPlayer();
        if (player != null) {
            float exp = data.getCurrentExp();
            float upgradeExp = data.getUpgradeExp();
            if (upgradeExp == 0) {
                return false;
            }
            if (exp >= upgradeExp) {
                player.setExp(0.99f);
                return true;
            }
            if (exp / upgradeExp < 0) {
                player.setExp(0);
                return true;
            }
            player.setExp(exp / upgradeExp);
            return true;
        }
        return false;
    }

    /**
     * 更新原版经验条同步
     * @param data
     * @return 更新成功返回true  更新失败返回false
     */
    public static boolean updateExp(PlayerData data){
        if (coverVanilla && data != null && data.getLevel().getLevelName().equalsIgnoreCase(coverLevel)) {
            boolean b1 = setLevel(data.getPlayer().getPlayer(), data.getCurrentLevel());
            boolean b2 = setExp(data);
            return b1 && b2;
        }
        return true;
    }

}
