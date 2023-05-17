package test;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskState;
import org.junit.jupiter.api.DisplayName;
import service.TaskManager;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

abstract class TaskManagerTest<T extends TaskManager> {
    T manager;

    @Test
    @DisplayName("Создание задачи")
    void addNewTask() {
        Task task = new Task("itIsName", "itIsDescr", TaskState.IN_PROGRESS,
                Instant.parse("2023-06-05T02:00:00Z"), 45);

        manager.createTask(task);

        final Integer savedId = task.getTaskId();
        final Task savedTask = manager.getTaskById(savedId);

        assertNotNull(savedTask, "Задача не сохранилась");
        assertEquals(task, savedTask, "Задачи не совпадают");

        final List<Task> tasksList = manager.getTasksList();

        assertEquals(tasksList.size(), 1, "Число задач в списке задач некорректное");
        assertNotNull(tasksList, "Список задач = null");
        assertTrue(tasksList.contains(task), "Задача не добавилась");
        assertEquals(task, tasksList.get(0), "Задачи совпадает с той, что в списке");
        assertNotNull(manager.getPrioritizedTasks(), "Приоритетные задачи не вернулись");
        assertTrue( (manager.getPrioritizedTasks().contains(savedTask)) &&
                (manager.getPrioritizedTasks().size() == 1), "Не сохранилось в приоритетные задачи" );

        Task badStartTimeTask = new Task("itIsName", "itIsDescr", TaskState.IN_PROGRESS,
                Instant.parse("2023-06-05T02:00:00Z"), 45);

        assertNull(manager.createTask(badStartTimeTask), "Создалось 2 задачи с одинаковым временем");
    }


    @Test
    @DisplayName("Создание эпика")
    void addNewEpic() {
        Epic epic = new Epic("itIsEpic", "itIsEpicDescr");
        manager.createEpic(epic);

        final Integer savedId = epic.getTaskId();
        final Task savedEpic = manager.getEpicById(savedId);

        assertNotNull(savedEpic, "Эпик не сохранился");
        assertEquals(epic, savedEpic, "Эпики не совпадают");

        final List<Epic> epicList = manager.getEpicsList();

        assertEquals(epicList.size(), 1, "Число эпиков в списке эпиков некорректное");
        assertNotNull(epicList, "Список эпиков = null");
        assertTrue(epicList.contains(epic), "Эпик не добавился");
        assertEquals(epic, epicList.get(0), "Эпик не совпадает с тем, что в списке");
    }

    @Test
    @DisplayName("Создание сабтаска")
    void addNewSubtask() {
        Epic epic = new Epic("itIsEpic", "itIsEpicDescr");

        manager.createEpic(epic);

        Subtask subtask = new Subtask("subname", "sub descr", TaskState.NEW, epic,
                Instant.parse("2023-06-05T02:00:00Z"), 45);

        manager.createSubtask(subtask);

        final Integer savedId = subtask.getTaskId();
        final Subtask savedSubtask = manager.getSubtaskById(savedId);

        //для сабтаска
        assertNotNull(savedSubtask, "Задача не сохранилась");
        assertEquals(subtask, savedSubtask, "Задачи не совпадают");

        final List<Subtask> subtaskList = manager.getSubtasksList();

        assertEquals(subtaskList.size(), 1, "Число сабтасков в списке задач некорректное");
        assertNotNull(subtaskList, "Список сабтасков = null");
        assertTrue(subtaskList.contains(subtask), "Сабтаск не добавился");
        assertEquals(subtask, subtaskList.get(0), "Сабтаск не совпадает с той, что в списке");
        assertNotNull(manager.getPrioritizedTasks(), "Приоритетные задачи не вернулись");
        //для эпика, к которому сабтаск относится
        assertTrue(epic.getSubTasksList().contains(subtask), "Саб не сохранился в списке сабов у эпика");
        assertEquals(epic.getTaskId(), subtask.getEpicBelongsId(), "Айди эпиков различаются");
    }


