package kr.teamcocoa.mysql;

import kr.teamcocoa.mysql.mysql.pool.ConnectionPool;
import kr.teamcocoa.mysql.mysql.pool.ConnectionPoolManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("커넥션 풀 테스트")
class ConnectionPoolTest {

    /*
     * org.junit 에 있는 어노테이션 들을 import 하면
     * 테스트 케이스가 작동하지 않는 현상이 JUnit5 에 있는거 같음...
     * org.junit.jupiter.api 에 있는 어노테이션 들을 사용하도록
     */

    private static ConnectionPool connectionPool;
    private static String dbName;

    @BeforeAll
    static void 테스트_준비() {
        dbName = "freefight";
        connectionPool = ConnectionPoolManager.createConnectionPool(
                "testPool",
                dbName,
                5,
                10
        );
    }

    @Test
    @DisplayName("큐 작동 테스트")
    void 큐_작동_테스트() {
        assertEquals(0, 0);
    }

    @Test
    @DisplayName("더미")
    void ㅁㄴㅇㄹ() {
        assertEquals(0, 0);
    }

    @AfterAll
    static void 테스트_종료() {
        ConnectionPoolManager.disconnectAllConnections();
    }

}