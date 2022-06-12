package kr.teamcocoa.mysql.main;

import org.bukkit.plugin.java.JavaPlugin;

public class MySQL extends JavaPlugin {

    private static MySQL instance;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {

    }
}