    @Test
    @DisplayName("Получение списка тасков - стадартный случай")
    void getTasksList() {
        List<Task> testTasks = List.of(new Task("itIsName", "itIsDescr", TaskState.IN_PROGRESS),
                new Task("itIsName2", "itIsDescr2", TaskState.DONE),
                new Task("itIsName3", "itIsDescr3", TaskState.NEW));

        for (Task task : testTasks) {
            manager.createTask(task);
        }
        Epic epic = manager.createEpic(new Epic("epic name", "epic descr"));
        manager.createSubtask(new Subtask("subname", "subdescr", TaskState.NEW, epic));

        List<Task> tasksList = manager.getTasksList();

        assertNotNull(tasksList, "Возвращается пустой список");
        assertEquals(tasksList.size(), 3, "Число задач неверное");
        assertEquals(new ArrayList<>(testTasks), tasksList, "Список созданных задач " +
                "некорректный");
    }

    @Test
    @DisplayName("Получение списка тасков - случай пустого списка задач")
    void getEmptyTasksList() {
        Epic epic = manager.createEpic(new Epic("epic", "descr"));
        manager.createSubtask(new Subtask("sub", "descr", TaskState.NEW, epic));

        List<Task> tasksList = manager.getTasksList();

        assertNotNull(tasksList, "Не возвращается список");
        assertTrue(tasksList.isEmpty(), "Список не пуст");
    }

    @Test
    @DisplayName("Получение списка эпиков - стандартный случай")
    void getEpicsList() {
        List<Epic> testEpics = List.of(new Epic("itIsEpic", "itIsEpicDescr"),
                new Epic("itIsEpic2", "itIsEpicDescr2"),
                new Epic("itIsEpic3", "itIsEpicDescr3"));

        for (Epic epic : testEpics) {
            manager.createEpic(epic);
        }

        manager.createTask(new Task("itIsName3", "itIsDescr3", TaskState.NEW));

        List<Epic> epicsList = manager.getEpicsList();

        assertNotNull(epicsList, "Возвращается пустой список");
        assertEquals(epicsList.size(), 3, "Число эпиков неверное");
        assertEquals(new ArrayList<>(testEpics), epicsList, "Список созданных эпиков некорректный");
    }

    @Test
    @DisplayName("Получение списка эпиков - случай пустого списка")
    void getEmptyEpicsList() {
        manager.createTask(new Task("itIsName3", "itIsDescr3", TaskState.NEW));

        List<Epic> epicList = manager.getEpicsList();

        assertNotNull(epicList, "Не возвращается список");
        assertTrue(epicList.isEmpty(), "Список не пуст");
    }

    @Test
    @DisplayName("Получение списка эпиков - стандартный случай")
    void getSubtasksList() {
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

        manager.createTask(new Task ("task", "descr", TaskState.NEW));

        List<Subtask> subtaskList = List.of(subtask, subtask2, subtask3);

        assertNotNull(subtaskList, "Список не вернулся");
        assertEquals(subtaskList.size(), 3, "Размер списка неверный");
        assertEquals(new ArrayList<>(subtaskList), subtaskList, "Список созданных задач " +
                "некорректный");
    }

    @Test
    @DisplayName("Удаление всех задач - стандартный случай")
    void deleteAllTasks() {
        List<Task> testTasks = List.of(new Task("itIsName", "itIsDescr", TaskState.IN_PROGRESS),
                new Task("itIsName2", "itIsDescr2", TaskState.DONE),
                new Task("itIsName3", "itIsDescr3", TaskState.NEW));

        for (Task task : testTasks) {
            manager.createTask(task);
        }
        assertNotNull(manager.getTasksList());
        assertFalse(manager.getTasksList().isEmpty(), "Список уже пуст");
        manager.deleteAllTasks();
        assertTrue(manager.getTasksList().isEmpty(), "Список не пуст");
    }

