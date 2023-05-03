package test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.InMemoryTaskManager;
import service.Managers;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    InMemoryTaskManager manager;

    @BeforeEach
    void setUp() {
        manager = Managers.getDefault();
    }

    @Test
    void getHistoryList() {
        super.getHistoryList(manager);
    }

    @Test
    void createTask() {
        super.createTask(manager);
    }

    @Test
    void createEpic() {
        super.createEpic(manager);
    }

    @Test
    void createSubtask() {
        super.createSubtask(manager);
    }

    @Test
    void getTasksList() {
        super.getTasksList(manager);
    }

    @Test
    void getEpicsList() {
        super.getEpicsList(manager);
    }

    @Test
    void getSubtasksList() {
        super.getSubtasksList(manager);
    }

    @Test
    void deleteAllTasks() {
        super.deleteAllTasks(manager);
    }

    @Test
    void deleteAllSubTasks() {
        super.deleteAllSubTasks(manager);
    }

    @Test
    void deleteAllEpics() {
        super.deleteAllEpics(manager);
    }

    @Test
    void getTaskById() {
        super.getTaskById(manager);
    }

    @Test
    void getEpicById() {
        super.getEpicById(manager);
    }

    @Test
    void getSubtaskById() {
        super.getSubtaskById(manager);
    }

    @Test
    void removeTaskById() {
        super.removeTaskById(manager);
    }

    @Test
    void removeEpicById() {
        super.removeEpicById(manager);
    }

    @Test
    void removeSubtaskById() {
        super.removeSubtaskById(manager);
    }

    @Test
    void updateTask() {
        super.updateTask(manager);
    }

    @Test
    void updateSubtask() {
        super.updateSubtask(manager);
    }

    @Test
    void updateEpic() {
        super.updateEpic(manager);
    }

    @Test
    void calculateEpicState() {
        super.calculateEpicState(manager);
    }

    @Test
    void calculateEpicStartTime() {
        super.calculateEpicStartTime(manager);
    }

    @Test
    void calculateEpicDuration() {
        super.calculateEpicDuration(manager);
    }

    @Test
    void getPrioritizedTasks() {
        super.getPrioritizedTasks(manager);
    }

    @Test
    void shouldReturnFalseWhenValidateTime() {
        super.shouldReturnFalseWhenValidateTime(manager);
    }
}