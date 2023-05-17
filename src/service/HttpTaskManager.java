package service;

import com.google.gson.reflect.TypeToken;
import model.Epic;
import model.Subtask;
import model.Task;
import server.KVTaskClient;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class HttpTaskManager extends FileBackedTasksManager implements TaskManager {
    private static KVTaskClient client;
    private static Gson gson;

    public HttpTaskManager(String kvServerUrl) throws ManagerSaveException {
        this.client = new KVTaskClient(kvServerUrl);
        this.gson = new Gson();
    }

    public HttpTaskManager(String kvServerUrl, long token) throws ManagerSaveException {
        this.client = new KVTaskClient(kvServerUrl, token);
        this.gson = new Gson();
        load();
    }

    public long getToken() {
        return client.getToken();
    }

    @Override
    protected void save() throws ManagerSaveException {
        client.put("tasks", gson.toJson(tasksList));
        client.put("epics", gson.toJson(epicsList));
        client.put("subtasks", gson.toJson(subtasksList));
        //тк типы задач разные => может быть сложно восстанавливать из json => наверное проще сохранять id
        // и восстановить по ним
        client.put("history", gson.toJson(getHistoryList().stream()
                .map(Task::getTaskId)
                .collect(Collectors.toList())));
        System.out.println("Сохранено на сервере");
    }

    public void load() {
        HashMap<Integer, Task> tasks = gson.fromJson(client.load("tasks"),
                new TypeToken<HashMap<Integer, Task>>(){}.getType());
        HashMap<Integer, Epic> epics = gson.fromJson(client.load("epics"),
                new TypeToken<HashMap<Integer, Epic>>(){}.getType());
        HashMap<Integer, Subtask> subtasks = gson.fromJson(client.load("subtasks"),
                new TypeToken<HashMap<Integer, Subtask>>(){}.getType());
        List<Integer> history = gson.fromJson(client.load("history"), new TypeToken<List<Integer>>(){}.getType());
        for (Integer id : tasks.keySet()) {
            this.tasksList.put(id, tasks.get(id));
            this.prioritizedTasks.add(tasks.get(id));
        }
        for (Integer id : subtasks.keySet()) {
            this.subtasksList.put(id, subtasks.get(id));
            this.prioritizedTasks.add(subtasks.get(id));
        }
        for (Integer id : epics.keySet()) {
            this.epicsList.put(id, epics.get(id));
        }
        Collections.reverse(history);
        for (Integer id : history) {
            this.getTaskById(id);
            this.getEpicById(id);
            this.getSubtaskById(id);
        }
    }
}
