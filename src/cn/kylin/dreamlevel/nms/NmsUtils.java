package cn.kylin.dreamlevel.nms;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class NmsUtils {

    public static List<String> levels;

    private static Class<Object> chatMsgTypeCls;
    private static Object chatMsgType;
    private static Class cpCls;
    private static Class<Object> entityPlayerCls;
    private static Class<Object> playerConnectionCls;
    private static Class<Object> iChatCls;
    private static Class<Object> chatBaseTextCls;
    private static Class<Object> playChatOutCls;

    static {
        // 初始化Class
        try {
            if (is112Plus()) {
                chatMsgTypeCls = getNmsClass("ChatMessageType");
                chatMsgType = chatMsgTypeCls.getMethod("a", byte.class).invoke(null, (byte) 2);
            }
            cpCls = getObcClass("entity.CraftPlayer");
            entityPlayerCls = getNmsClass("EntityPlayer");
            playerConnectionCls = getNmsClass("PlayerConnection");
            iChatCls = getNmsClass("IChatBaseComponent");
            chatBaseTextCls = getNmsClass("ChatComponentText");
            playChatOutCls = getNmsClass("PacketPlayOutChat");
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    public static void sendPacket(Player player, Object packet){
        try {
            Object craftPlayer = cpCls.cast(player);
            Object entityPlayer = cpCls.getMethod("getHandle").invoke(craftPlayer);
            Object playerConnection = entityPlayerCls.getField("playerConnection").get(entityPlayer);
            Method sP = playerConnectionCls.getMethod("sendPacket", getNmsClass("Packet"));
            sP.invoke(playerConnection, packet);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public static void sendActionBar(Player player, String msg){
        // PacketPlayOutChat
        try {
            Object iChatBase = chatBaseTextCls.getConstructor(String.class).newInstance(msg);
            Object playChatOutPacket;
            if (is112Plus()) {
                // 1.12+
                playChatOutPacket = playChatOutCls.getConstructor(iChatCls, chatMsgTypeCls).newInstance(iChatBase, chatMsgType);
            } else {
                // 1.8~1.11
                playChatOutPacket = playChatOutCls.getConstructor(iChatCls, byte.class).newInstance(iChatBase, (byte) 2);
            }
            sendPacket(player, playChatOutPacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static String getVersion(){
        String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        return version;
    }

    private static Class getNmsClass(String packet) throws ClassNotFoundException {
        String version = getVersion();
        String packetName = "net.minecraft.server." + version + "." + packet;
        return Class.forName(packetName);
    }

    private static Class getObcClass(String packet) throws ClassNotFoundException {
        String version = getVersion();
        String packetName = "org.bukkit.craftbukkit." + version + "." + packet;
        return Class.forName(packetName);
    }

    private static boolean is112Plus(){
        String version = getVersion();
        String[] args = version.split("_");
        if (args.length == 3) {
            String ver = args[1];
            try {
                return Integer.parseInt(ver) >= 12;
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

}
