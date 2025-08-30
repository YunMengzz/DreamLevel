package cn.kylin.dreamlevel.config;

import cn.kylin.dreamlevel.Main;
import cn.kylin.dreamlevel.api.data.Level;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.*;

public class LevelLoader {

    private static final File root = new File(Main.plugin.getDataFolder(), "level");

    public static void load(){
        if (!root.exists()) root.mkdirs();
        File def = new File(root, "default.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(def);
        String temp = "DreamLevel.";
        if (config.get(temp + "levelName") == null) {
            config.set(temp + "levelName", "dl");
            config.set(temp + "minLevel", 0);
            config.set(temp + "maxLevel", 100);
            config.set(temp + "upGradeExp", Arrays.asList("5:10", "10:50", "100:100"));
            config.set(temp + "expBall", 1);
            config.set(temp + "permissions", Arrays.asList("10:ten"));
            config.set(temp + "upGradeCmd.10", Arrays.asList("msg <playerName> &6十级了！", "say 2"));
            config.set(temp + "upGradeCmd.20", Arrays.asList("say 1"));
            config.set(temp + "upGradeCmd.e", Arrays.asList("msg <playerName> &b哇哦，恭喜你！你升到<uLevel>级了！"));
        }

        forLevel(root);
    }

    public static Level loadLevel(ConfigurationSection section){
        String levelName = section.getString("levelName");
        int minLevel = section.getInt("minLevel");
        int maxLevel = section.getInt("maxLevel");
        List<String> _upGradeExp = section.getStringList("upGradeExp");
        int expBall = section.getInt("expBall");
        List<String> _permissions = section.getStringList("permissions");
        ConfigurationSection cmdSection = section.getConfigurationSection("upGradeCmd");

        Map<Integer, Long> upGradeExp = parseUpgradeExpList(_upGradeExp);
        Map<Integer, String> permissions = parseUpgradePerList(_permissions, levelName);
        Map<Integer, List<String>> upGradeCmd = parseCmd(cmdSection);
        List<String> everyUpgradeCmd = parseEveryUpGradeCmd(cmdSection);

        Level level = new Level(levelName, maxLevel, upGradeExp, expBall, permissions, upGradeCmd);
        level.setMinLevel(minLevel);
        level.setEveryUpgradeCmd(everyUpgradeCmd);

        return level;
    }

    private static Map<Integer, Long> parseUpgradeExpList(List<String> list){
        Map<Integer, Long> upgradeExp = new LinkedHashMap<>();
        for (String str : list) {
            String[] args = str.split(":");
            if (args.length != 2) {
                continue;
            }
            upgradeExp.put(Integer.parseInt(args[0]), Long.parseLong(args[1]));
        }

        return upgradeExp;
    }

    private static Map<Integer, String> parseUpgradePerList(List<String> list, String levelName){
        Map<Integer, String> upgradePer = new HashMap<>();
        for (String str : list) {
            String[] args = str.split(":");
            if (args.length != 2) {
                break;
            }
            upgradePer.put(Integer.parseInt(args[0]), "dream.level." + levelName + "." + args[1]);
        }

        return upgradePer;
    }

    private static Map<Integer, List<String>> parseCmd(ConfigurationSection section){
        Set<String> keys = section.getKeys(false);

        List<String> cmds = null;
        Map<Integer, List<String>> map = new HashMap<>();
        for (String key : keys) {
            cmds = section.getStringList(key);
            if (cmds != null && (!"e".equalsIgnoreCase(key))) {
                int level = Integer.parseInt(key);
                map.put(level, cmds);
            }
        }

        return map;
    }

    private static List<String> parseEveryUpGradeCmd(ConfigurationSection section){
        return section.getStringList("e");
    }

    private static void forLevel(File file){
        if (file == null) return;
        if (!file.exists()) return;
        File[] files = file.listFiles();
        if (files == null) return;
        for (int i = 0; i < files.length; i++) {
            try {
                File f = files[i];
                if (f.isDirectory()) {
                    forLevel(f);
                } else if (f.getName().endsWith(".yml")) {
                    YamlConfiguration conf = YamlConfiguration.loadConfiguration(f);
                    Set<String> keys = conf.getKeys(false);
                    if (keys != null) {
                        for (String key : keys) {
                            ConfigurationSection section = conf.getConfigurationSection(key);
                            Level level = loadLevel(section);
                            Main.levels.put(level.getLevelName(), level);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

}
