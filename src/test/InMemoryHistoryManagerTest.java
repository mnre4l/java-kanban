package test;

import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.HistoryManager;
import service.Managers;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    HistoryManager historyManager;

    @BeforeEach
    void setUp() {
        historyManager = Managers.getDefaultHistory();
    }

    @Test
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

        assertEquals(new ArrayList<Task>(List.of(thirdTask, firstTask, secondTask)), historyManager.getHistory());
    }

    @Test
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
        historyManager.getHistory().forEach(task -> {
            assertTrue(List.of(firstTask.getTaskId(), secondTask.getTaskId(),
                    thirdTask.getTaskId()).contains(task.getTaskId()));
        });
    }
}