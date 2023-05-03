package test;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskState;
import service.TaskManager;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

abstract class TaskManagerTest<T extends TaskManager> {

    void createTask(T manager) {
        Task task = new Task("itIsName", "itIsDescr", TaskState.IN_PROGRESS,
                Instant.parse("2023-06-05T02:00:00Z"), 45);
        manager.createTask(task);

        final Integer savedId = task.getTaskId();
        final Task savedTask = manager.getTaskById(savedId);

        assertNotNull(savedTask, "Задача не сохранилась");
        assertEquals(task, savedTask, "Задачи не совпадают");
    }


    void createEpic(T manager) {
        Epic epic = new Epic("itIsEpic", "itIsEpicDescr");
        manager.createEpic(epic);

        final Integer savedId = epic.getTaskId();
        final Task savedEpic = manager.getEpicById(savedId);

        assertNotNull(savedEpic, "Задача не сохранилась");
        assertEquals(epic, savedEpic, "Задачи не совпадают");
    }


    void createSubtask(T manager) {
        Epic epic = new Epic("itIsEpic", "itIsEpicDescr");
        manager.createEpic(epic);
        Subtask subtask = new Subtask("subname", "sub descr", TaskState.NEW, epic,
                Instant.parse("2023-06-05T02:00:00Z"), 45);
        manager.createSubtask(subtask);

        final Integer savedId = subtask.getTaskId();
        final Subtask savedSubtask = manager.getSubtaskById(savedId);

        assertNotNull(savedSubtask, "Задача не сохранилась");
        assertEquals(subtask, savedSubtask, "Задачи не совпадают");
        assertTrue(epic.getSubTasksList().contains(subtask), "Саб не сохранился в списке сабов у эпика");
        assertEquals(epic.getTaskId(), subtask.getBelongsToEpicId(), "Айди эпиков различаются");
    }


    void getTasksList(T manager) {
        List<Task> testTasks = List.of(new Task("itIsName", "itIsDescr", TaskState.IN_PROGRESS),
                new Task("itIsName2", "itIsDescr2", TaskState.DONE),
                new Task("itIsName3", "itIsDescr3", TaskState.NEW));
        for (Task task : testTasks) {
            manager.createTask(task);
        }
        manager.createEpic(new Epic("itIsEpic", "itIsEpicDescr"));
        assertEquals(new ArrayList<>(testTasks), manager.getTasksList(), "Список созданных задач " +
                "некорректный");
    }


    void getEpicsList(T manager) {
        List<Epic> testEpics = List.of(new Epic("itIsEpic", "itIsEpicDescr"),
                new Epic("itIsEpic2", "itIsEpicDescr2"),
                new Epic("itIsEpic3", "itIsEpicDescr3"));
        for (Epic epic : testEpics) {
            manager.createEpic(epic);
        }
        manager.createTask(new Task("itIsName3", "itIsDescr3", TaskState.NEW));
        assertEquals(new ArrayList<>(testEpics), manager.getEpicsList(), "Список созданных задач " +
                "некорректный");
    }

    void getSubtasksList(T manager) {
        Epic firstEpic = new Epic("itIsEpic", "itIsEpicDescr");
        manager.createEpic(firstEpic);
        Subtask subtask = new Subtask("subname", "sub descr", TaskState.NEW, firstEpic);
        manager.createSubtask(subtask);

        Epic secondEpic = new Epic("itIsEpic2", "itIsEpicDescr2");
        manager.createEpic(secondEpic);
        Subtask subtask2 = new Subtask("subname2", "sub descr2", TaskState.NEW, secondEpic);
        Subtask subtask3 = new Subtask("subname3", "sub descr3", TaskState.NEW, secondEpic);
        manager.createSubtask(subtask2);
        manager.createSubtask(subtask3);

        List<Subtask> subtaskList = List.of(subtask, subtask2, subtask3);
        assertEquals(new ArrayList<>(subtaskList), manager.getSubtasksList(), "Список созданных задач " +
                "некорректный");
    }

