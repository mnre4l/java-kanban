package test;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import server.KVServer;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVServerTest {
    private static KVServer kvServer;
    private static final int PORT = KVServer.PORT;

    @BeforeAll
    static void setUp() {
        try {
            kvServer = new KVServer();
            kvServer.start();
        } catch (IOException e) {
            System.out.println("Ошибка при старте KVServer");
        }
    }

    @AfterAll
    static void stop() {
        kvServer.stop();
    }

    @Test
    void shouldReturnValueByKey() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            URI uri = URI.create("http://localhost:" + PORT + "/save" + "/iamkey?" + "API_TOKEN=DEBUG");
            final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString("iamvalue");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .POST(body)
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            URI uri2 = URI.create("http://localhost:" + PORT + "/load" + "/iamkey?" + "API_TOKEN=DEBUG");
            HttpRequest request2 = HttpRequest.newBuilder()
                    .uri(uri2)
                    .GET()
                    .build();
            HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

            assertEquals(response2.body(), "iamvalue");
        } catch (IOException | InterruptedException e) {
            System.out.println("Ошибка");
        }
    }
}
