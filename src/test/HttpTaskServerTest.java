package test;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskState;
import service.InMemoryTaskManager;
import service.Managers;
import controller.HttpTaskServer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;

import java.util.ArrayList;


public class HttpTaskServerTest {
    private HttpTaskServer httpTaskServer;
    private InMemoryTaskManager manager;
    private Gson gson;

    @BeforeEach
    void setUp() {
        try {
            manager = Managers.getInMemoryManager();
            httpTaskServer = new HttpTaskServer();
            gson = new Gson();

            httpTaskServer.start();

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @AfterEach
    void tearDown() {
        httpTaskServer.stop();
    }

    HttpResponse<String> makeGetRequest(String stringUrl) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            URI url = URI.create(stringUrl);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url)
                    .GET()
                    .build();

            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException ex) {
            System.out.println(ex.getMessage());
            return null;
        }
    }

    void makeDeleteRequest(String stringUrl) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            URI url = URI.create(stringUrl);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url)
                    .DELETE()
                    .build();

            client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException ex) {
            System.out.println(ex.getMessage());
        }
    }

    HttpResponse<String> makePostRequest(String stringUrl, String requestBody) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            URI url = URI.create(stringUrl);
            final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(requestBody);
            HttpRequest request = HttpRequest
                    .newBuilder()
                    .uri(url)
                    .POST(body)
                    .build();

            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException ex) {
            System.out.println(ex.getMessage());
            return null;
        }

    }

    @Test
    @DisplayName("Получение списка задач - стандартный случай")
    void shouldReturnTasks() {
        Task task1 = new Task("qq", "ne qq", TaskState.NEW);
        Task task2 = new Task("123", "32", TaskState.IN_PROGRESS);
        String json = gson.toJson(task1);
        String json2 = gson.toJson(task2);

        makePostRequest("http://localhost:" + HttpTaskServer.PORT  + "/tasks/task/", json);
        makePostRequest("http://localhost:" + HttpTaskServer.PORT  + "/tasks/task/", json2);

        manager.createTask(task1);
        manager.createTask(task2);

        HttpResponse<String> response = makeGetRequest("http://localhost:" + HttpTaskServer.PORT
                + "/tasks/task/");

        ArrayList<Task> tasks = gson.fromJson(response.body(), new TypeToken<ArrayList<Task>>(){}.getType());

        assertEquals(manager.getTasksList(), tasks);
    }

    @Test
    @DisplayName("Получение списка эпиков - стандартный случай")
    void shouldReturnEpics() {
        Epic epic1 = new Epic("qq", "ne qq");
        Epic epic2 = new Epic("123", "32");
        String json = gson.toJson(epic1);
        String json2 = gson.toJson(epic2);

        makePostRequest("http://localhost:" + HttpTaskServer.PORT  + "/tasks/epic/", json);
        makePostRequest("http://localhost:" + HttpTaskServer.PORT  + "/tasks/epic/", json2);
        manager.createEpic(epic1);
        manager.createEpic(epic2);

        HttpResponse<String> response = makeGetRequest("http://localhost:" + HttpTaskServer.PORT
                + "/tasks/epic/");
        ArrayList<Epic> epics = gson.fromJson(response.body(), new TypeToken<ArrayList<Epic>>(){}.getType());

        assertEquals(manager.getEpicsList(), epics);
    }

    @Test
    @DisplayName("Получение списка подзадач - стандартный случай")
    void shouldReturnSubtasks() {
        Epic epic = new Epic("111", "222");
        String json = gson.toJson(epic);

        manager.createEpic(epic);
        makePostRequest("http://localhost:" + HttpTaskServer.PORT  + "/tasks/epic/", json);

        Subtask subtask1 = new Subtask("qq", "ne qq", TaskState.IN_PROGRESS, 0);
        Subtask subtask2 = new Subtask("123", "32", TaskState.NEW, 0);
        String json2 = gson.toJson(subtask1);
        String json3 = gson.toJson(subtask2);

        makePostRequest("http://localhost:" + HttpTaskServer.PORT  + "/tasks/subtask/", json2);
        makePostRequest("http://localhost:" + HttpTaskServer.PORT  + "/tasks/subtask/", json3);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        HttpResponse<String> response = makeGetRequest("http://localhost:" + HttpTaskServer.PORT
                + "/tasks/subtask/");
        ArrayList<Subtask> subtasks = gson.fromJson(response.body(), new TypeToken<ArrayList<Subtask>>(){}.getType());

        assertEquals(manager.getSubtasksList(), subtasks);
    }

    @Test
    @DisplayName("Получение истории")
    void shouldReturnHistory() {
        Task task = new Task("qq", "ne qq", TaskState.NEW);
        String json = gson.toJson(task);

        makePostRequest("http://localhost:" + HttpTaskServer.PORT  + "/tasks/task/", json);
        manager.createTask(task);

        Task task2 = new Task("qq123", "ne q123q", TaskState.NEW);
        String json2 = gson.toJson(task2);

        makePostRequest("http://localhost:" + HttpTaskServer.PORT  + "/tasks/task/", json2);
        manager.createTask(task2);

        makeGetRequest("http://localhost:" + HttpTaskServer.PORT  + "/tasks/task/?id=1");
        makeGetRequest("http://localhost:" + HttpTaskServer.PORT  + "/tasks/task/?id=0");

        HttpResponse<String> response = makeGetRequest("http://localhost:" + HttpTaskServer.PORT
                + "/tasks/history");

        ArrayList<Task> history = gson.fromJson(response.body(), new TypeToken<ArrayList<Task>>(){}.getType());

        manager.getTaskById(1);
        manager.getTaskById(0);
        assertEquals(manager.getHistoryList(), history);
    }

    @Test
    @DisplayName("Получение приоритетных задач")
    void shouldReturnPrioritizedTasks() {
        Task task = new Task("qq", "ne qq", TaskState.NEW);
        String json = gson.toJson(task);

        makePostRequest("http://localhost:" + HttpTaskServer.PORT  + "/tasks/task/", json);
        manager.createTask(task);

        Task task2 = new Task("qq123", "ne q123q", TaskState.NEW);
        String json2 = gson.toJson(task2);

        makePostRequest("http://localhost:" + HttpTaskServer.PORT  + "/tasks/task/", json2);
        manager.createTask(task2);

        makeGetRequest("http://localhost:" + HttpTaskServer.PORT  + "/tasks/task/?id=1");
        makeGetRequest("http://localhost:" + HttpTaskServer.PORT  + "/tasks/task/?id=0");

        HttpResponse<String> response = makeGetRequest("http://localhost:" + HttpTaskServer.PORT
                + "/tasks/prioritized");
        ArrayList<Task> prioritized = gson.fromJson(response.body(), new TypeToken<ArrayList<Task>>(){}.getType());

        assertEquals(manager.getPrioritizedTasks(), prioritized);
    }

    @Test
    @DisplayName("Получение задачи по айди - стандартный случай")
    void shouldReturnTaskById() {
        Task task = new Task("qq", "ne qq", TaskState.NEW);
        String json = gson.toJson(task);

        makePostRequest("http://localhost:" + HttpTaskServer.PORT  + "/tasks/task/", json);
        manager.createTask(task);

        HttpResponse<String> response = makeGetRequest("http://localhost:" + HttpTaskServer.PORT
                + "/tasks/task/?id=0");
        Task returnedTask = gson.fromJson(response.body(), Task.class);

        assertEquals(returnedTask, manager.getTaskById(0));
    }

    @Test
    @DisplayName("Получение эпика по айди - стандартный случай")
    void shouldReturnEpicById() {
        Epic epic = new Epic("qq", "ne qq");
        String json = gson.toJson(epic);

        makePostRequest("http://localhost:" + HttpTaskServer.PORT  + "/tasks/epic/", json);
        manager.createEpic(epic);

        HttpResponse<String> response = makeGetRequest("http://localhost:" + HttpTaskServer.PORT
                + "/tasks/epic/?id=0");
        Epic returnedEpic = gson.fromJson(response.body(), Epic.class);

        assertEquals(returnedEpic, manager.getEpicById(0));
    }

    @Test
    @DisplayName("Получение сабтаска по айди - стандартный случай")
    void shouldReturnSubtaskById() {
        Epic epic = new Epic("qq", "ne qq");
        String json = gson.toJson(epic);

        manager.createEpic(epic);
        makePostRequest("http://localhost:" + HttpTaskServer.PORT  + "/tasks/epic/", json);

        Subtask subtask = new Subtask("qq", "123", TaskState.NEW, 0);
        String json2 = gson.toJson(subtask);

        manager.createSubtask(subtask);
        makePostRequest("http://localhost:" + HttpTaskServer.PORT  + "/tasks/subtask/", json2);

        HttpResponse<String> response = makeGetRequest("http://localhost:" + HttpTaskServer.PORT
                + "/tasks/subtask/?id=1");
        Subtask returnedSubtask = gson.fromJson(response.body(), Subtask.class);

        assertEquals(returnedSubtask, manager.getSubtaskById(1));
    }

    @Test
    @DisplayName("Создание задачи - стандартный случай")
    void shouldCreateTask() {
        Task task = new Task("qq", "ne qq", TaskState.NEW);
        String json = gson.toJson(task);
        HttpResponse<String> response = makePostRequest("http://localhost:" + HttpTaskServer.PORT
                + "/tasks/task/", json);

        assertEquals(response.body(), "Задача создана. Id:0");
        assertEquals(response.statusCode(), 201);

    }

    @Test
    @DisplayName("Создание эпика - стандартный случай")
    void shouldCreateEpic() {
        Epic epic = new Epic("qq", "ne qq");
        String json = gson.toJson(epic);
        HttpResponse<String> response = makePostRequest("http://localhost:" + HttpTaskServer.PORT
                + "/tasks/epic/", json);

        assertEquals(response.body(), "Эпик создан. Id:0");
        assertEquals(response.statusCode(), 201);
    }

    @Test
    @DisplayName("Создание сабтаска - стандартный случай")
    void shouldCreateSubtask() {
        Epic epic = new Epic("qq", "ne qq");
        String jsonEpic = gson.toJson(epic);

        makePostRequest("http://localhost:" + HttpTaskServer.PORT  + "/tasks/epic/", jsonEpic);

        Subtask subtask = new Subtask("qq", "ne qq", TaskState.NEW, 0);
        String jsonSubtask = gson.toJson(subtask);

        HttpResponse<String> response = makePostRequest("http://localhost:" + HttpTaskServer.PORT
                        + "/tasks/subtask/", jsonSubtask);

        assertEquals(response.body(), "Подзадача создана. Id:1");
        assertEquals(response.statusCode(), 201);
    }

    @Test
    @DisplayName("Обновление задачи - стандартный случай")
    void shouldUpdateTask() {
        Task task = new Task("qq", "ne qq", TaskState.NEW);
        String json = gson.toJson(task);

        makePostRequest("http://localhost:" + HttpTaskServer.PORT  + "/tasks/task/", json);

        HttpResponse<String> response = makeGetRequest("http://localhost:" + HttpTaskServer.PORT
                + "/tasks/task/?id=0");
        Task savedTask = gson.fromJson(response.body(), Task.class);

        savedTask.setTaskDescription("ne qq ne qq");

        String updJson = gson.toJson(savedTask);

        makePostRequest("http://localhost:" + HttpTaskServer.PORT  + "/tasks/task/?id=0", updJson);

        HttpResponse<String> response2 = makeGetRequest("http://localhost:" + HttpTaskServer.PORT
                + "/tasks/task/?id=0");
        Task newSavedTask = gson.fromJson(response2.body(), Task.class);

        assertEquals(newSavedTask, savedTask);
    }

    @Test
    @DisplayName("Обновление эпика - стандартный случай")
    void shouldUpdateEpic() {
        Epic epic = new Epic("qq", "ne qq");
        String json = gson.toJson(epic);

        makePostRequest("http://localhost:" + HttpTaskServer.PORT  + "/tasks/epic/", json);

        HttpResponse<String> response = makeGetRequest("http://localhost:" + HttpTaskServer.PORT
                + "/tasks/epic/?id=0");
        Epic savedEpic = gson.fromJson(response.body(), Epic.class);

        savedEpic.setTaskDescription("ne qq ne qq");

        String updJson = gson.toJson(savedEpic);

        makePostRequest("http://localhost:" + HttpTaskServer.PORT  + "/tasks/epic/?id=0", updJson);

        HttpResponse<String> response2 = makeGetRequest("http://localhost:" + HttpTaskServer.PORT
                + "/tasks/epic/?id=0");
        Epic newSavedEpic = gson.fromJson(response2.body(), Epic.class);

        assertEquals(newSavedEpic, savedEpic);
    }

    @Test
    @DisplayName("Обновление сабтаска - стандартный случай")
    void shouldUpdateSubtask() {
        Epic epic = new Epic("qq", "ne qq");
        String json = gson.toJson(epic);

        makePostRequest("http://localhost:" + HttpTaskServer.PORT  + "/tasks/epic/", json);

        Subtask subtask = new Subtask("name", "descr", TaskState.NEW, 0);
        String json2 = gson.toJson(subtask);

        makePostRequest("http://localhost:" + HttpTaskServer.PORT  + "/tasks/subtask/", json2);

        subtask.setTaskDescription("new descr");
        subtask.setTaskId(1);

        String json3 = gson.toJson(subtask);

        makePostRequest("http://localhost:" + HttpTaskServer.PORT  + "/tasks/subtask/?id=1", json3);

        HttpResponse<String> response = makeGetRequest("http://localhost:" + HttpTaskServer.PORT
                + "/tasks/subtask/?id=1");
        Subtask savedSubtask = gson.fromJson(response.body(), Subtask.class);

        assertEquals(savedSubtask.getTaskDescription(), subtask.getTaskDescription());
    }

    @Test
    @DisplayName("Удаление всех задач")
    void shouldDeleteTasks() {
        Task task = new Task("qq", "ne qq", TaskState.NEW);
        String json = gson.toJson(task);

        makePostRequest("http://localhost:" + HttpTaskServer.PORT  + "/tasks/task/", json);

        Task task2 = new Task("qq2", "ne qq2", TaskState.NEW);
        String json2 = gson.toJson(task2);

        makePostRequest("http://localhost:" + HttpTaskServer.PORT  + "/tasks/task/", json2);
        makeDeleteRequest("http://localhost:" + HttpTaskServer.PORT  + "/tasks/task/");

        HttpResponse<String> response = makeGetRequest("http://localhost:" + HttpTaskServer.PORT
                + "/tasks/task/");

        assertEquals("[]", response.body());
    }

    @Test
    @DisplayName("Удаление всех эпиков")
    void shouldDeleteEpics() {
        Epic epic = new Epic("qq", "ne qq");
        String json = gson.toJson(epic);

        makePostRequest("http://localhost:" + HttpTaskServer.PORT  + "/tasks/epic/", json);

        Epic epic2 = new Epic("qq2", "ne qq2");
        String json2 = gson.toJson(epic2);

        makePostRequest("http://localhost:" + HttpTaskServer.PORT  + "/tasks/epic/", json2);

        Subtask subtask = new Subtask("qq2", "ne qq2", TaskState.NEW, 1);
        String json3 = gson.toJson(subtask);

        makePostRequest("http://localhost:" + HttpTaskServer.PORT  + "/tasks/subtask/", json3);
        makeDeleteRequest("http://localhost:" + HttpTaskServer.PORT  + "/tasks/epic/");

        HttpResponse<String> response = makeGetRequest("http://localhost:" + HttpTaskServer.PORT
                + "/tasks/epic/");

        assertEquals("[]", response.body());

        HttpResponse<String> response2 = makeGetRequest("http://localhost:" + HttpTaskServer.PORT
                + "/tasks/subtask/");

        assertEquals("[]", response2.body());
    }

    @Test
    @DisplayName("Удаление всех сабтасков")
    void shouldDeleteSubtasks() {
        Epic epic = new Epic("qq", "ne qq");
        String json = gson.toJson(epic);

        makePostRequest("http://localhost:" + HttpTaskServer.PORT  + "/tasks/epic/", json);

        Epic epic2 = new Epic("qq2", "ne qq2");
        String json2 = gson.toJson(epic2);

        makePostRequest("http://localhost:" + HttpTaskServer.PORT  + "/tasks/epic/", json2);

        Subtask subtask = new Subtask("qq2", "ne qq2", TaskState.NEW, 1);
        String json3 = gson.toJson(subtask);

        makePostRequest("http://localhost:" + HttpTaskServer.PORT  + "/tasks/subtask/", json3);

        Subtask subtask2 = new Subtask("qq2", "ne qq2", TaskState.NEW, 1);
        String json4 = gson.toJson(subtask2);

        makePostRequest("http://localhost:" + HttpTaskServer.PORT  + "/tasks/subtask/", json4);
        makeDeleteRequest("http://localhost:" + HttpTaskServer.PORT  + "/tasks/subtask/");

        HttpResponse<String> response = makeGetRequest("http://localhost:" + HttpTaskServer.PORT
                + "/tasks/subtask/");

        assertEquals(response.body(), "[]");
    }

    @Test
    @DisplayName("Удаление задачи по айди - стандартный случай")
    void shouldDeleteTaskById() {
        Task task = new Task("qq", "ne qq", TaskState.NEW);
        String json = gson.toJson(task);

        makePostRequest("http://localhost:" + HttpTaskServer.PORT  + "/tasks/task/", json);
        makeDeleteRequest("http://localhost:" + HttpTaskServer.PORT  + "/tasks/task/?id=0");

        HttpResponse<String> response = makeGetRequest("http://localhost:" + HttpTaskServer.PORT
                + "/tasks/task/?id=0");

        assertEquals(response.body(), "Такой задачи нет");
    }

    @Test
    @DisplayName("Удаление подзадачи по айди - стандартный случай")
    void shouldDeleteSubtaskById() {
        Epic epic = new Epic("qq", "ne qq");
        String json = gson.toJson(epic);

        makePostRequest("http://localhost:" + HttpTaskServer.PORT  + "/tasks/epic/", json);

        Subtask subtask2 = new Subtask("name", "descr", TaskState.NEW, 0);
        String json2 = gson.toJson(subtask2);

        makePostRequest("http://localhost:" + HttpTaskServer.PORT  + "/tasks/subtask/", json2);
        makeDeleteRequest("http://localhost:" + HttpTaskServer.PORT  + "/tasks/subtask/?id=1");

        HttpResponse<String> response = makeGetRequest("http://localhost:" + HttpTaskServer.PORT
                + "/tasks/subtask/?id=1");

        assertEquals(response.body(), "Такой подзадачи нет");
    }

    @Test
    @DisplayName("Удаление эпика по айди - стандартный случай")
    void shouldDeleteEpicById() {
        Epic epic = new Epic("qq", "ne qq");
        String json = gson.toJson(epic);

        makePostRequest("http://localhost:" + HttpTaskServer.PORT  + "/tasks/epic/", json);
        makeDeleteRequest("http://localhost:" + HttpTaskServer.PORT  + "/tasks/epic/?id=0");

        HttpResponse<String> response = makeGetRequest("http://localhost:" + HttpTaskServer.PORT
                + "/tasks/epic/?id=0");
        assertEquals(response.body(), "Такого эпика нет");
    }

    @Test
    @DisplayName("Создание подзадачи, случай: пришла подзадача уже с id или без имени")
    void shouldReturnErrorWhenCreateBadSubtask() {
        Epic epic = new Epic("1", "2");
        String jsonEpic = gson.toJson(epic);

        makePostRequest("http://localhost:" + HttpTaskServer.PORT  + "/tasks/epic/", jsonEpic);

        Subtask subtask = new Subtask("", "", TaskState.NEW, 0);
        String jsonSubtask = gson.toJson(subtask);
        Subtask subtask2 = new Subtask("name", "descr", TaskState.NEW, 0);
        subtask2.setTaskId(222);
        String jsonSubtask2 = gson.toJson(subtask2);

        HttpResponse<String> response = makePostRequest("http://localhost:" + HttpTaskServer.PORT
                        + "/tasks/task/", jsonSubtask);
        HttpResponse<String> response2 = makePostRequest("http://localhost:" + HttpTaskServer.PORT
                        + "/tasks/task/", jsonSubtask2);
        assertEquals(response.statusCode(), 400);
        assertEquals(response2.statusCode(), 400);
    }

    @Test
    @DisplayName("Создание задачи, случай: пришла задача уже с id или без имени")
    void shouldReturnErrorWhenCreateBadTask() {
        Task task = new Task();
        Task task2 = new Task("mm", "mmm", TaskState.NEW);

        task2.setTaskId(555);

        String json = gson.toJson(task);
        String json2 = gson.toJson(task2);

        HttpResponse<String> response = makePostRequest("http://localhost:" + HttpTaskServer.PORT
                        + "/tasks/task/", json);
        HttpResponse<String> response2 = makePostRequest("http://localhost:" + HttpTaskServer.PORT
                        + "/tasks/task/", json2);

        assertEquals(response.statusCode(), 400);
        assertEquals(response2.statusCode(), 400);
    }

    @Test
    @DisplayName("Создание эпика, случай: пришел эпик уже с id или без имени")
    void shouldReturnErrorWhenCreateBadEpic() {
        Epic epic = new Epic("", "");
        Epic epic2 = new Epic("mm", "mmm");

        epic2.setTaskId(555);

        String json = gson.toJson(epic);
        String json2 = gson.toJson(epic2);

        HttpResponse<String> response = makePostRequest("http://localhost:" + HttpTaskServer.PORT
                        + "/tasks/epic/",
                json);
        HttpResponse<String> response2 = makePostRequest("http://localhost:" + HttpTaskServer.PORT
                        + "/tasks/epic/",
                json2);
        assertEquals(response.statusCode(), 400);
        assertEquals(response2.statusCode(), 400);
    }

    @Test
    @DisplayName("Обновление задачи, случай: нет id у пришедшей задачи")
    void shouldReturnErrorWhenUpdateNoIdTask() {
        Task task = new Task("qq", "ne qq", TaskState.NEW);
        String json = gson.toJson(task);

        makePostRequest("http://localhost:" + HttpTaskServer.PORT  + "/tasks/task/", json);

        Task task2 = new Task("qq2", "ne qq2", TaskState.NEW);
        String json2 = gson.toJson(task2);
        HttpResponse<String> response = makePostRequest("http://localhost:" + HttpTaskServer.PORT
                        + "/tasks/task/?id=0",
                json2);
        assertEquals(response.statusCode(), 400);
    }

    @Test
    @DisplayName("Обновление задачи, случай: не совпадает id у пришедшей задачи и параметр в запросе")
    void shouldReturnErrorWhenUpdateBadIdTask() {
        Task task = new Task("qq", "ne qq", TaskState.NEW);
        String json = gson.toJson(task);

        makePostRequest("http://localhost:" + HttpTaskServer.PORT  + "/tasks/task/", json);

        Task task2 = new Task("qq2", "ne qq2", TaskState.NEW);

        task2.setTaskId(555);

        String json2 = gson.toJson(task2);
        HttpResponse<String> response = makePostRequest("http://localhost:" + HttpTaskServer.PORT
                        + "/tasks/task/?id=0",
                json2);
        assertEquals(response.statusCode(), 400);
    }

    @Test
    @DisplayName("POST-запрос с некорректным JSON")
    void shouldReturnErrorWhenBadJson() {
        String someBadJson1 = "{sadasdasd}";
        String someBadJson2 = "\"taskName\":\"name\",\"taskDescription\":\"descr\",\"taskState\":\"sss\",\"taskType\":"
                + "\"UNKNOW\"";

        HttpResponse<String> response = makePostRequest("http://localhost:" + HttpTaskServer.PORT
                        + "/tasks/epic/",
                someBadJson1);
        HttpResponse<String> response2 = makePostRequest("http://localhost:" + HttpTaskServer.PORT
                        + "/tasks/task/",
                someBadJson2);
        assertEquals(response.statusCode(), 400);
        assertEquals(response2.statusCode(), 400);
    }

    @Test
    @DisplayName("Запрос с некорректным url")
    void shouldUseBadEndpoint() {
        HttpResponse<String> response = makeGetRequest("http://localhost:" + HttpTaskServer.PORT
                + "/tasks/endpoint/");
        assertEquals(response.body(), "bad endpoint");
    }
}