    void deleteAllTasks(T manager) {
        List<Task> testTasks = List.of(new Task("itIsName", "itIsDescr", TaskState.IN_PROGRESS),
                new Task("itIsName2", "itIsDescr2", TaskState.DONE),
                new Task("itIsName3", "itIsDescr3", TaskState.NEW));

        for (Task task : testTasks) {
            manager.createTask(task);
        }

        manager.deleteAllTasks();
        assertEquals(manager.getTasksList().size(), 0, "Список не пуст");
    }

    void deleteAllSubTasks(T manager) {
        Epic firstEpic = new Epic("itIsEpic", "itIsEpicDescr");
        manager.createEpic(firstEpic);
        Subtask subtask = new Subtask("subname", "sub descr", TaskState.NEW, firstEpic,
                Instant.parse("2023-06-05T02:00:00Z"), 15);
        manager.createSubtask(subtask);

        Epic secondEpic = new Epic("itIsEpic2", "itIsEpicDescr2");
        manager.createEpic(secondEpic);
        Subtask subtask2 = new Subtask("subname2", "sub descr2", TaskState.NEW, secondEpic,
                Instant.parse("2023-06-05T02:15:00Z"), 15);
        Subtask subtask3 = new Subtask("subname3", "sub descr3", TaskState.NEW, secondEpic,
                Instant.parse("2023-06-05T02:30:00Z"), 15);
        manager.createSubtask(subtask2);
        manager.createSubtask(subtask3);

        assertEquals(manager.getSubtasksList().size(), 3, "Список уже пуст");
        manager.deleteAllSubTasks();
        assertEquals(manager.getSubtasksList().size(), 0, "Список не пуст");
    }

    void deleteAllEpics(T manager) {
        Epic firstEpic = new Epic("itIsEpic", "itIsEpicDescr");
        manager.createEpic(firstEpic);
        Subtask subtask = new Subtask("subname", "sub descr", TaskState.NEW, firstEpic);
        manager.createSubtask(subtask);

        Epic secondEpic = new Epic("itIsEpic2", "itIsEpicDescr2");
        manager.createEpic(secondEpic);
        Subtask subtask2 = new Subtask("subname2", "sub descr2", TaskState.NEW, secondEpic);
        Subtask subtask3 = new Subtask("subname3", "sub descr3", TaskState.NEW, secondEpic);
        manager.createSubtask(subtask2);
        manager.createSubtask(subtask3);

        assertEquals(manager.getSubtasksList().size(), 3, "Список уже пуст");
        assertEquals(manager.getEpicsList().size(), 2, "Список уже пуст");
        manager.deleteAllEpics();
        assertEquals(manager.getSubtasksList().size(), 0, "Список субтасков не пуст");
        assertEquals(manager.getEpicsList().size(), 0, "Список субтасков не пуст");
    }

    void getTaskById(T manager) {
        Task task = new Task("itIsName", "itIsDescr", TaskState.IN_PROGRESS);

        manager.createTask(task);
        assertEquals(manager.getTaskById(0), task, "Задачи не совпадают");
        assertNull(manager.getTaskById(100500), "Возвращен не null");
    }

    void getEpicById(T manager) {
        Epic epic = new Epic("itIsEpic", "itIsEpicDescr");

        manager.createEpic(epic);
        assertEquals(manager.getEpicById(0), epic, "Задачи не совпадают");
        assertNull(manager.getEpicById(100500), "Возвращен не null");
    }

    void getSubtaskById(T manager) {
        Epic firstEpic = new Epic("itIsEpic", "itIsEpicDescr");
        manager.createEpic(firstEpic);
        Subtask subtask = new Subtask("subname", "sub descr", TaskState.NEW, firstEpic,
                Instant.parse("2023-06-05T02:15:00Z"), 15);
        manager.createSubtask(subtask);

        assertEquals(manager.getSubtaskById(1), subtask, "Задачи не совпадают");
        assertNull(manager.getSubtaskById(100500), "Возвращен не null");
    }

