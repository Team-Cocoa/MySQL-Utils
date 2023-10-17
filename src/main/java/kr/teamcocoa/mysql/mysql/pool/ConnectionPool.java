package kr.teamcocoa.mysql.mysql.pool;

import kr.teamcocoa.mysql.mysql.MySQL;
import lombok.AccessLevel;
import lombok.Getter;

import java.util.Deque;
import java.util.Queue;
import java.util.concurrent.*;

public class ConnectionPool {

    private int size;
    private String dbName;

    @Getter(AccessLevel.PROTECTED)
    private Deque<MySQL> queue;
    private Queue<CompletableFuture<MySQL>> waitingConnectionPool;

    protected ConnectionPool(String dbName, int size) {
        this.dbName = dbName;
        this.size = size;
        this.queue = new LinkedBlockingDeque<>(size);
        this.waitingConnectionPool = new LinkedBlockingQueue<>();
    }

    public void init() {
        for (int i = 0; i < size; i++) {
            MySQL mySQL = new MySQL(this.dbName);
            mySQL.connect();
            queue.add(mySQL);
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
        else {
            queue.add(mySQL);
        }
    }

}
