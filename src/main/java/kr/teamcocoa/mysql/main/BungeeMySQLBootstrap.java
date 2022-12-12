package kr.teamcocoa.mysql.main;

import kr.teamcocoa.mysql.mysql.MySQLManager;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeeMySQLBootstrap extends Plugin {

    private static BungeeMySQLBootstrap instance;

    public static BungeeMySQLBootstrap getInstance() {
        return instance;
    }

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        MySQLManager.startKeepAlive();
    }

    @Override
    public void onDisable() {
        MySQLManager.stopKeepAlive();
        MySQLManager.disconnectAllConnections();
    }
}
