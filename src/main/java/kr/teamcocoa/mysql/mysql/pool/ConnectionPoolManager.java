package kr.teamcocoa.mysql.mysql.pool;

import kr.teamcocoa.mysql.mysql.MySQL;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConnectionPoolManager {

    private static ScheduledExecutorService keepAliveTask = Executors.newSingleThreadScheduledExecutor();

    private static ConcurrentHashMap<String, ConnectionPool> connectionPoolMap = new ConcurrentHashMap<>();

    private static List<MySQL> mySQLList = new LinkedList<>();

    @Getter
    private static boolean keepAliveRunning = false;

    public static ConnectionPool createConnectionPool(String name, String dbName, int size) {
        ConnectionPool connectionPool = new ConnectionPool(dbName, size);
        connectionPool.init();
        for (MySQL mySQL : connectionPool.getQueue()) {
            mySQLList.add(mySQL);
        }
        connectionPoolMap.put(name, connectionPool);
        return connectionPool;
    }

    public static boolean hasPool(String name) {
        return connectionPoolMap.containsKey(name);
    }

    public static ConnectionPool getPool(String name) {
        return connectionPoolMap.getOrDefault(name, null);
    }

    public static Collection<ConnectionPool> getAllPools() {
        return connectionPoolMap.values();
    }

    public static void disconnectAllConnections() {
        for (MySQL mySQL : mySQLList) {
            mySQL.disconnect();
        }
    }

    public static void startKeepAlive() {
        if(keepAliveRunning) {
            return;
        }
        keepAliveRunning = true;
        keepAliveTask.scheduleAtFixedRate(() -> {
            long currentTime = System.currentTimeMillis();
            for (MySQL mySQL : mySQLList) {
                if(currentTime - mySQL.getLastTransactionStamp() > TimeUnit.MINUTES.toMillis(30)) {
                    try(PreparedStatement preparedStatement = mySQL.getPreparedStatement("select 1");
                        ResultSet rs = preparedStatement.executeQuery()) {
                        System.out.println("[MySQL] KeepAlive success for " + mySQL.getDbName() + ".");
                    }
                    catch (SQLException e) {
                        e.printStackTrace();
                        System.out.println("[MySQL] KeepAlive failed for " + mySQL.getDbName() + ".");
                    }
                }
            }
        }, 0, 1, TimeUnit.HOURS);
    }

    public static void stopKeepAlive() {
        if(!keepAliveRunning) {
            return;
        }
        keepAliveTask.shutdown();
    }

}
