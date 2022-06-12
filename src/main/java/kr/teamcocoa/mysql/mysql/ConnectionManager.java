package kr.teamcocoa.mysql.mysql;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConnectionManager {

    private static ConnectionManager instance;

    public static ConnectionManager getInstance() {
        if(instance == null) {
            instance = new ConnectionManager();
        }
        return instance;
    }

    private List<Connection> connectionList = new LinkedList<>();
    private HashMap<String, Connection> connectionHashMap = new HashMap<>();

    public Connection getConnection(String dbName) {
        if(connectionHashMap.containsKey(dbName)) {
            return connectionHashMap.get(dbName);
        }
        Connection connection = new Connection(dbName);
        connectionList.add(connection);
        connectionHashMap.put(dbName, connection);
        return connection;
    }

    public void removeConnection(String dbName) {
        if(!connectionHashMap.containsKey(dbName)) {
            return;
        }
        Connection connection = getConnection(dbName);
        connection.disconnect();
        connectionHashMap.remove(dbName);
        connectionList.remove(connection);
    }
}
