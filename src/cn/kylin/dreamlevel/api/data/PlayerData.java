package cn.kylin.dreamlevel.api.data;

import cn.kylin.dreamlevel.Main;
import cn.kylin.dreamlevel.config.LangLoader;
import cn.kylin.dreamlevel.data.DataGeneral;
import cn.kylin.dreamlevel.api.event.DreamEventCaller;
import cn.kylin.dreamlevel.api.event.DreamPlayerLevelUpEvent;
import cn.kylin.dreamlevel.nms.NmsUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class PlayerData implements ConfigurationSerializable {
    private final Level level;
    private final OfflinePlayer player;
    private int currentLevel;
    private long upgradeExp;
    private long currentExp;
    // 多倍
    // 倍数
    public double times;
    // 时间
    public int time;


    public PlayerData(Level level, OfflinePlayer player){
        this.level = level;
        this.player = player;
        if (player == null || level == null) {
            // delete
            DataGeneral.deletePlayerData(this);
            return;
        }
        setCurrentLevel(level.getMinLevel());
        currentExp = 0;
        times = 1;
        time = 0;
        level.datas.put(player.getName(), this);
    }

    public Level getLevel() {
        return level;
    }

    public OfflinePlayer getPlayer() {
        return player;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public long getUpgradeExp() {
        return upgradeExp;
    }

    public long getCurrentExp() {
        return currentExp;
    }


    /**
     * 需计算upgradeExp
     * @param currentLevel
     */
    public void setCurrentLevel(int currentLevel) {
        this.currentLevel = currentLevel;
        this.upgradeExp = level.getUpGradeExp(currentLevel);
    }

    /**
     *
     * @param addLevel
     * @return  -1 大于最大等级     -2 小于最小等级   0  正常升级
     */
    public int addLevel(int addLevel){
        int maxLevel = level.getMaxLevel();
        int minLevel = level.getMinLevel();
        int uLevel = addLevel + currentLevel;
        if (uLevel > maxLevel) {
            setCurrentLevel(maxLevel);
            return -1;
        } else if (uLevel < minLevel) {
            setCurrentLevel(minLevel);
            return -2;
        }
        setCurrentLevel(uLevel);
        return 0;
    }

    public void setCurrentExp(long currentExp) {
        this.currentExp = currentExp;
    }


    @Override
    public Map<String, Object> serialize() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("levelName", level.getLevelName());
        map.put("playerName", player.getName());
        map.put("currentLevel", currentLevel);
        map.put("currentExp", (Long)currentExp);
        map.put("times", times);
        map.put("time", time);

        return map;
    }


    public static PlayerData deserialize(Map<String, Object> map){
        String levelName = (String) map.get("levelName");
        String playerName = (String) map.get("playerName");
        Level level = Main.levels.get(levelName);
        OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);
        PlayerData data = new PlayerData(level, player);
        data.setCurrentLevel((Integer)map.get("currentLevel"));
        if (map.get("currentExp") instanceof Integer) {
            data.setCurrentExp((Integer)map.get("currentExp"));
        } else data.setCurrentExp((Long) map.get("currentExp"));
        data.times = (Double) map.get("times");
        data.time = (Integer) map.get("time");

        return data;
    }


    public boolean isDuoBei(){
        if (time > 0) {
            return true;
        }
        return false;
    }

    public void giveExp(long exp){
        if (exp < 0) {
            takeExp(-exp);
        }
        final long addExp = exp;
        Player p = this.player.getPlayer();
        if (p == null) {
            return;
        }
        int level = getCurrentLevel();
        long upGradeExp = getUpgradeExp();
        exp += getCurrentExp();
        while (exp > 0){
            if (exp < upGradeExp) {
                setCurrentExp(exp);
                break;
            } else if (exp == upGradeExp) {
                DreamPlayerLevelUpEvent event = DreamEventCaller.playerLevelUp(this);
                if (event.isCancelled()) {
                    setCurrentExp(upGradeExp);
                    sendActionBar(addExp);
                    String msg  = LangLoader.getLevelUp(p)
                            .replaceAll("<playerName>", this.getPlayer().getName())
                            .replaceAll("<levelName>", this.getLevel().getLevelName())
                            .replaceAll("<currentLevel>", this.getCurrentLevel() + "")
                            .replaceAll("<uLevel>", this.getCurrentLevel() + 1 + "");
                    LangLoader.sendMsg(p, msg, "msg");
                    DreamEventCaller.playerDataUpdate(this);
                    return;
                }
                this.setCurrentLevel(currentLevel + 1);
                exp = 0;
            } else {
                DreamPlayerLevelUpEvent event = DreamEventCaller.playerLevelUp(this);
                if (event.isCancelled()) {
                    setCurrentExp(upGradeExp);
                    exp -= upGradeExp;
                    sendActionBar(addExp);
                    DreamEventCaller.playerDataUpdate(this);
                    return;
                }
                this.setCurrentLevel(currentLevel + 1);
                exp = exp - upGradeExp;
                upGradeExp = this.upgradeExp;
            }
        }

        setCurrentExp(exp);

        // actionBar
        sendActionBar(addExp);

        DreamEventCaller.playerDataUpdate(this);

    }

    /**
     * 取经验方法
     * @param exp
     */
    public void takeExp(long exp){
        int level = getCurrentLevel();
        long upGradeExp;
        if (exp <= getCurrentExp()) {
            setCurrentExp(getCurrentExp() - exp);
        } else {
            exp -= getCurrentExp();
            while (exp >0){
                level --;
                if (level < this.level.getMinLevel()) {
                    exp = 0;
                    break;
                }
                upGradeExp = this.level.getUpGradeExp(level);
                if (exp > upGradeExp) {
                    exp -= upGradeExp;
                } else if (exp == upGradeExp) {
                    exp = 0;
                } else {
                    exp = upGradeExp - exp;
                    break;
                }
            }
            setCurrentLevel(Math.max(level, this.level.getMinLevel()));
            setCurrentExp(Math.max(0, exp));
        }

        DreamEventCaller.playerDataUpdate(this);
    }


    private void sendActionBar(long addExp){
        if (Main.enableActionBar && NmsUtils.levels.contains(this.getLevel().getLevelName())) {
            if (this.getPlayer().getPlayer() != null){
                Player player = this.getPlayer().getPlayer();
                String getExp = LangLoader.getGetExp(player)
                        .replaceAll("<uExp>", getCurrentExp() + "")
                        .replaceAll("<maxExp>", getUpgradeExp()+"")
                        .replaceAll("<addExp>", addExp + "")
                        .replaceAll("<levelName>", getLevel().getLevelName());
                LangLoader.sendMsg(player, getExp, "actionbar");
            }
        }
    }


}