    @Test
    @DisplayName("Удаление всех задач - случай пустого списка")
    void deleteAllTasksWhenTaskListIsEmpty() {
        assertNotNull(manager.getTasksList());
        assertTrue(manager.getTasksList().isEmpty(), "Список уже не пуст");
        manager.deleteAllTasks();
        assertTrue(manager.getTasksList().isEmpty(), "Список не пуст");
    }

    @Test
    @DisplayName("Удаление всех сабтасков - стандартный случай")
    void deleteAllSubTasks() {
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
        assertNotNull(manager.getSubtasksList(), "Список не вернулся");
        assertEquals(manager.getSubtasksList().size(), 0, "Список не пуст");
    }

    @Test
    @DisplayName("Удаление всех сабтасков - случай сабтаск лист пуст")
    void deleteAllSubtaskWhenSublistIsEmpty() {
        assertNotNull(manager.getSubtasksList());
        assertTrue(manager.getSubtasksList().isEmpty(), "Список уже не пуст");
        manager.deleteAllSubTasks();
        assertTrue(manager.getSubtasksList().isEmpty(), "Список не пуст");
    }

    @Test
    @DisplayName("Удаление всех эпиков - стадартный случай")
    void deleteAllEpics() {
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
        assertNotNull(manager.getSubtasksList());
        assertNotNull(manager.getEpicsList());
        assertEquals(manager.getSubtasksList().size(), 0, "Список субтасков не пуст");
        assertEquals(manager.getEpicsList().size(), 0, "Список субтасков не пуст");
    }

    @Test
    @DisplayName("Получить задачу - стадартный случай")
    void getTaskById() {
        Task task = new Task("itIsName", "itIsDescr", TaskState.IN_PROGRESS);

        manager.createTask(task);

        Task savedTask = manager.getTaskById(0);

        assertNotNull(task);
        assertEquals(savedTask, task, "Задачи не совпадают");
        assertTrue(manager.getHistoryList().contains(savedTask), "Задача не попала в историю");
    }

    @Test
    @DisplayName("Получить задачу - случай неверного id")
    void shouldReturnNullWhenGetTaskByBadId() {
        Task task = new Task("itIsName", "itIsDescr", TaskState.IN_PROGRESS);

        manager.createTask(task);

        Task savedTask = manager.getTaskById(10500);
        assertNull(savedTask, "Вернулся не null");

        List<Task> history = manager.getHistoryList();

        assertFalse(history.contains(savedTask), "Задача оказалась в истории");
    }

    @Test
    @DisplayName("Получить эпик - стандартный случай")
    void getEpicById() {
        Epic epic = new Epic("itIsEpic", "itIsEpicDescr");
        Epic savedEpic = manager.createEpic(epic);

        assertNotNull(manager.getEpicById(0), "Вернулся null");
        assertEquals(manager.getEpicById(0), savedEpic, "Задачи не совпадают");
        assertTrue(manager.getHistoryList().contains(savedEpic), "Задача не попала в историю");
    }

    @Test
    @DisplayName("Получить эпик - случай неверного id")
    void shouldReturnNullWhenGetEpicByBadId() {
        Epic epic = new Epic("itIsEpic", "itIsEpicDescr");

        manager.createEpic(epic);

        Epic savedEpic = manager.getEpicById(10500);

        assertNull(savedEpic, "Вернулся не null");
        List<Task> history = manager.getHistoryList();

        assertFalse(history.contains(savedEpic), "Задача оказалась в истории");
    }

    @Test
    @DisplayName("Получить сабтаск по айди - стандартный случай")
    void getSubtaskById() {
        Epic firstEpic = new Epic("itIsEpic", "itIsEpicDescr");
        manager.createEpic(firstEpic);
        Subtask subtask = new Subtask("subname", "sub descr", TaskState.NEW, firstEpic,
                Instant.parse("2023-06-05T02:15:00Z"), 15);
        manager.createSubtask(subtask);

        Subtask savedSubtask = manager.getSubtaskById(1);

        assertNotNull(savedSubtask, "Вернулся null");
        assertTrue(manager.getHistoryList().contains(savedSubtask), "Задача не попала в историю");
        assertEquals(savedSubtask, subtask, "Задачи не совпадают");
    }

