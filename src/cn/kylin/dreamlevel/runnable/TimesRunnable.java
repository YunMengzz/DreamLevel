package cn.kylin.dreamlevel.runnable;

import cn.kylin.dreamlevel.Main;
import cn.kylin.dreamlevel.api.data.Level;
import cn.kylin.dreamlevel.api.data.PlayerData;
import cn.kylin.dreamlevel.api.event.DreamEventCaller;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.Set;

public class TimesRunnable implements Runnable {

    public static BukkitTask task;

    @Override
    public void run() {
        Set<String> keys = Main.levels.keySet();
        Level level;
        for (String key : keys) {
            level = Main.levels.get(key);
            Set<String> keySet = level.datas.keySet();
            for (String s : keySet) {
                PlayerData data = level.datas.get(s);
                if (data.time > 1) {
                    data.time --;
                } else if (data.time == 1) {
                    data.times = 1;
                    data.time = 0;
                    Player p = data.getPlayer().getPlayer();
                    if (p != null) p.sendMessage("您的多倍经验已过期！");
                }
                DreamEventCaller.playerDataUpdate(data);
            }
        }
    }

    public static void runTask(){
        task = Bukkit.getScheduler().runTaskTimer(Main.plugin, new TimesRunnable(), 1200, 1200);
    }
}
