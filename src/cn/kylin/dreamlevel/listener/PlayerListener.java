package cn.kylin.dreamlevel.listener;

import cn.kylin.dreamlevel.Main;
import cn.kylin.dreamlevel.api.DlApi;
import cn.kylin.dreamlevel.api.data.Level;
import cn.kylin.dreamlevel.api.data.PlayerData;
import cn.kylin.dreamlevel.config.LangLoader;
import cn.kylin.dreamlevel.covervanilla.CoverManager;
import cn.kylin.dreamlevel.data.DataGeneral;
import cn.kylin.dreamlevel.api.event.DreamEventCaller;
import cn.kylin.dreamlevel.api.event.DreamPlayerGetExpEvent;
import cn.kylin.dreamlevel.api.event.DreamPlayerLevelUpEvent;
import cn.kylin.dreamlevel.permissions.PerManager;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;

public class PlayerListener implements Listener {

    // true可以发送 false不可发送
    public static Map<String, Boolean> map = new HashMap<>();

    /**
     * 检查是否都存在data
     * 跨服请勿使用离线指令功能！
     * @param event
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        Set<String> keySet = Main.levels.keySet();
        map.put(player.getName(), true);
        Level level;
        for (String s : keySet) {
            level = Main.levels.get(s);
            PlayerData data = level.datas.get(player.getName());
            if (data == null) {
                DataGeneral.insertPlayerData(new PlayerData(level, Bukkit.getOfflinePlayer(player.getName())));
            }
        }

        // 支持跨服
        if (Main.kuaFu) {
            LangLoader.sendMsg(player, LangLoader.getKuaFuStart(player), "msg");
            Bukkit.getScheduler().runTaskLater(Main.plugin,()->{
                Player p = event.getPlayer();
                for (String levelName : Main.levels.keySet()) {
                    DataGeneral.getPlayerData(p, Main.levels.get(levelName));
                }
                // cover
                Level l = Main.levels.get(CoverManager.coverLevel);
                if (l != null) {
                    PlayerData data = l.datas.get(player.getName());
                    CoverManager.updateExp(data);
                }
                LangLoader.sendMsg(player, LangLoader.getKuaFuEnd(player), "msg");
            }, 20);
        } else {
            // cover
            level = Main.levels.get(CoverManager.coverLevel);
            if (level != null) {
                PlayerData data = level.datas.get(player.getName());
                CoverManager.updateExp(data);
            }
        }


    }


    /**
     * 保存数据
     * @param event
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
        Player player = event.getPlayer();
        Set<String> keySet = Main.levels.keySet();
        Level level;
        for (String s : keySet) {
            level = Main.levels.get(s);
            PlayerData data = level.datas.get(player.getName());
            DataGeneral.updatePlayerData(data);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerLevelUp(DreamPlayerLevelUpEvent event){
        if (event.isCancelled()) return;
        // checkPer
        PlayerData data = event.getPlayerData();
        int currentLevel = data.getCurrentLevel();
        boolean b = PerManager.checkHasPermission(data);
        if (!b) {
            sendNoLevelUpPerMsg(data.getPlayer().getPlayer());
            event.setCancelled(true);
            return;
        }

        executeCmd(data);

    }

    /**
     * 多倍
     * @param event
     */
    @EventHandler
    public void onPlayerGetExp(DreamPlayerGetExpEvent event){
        PlayerData data = event.getPlayerData();
        if ()
        if (data.time != 0) {
            long getExp = event.getGetExp();
            event.setGetExp(getExp * (long)data.times);
        }

    }

    /**
     * 捡经验球
     * @param event
     */
    @EventHandler
    public void onPlayerExpChange(PlayerExpChangeEvent event){
        long exp = event.getAmount();
        Player player = event.getPlayer();
        Set<String> keySet = Main.levels.keySet();
        Level level;
        PlayerData data;
        for (String s : keySet) {
            level = Main.levels.get(s);
            if (level.getExpBall() <= 0) break;
            data = level.datas.get(player.getName());
            DreamPlayerGetExpEvent e = DreamEventCaller.playerGetExp(data, exp);
            data.giveExp(e.getGetExp() * level.getExpBall());
            DreamEventCaller.playerDataUpdate(data);
        }
    }


    public static void sendNoLevelUpPerMsg(Player player){
        Boolean b = map.get(player.getName());
        if (b == null || b){
            LangLoader.sendMsg(player, LangLoader.getNoLevelupPer(player), "msg");
            map.put(player.getName(), false);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerExpChange2(PlayerExpChangeEvent event){
        if (CoverManager.coverVanilla) {
            event.setAmount(0);
        }
    }



    private static void executeCmd(PlayerData data){
        Level level = data.getLevel();
        int l = data.getCurrentLevel();
        int uLevel = l + 1;
        Map<Integer, List<String>> map = level.getUpGradeCmd();
        if (map.containsKey(uLevel)) {
            List<String> cmds = map.get(uLevel);
            CommandSender sender = Bukkit.getConsoleSender();
            for (String str : cmds) {
                if (str.startsWith("player:") && data.getPlayer().getPlayer() != null) {
                    // 当前缀为player:并且玩家在线时
                    sender = data.getPlayer().getPlayer();
                    str = str.substring(7).trim();
                } else if (str.startsWith("console:")) {
                    str = str.substring(8).trim();
                }
                String cmd = str.replaceAll("<playerName>", data.getPlayer().getName()).replaceAll("<uLevel>", uLevel + "").replaceAll("<currentLevel>", data.getCurrentLevel() + "");
                String s = PlaceholderAPI.setPlaceholders(data.getPlayer(), cmd);
                boolean b = Bukkit.dispatchCommand(sender, s);
            }
        }

        // everyUpgradeCmd
        List<String> e = level.getEveryUpgradeCmd();
        if (e != null) {
            CommandSender sender = Bukkit.getConsoleSender();
            for (String str : e) {
                String cmd = str.replaceAll("<playerName>", data.getPlayer().getName()).replaceAll("<uLevel>", l + 1 + "").replaceAll("<currentLevel>", data.getCurrentLevel() + "");
                String s = PlaceholderAPI.setPlaceholders(data.getPlayer(), cmd);
                Bukkit.dispatchCommand(sender, s);
            }
        }
    }

}
