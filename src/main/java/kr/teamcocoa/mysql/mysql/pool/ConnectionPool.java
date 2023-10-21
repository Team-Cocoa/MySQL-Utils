package kr.teamcocoa.mysql.mysql.pool;

import kr.teamcocoa.mysql.mysql.MySQL;
import lombok.AccessLevel;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Deque;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

public class ConnectionPool {

    private int initialSize;

    @Getter
    private int currentSize;
    private int maximumSize;
    private String dbName;

    @Getter(AccessLevel.PROTECTED)
    private ArrayList<MySQL> connections;

    @Getter(AccessLevel.PROTECTED)
    private Deque<MySQL> queue;

    private Queue<CompletableFuture<MySQL>> waitingConnectionPool;

    protected ConnectionPool(String dbName, int initialSize, int maximumSize) {
        this.dbName = dbName;
        this.initialSize = initialSize;
        this.maximumSize = maximumSize;
        this.currentSize = 0;
        this.queue = new LinkedBlockingDeque<>(maximumSize);
        this.connections = new ArrayList<>(maximumSize);
        this.waitingConnectionPool = new LinkedBlockingQueue<>();
    }

    public void init() {
        for (int i = 0; i < initialSize; i++) {
            registerNewConnection();
        }
    }

    private MySQL registerNewConnection() {
        if(currentSize >= maximumSize) {
            throw new IllegalStateException("Queue is full! We can't add more connection.");
        }
        MySQL mySQL = new MySQL(this.dbName);
        mySQL.connect();
        queue.add(mySQL);
        connections.add(mySQL);
        this.currentSize++;
        checkWaitingConnection(mySQL);
        return mySQL;
    }

    private void removeConnection(MySQL mySQL) {
        if(currentSize <= initialSize) {
            throw new IllegalStateException("We can't remove more connection.");
        }
        if(!connections.contains(mySQL)) {
            throw new IllegalStateException("This connection isn't part of this pool!");
        }
        connections.remove(mySQL);
        mySQL.disconnect();
        this.currentSize--;
    }

    private void checkWaitingConnection(MySQL mySQL) {
        if(!waitingConnectionPool.isEmpty()) {
            CompletableFuture<MySQL> completableFuture = waitingConnectionPool.poll();
            completableFuture.complete(mySQL);
        }
    }

    public MySQL getConnection() {
        // 큐가 비어있다면 새로운 커넥션이 반납 될때 까지 무한 대기
        // 이러면 CompletableFuture 를 쓰는 이유가 없는건 맞는데
        // 그냥 Future 인스턴스를 얻을 수 있는 방법을 모루겄어
        // 미안하지만 그냥 이거 쓸래
        if(queue.size() == 0) {
            try {
                CompletableFuture<MySQL> future = new CompletableFuture<>();
                waitingConnectionPool.add(future);
                if(currentSize >= maximumSize) {
                    registerNewConnection();
                }
                return future.get();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        // 일단 정석대로 구현하자
        return queue.pollLast();
    }

    public void returnConnection(MySQL mySQL) {
        if(!waitingConnectionPool.isEmpty()) {
            CompletableFuture<MySQL> completableFuture = waitingConnectionPool.poll();
            completableFuture.complete(mySQL);
        }
        else if(this.currentSize > this.initialSize) {
            removeConnection(mySQL);
        } 
        else {
            queue.add(mySQL);
        }
    }
}
