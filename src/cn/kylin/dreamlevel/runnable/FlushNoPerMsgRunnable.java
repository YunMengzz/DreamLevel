package cn.kylin.dreamlevel.runnable;

import cn.kylin.dreamlevel.Main;
import cn.kylin.dreamlevel.listener.PlayerListener;
import org.bukkit.Bukkit;

import java.util.Set;

public class FlushNoPerMsgRunnable implements Runnable {

    @Override
    public void run() {
        Set<String> keys = PlayerListener.map.keySet();
        for (String key : keys) {
            PlayerListener.map.put(key, true);
        }
    }

    public static void runTask(){
        Bukkit.getScheduler().runTaskTimer(Main.plugin, new FlushNoPerMsgRunnable(), 3*1200, 3*1200);
    }
}
