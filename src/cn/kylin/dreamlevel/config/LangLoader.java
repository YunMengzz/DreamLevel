package cn.kylin.dreamlevel.config;

import cn.kylin.dreamlevel.Main;
import cn.kylin.dreamlevel.nms.NmsUtils;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LangLoader {

    public static File langFile;

    public static String noCmdPer;

    public static String help;
    public static String give;
    public static String take;
    public static String set;
    public static String duoBeiInfo;


    private static String info;
    private static String levelUp;
    private static String getExp;
    private static String allPlayerDuobei;
    private static String noLevelupPer;
    private static String kuaFuStart;
    private static String kuaFuEnd;
    private static String onMobDeathGiveExpMsg;

    public static void init(){
        langFile = new File(Main.plugin.getDataFolder(), "lang.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(langFile);
        noCmdPer = ChatColor.translateAlternateColorCodes('&', config.getString("language.nocmdper"));
        noLevelupPer = chatColor(config.getString("language.nolevelupper"));
        levelUp = chatColor(config.getString("language.levelup"));
        getExp = chatColor(config.getString("language.getexp"));
        info = parseInfo(config.getStringList("language.info"));
        allPlayerDuobei = chatColor(config.getString("language.allPlayerDuobei"));
        kuaFuStart = chatColor(config.getString("language.kuaFuStart"));
        kuaFuEnd = chatColor(config.getString("language.kuaFuEnd"));
        onMobDeathGiveExpMsg = chatColor(config.getString("language.onMobDeathGiveExpMsg"));
        initHelp();
        Main.plugin.getLogger().info(ChatColor.translateAlternateColorCodes('&', "&a[DreamLevel]语言文件加载成功！"));
    }

    private static void initHelp(){
        List<String> helpList = new ArrayList<>();
        helpList.add("&7=============== &bDreamLevel &7===============");
        helpList.add("&f/dl get [playername] [levelname] &7- &c获取玩家的等级信息");
        helpList.add("&f/dl give &7- &c给予玩家等级/经验的帮助");
        helpList.add("&f/dl take &7- &c删除玩家等级/经验的帮助");
        helpList.add("&f/dl set &7- &c设置玩家等级/经验的帮助");
        helpList.add("&f/dl duobei &7- &c关于多倍经验的帮助");
        helpList.add("&f/dl reload &7- &c重载插件");
        helpList.add("&7=============== &b启梦团队 &7================");
        help = parseInfo(helpList);
        ArrayList<String> giveList = new ArrayList<>();
        giveList.add("&7=============== &bDreamLevel &7===============");
        giveList.add("&f/dl give [playername] [levelname] [amount][L] [true/false]&7- &c给玩家&b[playername]的[levelname]等级 &a[amount] &c经验/等级");
        giveList.add("&4[amount] 后面加L是增加等级，不加是增加经验。");
        giveList.add("&4[true/false] 为是否触发多倍经验 填true触发 false不触发 默认为true 只针对经验 等级无效");
        giveList.add("&d例:");
        giveList.add("&f/dl give player dl 10 &c增加玩家player 的dl等级 10点经验");
        giveList.add("&f/dl give player dl 10L &c增加玩家player 的dl等级 10级");
        giveList.add("&f/dl give player dl 10 true &c增加玩家player 的dl等级 10点经验 触发多倍经验");
        giveList.add("&f/dl give player dl 10 false &c增加玩家player 的dl等级 10点经验 不触发多倍经验");
        giveList.add("&7=============== &b启梦团队 &7================");
        give = parseInfo(giveList);

        ArrayList<String> takeList = new ArrayList<>();
        takeList.add("&7=============== &bDreamLevel &7===============");
        takeList.add("&f/dl take [playername] [levelname] [amount][L] &7- &c删除玩家&b<playername>的[levelname]等级 &a[amount] &c经验/等级");
        takeList.add("&4[amount] 后面加L是删除等级，不加是删除经验。");
        takeList.add("&d例: ");
        takeList.add("&f/dl take player dl 10 &c删除玩家player的dl等级 10点经验");
        takeList.add("&f/dl take player dl 10L &c删除玩家player的dl等级 10级");
        takeList.add("&7=============== &b启梦团队 &7================");
        take = parseInfo(takeList);

        ArrayList<String> setList = new ArrayList<>();
        setList.add("&7=============== &bDreamLevel &7===============");
        setList.add("&f/dl set [playername] [levelname] [amount][L]&7- &c设置玩家&b<playername>的[levelname]等级 &a[amount] &c经验/等级");
        setList.add("&4[amount] 后面加L是设置等级，不加是设置经验。");
        setList.add("&d例: ");
        setList.add("&f/dl set player dl 10 &c设置玩家player的dl等级 10点经验");
        setList.add("&f/dl set player dl 10L &c设置玩家player的dl等级 10级");
        setList.add("&7=============== &b启梦团队 &7================");
        set = parseInfo(setList);

        ArrayList<String> duoBeiList = new ArrayList<>();
        duoBeiList.add("&7=============== &bDreamLevel &7===============");
        duoBeiList.add("&fduobei set [playername] [levelname] [times] [time] &7- &c设置玩家<playername>的[levelname]等级获得经验的倍数为<times>,持续<time>分钟  //playername levelname  可以用*代表全部");
        duoBeiList.add("&d例: ");
        duoBeiList.add("&f/dl duobei set player dl 2 10 &c设置玩家player的dl等级获得经验的倍数为2倍,持续10分钟");
        duoBeiList.add("&7=============== &b启梦团队 &7================");
        duoBeiInfo = parseInfo(duoBeiList);
    }


    private static String parseInfo(List<String> list){
        StringBuilder info = new StringBuilder();
        info.append("\n");
        for (String s : list) {
            info.append(ChatColor.translateAlternateColorCodes('&', s)).append(" \n");
        }
        // 删去最后一个\n
        return info.substring(0, info.length() - 3);
    }

    private static String chatColor(String str){
        if (str == null) return null;
        return ChatColor.translateAlternateColorCodes('&', str);
    }



    public static String getInfo(OfflinePlayer player) {
        return PlaceholderAPI.setPlaceholders(player, info);
    }

    public static String getLevelUp(Player player) {
        return PlaceholderAPI.setPlaceholders(player, levelUp);
    }

    public static String getGetExp(OfflinePlayer player) {
        return PlaceholderAPI.setPlaceholders(player, getExp);
    }

    public static String getAllPlayerDuobei(Player player) {
        return PlaceholderAPI.setPlaceholders(player, allPlayerDuobei);
    }


    public static String getNoLevelupPer(Player player) {
        return PlaceholderAPI.setPlaceholders(player, noLevelupPer);
    }

    public static String getKuaFuStart(Player player) {
        return PlaceholderAPI.setPlaceholders(player, kuaFuStart);
    }

    public static String getKuaFuEnd(Player player) {
        return PlaceholderAPI.setPlaceholders(player, kuaFuEnd);
    }

    public static String getOnMobDeathGiveExpMsg(Player player) {
        return PlaceholderAPI.setPlaceholders(player, onMobDeathGiveExpMsg);
    }

    /**
     * sendMsg Method  def has two value:  msg and actionbar
     * @param player
     * @param msg
     * @param def
     */
    public static void sendMsg(Player player, String msg, String def){
        if (player == null) return;
        if (msg.startsWith("msg:")) {
            player.sendMessage(msg.substring(4));
        } else if (msg.startsWith("actionbar:")){
            NmsUtils.sendActionBar(player, msg.substring(10));
        } else {
            if ("actionbar".equalsIgnoreCase(def)) {
                NmsUtils.sendActionBar(player, msg);
            } else {
                player.sendMessage(msg);
            }
        }

    }
}