    @Test
    @DisplayName("Получить сабтаск по айди - случай неверного айди")
    void shouldReturnNullWhenGetSubtaskByBadId() {
        Epic firstEpic = new Epic("itIsEpic", "itIsEpicDescr");
        manager.createEpic(firstEpic);
        Subtask subtask = new Subtask("subname", "sub descr", TaskState.NEW, firstEpic,
                Instant.parse("2023-06-05T02:15:00Z"), 15);
        manager.createSubtask(subtask);

        Subtask savedSubtask = manager.getSubtaskById(100500);
        assertNull(savedSubtask, "Вернулся null");

        List<Task> history = manager.getHistoryList();

        assertFalse(history.contains(savedSubtask), "Задача оказалась в истории");
    }

    @Test
    @DisplayName("Удалить задачу по айди - стандартный случай")
    void removeTaskById() {
        Task task = new Task("itIsName", "itIsDescr", TaskState.IN_PROGRESS);

        manager.createTask(task);

        Task savedTask = manager.getTaskById(0);

        manager.removeTaskById(task.getTaskId());

        assertFalse(manager.getTasksList().contains(savedTask), "Задача не удалилась");
        assertFalse(manager.getPrioritizedTasks().contains(savedTask), "Задача не удалилась из приоритетов");
        assertFalse(manager.getHistoryList().contains(savedTask), "Задача не удалилась из истории");
    }

    @Test
    @DisplayName("Удалить задачу по айди - случай неверного айди")
    void shouldDoNothingWhenRemoveTaskByBadId() {
        Task task = new Task("itIsName", "itIsDescr", TaskState.IN_PROGRESS);

        manager.createTask(task);

        List<Task> savedTasksList = manager.getTasksList();
        List<Task> savedPrioritizedTasks = manager.getPrioritizedTasks();
        List<Task> savedHistory = manager.getHistoryList();

        manager.removeTaskById(1000500);
        assertEquals(savedHistory, manager.getHistoryList());
        assertEquals(savedPrioritizedTasks, manager.getPrioritizedTasks());
        assertEquals(savedTasksList, manager.getTasksList());
    }

    @Test
    @DisplayName("Удалить эпик по айди - стандартный случай")
    void removeEpicById() {
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
        List<Subtask> savedSubtaskList = secondEpic.getSubTasksList();

        manager.removeEpicById(secondEpicId);
        assertFalse(manager.getEpicsList().contains(secondEpic), "Эпик не удалился");
        assertFalse(manager.getHistoryList().contains(secondEpic));
        for (Subtask task : savedSubtaskList) {
            assertFalse(manager.getSubtasksList().contains(task));
            assertFalse(manager.getPrioritizedTasks().contains(task));
            assertFalse(manager.getHistoryList().contains(task));
        }
    }

    @Test
    @DisplayName("Удалить эпик по айди - случай неверного айди")
    void shouldDoNothingWhenRemoveEpicByBadId() {
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

        List<Subtask> savedSubtaskList = manager.getSubtasksList();
        List<Epic> savedEpicList = manager.getEpicsList();
        List<Task> history = manager.getHistoryList();
        List<Task> prioritizedTasks = manager.getPrioritizedTasks();

        manager.removeEpicById(5555555);

        assertEquals(savedEpicList, manager.getEpicsList());
        assertEquals(savedSubtaskList, manager.getSubtasksList());
        assertEquals(history, manager.getHistoryList());
        assertEquals(prioritizedTasks, manager.getPrioritizedTasks());
    }