    void removeTaskById(T manager) {
        Task task = new Task("itIsName", "itIsDescr", TaskState.IN_PROGRESS);

        manager.createTask(task);
        assertEquals(manager.getTaskById(task.getTaskId()), task, "Задачи не совпадают");
        manager.removeTaskById(task.getTaskId());
        assertFalse(manager.getTasksList().contains(task), "Задача не удалилась");
    }

    void removeEpicById(T manager) {
        Epic firstEpic = new Epic("itIsEpic", "itIsEpicDescr");

        manager.createEpic(firstEpic);

        Subtask subtask = new Subtask("subname", "sub descr", TaskState.NEW, firstEpic,
                Instant.parse("2023-06-05T02:15:00Z"), 15);

        manager.createSubtask(subtask);

        Epic secondEpic = new Epic("itIsEpic2", "itIsEpicDescr2");

        manager.createEpic(secondEpic);

        Subtask subtask2 = new Subtask("subname2", "sub descr2", TaskState.NEW, secondEpic,
                Instant.parse("2023-06-05T02:30:00Z"), 15);
        Subtask subtask3 = new Subtask("subname3", "sub descr3", TaskState.NEW, secondEpic,
                Instant.parse("2023-06-05T02:45:00Z"), 15);

        manager.createSubtask(subtask2);
        manager.createSubtask(subtask3);

        Integer secondEpicId = secondEpic.getTaskId();
        List<Subtask> subtaskList = secondEpic.getSubTasksList();

        manager.removeEpicById(secondEpicId);
        assertFalse(manager.getEpicsList().contains(secondEpic), "Эпик не удалился");
        for (Subtask task : subtaskList) {
            assertFalse(manager.getSubtasksList().contains(task));
        }
    }

    void removeSubtaskById(T manager) {
        Epic epic = new Epic("itIsEpic2", "itIsEpicDescr2");

        manager.createEpic(epic);

        Subtask subtask2 = new Subtask("subname2", "sub descr2", TaskState.NEW, epic);
        Subtask subtask3 = new Subtask("subname3", "sub descr3", TaskState.NEW, epic);

        manager.createSubtask(subtask2);
        manager.createSubtask(subtask3);
        manager.removeSubtaskById(subtask3.getTaskId());

        assertFalse(epic.getSubTasksList().contains(subtask3), "Саб не удалился из списка сабов эпика");
        assertFalse(manager.getSubtasksList().contains(subtask3), "Саб не удалился из списка сабов");
    }

    void updateTask(T manager) {
        Task task = new Task("itIsName", "itIsDescr", TaskState.IN_PROGRESS);

        manager.createTask(task);
        task.setTaskName("itIsNewName");
        task.setStartTime(Instant.parse("2023-06-05T02:45:00Z"));
        task.setDuration(15);
        manager.updateTask(task);

        assertEquals("itIsNewName", manager.getTaskById(task.getTaskId()).getTaskName());
        assertTrue(manager.getTasksList().contains(task));
    }

    void updateSubtask(T manager) {
        Epic epic = new Epic("itIsEpic2", "itIsEpicDescr2");

        manager.createEpic(epic);

        Subtask subtask2 = new Subtask("subname2", "sub descr2", TaskState.NEW, epic);
        Subtask subtask3 = new Subtask("subname3", "sub descr3", TaskState.NEW, epic);

        manager.createSubtask(subtask2);
        manager.createSubtask(subtask3);
        subtask2.setTaskName("itIsNewName");
        subtask2.setStartTime(Instant.parse("2023-06-05T02:45:00Z"));
        subtask2.setDuration(15);
        manager.updateSubtask(subtask2);

        assertEquals("itIsNewName", manager.getSubtaskById(subtask2.getTaskId()).getTaskName());
        assertTrue(manager.getSubtasksList().contains(subtask2));
    }

