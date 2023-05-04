package test;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.FileBackedTasksManager;
import service.Managers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {
    FileBackedTasksManager manager;
    Path path = Paths.get("taskmanager.csv");

    @BeforeEach
    void setUp() {
        manager = Managers.getFileManager(path);
    }

    @Test
    void loadFromFile() {
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

        assertArrayEquals(manager2.getTasksList().toArray(), manager.getTasksList().toArray());
        assertArrayEquals(manager2.getSubtasksList().toArray(), manager.getSubtasksList().toArray());
        assertArrayEquals(manager2.getEpicsList().toArray(), manager.getEpicsList().toArray());
        assertArrayEquals(manager2.getHistoryList().toArray(), manager.getHistoryList().toArray());
    }

    @Test
    void createTask() {
        super.createTask(manager);
        try {
            String content = Files.readString(path);
            assertEquals("id,type,name,status,description,startTime,duration,endTime,epic\n" +
                            "0,TASK,itIsName,IN_PROGRESS,itIsDescr,2023-06-05T02:00:00Z,45,2023-06-05T02:45:00Z,\n" +
                            "\n" +
                            "0",
                    content, "Запись в файл некорректная");
        } catch (IOException e) {
            assertTrue(false, "Файл недоступен");
        }
    }

    @Test
    void createEpic() {
        super.createEpic(manager);
        try {
            String content = Files.readString(path);
            assertEquals("id,type,name,status,description,startTime,duration,endTime,epic\n" +
                            "0,EPIC,itIsEpic,NEW,itIsEpicDescr," + manager.getEpicById(0).getStartTime() + ",0," +
                            manager.getEpicById(0).getEndTime() + ",\n" +
                            "\n" +
                            "0",
                    content, "Запись в файл некорректная");
        } catch (IOException e) {
            assertTrue(false, "Файл недоступен");
        }
    }

    @Test
    void createSubtask() {
        super.createSubtask(manager);
        try {
            String content = Files.readString(path);
            assertEquals("id,type,name,status,description,startTime,duration,endTime,epic\n" +
                            "0,EPIC,itIsEpic,NEW,itIsEpicDescr,2023-06-05T02:00:00Z,45,2023-06-05T02:45:00Z,\n" +
                            "1,SUBTASK,subname,NEW,sub descr,2023-06-05T02:00:00Z,45,2023-06-05T02:45:00Z,0" +
                            "\n" +
                            "\n" +
                            "1",
                    content, "Запись в файл некорректная");
        } catch (IOException e) {
            assertTrue(false, "Файл недоступен");
        }
    }

    @Test
    void deleteAllTasks() {
        super.deleteAllTasks(manager);
        try {
            String content = Files.readString(path);
            assertEquals("id,type,name,status,description,startTime,duration,endTime,epic\n",
                    content, "Запись в файл некорректная");
        } catch (IOException e) {
            assertTrue(false, "Файл недоступен");
        }
    }

    @Test
    void deleteAllSubTasks() {
        super.deleteAllSubTasks(manager);
        try {
            String content = Files.readString(path);
            assertEquals("id,type,name,status,description,startTime,duration,endTime,epic\n" +
                    "0,EPIC,itIsEpic,NEW,itIsEpicDescr," + manager.getEpicById(0).getStartTime() +",0," +
                    manager.getEpicById(0).getEndTime() + ",\n" +
                    "2,EPIC,itIsEpic2,NEW,itIsEpicDescr2," + manager.getEpicById(2).getStartTime() +",0," +
                            manager.getEpicById(2).getEndTime() + ",\n",
                    content,
                    "Запись в файл некорректная");
        } catch (IOException e) {
            assertTrue(false, "Файл недоступен");
        }
    }

    @Test
    void deleteAllEpics() {
        super.deleteAllEpics(manager);
        try {
            String content = Files.readString(path);
            assertEquals("id,type,name,status,description,startTime,duration,endTime,epic\n",
                    content,
                    "Запись в файл некорректная");
        } catch (IOException e) {
            assertTrue(false, "Файл недоступен");
        }
    }

    @Test
    void getTaskById() {
        super.getTaskById(manager);
        try {
            String content = Files.readString(path);
            assertEquals("id,type,name,status,description,startTime,duration,endTime,epic\n" +
                    "0,TASK,itIsName,IN_PROGRESS,itIsDescr," + manager.getTaskById(0).getStartTime() + ",0," +
                    manager.getTaskById(0).getEndTime() + ",\n"
                    + "\n"
                    + "0", content, "Запись в файл некорректная");
        } catch (IOException e) {
            assertTrue(false, "Файл недоступен");
        }
    }

    @Test
    void getEpicById() {
        super.getEpicById(manager);
        try {
            String content = Files.readString(path);
            assertEquals("id,type,name,status,description,startTime,duration,endTime,epic\n" +
                    "0,EPIC,itIsEpic,NEW,itIsEpicDescr," + manager.getEpicById(0).getStartTime() + ",0," +
                    manager.getEpicById(0).getEndTime() + ",\n" +
                    "\n" +
                    "0", content, "Запись в файл некорректная");
        } catch (IOException e) {
            assertTrue(false, "Файл недоступен");
        }
    }

    @Test
    void getSubtaskById() {
        super.getSubtaskById(manager);
        try {
            String content = Files.readString(path);
            assertEquals("id,type,name,status,description,startTime,duration,endTime,epic\n" +
                    "0,EPIC,itIsEpic,NEW,itIsEpicDescr,2023-06-05T02:15:00Z,15,2023-06-05T02:30:00Z,\n"
                    + "1,SUBTASK,subname,NEW,sub descr,2023-06-05T02:15:00Z,15,2023-06-05T02:30:00Z,0\n"
                    + "\n"
                    + "1", content, "Запись в файл некорректная");
        } catch (IOException e) {
            assertTrue(false, "Файл недоступен");
        }
    }

    @Test
    void removeTaskById() {
        super.removeTaskById(manager);
        try {
            String content = Files.readString(path);
            assertEquals("id,type,name,status,description,startTime,duration,endTime,epic\n",
                    content,
                    "Запись в файл некорректная");
        } catch (IOException e) {
            assertTrue(false, "Файл недоступен");
        }
    }

    @Test
    void removeEpicById() {
        super.removeEpicById(manager);
        try {
            String content = Files.readString(path);
            assertEquals("id,type,name,status,description,startTime,duration,endTime,epic\n" +
                    "0,EPIC,itIsEpic,NEW,itIsEpicDescr,2023-06-05T02:15:00Z,15,2023-06-05T02:30:00Z,\n" +
                    "1,SUBTASK,subname,NEW,sub descr,2023-06-05T02:15:00Z,15,2023-06-05T02:30:00Z,0\n", content, "Запись в файл некорректная");
        } catch (IOException e) {
            assertTrue(false, "Файл недоступен");
        }
    }

    @Test
    void removeSubtaskById() {
        super.removeSubtaskById(manager);
        try {
            String content = Files.readString(path);
            assertEquals("id,type,name,status,description,startTime,duration,endTime,epic\n" +
                    "0,EPIC,itIsEpic2,NEW,itIsEpicDescr2," + manager.getEpicById(0).getStartTime() + ",0," +
                    manager.getEpicById(0).getEndTime() + ",\n" +
                    "1,SUBTASK,subname2,NEW,sub descr2," + manager.getSubtaskById(1).getStartTime() + ",0," +
                    manager.getSubtaskById(1).getEndTime() + ",0\n",
                    content,
                    "Запись в файл некорректная");
        } catch (IOException e) {
            assertTrue(false, "Файл недоступен");
        }
    }

    @Test
    void updateTask() {
        super.updateTask(manager);
        try {
            String content = Files.readString(path);
            assertEquals("id,type,name,status,description,startTime,duration,endTime,epic\n" +
                    "0,TASK,itIsNewName,IN_PROGRESS,itIsDescr,2023-06-05T02:45:00Z,15,2023-06-05T03:00:00Z,\n" +
                    "\n" +
                    "0", content, "Запись в файл некорректная");
        } catch (IOException e) {
            assertTrue(false, "Файл недоступен");
        }
    }

    @Test
    void updateSubtask() {
        super.updateSubtask(manager);
        try {
            String content = Files.readString(path);
            assertEquals("id,type,name,status,description,startTime,duration,endTime,epic\n" +
                    "0,EPIC,itIsEpic2,NEW,itIsEpicDescr2," + manager.getEpicById(0).getStartTime() + ",15,"
                    + manager.getEpicById(0).getEndTime() + ",\n" +
                    "1,SUBTASK,itIsNewName,NEW,sub descr2,2023-06-05T02:45:00Z,15,2023-06-05T03:00:00Z,0\n" +
                    "2,SUBTASK,subname3,NEW,sub descr3," + manager.getSubtaskById(2).getStartTime() + ",0," +
                    manager.getSubtaskById(2).getEndTime() + ",0\n" +
                    "\n" +
                    "1", content, "Запись в файл некорректная");
        } catch (IOException e) {
            assertTrue(false, "Файл недоступен");
        }
    }

    @Test
    void updateEpic() {
        super.updateEpic(manager);
        try {
            String content = Files.readString(path);
            assertEquals("id,type,name,status,description,startTime,duration,endTime,epic\n" +
                    "0,EPIC,itIsNewName,NEW,itIsDescr," + manager.getEpicById(0).getStartTime() + "," +
                    manager.getEpicById(0).getDuration() + "," + manager.getEpicById(0).getEndTime() + ",\n" +
                    "\n" +
                    "0", content, "Запись в файл некорректная");
        } catch (IOException e) {
            assertTrue(false, "Файл недоступен");
        }
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