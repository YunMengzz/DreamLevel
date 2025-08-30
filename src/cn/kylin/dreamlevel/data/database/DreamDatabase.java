package cn.kylin.dreamlevel.data.database;

import cn.kylin.dreamlevel.Main;
import cn.kylin.dreamlevel.api.data.Level;
import cn.kylin.dreamlevel.api.data.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.sql.*;
import java.util.Properties;

public class DreamDatabase {

    private static String url;
    private static String username;
    private static String password;

    private static boolean exists = false;

    public static void init(String host, int port, String database, String username, String password){
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        url = "jdbc:mysql://" + host + ":" + port + "/" + database + "?verifyServerCertificate=false&useSSL=false";
        DreamDatabase.username = username;
        DreamDatabase.password = password;

        createDataTable();
        // 自动存储到levels.data里面
        getAllPlayerData();
    }

    public static void init(Properties prop){
        if (prop == null) return;
        init(prop.getProperty("host"), Integer.parseInt(prop.getProperty("port")), prop.getProperty("database"), prop.getProperty("username"), prop.getProperty("password"));
    }

    /***
     * remember close connection!!!!!
     * @return MySQL Connection Object
     */
    private static Connection getConn(){
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    /**
     * 判断是否存在表 不存在就创建一个
     */
    private static void createDataTable(){
        Connection conn = getConn();
        if (conn == null) return;
        Statement stmt = null;

        try {
            stmt = conn.createStatement();
            String sql = "select 1 from `playerdata`;";
            stmt.executeQuery(sql);
            exists = true;
        } catch (Exception e) {
            if (stmt != null) {
                String sql = "create table `playerdata`(" +
                        "`playername` varchar(32) CHARACTER set utf8 NOT NULL," +
                        "`levelname` varchar(32) CHARACTER set utf8 NOT NULL," +
                        "`currentLevel` int NOT NULL," +
                        "`currentExp` bigint NOT NULL," +
                        "`times` double(16,2)," +
                        "`time` int);";
                try {
                    stmt.executeUpdate(sql);
                    exists = true;
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                } finally {
                    try {
                        stmt.close();
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                    try {
                        conn.close();
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
            }
        }
    }

    public static PlayerData getPlayerData (OfflinePlayer player, Level level){
        if (player == null || level == null) return null;
        String sql = "select * from `playerdata` where `playername` = ? and `levelname` = ?;";
        Connection conn = getConn();
        if (conn != null) {
            PreparedStatement pstmt = null;
            try{
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, player.getName());
                pstmt.setString(2, level.getLevelName());
                ResultSet rs = pstmt.executeQuery();
                boolean b = rs.next();
                if (b) {
                    int currentLevel = rs.getInt(3);
                    long currentExp = rs.getLong(4);
                    double times = rs.getDouble(5);
                    int time = rs.getInt(6);
                    PlayerData data = new PlayerData(level, player);
                    data.setCurrentLevel(currentLevel);
                    data.setCurrentExp(currentExp);
                    data.times = times;
                    data.time = time;
                    return data;
                } else {
                    return null;
                }

            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                if (pstmt != null) {
                    try {
                        pstmt.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static void insertPlayerData(PlayerData data){
        if (data == null) return;
        String s = "select * from `playerdata` where `playername` = ? and `levelname` = ?;";
        PreparedStatement p = null;
        // check whether data is exists or not
        try {
            p = getConn().prepareStatement(s);
            p.setString(1, data.getPlayer().getName());
            p.setString(2, data.getLevel().getLevelName());
            ResultSet rs = p.executeQuery();
            boolean b = rs.next();
            if (b) {
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String sql = "insert into `playerdata` values(?,?,?,?,?,?);";
        PreparedStatement stmt = null;
        try {
            stmt = getConn().prepareStatement(sql);
            stmt.setString(1,data.getPlayer().getName());
            stmt.setString(2,data.getLevel().getLevelName());
            stmt.setInt(3,data.getCurrentLevel());
            stmt.setLong(4,data.getCurrentExp());
            stmt.setDouble(5,data.times);
            stmt.setInt(6,data.time);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * "`playername` varchar(32) CHARACTER set utf8 NOT NULL," +
     * "`levelname` varchar(32) CHARACTER set utf8 NOT NULL," +
     * "`currentLevel` int NOT NULL," +
     * "`currentExp` int NOT NULL," +
     * "`saveExp` int," +
     * "`times` double(16,2)," +
     * "`time` int);";*/
    public static void updatePlayerData(PlayerData data){
        if (data == null) return;
        String sql = "update `playerdata` set `currentLevel` = ?, `currentExp` = ?, `times` = ?, `time` = ? where `playername` = ? and `levelname` = ?";
        PreparedStatement stmt = null;
        try {
            stmt = getConn().prepareStatement(sql);
            stmt.setInt(1,data.getCurrentLevel());
            stmt.setLong(2,data.getCurrentExp());
            stmt.setDouble(3,data.times);
            stmt.setInt(4, data.time);
            stmt.setString(5, data.getPlayer().getName());
            stmt.setString(6,data.getLevel().getLevelName());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public static void deletePlayerData(PlayerData data){ 
        if (data == null) return;
        String sql = "delete * from `playerdata` where `playername` = ? and `levelname` = ?";
        PreparedStatement stmt = null;
        try {
            stmt = getConn().prepareStatement(sql);
            stmt.setString(1,data.getPlayer().getName());
            stmt.setString(2,data.getLevel().getLevelName());
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void getAllPlayerData(){
        String sql = "select * from `playerdata`";
        Statement stmt = null;
        try{
            stmt = getConn().createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()){
                String playerName = rs.getString(1);
                OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);
                String levelName = rs.getString(2);
                Level level = Main.levels.get(levelName);
                getPlayerData(player, level);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
