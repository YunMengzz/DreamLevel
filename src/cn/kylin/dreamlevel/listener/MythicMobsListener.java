package cn.kylin.dreamlevel.listener;

import cn.kylin.dreamlevel.api.DlApi;
import cn.kylin.dreamlevel.api.data.PlayerData;
import cn.kylin.dreamlevel.config.LangLoader;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent;
import io.lumine.xikage.mythicmobs.mobs.MythicMob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.Random;

public class MythicMobsListener implements Listener {

    @EventHandler
    public void onMythicMobDeathEvent(MythicMobDeathEvent event){
        MythicMob mob = event.getMobType();
        List<String> drops = mob.getDrops();
        if (event.getKiller() instanceof Player) {
            Player p = (Player) event.getKiller();
            for (String s : drops) {
                if (s != null) {
                    String str = s.trim();
                    String[] args = str.split(" ");
                    if (args.length == 5 && "dl".equalsIgnoreCase(args[0]) && "exp".equalsIgnoreCase(args[1])) {
                        String _exp = args[2];
                        String[] split = _exp.split("-");
                        if (split.length == 2) {
                            int minExp = Integer.parseInt(split[0]);
                            int maxExp = Integer.parseInt(split[1]);
                            if (maxExp < minExp) {
                                int a = minExp;
                                minExp = maxExp;
                                maxExp = a;
                            }
                            int i = maxExp - minExp;
                            Random r = new Random();
                            int i1 = r.nextInt(i + 1);
                            int uExp = minExp + i1;
                            String levelName = args[3];
                            double v = r.nextDouble();
                            if (v <= Double.parseDouble(args[4])) {
                                PlayerData playerData = DlApi.getPlayerData(p.getName(), levelName);
                                playerData.giveExp(uExp);
                                String msg = LangLoader.getOnMobDeathGiveExpMsg(p)
                                        .replaceAll("<levelName>", playerData.getLevel().getLevelName())
                                        .replaceAll("<getExp>", uExp + "");
                                LangLoader.sendMsg(p, msg, "msg");
                            }
                        }
                    }
                }
            }
        }
    }

}
