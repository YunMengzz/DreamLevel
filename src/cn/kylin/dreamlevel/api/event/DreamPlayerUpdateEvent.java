package cn.kylin.dreamlevel.api.event;

import cn.kylin.dreamlevel.api.data.PlayerData;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class DreamPlayerUpdateEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private PlayerData data;

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList(){
        return handlers;
    }

    public PlayerData getData() {
        return data;
    }

    public void setData(PlayerData data) {
        this.data = data;
    }
}
