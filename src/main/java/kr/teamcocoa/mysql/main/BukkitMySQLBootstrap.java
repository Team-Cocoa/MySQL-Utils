package kr.teamcocoa.mysql.main;

import kr.teamcocoa.mysql.mysql.MySQLManager;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public class BukkitMySQLBootstrap extends JavaPlugin {

    @Getter
    private static BukkitMySQLBootstrap instance;

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
