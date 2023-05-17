package test;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskState;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.KVServer;
import service.HttpTaskManager;
import service.ManagerSaveException;
import service.Managers;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskManagerTest {
    private static KVServer kvServer;
    private HttpTaskManager httpTaskManager;

    @BeforeEach
    void kvServerStart() {
        try {
            kvServer = new KVServer();
            kvServer.start();
            httpTaskManager = Managers.getDefault("http://localhost:" + KVServer.PORT);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @AfterEach
    void kvServerStop() {
        kvServer.stop();
    }

    @Test
    void shouldSaveAndLoadDefaultCase() throws ManagerSaveException {
        Task task = httpTaskManager.createTask(new Task("qq", "ne qq", TaskState.NEW));
        Epic epic = httpTaskManager.createEpic(new Epic("qq epic", "ne qq epic"));
        Subtask subtask = httpTaskManager.createSubtask(new Subtask("qq subtask", "sss", TaskState.IN_PROGRESS,
                epic.getTaskId()));
        httpTaskManager.getTaskById(task.getTaskId());
        httpTaskManager.getEpicById(epic.getTaskId());
        httpTaskManager.getSubtaskById(subtask.getTaskId());
        long token = httpTaskManager.getToken();

        HttpTaskManager loadedManager = new HttpTaskManager("http://localhost:" + KVServer.PORT, token);

        assertEquals(httpTaskManager.getTasksList(), loadedManager.getTasksList());
        assertEquals(httpTaskManager.getEpicsList(), loadedManager.getEpicsList());
        assertEquals(httpTaskManager.getSubtasksList(), loadedManager.getSubtasksList());
        assertEquals(httpTaskManager.getPrioritizedTasks(), loadedManager.getPrioritizedTasks());
        assertEquals(httpTaskManager.getHistoryList(), loadedManager.getHistoryList());
    }
}