package cn.kylin.dreamlevel.command;

import cn.kylin.dreamlevel.Main;
import cn.kylin.dreamlevel.api.data.Level;
import cn.kylin.dreamlevel.api.data.PlayerData;
import cn.kylin.dreamlevel.config.LangLoader;
import cn.kylin.dreamlevel.api.event.DreamEventCaller;
import cn.kylin.dreamlevel.api.event.DreamPlayerGetExpEvent;
import cn.kylin.dreamlevel.api.event.DreamPlayerLevelUpEvent;
import cn.kylin.dreamlevel.listener.PlayerListener;
import cn.kylin.dreamlevel.permissions.PerManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Set;

/*
give [playername] [levelname] [数值]     //数值后不加L为经验 加L为给等级   增加
set [playername] [levelname] [数值]     //数值后不加L为经验 加L为给等级   设置
take [playername] [levelname] [数值]    减少
help
reload
get [playername] [levelname]    获取
duobei
    set [playername] [levelname] [times] [time]  // playername levelname  可以用*代表全部
    get [playername] [levelname]
*/
public class CmdExecutor{
    public static boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (args == null) return true;
        if (args.length == 0) {
            help(sender);
        } else if ("help".equalsIgnoreCase(args[0])) {
            help(sender);
        } else if ("set".equalsIgnoreCase(args[0])) {
            if (!sender.isOp()) {
                sender.sendMessage(LangLoader.noCmdPer);
                return true;
            }
            set(sender, args);
        } else if ("take".equalsIgnoreCase(args[0])) {
            if (!sender.isOp()) {
                sender.sendMessage(LangLoader.noCmdPer);
                return true;
            }
            take(sender, args);
        }else if ("give".equalsIgnoreCase(args[0])) {
            if (!sender.isOp()) {
                sender.sendMessage(LangLoader.noCmdPer);
                return true;
            }
            give(sender, args);
        }else if ("reload".equalsIgnoreCase(args[0])) {
            if (!sender.isOp()) {
                sender.sendMessage(LangLoader.noCmdPer);
                return true;
            }
            reload(sender);
        }else if ("get".equalsIgnoreCase(args[0])) {
            get(sender, args);
        }else if ("duobei".equalsIgnoreCase(args[0])) {
            if (!sender.isOp()) {
                sender.sendMessage(LangLoader.noCmdPer);
                return true;
            }
            duobei(sender, args);
        }else help(sender);
        return true;
    }

    private static void help(CommandSender sender){
        sender.sendMessage(LangLoader.help);
    }

    // set [playername] [levelname] [数值]     //数值后不加L为经验 加L为给等级   设置
    private static void set(CommandSender sender, String[] args){
        if (args.length == 4) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
            if (!kuaFu(player)) {
                sender.sendMessage("跨服不支持离线指令 如不需跨服可在config.yml中配置");
                return;
            }
            Level level = Main.levels.get(args[2]);
            if (player != null && level != null) {
                PlayerData data = level.datas.get(args[1]);
                String value = args[3];
                if (value != null) {
                    try {
                        if (value.endsWith("l") || value.endsWith("L")) {
                            value = value.substring(0, value.length() - 1);
                            int i = Integer.parseInt(value);
                            if (i > level.getMaxLevel()) {
                                sender.sendMessage("大于最大等级 已设置成最大等级: " + level.getMaxLevel());
                                data.setCurrentLevel(level.getMaxLevel());
                            } else if (i < level.getMinLevel()) {
                                sender.sendMessage("小于最小等级 已设置成最小等级: " + level.getMinLevel());
                                data.setCurrentLevel(level.getMinLevel());
                            } else {
                                data.setCurrentLevel(i);
                                sender.sendMessage("设置等级成功！");
                            }
                            DreamEventCaller.playerDataUpdate(data);
                            return;
                        } else  {
                            long i = Long.parseLong(value);
                            data.setCurrentExp(i);
                            sender.sendMessage("设置经验成功！");
                            DreamEventCaller.playerDataUpdate(data);
                            return;
                        }
                    } catch (Exception e) {
                    }
                }
            }
        }
        sender.sendMessage(LangLoader.set);
    }

    // take [playername] [levelname] [数值]    减少
    private static void take(CommandSender sender, String[] args){
        if (args.length == 4) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
            if (!kuaFu(player)) {
                sender.sendMessage("跨服不支持离线指令 如不需跨服可在config.yml中配置");
                return;
            }
            Level level = Main.levels.get(args[2]);
            if (level != null && player != null) {
                PlayerData data = level.datas.get(args[1]);
                if (data != null) {
                    String value = args[3];
                    try{
                        if (value.endsWith("L") && value.endsWith("l")) {
                            String str = value.substring(0, value.length() - 1);
                            int i = Integer.parseInt(str);
                            //take level
                            int i1 = data.addLevel(i);
                            if (i1 == -1) {
                                sender.sendMessage("大于最大等级 已设置成最大等级: " + level.getMaxLevel());
                            } else if (i1 == -2) {
                                sender.sendMessage("小于最小等级 已设置成最小等级: " + level.getMinLevel());
                            } else {
                                sender.sendMessage("减少等级成功！");
                            }
                            return;
                        } else {
                            int i = Integer.parseInt(value);
                            data.takeExp(i);
                            sender.sendMessage("减少经验成功！");
                            return;
                        }
                    } catch (Exception e) {
                    }
                }
            }
        }
        sender.sendMessage(LangLoader.take);
    }
                                        // 是否触发多倍经验
    // give [playername] [levelname] [数值] [true/false]     //数值后不加L为经验 加L为给等级   增加
    private static void give(CommandSender sender, String[] args){
        if (args.length == 4 || args.length == 5) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
            if (!kuaFu(player)) {
                sender.sendMessage("跨服不支持离线指令 如不需跨服可在config.yml中配置");
                return;
            }
            Level level = Main.levels.get(args[2]);
            if (player != null && level != null) {
                PlayerData data = level.datas.get(args[1]);
                String value = args[3];
                if (value != null) {

                    try {
                        if (value.endsWith("L") || value.endsWith("l")) {
                            String str = value.substring(0, value.length() - 1);
                            int i = Integer.parseInt(str);
                            for (int j = 0; j < i; j++) {
                                boolean b = PerManager.checkHasPermission(data);
                                if (b) {
                                    data.setCurrentLevel(data.getCurrentLevel() + 1);
                                } else {
                                    Player p = player.getPlayer();
                                    if (p != null) {
                                        PlayerListener.sendNoLevelUpPerMsg(p);
                                    }
                                    sender.sendMessage("无权限升级！");
                                    break;
                                }
                            }
                            sender.sendMessage("增加等级成功！");
                            DreamEventCaller.playerDataUpdate(data);
                            return;
                        } else {
                            long i = Long.parseLong(value);
                            long exp;
                            if (args.length == 5) {
                                if (Boolean.parseBoolean(args[4])) {
                                    DreamPlayerGetExpEvent event = DreamEventCaller.playerGetExp(data, i);
                                    exp = event.getGetExp();
                                } else exp = i;
                            } else {
                                DreamPlayerGetExpEvent event = DreamEventCaller.playerGetExp(data, i);
                                exp = event.getGetExp();
                            }
                            if (data.getPlayer().getPlayer() == null) {
                                sender.sendMessage("玩家未在线，将升级经验存储至saveExp。");
                                DreamEventCaller.playerDataUpdate(data);
                                return;
                            } else {
                                data.giveExp(exp);
                                sender.sendMessage("增加经验成功！ 当前等级: " + data.getCurrentLevel() + "  当前经验: " + data.getCurrentExp());
                                return;
                            }
                        }
                    } catch (Exception e) {
                    }

                }
            }
        }
        sender.sendMessage(LangLoader.give);
    }

    private static void reload(CommandSender sender){
        Main.plugin.reload();
        sender.sendMessage("DreamLevel" + Main.version + "  重载成功！");
    }

    // get [playername] [levelname]    获取
    private static void get(CommandSender sender, String[] args){
        if (args.length == 3) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
            if (!kuaFu(player)) {
                sender.sendMessage("跨服不支持离线指令 如不需跨服可在config.yml中配置");
                return;
            }
            Level level = Main.levels.get(args[2]);
            if (player != null && level != null) {
                String info = LangLoader.getInfo(player);
                PlayerData data = level.datas.get(args[1]);
                String s = info.replaceAll("<playerName>", data.getPlayer().getName())
                        .replaceAll("<levelName>", data.getLevel().getLevelName())
                        .replaceAll("<currentLevel>", data.getCurrentLevel() + "")
                        .replaceAll("<currentExp>", data.getCurrentExp() + "")
                        .replaceAll("<upgradeExp>", data.getUpgradeExp() + "")
                        .replaceAll("<times>", data.times + "")
                        .replaceAll(   "<time>", data.time == 0 ? "无" : data.time + "分钟");
                sender.sendMessage(s);
                return;
            }
            sender.sendMessage("您输入的玩家名/等级名有问题！");
        }
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f/dl get [playername] [levelname] &7- &c获取玩家的等级信息"));

    }

    /*
    duobei set [playername] [levelname] [times] [time]  // playername levelname  可以用*代表全部
     */
    private static void duobei(CommandSender sender, String[] args){
        if (args.length == 6 && "set".equalsIgnoreCase(args[1])) {
            if ("*".equalsIgnoreCase(args[2])) {
                // all player
                Collection<? extends Player> players = Bukkit.getOnlinePlayers();
                if ("*".equalsIgnoreCase(args[3])) {
                    // all level
                    Set<String> keySet = Main.levels.keySet();
                    Level level;
                    for (String s : keySet) {
                        level = Main.levels.get(s);
                        for (Player player : players) {
                            duoBeiUtil(player, level, sender, args);
                        }
                    }
                    sender.sendMessage("全服全等级多倍经验设置成功！ 剩余时间：" + args[5]);

                    for (Player player : players) {
                        String msg = LangLoader.getAllPlayerDuobei(player)
                                .replaceAll("<times>", args[4])
                                .replaceAll("<time>", args[5]);
                        LangLoader.sendMsg(player, msg, "msg");
                    }
                    return;

                } else {
                    Level level = Main.levels.get(args[3]);
                    if (level == null) {
                        sender.sendMessage(LangLoader.duoBeiInfo);
                        return;
                    }
                    for (Player player : players) {
                        duoBeiUtil(player, level, sender, args);
                    }

                    sender.sendMessage("全服全等级多倍经验设置成功！ 剩余时间：" + args[5]);
                    for (Player player : players) {
                        String msg = LangLoader.getAllPlayerDuobei(player)
                                .replaceAll("<times>", args[4])
                                .replaceAll("<time>", args[5]);
                        LangLoader.sendMsg(player, msg, "msg");
                    }
                    return;
                }

            } else if ("*".equalsIgnoreCase(args[3])) {
                OfflinePlayer player = Bukkit.getOfflinePlayer(args[2]);
                Set<String> keySet = Main.levels.keySet();
                if (player == null) {
                    sender.sendMessage(LangLoader.duoBeiInfo);
                    return;
                }
                Level level;
                for (String s : keySet) {
                    level = Main.levels.get(s);
                    duoBeiUtil(player, level, sender, args);
                    return;
                }
            }else {
                OfflinePlayer player = Bukkit.getOfflinePlayer(args[2]);
                if (!kuaFu(player)) {
                    sender.sendMessage("跨服不支持离线指令 如不需跨服可在config.yml中配置");
                    return;
                }
                Level level = Main.levels.get(args[3]);
                duoBeiUtil(player, level, sender, args);
                sender.sendMessage("设置多倍经验成功！ 倍数: " + args[4] + "  时间: " + args[5]);
                return;
            }

        }
        sender.sendMessage(LangLoader.duoBeiInfo);
    }








    private static void duoBeiUtil(OfflinePlayer player, Level level, CommandSender sender, String[] args){
        if (player != null && level != null) {
            PlayerData data = level.datas.get(player.getName());
            int times;
            int time;
            try {
                times = Integer.parseInt(args[4]);
                time = Integer.parseInt(args[5]);
            } catch (Exception e) {
                sender.sendMessage(LangLoader.duoBeiInfo);
                return;
            }
            if (time <= 0 || times == 1) {
                sender.sendMessage("时间或倍数无意义!");
                return;
            }
            if (data.time != 0) {
                // 存在time
                if (data.time >= time) {
                    final double finalTimes = data.times;
                    final String playerName = player.getName();
                    final String levelName = level.getLevelName();
                    data.times += times;
                    Bukkit.getScheduler().runTaskLater(Main.plugin, ()->{
                        OfflinePlayer p = Bukkit.getOfflinePlayer(playerName);
                        Level l = Main.levels.get(levelName);
                        PlayerData playerData = l.datas.get(playerName);
                        if (playerData.time != 0) {
                            playerData.times = finalTimes;
                        }
                        DreamEventCaller.playerDataUpdate(playerData);
                    }, time);
                    DreamEventCaller.playerDataUpdate(data);
                    return;
                } else{
                    // data.time < time
                    final double finalTimes = times;
                    final String playerName = player.getName();
                    final String levelName = level.getLevelName();
                    data.times += times;
                    Bukkit.getScheduler().runTaskLater(Main.plugin, ()->{
                        OfflinePlayer p = Bukkit.getOfflinePlayer(playerName);
                        Level l = Main.levels.get(levelName);
                        PlayerData playerData = l.datas.get(playerName);
                        if (playerData.time != 0) {
                            playerData.times = finalTimes;
                        }
                        if (p.getPlayer() != null) {
                            p.getPlayer().sendMessage("您的" + levelName + "  " + finalTimes +"倍经验已过期 ");
                        }
                        DreamEventCaller.playerDataUpdate(playerData);
                    }, data.time);
                    data.time = time;
                    DreamEventCaller.playerDataUpdate(data);
                    return;
                }
            } else {
                data.time = time;
                data.times = times;
                DreamEventCaller.playerDataUpdate(data);
                return;
            }
        }

        sender.sendMessage(LangLoader.duoBeiInfo);
    }


    public static boolean kuaFu(OfflinePlayer p){
        return !Main.kuaFu || p.getPlayer() != null;
    }

}
