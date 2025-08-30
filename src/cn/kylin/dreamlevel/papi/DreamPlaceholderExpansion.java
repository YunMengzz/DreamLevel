package cn.kylin.dreamlevel.papi;

import cn.kylin.dreamlevel.Main;
import cn.kylin.dreamlevel.api.data.Level;
import cn.kylin.dreamlevel.api.data.PlayerData;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class DreamPlaceholderExpansion extends PlaceholderExpansion {

    private Main plugin;

    @Override
    public String getIdentifier(){
        return "dl";
    }

    @Override
    public String getAuthor() {
        return plugin.getDescription().getAuthors().toString();
    }

    @Override
    public String getVersion() {
        return Main.version;
    }

    @Override
    public boolean persist(){
        return true;
    }

    @Override
    public boolean canRegister(){
        return true;
    }

    public DreamPlaceholderExpansion(Main plugin){
        this.plugin = plugin;
    }

    /**
     * %dl_<levelName>:<placeholder>%
     * @param player
     * @param arg
     * @return
     */
    @Override
    public String onPlaceholderRequest(Player player, String arg) {
        if (player == null) return null;
        String[] args = arg.split(":");
        Level level = Main.levels.get(args[0]);
        if (level == null) return null;
        PlayerData data = level.datas.get(player.getName());

        String value = args[1];


        if ("level".equalsIgnoreCase(value)) {
            return data.getCurrentLevel() + "";
        }

        if ("exp".equalsIgnoreCase(value)) {
            return data.getCurrentExp() + "";
        }

        if ("isDuoBei".equalsIgnoreCase(value)) {
            return (data.isDuoBei()) + "";
        }

        if ("time".equalsIgnoreCase(value)) {
            return data.time + "";
        }

        if ("times".equalsIgnoreCase(value)) {
            return data.times + "";
        }
        /*
        2 增加个%dl_maxLevel% 获取最大等级变量
        3 增加个%dl_upgradeExp% 获取升级所需经验变量
        4 增加个%dl_expPercentage% 获取升级所需经验的百分比
        */
        if ("maxLevel".equalsIgnoreCase(value)) {
            return data.getLevel().getMaxLevel() + "";
        }

        if ("minLevel".equalsIgnoreCase(value)) {
            return data.getLevel().getMinLevel() + "";
        }

        if ("upgradeExp".equalsIgnoreCase(value)) {
            return data.getUpgradeExp() + "";
        }

        if ("expPercentage".equalsIgnoreCase(value)) {
            if (args.length == 2) {
                // dl_dl:expPercentage  小数
                float f1 = data.getCurrentExp();
                float f2 = data.getUpgradeExp();
                if (f2 == 0) {
                    return null;
                }
                return f1 / f2 + "";
            } else if (args.length == 3) {
                // dl_dl:expPercentage:1  百分  xx.x%
                int i;
                try{
                    i = Integer.parseInt(args[2]);
                } catch (Exception e) {
                    return "您输入的变量有问题 详见DreamLevel帖子变量部分";
                }
                double d1 = data.getCurrentExp();
                double d2 = data.getUpgradeExp();
                if (d2 == 0) return null;
                String v = d1 / d2 + "";
                String[] as = v.split("\\.");
                if (i == 1) {
                    if (as.length == 1) return v + ".0";
                    if (as[1].length() > 1) {
                        return as[0] + "." + as[1].substring(0, 1);
                    } else if (as[1].length() == 0) return as[0] + "." + 0;
                    return v;

                }
                // dl_dl:expPercentage:2  百分  xx.xx%
                else if (i == 2){
                    if (as.length == 1) return v + ".00";
                    if (as[1].length() > 2) {
                        return as[0] + "." + as[1].substring(0, 2);
                    } else if (as[1].length() == 1) {
                        return v + "0";
                    } else if (as[1].length() == 0) {
                        return v + "00";
                    }

                }

                // 其他  小数
                float f1 = data.getCurrentExp();
                float f2 = data.getUpgradeExp();
                if (f2 == 0) {
                    return null;
                }
                return f1 / f2 + "";
            }
        }

        if ("canLevelUp".equalsIgnoreCase(value)) {
            return player.hasPermission(data.getLevel().getPermissions().get(data.getCurrentLevel())) + "";
        }

        return null;
    }
}
