package test;

import model.Task;
import model.TaskState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    private Task task;

    @BeforeEach
    void createTask() {
        task = new Task("itIsName", "itIsDescription", TaskState.NEW,
                Instant.parse("2023-06-05T02:00:00Z"), 30);
    }

    @Test
    void setTaskState() {
        task.setTaskState(TaskState.NEW);
        assertEquals(TaskState.NEW, task.getTaskState(), "Некорректно устанавливается статус задачи");

        task.setTaskState(TaskState.DONE);
        assertEquals(TaskState.DONE, task.getTaskState(), "Некорректно устанавливается статус задачи");

        task.setTaskState(TaskState.IN_PROGRESS);
        assertEquals(TaskState.IN_PROGRESS, task.getTaskState(), "Некорректно устанавливается статус задачи");
    }

    @Test
    void testToString() {
        task.setTaskId(123);
        assertEquals("123,TASK,itIsName,NEW,itIsDescription,2023-06-05T02:00:00Z,30," +
                "2023-06-05T02:30:00Z,", task.toString(), "Некорректное " +
                "отображение задачи в строку");
    }

    @Test
    void setTaskName() {
        task.setTaskName("ku-ku");
        assertEquals("ku-ku", task.getTaskName(), "Некорректно устанавливается имя задачи");
    }

    @Test
    void setTaskDescription() {
        task.setTaskDescription("new descr");
        assertEquals("new descr", task.getTaskDescription(), "Некорректно устанавливается описание");
    }

    @Test
    void setTaskId() {
        task.setTaskId(100500);
        assertEquals(100500, task.getTaskId(), "Некорректно устанавливается айди задачи");
    }

    @Test
    void getTaskDescription() {
        task.setTaskDescription("hhh");
        assertEquals("hhh", task.getTaskDescription(), "Некорректно получаем описание");
    }

    @Test
    void getTaskId() {
        task.setTaskId(999);
        assertEquals(999, task.getTaskId(), "Некорректно получаем айди задачи");
    }

    @Test
    void getTaskState() {
        task.setTaskState(TaskState.IN_PROGRESS);
        assertEquals(TaskState.IN_PROGRESS, task.getTaskState(), "Некорректно получаем статус задачи");
    }

    @Test
    void getTaskName() {
        task.setTaskName("kekekek");
        assertEquals("kekekek", task.getTaskName(), "Некорректно получаем имя задачи");
    }
}