package test;

import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.HistoryManager;
import service.Managers;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Тестирование менеджера истории")
class InMemoryHistoryManagerTest {
    HistoryManager historyManager;

    @BeforeEach
    void setUp() {
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    @DisplayName("Добавление нескольких задач в историю")
    void add() {
        Task firstTask = new Task("first task name", "first task descr", TaskState.NEW);
        firstTask.setTaskId(555);
        Task secondTask = new Task("second task name", "second task descr", TaskState.IN_PROGRESS);
        secondTask.setTaskId(999);
        Task thirdTask = new Task("third task name", "third task descr", TaskState.DONE);
        thirdTask.setTaskId(777);

        historyManager.add(secondTask);
        historyManager.add(firstTask);
        historyManager.add(thirdTask);
        historyManager.add(secondTask);
        assertEquals(historyManager.getHistory().size(), 3, "Число сохраненных задач нерверное");
        assertNotNull(historyManager.getHistory(), "Список истории = null");
        historyManager.getHistory().forEach(task -> {
            assertTrue(List.of(firstTask.getTaskId(), secondTask.getTaskId(),
                    thirdTask.getTaskId()).contains(task.getTaskId()), "Задачи нет в истории: " + task.getTaskName());
        });
    }

    @Test
    @DisplayName("Добавление одинаковых задач в историю")
    void addDuplicate() {
        Task task = new Task("first task name", "first task descr", TaskState.NEW);
        task.setTaskId(555);

        historyManager.add(task);
        historyManager.add(task);
        historyManager.add(task);
        assertEquals(historyManager.getHistory().size(), 1, "Число сохраненных задач неверное");
        assertTrue(historyManager.getHistory().contains(task));
        assertNotNull(historyManager.getHistory(), "Список истории = null");
    }

    @Test
    @DisplayName("Получение непустой истории")
    void getHistory() {
        Task firstTask = new Task("first task name", "first task descr", TaskState.NEW);
        firstTask.setTaskId(555);
        Task secondTask = new Task("second task name", "second task descr", TaskState.IN_PROGRESS);
        secondTask.setTaskId(999);
        Task thirdTask = new Task("third task name", "third task descr", TaskState.DONE);
        thirdTask.setTaskId(777);

        historyManager.add(secondTask);
        historyManager.add(firstTask);
        historyManager.add(thirdTask);
        assertNotNull(historyManager.getHistory(), "Список истории = null");
        assertEquals(new ArrayList<>(List.of(thirdTask, firstTask, secondTask)), historyManager.getHistory());
    }

    @Test
    @DisplayName("Получение пустой истории")
    void getEmptyHistory() {
        assertNotNull(historyManager.getHistory(), "Список истории = null");
        assertTrue(historyManager.getHistory().isEmpty(), "История не пустая");
    }

    @Test
    @DisplayName("Удаление задачи из истории: стандартный случай, середина")
    void remove() {
        Task firstTask = new Task("first task name", "first task descr", TaskState.NEW);
        firstTask.setTaskId(1);
        Task secondTask = new Task("second task name", "second task descr", TaskState.IN_PROGRESS);
        secondTask.setTaskId(2);
        Task thirdTask = new Task("third task name", "third task descr", TaskState.DONE);
        thirdTask.setTaskId(3);

        historyManager.add(secondTask);
        historyManager.add(firstTask);
        historyManager.add(thirdTask);
        historyManager.remove(1);
        assertNotNull(historyManager.getHistory(), "Список истории = null");
        assertArrayEquals(List.of(thirdTask, secondTask).toArray(), historyManager.getHistory().toArray());
    }

    @Test
    @DisplayName("Удаление задачи: пустая история")
    void removeTaskFromEmptyHistory() {
        historyManager.remove(555);
    }

    @Test
    @DisplayName("Удаление задачи: отсутствующая задача")
    void removeTaskThatNotInHistory() {
        Task firstTask = new Task("first task name", "first task descr", TaskState.NEW);

        firstTask.setTaskId(1);
        historyManager.add(firstTask);
        historyManager.remove(100500);
        assertNotNull(historyManager.getHistory(), "Список истории = null");
    }

    @Test
    @DisplayName("Удаление задачи: первая по списку истории")
    void removeFirstTaskInHistory() {
        Task firstTask = new Task("first task name", "first task descr", TaskState.NEW);
        firstTask.setTaskId(1);
        Task secondTask = new Task("second task name", "second task descr", TaskState.IN_PROGRESS);
        secondTask.setTaskId(2);
        Task thirdTask = new Task("third task name", "third task descr", TaskState.DONE);
        thirdTask.setTaskId(3);

        historyManager.add(firstTask);
        historyManager.add(secondTask);
        historyManager.add(thirdTask);
        historyManager.remove(3);
        assertArrayEquals(List.of(secondTask, firstTask).toArray(), historyManager.getHistory().toArray());
        assertNotNull(historyManager.getHistory(), "Список истории = null");
    }

    @Test
    @DisplayName("Удаление задачи: последняя по списку истории")
    void removeLastTaskInHistory() {
        Task firstTask = new Task("first task name", "first task descr", TaskState.NEW);
        firstTask.setTaskId(1);
        Task secondTask = new Task("second task name", "second task descr", TaskState.IN_PROGRESS);
        secondTask.setTaskId(2);
        Task thirdTask = new Task("third task name", "third task descr", TaskState.DONE);
        thirdTask.setTaskId(3);

        historyManager.add(firstTask);
        historyManager.add(secondTask);
        historyManager.add(thirdTask);
        historyManager.remove(1);
        assertArrayEquals(List.of(thirdTask, secondTask).toArray(), historyManager.getHistory().toArray());
        assertNotNull(historyManager.getHistory(), "Список истории = null");
    }
}