    void updateEpic(T manager) {
        Epic epic = new Epic("itIsName", "itIsDescr");

        manager.createEpic(epic);
        epic.setTaskName("itIsNewName");
        manager.updateEpic(epic);

        assertEquals("itIsNewName", manager.getEpicById(epic.getTaskId()).getTaskName());
        assertTrue(manager.getEpicsList().contains(epic));
    }

    void getHistoryList(T manager) {
        Task task = new Task("itIsName", "itIsDescr", TaskState.IN_PROGRESS);
        manager.createTask(task);
        Epic epic = new Epic("itIsName", "itIsDescr");
        manager.createEpic(epic);
        Subtask subtask = new Subtask("subname2", "sub descr2", TaskState.NEW, epic);
        manager.createSubtask(subtask);

        Integer epicId = epic.getTaskId();
        Integer subtaskId = subtask.getTaskId();

        manager.getEpicById(epicId);
        manager.getSubtaskById(subtaskId);

        assertEquals(List.of(subtask, epic), manager.getHistoryList(), "Истории не совпадают");
    }

    void calculateEpicState(T manager) {
        //случай - создание нового эпика, пустой список подзадач
        Epic newEpic = new Epic("itIsName", "itIsDescr");
        manager.createEpic(newEpic);
        shouldReturnNewWhenCalculateStatus(newEpic, manager);

        //стандартный случай - сабтаски всех типов
        Epic defaultEpic = new Epic("itIsName", "itIsDescr");
        manager.createEpic(defaultEpic);
        Subtask defaultSubtask1 = new Subtask("subname1", "sub descr1", TaskState.NEW, defaultEpic);
        manager.createSubtask(defaultSubtask1);
        Subtask defaultSubtask2 = new Subtask("subname2", "sub descr2", TaskState.IN_PROGRESS, defaultEpic);
        manager.createSubtask(defaultSubtask2);
        Subtask defaultSubtask3 = new Subtask("subname3", "sub descr3", TaskState.DONE, defaultEpic);
        manager.createSubtask(defaultSubtask3);
        shouldReturnInProgressWhenCalculateStaus(defaultEpic, manager);

        //случай - все подзадачи новые
        Epic fullSubNewEpic = new Epic("itIsName", "itIsDescr");
        manager.createEpic(fullSubNewEpic);
        Subtask newSubtask1 = new Subtask("subname1", "sub descr1", TaskState.NEW, fullSubNewEpic);
        Subtask newSubtask2 = new Subtask("subname1", "sub descr1", TaskState.NEW, fullSubNewEpic);
        manager.createSubtask(newSubtask1);
        manager.createSubtask(newSubtask2);
        shouldReturnNewWhenCalculateStatus(fullSubNewEpic, manager);

        //случай - все подзадачи сделанные
        Epic fullDoneEpic = new Epic("itIsName", "itIsDescr");
        manager.createEpic(fullDoneEpic);
        Subtask doneSubtask1 = new Subtask("subname1", "sub descr1",
                TaskState.DONE, fullDoneEpic);
        Subtask doneSubtask2 = new Subtask("subname1", "sub descr1",
                TaskState.DONE, fullDoneEpic);
        manager.createSubtask(doneSubtask1);
        manager.createSubtask(doneSubtask2);
        shouldReturnDoneWhenCalculateStaus(fullDoneEpic, manager);

        //случай - подзадачи со статусами NEW и DONE only
        Epic newAndDoneOnlyEpic = new Epic("itIsName", "itIsDescr");
        manager.createEpic(newAndDoneOnlyEpic);
        Subtask newSubtask = new Subtask("subname1", "sub descr1",
                TaskState.NEW, newAndDoneOnlyEpic);
        Subtask doneSubtask = new Subtask("subname1", "sub descr1",
                TaskState.DONE, newAndDoneOnlyEpic);
        manager.createSubtask(newSubtask);
        manager.createSubtask(doneSubtask);
        shouldReturnInProgressWhenCalculateStaus(newAndDoneOnlyEpic, manager);

        //случай - подзадачи со статусами IN_PROGRESS only
        Epic inProgressOnlyEpic = new Epic("itIsName", "itIsDescr");
        manager.createEpic(inProgressOnlyEpic);
        Subtask inProgressSub1 = new Subtask("subname1", "sub descr1",
                TaskState.IN_PROGRESS, inProgressOnlyEpic);
        Subtask inProgressSub2 = new Subtask("subname1", "sub descr1",
                TaskState.IN_PROGRESS, inProgressOnlyEpic);
        manager.createSubtask(inProgressSub1);
        manager.createSubtask(inProgressSub2);
        shouldReturnInProgressWhenCalculateStaus(inProgressOnlyEpic, manager);
    }

