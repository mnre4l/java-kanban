package test;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.FileBackedTasksManager;
import service.Managers;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {
    Path path = Paths.get("taskmanager.csv");

    @BeforeEach
    void setUp() {
        manager = Managers.getFileManager(path);
    }

    @Test
    @DisplayName("Проверка работы по сохранению и восстановлению состояния - стандартная")
    void defaultLoadFromFile() {
        manager.createTask(new Task("task1", "descr1", TaskState.IN_PROGRESS,
                Instant.parse("2023-06-05T02:00:00Z"), 30));
        Epic epic = manager.createEpic(new Epic("epic1", "descr epic1"));
        manager.createSubtask(new Subtask("sub1", "descr1", TaskState.NEW, epic,
                Instant.parse("2023-06-05T03:00:00Z"), 30));
        manager.createSubtask(new Subtask("sub2", "descr2", TaskState.DONE, epic,
                Instant.parse("2023-06-05T04:00:00Z"), 30));
        manager.getEpicById(1);
        manager.getTaskById(0);

        FileBackedTasksManager manager2 = FileBackedTasksManager.loadFromFile(path);

        assertNotEquals(manager2, manager); //проверка, что разные ссылки
        assertArrayEquals(manager2.getTasksList().toArray(), manager.getTasksList().toArray());
        assertArrayEquals(manager2.getSubtasksList().toArray(), manager.getSubtasksList().toArray());
        assertArrayEquals(manager2.getEpicsList().toArray(), manager.getEpicsList().toArray());
        assertArrayEquals(manager2.getHistoryList().toArray(), manager.getHistoryList().toArray());
        assertArrayEquals(manager2.getPrioritizedTasks().toArray(), manager.getPrioritizedTasks().toArray());
    }

    @Test
    @DisplayName("Проверка работы по сохранению и восстановлению состояния - пустой список задач")
    void emptyTaskListLoadFromFile() {
        FileBackedTasksManager manager2 = FileBackedTasksManager.loadFromFile(path);

        assertTrue(manager2.getHistoryList().isEmpty());
        assertTrue(manager2.getTasksList().isEmpty());
        assertTrue(manager2.getPrioritizedTasks().isEmpty());
        assertTrue(manager2.getEpicsList().isEmpty());
        assertTrue(manager2.getSubtasksList().isEmpty());
    }

    @Test
    @DisplayName("Проверка работы по сохранению и восстановлению состояния - эпик без подзадач")
    void justEpicLoadFromFile() {
        Epic epic = manager.createEpic(new Epic("epic1", "descr epic1"));
        FileBackedTasksManager manager2 = FileBackedTasksManager.loadFromFile(path);

        assertTrue(manager2.getHistoryList().isEmpty());
        assertTrue(manager2.getTasksList().isEmpty());
        assertTrue(manager2.getPrioritizedTasks().isEmpty());
        assertEquals(manager2.getEpicsList().size(), 1, "Некорректный список с эпиками");
        assertTrue(manager2.getSubtasksList().isEmpty());
    }

    @Test
    @DisplayName("Проверка работы по сохранению и восстановлению состояния - пустая история")
    void emptyHistoryLoadFromFile() {
        manager.createTask(new Task("task1", "descr1", TaskState.IN_PROGRESS,
                Instant.parse("2023-06-05T02:00:00Z"), 30));
        Epic epic = manager.createEpic(new Epic("epic1", "descr epic1"));
        manager.createSubtask(new Subtask("sub1", "descr1", TaskState.NEW, epic,
                Instant.parse("2023-06-05T03:00:00Z"), 30));
        manager.createSubtask(new Subtask("sub2", "descr2", TaskState.DONE, epic,
                Instant.parse("2023-06-05T04:00:00Z"), 30));

        FileBackedTasksManager manager2 = FileBackedTasksManager.loadFromFile(path);

        assertNotEquals(manager2, manager); //проверка, что разные ссылки
        assertArrayEquals(manager2.getTasksList().toArray(), manager.getTasksList().toArray());
        assertArrayEquals(manager2.getSubtasksList().toArray(), manager.getSubtasksList().toArray());
        assertArrayEquals(manager2.getEpicsList().toArray(), manager.getEpicsList().toArray());
        assertArrayEquals(manager2.getHistoryList().toArray(), manager.getHistoryList().toArray());
        assertArrayEquals(manager2.getPrioritizedTasks().toArray(), manager.getPrioritizedTasks().toArray());
    }
}