package server;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    private long token;
    private String serverUrl;
    private final HttpClient client = HttpClient.newHttpClient();

    public KVTaskClient(String stringUrl) {
        init(stringUrl);
    }

    public KVTaskClient(String stringUrl, long token) {
        this.serverUrl = stringUrl;
        this.token = token;
        System.out.println("Токен (без регистрации): " + token);
    }

    private void init(String stringUrl) {
        this.serverUrl = stringUrl;
        URI registerUrl = URI.create(stringUrl + "/register");
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(registerUrl)
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            this.token = Long.parseLong(response.body());
            System.out.println("KVClient зарегистрировался, токен: " + token);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void put(String key, String value) {
        try {
            final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(value);
            URI uri = URI.create(serverUrl + "/save/" + key + "/?API_TOKEN=" + token);
            System.out.println(uri);
            HttpRequest request = HttpRequest
                    .newBuilder()
                    .uri(uri)
                    .POST(body)
                    .build();
            client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public String load(String key) {
        try {
            URI uri = URI.create(serverUrl + "/load/" + key + "/?API_TOKEN=" + token);
            HttpRequest request = HttpRequest
                    .newBuilder()
                    .uri(uri)
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (IOException | InterruptedException ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }

    public long getToken() {
        return token;
    }
}