    void shouldReturnNewWhenCalculateStatus(Epic epic, T manager) {
        TaskState state = manager.calculateEpicState(epic);
        assertEquals(TaskState.NEW, state, "Статус должен быть равен NEW");
    }

    void shouldReturnInProgressWhenCalculateStaus(Epic epic, T manager) {
        TaskState state = manager.calculateEpicState(epic);
        assertEquals(TaskState.IN_PROGRESS, state, "Статус должен быть равен IN_PROGRESS");
    }

    void shouldReturnDoneWhenCalculateStaus(Epic epic, T manager) {
        TaskState state = manager.calculateEpicState(epic);
        assertEquals(TaskState.DONE, state, "Статус должен быть равен DONE");
    }

    void calculateEpicStartTime(T manager) {
        Epic epic = new Epic("epic name", "epic descr");

        manager.createEpic(epic);

        Subtask subtask1 = new Subtask("subname1",
                "sub descr1",
                TaskState.NEW,
                epic,
                Instant.parse("2023-06-05T02:45:00Z"),
                45);
        Subtask subtask2 = new Subtask("subname1",
                "sub descr1",
                TaskState.NEW,
                epic,
                Instant.parse("2023-06-05T03:00:00Z"),
                110);

        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
        assertEquals(Instant.parse("2023-06-05T02:45:00Z"), manager.calculateEpicStartTime(epic));
    }

    void calculateEpicDuration(T manager) {
        Epic epic = new Epic("epic name", "epic descr");

        manager.createEpic(epic);

        Subtask subtask1 = new Subtask("subname1",
                "sub descr1",
                TaskState.NEW,
                epic,
                Instant.now().plusSeconds(60 * 10),
                45);
        Subtask subtask2 = new Subtask("subname1",
                "sub descr1",
                TaskState.NEW,
                epic,
                Instant.now().plusSeconds(60 * 60),
                110);

        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
        assertEquals(45 + 110, manager.calculateEpicDuration(epic));
    }

    void getPrioritizedTasks(T manager) {
        Task task1 = new Task("name1", "descr1", TaskState.NEW);
        manager.createTask(task1);
        Task task2 = new Task("name2", "descr2", TaskState.IN_PROGRESS,
                    Instant.parse("2023-06-05T02:45:00Z"), 15);
        manager.createTask(task2);
        Task task3 = new Task("name3", "descr3", TaskState.IN_PROGRESS,
                Instant.parse("2023-06-05T03:00:00Z"), 15);
        manager.createTask(task3);
        assertEquals(new ArrayList<Task>(List.of(task2, task3, task1)), manager.getPrioritizedTasks(), "Неверный приоритет");
    }

    void shouldReturnFalseWhenValidateTime(T manager) {
        Task task = manager.createTask(new Task("name", "descr", TaskState.IN_PROGRESS,
                Instant.parse("2023-06-05T02:41:00Z"), 15));
        assertNull(task);
        Task task2 = manager.createTask(new Task("name", "descr", TaskState.IN_PROGRESS,
                Instant.parse("2023-06-05T02:41:00Z"), 14));
        assertNull(task2);
    }
}