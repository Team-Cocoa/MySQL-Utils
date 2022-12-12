package kr.teamcocoa.mysql.main;

import kr.teamcocoa.mysql.mysql.MySQLManager;
import org.bukkit.plugin.java.JavaPlugin;

public class BukkitMySQLBootstrap extends JavaPlugin {

    private static BukkitMySQLBootstrap instance;

    public static BukkitMySQLBootstrap getInstance() {
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
