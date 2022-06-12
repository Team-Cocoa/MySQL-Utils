package kr.teamcocoa.mysql.mysql;

import lombok.Getter;
import org.bukkit.Bukkit;

import java.sql.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Getter
public class Connection {
    
    private String dbName;
    private java.sql.Connection connection;
    private ExecutorService preventDisconnectTask;

    protected Connection(String dbName) {
        this.dbName = dbName;
        this.preventDisconnectTask = Executors.newFixedThreadPool(1);
    }

    public void connect() {
        if (!isConnected()) {
            try {
                this.connection = DriverManager.getConnection("jdbc:mysql://localhost/" + dbName, "root", "root");
                this.preventDisconnectTask.execute(() -> {
                    while(true) {
                        try(    PreparedStatement preparedStatement = getPreparedStatement("select 1");
                                ResultSet rs = preparedStatement.executeQuery()) {
                            Thread.sleep(1000 * 60 * 60);
                        }
                        catch (SQLException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
                Bukkit.getLogger().info("[MySQL] Successfully connected to MySQL(Database : " + dbName + ")!");
            } catch (SQLException e) {
                e.printStackTrace();
                Bukkit.getLogger().info("[MySQL] Failed to connect to MySQL(Database : " + dbName + ")!");
            }
        }
    }

    public boolean isConnected() {
        if (connection == null) {
            return false;
        }
        else {
            try {
                return !connection.isClosed();
            }
            catch(Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    public void disconnect() {
        if (isConnected()) {
            try {
                connection.close();
                System.out.println("[MySQL] Successfully disconnected to MySQL (Database : " + dbName + ")!");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void update(String sql) {
        if(isConnected()) {
            try(PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.execute();
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public PreparedStatement getPreparedStatement(String sql) {
        if(isConnected()) {
            try {
                return connection.prepareStatement(sql);
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
