package cn.kylin.dreamlevel.api.data;

import cn.kylin.dreamlevel.Main;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Level {
    private String levelName;
    private int maxLevel;
    private Map<Integer, Long> upGradeExp;
    private int expBall;
    // dream.level.[levelname].[pername]
    private Map<Integer, String> permissions;
    private Map<Integer, List<String>> upGradeCmd;
    private List<String> everyUpgradeCmd;
    private int minLevel;
    public Map<String, PlayerData> datas;

    public Level(String levelName, int maxLevel, Map<Integer, Long> upGradeExp, int expBall, Map<Integer, String> permissions, Map<Integer, List<String>> upGradeCmd) {
        this.levelName = levelName;
        this.maxLevel = maxLevel;
        this.upGradeExp = upGradeExp;
        this.expBall = expBall;
        this.permissions = permissions;
        this.upGradeCmd = upGradeCmd;
        datas = new HashMap<>();
        Main.levels.put(this.levelName, this);
    }

    public String getLevelName() {
        return levelName;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public int getExpBall() {
        return expBall;
    }

    public int getMinLevel() {
        return minLevel;
    }

    public void setMinLevel(int minLevel) {
        this.minLevel = minLevel;
    }

    public Map<Integer, String> getPermissions() {
        return permissions;
    }

    // 获取升级执行指令
    public Map<Integer, List<String>> getUpGradeCmd() {
        return upGradeCmd;
    }


    // 获取升级经验
    public long getUpGradeExp(int currentLevel) {
        Set<Integer> keys = upGradeExp.keySet();
        if (currentLevel >= maxLevel) {
            return Integer.MAX_VALUE;
        }
        for (int level : keys) {
            if (level > currentLevel) {
                return upGradeExp.get(level);
            }
        }
        return Integer.MAX_VALUE;
    }

    public List<String> getEveryUpgradeCmd() {
        return everyUpgradeCmd;
    }

    public void setEveryUpgradeCmd(List<String> everyUpgradeCmd) {
        this.everyUpgradeCmd = everyUpgradeCmd;
    }
}
