package controller;

import com.google.gson.*;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Optional;

import model.*;
import service.FileBackedTasksManager;
import service.Managers;

public class HttpTaskServer {

    public static final int PORT = 8080;
    private final HttpServer httpServer;
    private final FileBackedTasksManager manager;
    private final Gson gson;

    public HttpTaskServer() throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", this::handle);
        manager = Managers.getFileManager(Paths.get("taskmanager.csv"));
        gson = new Gson();
    }

    private void writeResponse(HttpExchange exchange, String responseString, int responseCode) throws IOException {
        if (responseString.isBlank()) {
            exchange.sendResponseHeaders(responseCode, 0);
        } else {
            byte[] bytes = responseString.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(responseCode, 0);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }
        exchange.close();
    }

    public void start() {
        httpServer.start();
        System.out.println("старт HttpTaskServer, порт: " + PORT);
    }

    public void stop() {
        httpServer.stop(1);
        System.out.println("стоп HttpTaskServer, порт: " + PORT);
    }

    private void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getQuery() == null
                ? exchange.getRequestURI().getPath()
                : (exchange.getRequestURI().getPath() + "?" + exchange.getRequestURI().getQuery());

        Endpoints endpoint = getEndpointByRequest(path, exchange.getRequestMethod());

        switch (endpoint) {
            case GET_TASKS: {
                handleGetTasks(exchange);
                break;
            }
            case GET_EPICS: {
                handleGetEpics(exchange);
                break;
            }
            case GET_SUBTASKS: {
                handleGetSubtasks(exchange);
                break;
            }
            case GET_HISTORY: {
                handleGetHistory(exchange);
                break;
            }
            case GET_PRIORITIZED_TASKS: {
                handleGetPrioritizedTasks(exchange);
                break;
            }
            case GET_TASK_BY_ID: {
                handleGetTaskById(exchange);
                break;
            }
            case GET_EPIC_BY_ID: {
                handleGetEpicById(exchange);
                break;
            }
            case GET_SUBTASK_BY_ID: {
                handleGetSubTaskById(exchange);
                break;
            }
            case POST_TASK: {
                handlePostTask(exchange);
                break;
            }
            case POST_EPIC: {
                handlePostEpic(exchange);
                break;
            }
            case POST_SUBTASK: {
                handlePostSubtask(exchange);
                break;
            }
            case POST_UPDATE_TASK: {
                handlePostUpdateTask(exchange);
                break;
            }
            case POST_UPDATE_EPIC: {
                handlePostUpdateEpic(exchange);
                break;
            }
            case POST_UPDATE_SUBTASK: {
                handlePostUpdateSubtask(exchange);
                break;
            }
            case DELETE_TASKS: {
                handleDeleteTasks(exchange);
                break;
            }
            case DELETE_SUBTASKS: {
                handleDeleteSubtasks(exchange);
                break;
            }
            case DELETE_EPICS: {
                handleDeleteEpics(exchange);
                break;
            }
            case DELETE_TASK_BY_ID: {
                handleDeleteTaskById(exchange);
                break;
            }
            case DELETE_EPIC_BY_ID: {
                handleDeleteEpicById(exchange);
                break;
            }
            case DELETE_SUBTASK_BY_ID: {
                handleDeleteSubtaskById(exchange);
                break;
            }
            default: {
                writeResponse(exchange, "bad endpoint", 404);
            }
        }
    }

    private void handleDeleteSubtaskById(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();

        if (!validateQuery(query)) {
            writeResponse(exchange, "Некорректные параметры в запросе", 404);
        }

        Integer id = getOptionalOfTaskId(getFirstParameterValue(query)).get();

        if (manager.getSubtaskById(id) == null) {
            writeResponse(exchange, "Такой задачи нет", 404);
        } else {
            manager.removeSubtaskById(id);
            writeResponse(exchange, "", 200);
        }
    }

    private void handleDeleteEpicById(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();

        if (!validateQuery(query)) {
            writeResponse(exchange, "Некорректные параметры в запросе", 404);
        }

        Integer id = getOptionalOfTaskId(getFirstParameterValue(query)).get();

        if (manager.getEpicById(id) == null) {
            writeResponse(exchange, "Такой задачи нет", 404);
        } else {
            manager.removeEpicById(id);
            writeResponse(exchange, "", 200);
        }
    }

    private void handleDeleteTaskById(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();

        if (!validateQuery(query)) {
            writeResponse(exchange, "Некорректные параметры в запросе", 404);
        }

        Integer id = getOptionalOfTaskId(getFirstParameterValue(query)).get();

        if (manager.getTaskById(id) == null) {
            writeResponse(exchange, "Такой задачи нет", 404);
        } else {
            manager.removeTaskById(id);
            writeResponse(exchange, "", 200);
        }
    }

    private void handleDeleteEpics(HttpExchange exchange) throws IOException {
        manager.deleteAllEpics();
        if (manager.getEpicsList().isEmpty()) {
            writeResponse(exchange, "", 200);
        } else {
            writeResponse(exchange, "", 500);
        }
    }

    private void handleDeleteSubtasks(HttpExchange exchange) throws IOException {
        manager.deleteAllSubTasks();
        if (manager.getSubtasksList().isEmpty()) {
            writeResponse(exchange, "", 200);
        } else {
            writeResponse(exchange, "", 500);
        }
    }

    private void handleDeleteTasks(HttpExchange exchange) throws IOException {
        manager.deleteAllTasks();
        if (manager.getTasksList().isEmpty()) {
            writeResponse(exchange, "", 200);
        } else {
            writeResponse(exchange, "", 500);
        }
    }

    private void handlePostUpdateSubtask(HttpExchange exchange) throws IOException {
        System.out.println("POST-запрос на обновление сабтаска");
        String requestBody = getRequestBodyAsString(exchange);

        try {

            Subtask subtask = gson.fromJson(requestBody, Subtask.class);
            String query = exchange.getRequestURI().getQuery();
            Integer id = getOptionalOfTaskId(getFirstParameterValue(query)).get();

            if (isCorrectParametersWhenUpdateTask(subtask, id)) {
                manager.updateSubtask(subtask);
                writeResponse(exchange, "Задача обновлена. Id:" + subtask.getTaskId(), 201);
            } else {
                writeResponse(exchange, "Некорректный JSON", 400);
            }
        } catch (JsonSyntaxException ex) {
            System.out.println(ex.getMessage());
            writeResponse(exchange, "Некорректный JSON", 400);
        }
    }

    private void handlePostUpdateEpic(HttpExchange exchange) throws IOException {
        System.out.println("POST-запрос на обновление эпика");
        String requestBody = getRequestBodyAsString(exchange);

        try {

            Epic epic = gson.fromJson(requestBody, Epic.class);
            String query = exchange.getRequestURI().getQuery();
            Integer id = getOptionalOfTaskId(getFirstParameterValue(query)).get();

            if (isCorrectParametersWhenUpdateTask(epic, id)) {
                manager.updateEpic(epic);
                writeResponse(exchange, "Задача обновлена. Id:" + epic.getTaskId(), 201);
            } else {
                writeResponse(exchange, "Некорректный JSON", 400);
            }
        } catch (JsonSyntaxException ex) {
            System.out.println(ex.getMessage());
            writeResponse(exchange, "Некорректный JSON", 400);
        }
    }

    private void handlePostUpdateTask(HttpExchange exchange) throws IOException {
        System.out.println("POST-запрос обновления Task");
        String requestBody = getRequestBodyAsString(exchange);

        try {

            Task task = gson.fromJson(requestBody, Task.class);
            String query = exchange.getRequestURI().getQuery();
            Integer id = getOptionalOfTaskId(getFirstParameterValue(query)).get();

            if (isCorrectParametersWhenUpdateTask(task, id)) {
                manager.updateTask(task);
                writeResponse(exchange, "Задача обновлена. Id:" + task.getTaskId(), 201);
            } else {
                writeResponse(exchange, "Некорректный JSON", 400);
            }
        } catch (JsonSyntaxException ex) {
            System.out.println(ex.getMessage());
            writeResponse(exchange, "Некорректный JSON", 400);
        }
    }

    private void handlePostSubtask(HttpExchange exchange) throws IOException {
        String requestBody = getRequestBodyAsString(exchange);
        System.out.println("POST-запрос на создание сабтаска");

        try {

            Subtask subtask = gson.fromJson(requestBody, Subtask.class);

            if (isCorrectParametersWhenCreateTask(subtask)) {
                manager.createSubtask(subtask);
                writeResponse(exchange, "Подзадача создана. Id:" + subtask.getTaskId(), 201);
            } else {
                writeResponse(exchange, "Некорректный JSON", 400);
            }
        } catch (JsonSyntaxException ex) {
            System.out.println(ex.getMessage());
            writeResponse(exchange, "Некорректный JSON", 400);
        }
    }

    private void handlePostEpic(HttpExchange exchange) throws IOException {
        System.out.println("POST-запрос на создание эпика");
        String requestBody = getRequestBodyAsString(exchange);

        try {

            Epic epic = gson.fromJson(requestBody, Epic.class);

            if (isCorrectParametersWhenCreateTask(epic)) {
                manager.createEpic(epic);
                writeResponse(exchange, "Эпик создан. Id:" + epic.getTaskId(), 201);
            } else {
                writeResponse(exchange, "Некорректный JSON", 400);
            }
        } catch (JsonSyntaxException ex) {
            System.out.println(ex.getMessage());
            writeResponse(exchange, "Некорректный JSON", 400);
        }
    }

    private void handlePostTask(HttpExchange exchange) throws IOException {
        System.out.println("POST-запрос на создание Task task");
        String requestBody = getRequestBodyAsString(exchange);
        System.out.println(requestBody);

        try {
            Task task = gson.fromJson(requestBody, Task.class);

            if (isCorrectParametersWhenCreateTask(task)) {
                manager.createTask(task);
                writeResponse(exchange, "Задача создана. Id:" + task.getTaskId(), 201);
            } else {
                writeResponse(exchange, "Некорректный JSON", 400);
            }
        } catch (JsonSyntaxException ex) {
            System.out.println(ex.getMessage());
            writeResponse(exchange, "Некорректный JSON", 400);
        }
    }

    private boolean isCorrectParametersWhenCreateTask(Task task) {
        return task.getTaskId() == null
                && task.getTaskState() != null
                && !task.getTaskName().isBlank()
                && !task.getTaskDescription().isBlank();
    }

    private boolean isCorrectParametersWhenUpdateTask(Task task, Integer idFromJson) {
        return task.getTaskId() != null
                && idFromJson == task.getTaskId()
                && task.getTaskState() != null
                && !task.getTaskName().isBlank()
                && !task.getTaskDescription().isBlank();
    }

    private void handleGetSubTaskById(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();

        if (!validateQuery(query)) {
            writeResponse(exchange, "Некорректные параметры в запросе", 404);
        }

        Integer id = getOptionalOfTaskId(getFirstParameterValue(query)).get();
        Subtask subtask = manager.getSubtaskById(id);

        if (subtask == null) {
            writeResponse(exchange, "Такой подзадачи нет", 404);
        } else {
            writeResponse(exchange, gson.toJson(subtask), 200);
        }
    }

    private void handleGetTaskById(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();

        if (!validateQuery(query)) {
            writeResponse(exchange, "Некорректные параметры в запросе", 404);
        }

        Integer id = getOptionalOfTaskId(getFirstParameterValue(query)).get();
        Task task = manager.getTaskById(id);

        if (task == null) {
            writeResponse(exchange, "Такой задачи нет", 404);
        } else {
            writeResponse(exchange, gson.toJson(task), 200);
        }
    }

    private void handleGetEpicById(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();

        if (!validateQuery(query)) {
            writeResponse(exchange, "Некорректные параметры в запросе", 404);
        }

        Integer id = getOptionalOfTaskId(getFirstParameterValue(query)).get();
        Epic epic = manager.getEpicById(id);

        if (epic == null) {
            writeResponse(exchange, "Такого эпика нет", 404);
        } else {
            writeResponse(exchange, gson.toJson(epic), 200);
        }
    }

    private void handleGetPrioritizedTasks(HttpExchange exchange) throws IOException {
        writeResponse(exchange, gson.toJson(manager.getPrioritizedTasks()), 200);
    }

    private void handleGetHistory(HttpExchange exchange) throws IOException {
        writeResponse(exchange, gson.toJson(manager.getHistoryList()), 200);
    }

    private void handleGetSubtasks(HttpExchange exchange) throws IOException {
        writeResponse(exchange, gson.toJson(manager.getSubtasksList()), 200);
    }

    private void handleGetTasks(HttpExchange exchange) throws IOException {
        writeResponse(exchange, gson.toJson(manager.getTasksList()), 200);
    }

    private void handleGetEpics(HttpExchange exchange) throws IOException {
        writeResponse(exchange, gson.toJson(manager.getEpicsList()), 200);
    }

    private Endpoints getEndpointByRequest(String requestPath, String requestMethod) {
        String[] splitted = requestPath.split("/");

        switch (requestMethod) {
            case "GET": {
                if (splitted.length == 3 && splitted[1].equals("tasks") && splitted[2].equals("task")) {
                    return Endpoints.GET_TASKS;
                }
                if (splitted.length == 3 && splitted[1].equals("tasks") && splitted[2].equals("epic")) {
                    return Endpoints.GET_EPICS;
                }
                if (splitted.length == 3 && splitted[1].equals("tasks") && splitted[2].equals("subtask")) {
                    return Endpoints.GET_SUBTASKS;
                }
                if (splitted.length == 3 && splitted[1].equals("tasks") && splitted[2].equals("history")) {
                    return Endpoints.GET_HISTORY;
                }
                if (splitted.length == 3 && splitted[1].equals("tasks") && splitted[2].equals("prioritized")) {
                    return Endpoints.GET_PRIORITIZED_TASKS;
                }
                if (splitted.length == 4 && splitted[1].equals("tasks") && splitted[2].equals("task")) {
                    return Endpoints.GET_TASK_BY_ID;
                }
                if (splitted.length == 4 && splitted[1].equals("tasks") && splitted[2].equals("epic")) {
                    return Endpoints.GET_EPIC_BY_ID;
                }
                if (splitted.length == 4 && splitted[1].equals("tasks") && splitted[2].equals("subtask")) {
                    return Endpoints.GET_SUBTASK_BY_ID;
                }
                break;
            }
            case "POST": {
                if (splitted.length == 3 && splitted[1].equals("tasks") && splitted[2].equals("task")) {
                    return Endpoints.POST_TASK;
                }
                if (splitted.length == 3 && splitted[1].equals("tasks") && splitted[2].equals("epic")) {
                    return Endpoints.POST_EPIC;
                }
                if (splitted.length == 3 && splitted[1].equals("tasks") && splitted[2].equals("subtask")) {
                    return Endpoints.POST_SUBTASK;
                }
                if (splitted.length == 4 && splitted[1].equals("tasks") && splitted[2].equals("task")) {
                    return Endpoints.POST_UPDATE_TASK;
                }
                if (splitted.length == 4 && splitted[1].equals("tasks") && splitted[2].equals("epic")) {
                    return Endpoints.POST_UPDATE_EPIC;
                }
                if (splitted.length == 4 && splitted[1].equals("tasks") && splitted[2].equals("subtask")) {
                    return Endpoints.POST_UPDATE_SUBTASK;
                }
                break;
            }
            case "DELETE": {
                if (splitted.length == 3 && splitted[1].equals("tasks") && splitted[2].equals("task")) {
                    return Endpoints.DELETE_TASKS;
                }
                if (splitted.length == 3 && splitted[1].equals("tasks") && splitted[2].equals("epic")) {
                    return Endpoints.DELETE_EPICS;
                }
                if (splitted.length == 3 && splitted[1].equals("tasks") && splitted[2].equals("subtask")) {
                    return Endpoints.DELETE_SUBTASKS;
                }
                if (splitted.length == 4 && splitted[1].equals("tasks") && splitted[2].equals("task")) {
                    return Endpoints.DELETE_TASK_BY_ID;
                }
                if (splitted.length == 4 && splitted[1].equals("tasks") && splitted[2].equals("epic")) {
                    return Endpoints.DELETE_EPIC_BY_ID;
                }
                if (splitted.length == 4 && splitted[1].equals("tasks") && splitted[2].equals("subtask")) {
                    return Endpoints.DELETE_SUBTASK_BY_ID;
                }
                break;
            }
        }
        return Endpoints.UNKNOW;
    }

    private boolean validateQuery(String queryString) {
        //проверяется, что параметр 1 и имеет вид id=число
        String[] splittedRequestParameters = queryString.split("=");
        String mayBeId = getFirstParameterValue(queryString);

        return (splittedRequestParameters.length == 2) && (splittedRequestParameters[0].equals("id"))
                && (!mayBeId.isBlank()) && (getOptionalOfTaskId(mayBeId).isPresent());

    }

    private String getFirstParameterValue(String queryString) {
        return queryString.split("=")[1];
    }

    private Optional<Integer> getOptionalOfTaskId(String mayBeId) {
        try {
            return Optional.of(Integer.parseInt(mayBeId));
        } catch (NumberFormatException ex) {
            return Optional.empty();
        }
    }

    private String getRequestBodyAsString(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
        String body = new String(is.readAllBytes());

        is.close();
        return body;
    }
}
