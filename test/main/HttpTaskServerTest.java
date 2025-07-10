package main;

import com.google.gson.Gson;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public abstract class HttpTaskServerTest {

    protected TaskManager taskManager;
    protected HttpTaskServer httpTaskServer;
    protected final Gson gson = HttpTaskServer.getGson();
    protected final static HttpClient CLIENT = HttpClient.newHttpClient();

    public HttpResponse<String> getResponseForPost(String stringJson, String url) throws IOException, InterruptedException {
        URI currentUrl = URI.create(url);
        HttpRequest request = HttpRequest.newBuilder().uri(currentUrl).POST(HttpRequest.BodyPublishers.ofString(stringJson)).build();
        return CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public HttpResponse<String> getNotPostResponse(String url, String method) throws IOException, InterruptedException {
        URI currentUrl = URI.create(url);
        switch (method) {
            case "GET": {
                HttpRequest request = HttpRequest.newBuilder().uri(currentUrl).GET().build();
                return CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            }
            case "DELETE": {
                HttpRequest request = HttpRequest.newBuilder().uri(currentUrl).DELETE().build();
                return CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            }
            default:
                throw new IllegalArgumentException("Неподдерживаемый HTTP-метод: " + method);
        }
    }

    @BeforeEach
    public void setUp() throws IOException {
        taskManager = new InMemoryTaskManager();
        httpTaskServer = new HttpTaskServer(taskManager);
        httpTaskServer.start();
    }

    @AfterEach
    public void shutDown() {
        httpTaskServer.stop();
    }
}