    @Test
    @DisplayName("Удалить сабтаск по айди - стандартный случай")
    void removeSubtaskById() {
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

    @Test
    @DisplayName("Удалить сабтаск по айди - случай неверного айди")
    void shouldDoNothingWhenRemoveSubtaskByBadId() {
        Epic epic = new Epic("itIsEpic2", "itIsEpicDescr2");

        manager.createEpic(epic);

        Subtask subtask2 = new Subtask("subname2", "sub descr2", TaskState.NEW, epic);
        Subtask subtask3 = new Subtask("subname3", "sub descr3", TaskState.NEW, epic);

        manager.createSubtask(subtask2);
        manager.createSubtask(subtask3);

        List<Subtask> savedSubtaskList = manager.getSubtasksList();
        List<Epic> savedEpicList = manager.getEpicsList();
        List<Task> history = manager.getHistoryList();
        List<Task> prioritizedTasks = manager.getPrioritizedTasks();

        manager.removeSubtaskById(5555555);

        assertEquals(savedEpicList, manager.getEpicsList());
        assertEquals(savedSubtaskList, manager.getSubtasksList());
        assertEquals(history, manager.getHistoryList());
        assertEquals(prioritizedTasks, manager.getPrioritizedTasks());
    }

    @Test
    @DisplayName("Обновить таск - стандартный случай")
    void updateTask() {
        Task oldTask = new Task("itIsName", "itIsDescr", TaskState.IN_PROGRESS);

        manager.createTask(oldTask);
        oldTask.setTaskName("itIsNewName");
        oldTask.setStartTime(Instant.parse("2023-06-05T02:45:00Z"));
        oldTask.setDuration(15);

        List<Task> history = manager.getHistoryList();

        manager.updateTask(oldTask);

        assertTrue(manager.getPrioritizedTasks().contains(oldTask), "Задачи нет в приоритетах");
        assertEquals(history, manager.getHistoryList(), "История не должна меняться");
        assertTrue(manager.getTasksList().contains(oldTask), "Задачи нет в листе");
    }

    @Test
    @DisplayName("Обновить таск - случай неверного айди у таска")
    void shouldDoNothingWhenUpdateTaskWithBadId() {
        Task oldTask = new Task("itIsName", "itIsDescr", TaskState.IN_PROGRESS);

        manager.createTask(oldTask);

        Task task = new Task("itIsName2", "itIsDescr2", TaskState.IN_PROGRESS);

        task.setTaskId(100500);
        manager.updateTask(task);
        assertFalse(manager.getTasksList().contains(task));
    }

    @Test
    @DisplayName("Обновить сабтаск - стандартный случай")
    void updateSubtask() {
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

        assertTrue(manager.getSubtasksList().contains(subtask2));
        assertTrue(manager.getPrioritizedTasks().contains(subtask2));
    }

    @Test
    @DisplayName("Обновить сабтаск - случай неверного айди у сабтаска")
    void shouldDoNothingWhenUpdateSubtaskWithBadId() {
        Epic epic = new Epic("itIsEpic2", "itIsEpicDescr2");

        manager.createEpic(epic);

        Subtask subtask2 = new Subtask("subname2", "sub descr2", TaskState.NEW, epic);
        Subtask subtask3 = new Subtask("subname3", "sub descr3", TaskState.NEW, epic);

        manager.createSubtask(subtask2);
        manager.createSubtask(subtask3);
        subtask3.setDuration(500);
        subtask3.setTaskName("qq");
        subtask3.setTaskId(100500);

        List<Subtask> savedSubList = manager.getSubtasksList();
        List<Task> history = manager.getHistoryList();
        List<Task> prioritizedTasks = manager.getPrioritizedTasks();

        manager.updateTask(subtask3);
        assertEquals(savedSubList, manager.getSubtasksList());
        assertEquals(history, manager.getHistoryList());
        assertEquals(prioritizedTasks, manager.getPrioritizedTasks());
    }

    @Test
    @DisplayName("Обновить эпик - стандартный случай")
    void updateEpic() {
        Epic epic = new Epic("itIsName", "itIsDescr");

        manager.createEpic(epic);
        epic.setTaskName("itIsNewName");
        manager.updateEpic(epic);

        assertTrue(manager.getEpicsList().contains(epic));
    }

    @Test
    @DisplayName("Получить историю - стандартный случай")
    void getHistoryList() {
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

    @Test
    @DisplayName("Получить историю - случай пустой истории")
    void shouldReturnEmptyHistory() {
        Task task = new Task("itIsName", "itIsDescr", TaskState.IN_PROGRESS);

        manager.createTask(task);

        Epic epic = new Epic("itIsName", "itIsDescr");

        manager.createEpic(epic);

        Subtask subtask = new Subtask("subname2", "sub descr2", TaskState.NEW, epic);

        manager.createSubtask(subtask);
        assertTrue(manager.getHistoryList().isEmpty(), "История не пустая");
    }

    @Test
    @DisplayName("Расчет статсуса эпика - стандартный случай, сабтаски всех статусов")
    void calculateEpicState() {
        Epic defaultEpic = new Epic("itIsName", "itIsDescr");

        manager.createEpic(defaultEpic);

        Subtask defaultSubtask1 = new Subtask("subname1", "sub descr1", TaskState.NEW, defaultEpic);
        Subtask defaultSubtask2 = new Subtask("subname2", "sub descr2", TaskState.IN_PROGRESS, defaultEpic);
        Subtask defaultSubtask3 = new Subtask("subname3", "sub descr3", TaskState.DONE, defaultEpic);

        manager.createSubtask(defaultSubtask1);
        manager.createSubtask(defaultSubtask2);
        manager.createSubtask(defaultSubtask3);

        assertEquals(manager.calculateEpicState(defaultEpic), TaskState.IN_PROGRESS);
    }

    @Test
    @DisplayName("Расчет статсуса эпика - случай пустого списка подзадач")
    void calculateEpicStateWhenNoSubstasks() {
        Epic epicWithoutSubtasks = new Epic("itIsName", "itIsDescr");

        manager.createEpic(epicWithoutSubtasks);
        assertEquals(manager.calculateEpicState(epicWithoutSubtasks), TaskState.NEW);
    }

    @Test
    @DisplayName("Расчет статсуса эпика - случай все подзадачи NEW")
    void calculateEpicStateWhenAllSubstasksAreNew() {
        Epic epicOnlyNewSubtasks = new Epic("itIsName", "itIsDescr");

        manager.createEpic(epicOnlyNewSubtasks);

        Subtask sub1 = new Subtask("subname1", "sub descr1",
                TaskState.NEW, epicOnlyNewSubtasks);
        Subtask sub2 = new Subtask("subname2", "sub descr2",
                TaskState.NEW, epicOnlyNewSubtasks);
        Subtask sub3 = new Subtask("subname3", "sub descr3",
                TaskState.NEW, epicOnlyNewSubtasks);

        manager.createSubtask(sub1);
        manager.createSubtask(sub2);
        manager.createSubtask(sub3);

        assertEquals(manager.calculateEpicState(epicOnlyNewSubtasks), TaskState.NEW);
    }

    @Test
    @DisplayName("Расчет статсуса эпика - случай все подзадачи DONE")
    void calculateEpicStateWhenAllSubstasksAreDone() {
        Epic epicOnlyDoneSubtasks = new Epic("itIsName", "itIsDescr");

        manager.createEpic(epicOnlyDoneSubtasks);

        Subtask sub1 = new Subtask("subname1", "sub descr1",
                TaskState.DONE, epicOnlyDoneSubtasks);
        Subtask sub2 = new Subtask("subname2", "sub descr2",
                TaskState.DONE, epicOnlyDoneSubtasks);
        Subtask sub3 = new Subtask("subname3", "sub descr3",
                TaskState.DONE, epicOnlyDoneSubtasks);

        manager.createSubtask(sub1);
        manager.createSubtask(sub2);
        manager.createSubtask(sub3);

        assertEquals(manager.calculateEpicState(epicOnlyDoneSubtasks), TaskState.DONE);
    }

    @Test
    @DisplayName("Расчет статсуса эпика - случай все подзадачи DONE или NEW")
    void calculateEpicStateWhenAllSubstasksAreDoneOrNew() {
        Epic epicOnlyDoneAndNewSubtasks = new Epic("itIsName", "itIsDescr");

        manager.createEpic(epicOnlyDoneAndNewSubtasks);

        Subtask sub1 = new Subtask("subname1", "sub descr1",
                TaskState.DONE, epicOnlyDoneAndNewSubtasks);
        Subtask sub2 = new Subtask("subname2", "sub descr2",
                TaskState.DONE, epicOnlyDoneAndNewSubtasks);
        Subtask sub3 = new Subtask("subname3", "sub descr3",
                TaskState.NEW, epicOnlyDoneAndNewSubtasks);

        manager.createSubtask(sub1);
        manager.createSubtask(sub2);
        manager.createSubtask(sub3);

        assertEquals(manager.calculateEpicState(epicOnlyDoneAndNewSubtasks), TaskState.IN_PROGRESS);
    }

    @Test
    @DisplayName("Расчет статсуса эпика - случай все подзадачи IN_PROGRESS")
    void calculateEpicStateWhenAllSubstasksAreInProgress() {
        Epic epicOnlyInProgressSubtasks = new Epic("itIsName", "itIsDescr");

        manager.createEpic(epicOnlyInProgressSubtasks);

        Subtask sub1 = new Subtask("subname1", "sub descr1",
                TaskState.IN_PROGRESS, epicOnlyInProgressSubtasks);
        Subtask sub2 = new Subtask("subname2", "sub descr2",
                TaskState.IN_PROGRESS, epicOnlyInProgressSubtasks);
        Subtask sub3 = new Subtask("subname3", "sub descr3",
                TaskState.IN_PROGRESS, epicOnlyInProgressSubtasks);

        manager.createSubtask(sub1);
        manager.createSubtask(sub2);
        manager.createSubtask(sub3);

        assertEquals(manager.calculateEpicState(epicOnlyInProgressSubtasks), TaskState.IN_PROGRESS);
    }

    @Test
    @DisplayName("Расчет времени начала эпика")
    void calculateEpicStartTime() {
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

    @Test
    @DisplayName("Расчет продолжительности эпика")
    void calculateEpicDuration() {
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

    @Test
    @DisplayName("Получение приоритетного списка - стандартный случай")
    void getPrioritizedTasks() {
        Task task1 = new Task("name1", "descr1", TaskState.NEW);
        manager.createTask(task1);
        Task task2 = new Task("name2", "descr2", TaskState.IN_PROGRESS,
                    Instant.parse("2023-06-05T02:45:00Z"), 15);
        manager.createTask(task2);
        Task task3 = new Task("name3", "descr3", TaskState.IN_PROGRESS,
                Instant.parse("2023-06-05T03:00:00Z"), 15);
        manager.createTask(task3);
        assertEquals(new ArrayList<>(List.of(task2, task3, task1)), manager.getPrioritizedTasks(), "Неверный приоритет");
    }

    @Test
    @DisplayName("Получение приоритетного списка - случай пустого списка задач и подзадач")
    void getPrioritizedTasksWhenNoTasksAndNoSubtasks() {
        assertTrue(manager.getPrioritizedTasks().isEmpty());
    }

    @Test
    @DisplayName("Проверка времени задачи - задача не попадает в сетку")
    void shouldReturnNullWhenValidateTimeCauseBadTime() {
        Task task = manager.createTask(new Task("name", "descr", TaskState.IN_PROGRESS,
                Instant.parse("2023-06-05T02:41:00Z"), 15));
        assertNull(task, "Создается задача с недопустимым временем");
    }

    @Test
    @DisplayName("Проверка времени задачи - продолжительность задачи меньше допустимой")
    void shouldReturnNullWhenValidateTimeCauseBadDuration() {
        Task task = manager.createTask(new Task("name", "descr", TaskState.IN_PROGRESS,
                Instant.parse("2023-06-05T02:41:00Z"), 1));
        assertNull(task, "Создается задача с недопустимой продолжительностью");
    }
}