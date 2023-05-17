package test;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.KVServer;
import server.KVTaskClient;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class KVTaskClientTest {
    private KVServer kvServer;
    private KVTaskClient client;

    @BeforeEach
    void setUp() {
        try {
            kvServer = new KVServer();
            kvServer.start();
            client = new KVTaskClient("http://localhost:" + KVServer.PORT);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @AfterEach
    void tearDown() {
        kvServer.stop();
    }

    @Test
    void shouldReturnCorrectValue() {
        client.put("itiskeeey", "hope its value");
        client.put("itiskeeey2", "hope its value2");

        String mayBeValue = client.load("itiskeeey");
        String mayBeValue2 = client.load("itiskeeey2");

        assertEquals("hope its value", mayBeValue);
        assertEquals("hope its value2", mayBeValue2);
    }

    @Test
    void shouldReturnLastSentValue() {
        client.put("itiskeeey", "hope its value");
        client.put("itiskeeey", "hope its value ver 2");

        String mayBeValue = client.load("itiskeeey");

        assertEquals("hope its value ver 2", mayBeValue);
    }
}