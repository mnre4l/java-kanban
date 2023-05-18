package service;

import model.*;
import java.time.Instant;
import java.time.ZoneId;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected final HashMap<Integer, Task> tasksList = new HashMap<>();
    protected final HashMap<Integer, Epic> epicsList = new HashMap<>();
    protected final HashMap<Integer, Subtask> subtasksList = new HashMap<>();
    protected final HashMap<Instant, Boolean> timeBookingTable = new HashMap<>();
    protected final TreeSet<Task> prioritizedTasks = new TreeSet<>((o1, o2) -> {
        if (o1.getTaskId().equals(o2.getTaskId())) {
            return 0;
        }
        if (!o1.isUserSetTime()) {
            return 1;
        }
        if (!o2.isUserSetTime()) {
            return -1;
        }
        return o1.getStartTime().compareTo(o2.getStartTime());
        });
    protected Integer taskId = 0;
    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    protected static final int MIN_TASK_DURATION_MINUTES = 15;

    public InMemoryTaskManager() {
        initBooking();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    @Override
    public List<Task> getHistoryList() {
        return historyManager.getHistory();
    }

    @Override
    public Task createTask(Task task) {
        if (!isValidateTaskTime(task)) {
            return null;
        }
        tasksList.put(taskId, task);
        task.setTaskId(taskId);
        prioritizedTasks.add(task);
        taskId++;
        return task;
    }

    @Override
    public Epic createEpic(Epic epic) {
        epicsList.put(taskId, epic);
        epic.setTaskId(taskId);
        taskId++;
        return epic;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        if (!isValidateTaskTime(subtask)) {
            return null;
        }
        subtasksList.put(taskId, subtask);
        subtask.setTaskId(taskId);
        prioritizedTasks.add(subtask);
        taskId++;

        Epic epic;
        epic = epicsList.get(subtask.getEpicBelongsId());

        epic.addSubTask(subtask);
        epic.setTaskState(calculateEpicState(epic));
        epic.setDuration(calculateEpicDuration(epic));
        epic.setStartTime(calculateEpicStartTime(epic));
        epic.setEndTime(epic.getDuration());
        return subtask;
    }

    @Override
    public List<Task> getTasksList() {
        return new ArrayList<>(tasksList.values());
    }

    @Override
    public List<Epic> getEpicsList() { //не очень понятно, нужно ли в этом случае обновлять историю - пока не стал
        return new ArrayList<>(epicsList.values());
    }

    @Override
    public List<Subtask> getSubtasksList() { //не очень понятно, нужно ли в этом случае обновлять историю - пока не стал
        return new ArrayList<>(subtasksList.values());
    }

    @Override
    public void deleteAllTasks() {
        for (Integer id : tasksList.keySet()) {
            prioritizedTasks.remove(getTaskById(id));
            historyManager.remove(id);
        }
        tasksList.clear();
    }

    @Override
    public void deleteAllSubTasks() {
        for(Integer id : subtasksList.keySet()) {
            prioritizedTasks.remove(getSubtaskById(id));
            historyManager.remove(id);

            Integer epicId = subtasksList.get(id).getEpicBelongsId();

            epicsList.get(epicId).setDuration(0);
            epicsList.get(epicId).setStartTime(Instant.now());
            epicsList.get(epicId).setEndTime(0);
            epicsList.get(epicId).setTaskState(TaskState.NEW);
            epicsList.get(epicId).getSubTasksList().clear();
        }
        subtasksList.clear();
    }

    @Override
    public void deleteAllEpics() {
        deleteAllSubTasks();
        for (Integer id : epicsList.keySet()) {
            historyManager.remove(id);
        }
        epicsList.clear();
    }

    @Override
    public Task getTaskById(Integer id) {
        if (tasksList.containsKey(id)) {
            historyManager.add(tasksList.get(id));
            return tasksList.get(id);
        } else {
            System.out.println("Таска с таким id нет");
            return null;
        }
    }

    @Override
    public Epic getEpicById(Integer id) {
        if (epicsList.containsKey(id)) {
            historyManager.add(epicsList.get(id));
            return epicsList.get(id);
        } else {
            System.out.println("Эпика с таким id нет");
            return null;
        }
    }

    @Override
    public Subtask getSubtaskById(Integer id) {
        if (subtasksList.containsKey(id)) {
            historyManager.add(subtasksList.get(id));
            return subtasksList.get(id);
        } else {
            System.out.println("Сабтаска с таким id нет");
            return null;
        }
    }

    @Override
    public void removeTaskById(Integer id) {
        if (tasksList.containsKey(id)) {
            prioritizedTasks.remove(getTaskById(id));
            tasksList.remove(id);
            historyManager.remove(id);
        } else {
            System.out.println("Таска с таким id нет");
        }
    }

    @Override
    public void removeEpicById(Integer id) {
        if (!epicsList.containsKey(id)) {
            System.out.println("Эпика с таким айди нет");
            return;
        }

        Epic epic = epicsList.get(id);
        for (Subtask subtask : epic.getSubTasksList()) {
            historyManager.remove(subtask.getTaskId());
            subtasksList.remove(subtask.getTaskId());
            prioritizedTasks.remove(subtask);
        }
        epicsList.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void removeSubtaskById(Integer id) {
        if (!subtasksList.containsKey(id)) {
            System.out.println("Субтаска с таким id у этого эпика нет");
            return;
        }

        Subtask subtask;
        Epic epic;
        subtask = subtasksList.get(id);
        prioritizedTasks.remove(subtask);
        epic = epicsList.get(subtask.getEpicBelongsId());
        epic.deleteSubTask(subtask); //удалили ссылку на суб из эпика
        subtasksList.remove(id);
        epic.setTaskState(calculateEpicState(epic));
        epic.setDuration(calculateEpicDuration(epic));
        epic.setStartTime(calculateEpicStartTime(epic));
        epic.setEndTime(epic.getDuration());
        historyManager.remove(id);
    }

    @Override
    public void updateTask(Task task) {
        if (isValidateTaskTime(task) && tasksList.containsKey(task.getTaskId())) {
            prioritizedTasks.remove(task); //у задач разные поля, но компаратор возвращает 0 при равных айди
            tasksList.put(task.getTaskId(), task);
            prioritizedTasks.add(task);
        } else {
            System.out.println("Задачи с таким id нет или время в обновленной задаче некорректное");
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (isValidateTaskTime(subtask) && subtasksList.containsKey(subtask.getTaskId())) {
            prioritizedTasks.remove(subtask);
            subtasksList.put(subtask.getTaskId(), subtask);

            Epic epic;
            epic = epicsList.get(subtask.getEpicBelongsId());

            prioritizedTasks.remove(epic);
            epic.setTaskState(calculateEpicState(epic));
            epic.setDuration(calculateEpicDuration(epic));
            epic.setStartTime(calculateEpicStartTime(epic));
            epic.setEndTime(epic.getDuration());
            prioritizedTasks.add(subtask);
            prioritizedTasks.add(epic);
        } else {
            System.out.println("Время в обновленной задаче некорректное");
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        epicsList.put(epic.getTaskId(), epic);
    }

    public TaskState calculateEpicState(Epic epic) {
        int newTasksCount = 0;
        int doneTasksCount = 0;

        if (epic.getSubTasksList().isEmpty()) {
            return TaskState.NEW;
        }
        for (Subtask subtask : epic.getSubTasksList()) {
            if (subtask.getTaskState() == TaskState.IN_PROGRESS) {
                return TaskState.IN_PROGRESS;
            } else if (subtask.getTaskState() == TaskState.DONE) {
                doneTasksCount++;
            } else {
                newTasksCount++;
            }
        }
        if (epic.getSubTasksList().size() == newTasksCount) {
            return TaskState.NEW;
        } else if (epic.getSubTasksList().size() == doneTasksCount) {
            return TaskState.DONE;
        } else {
            return TaskState.IN_PROGRESS;
        }
    }

    public Instant calculateEpicStartTime(Epic epic) {
        Instant startTime = Instant.MAX;

        for (Subtask subtask : epic.getSubTasksList()) {
            if (subtask.getStartTime().compareTo(startTime) < 0) {
                startTime = subtask.getStartTime();
            }
        }
        return startTime;
    }

    public int calculateEpicDuration(Epic epic) {
        int duration = 0;

        for (Subtask subtask : epic.getSubTasksList()) {
            duration += subtask.getDuration();
        }
        return duration;
    }

    protected void initBooking() {
        int currentYear = Instant.now().atZone(ZoneId.systemDefault()).getYear();
        int currentMonth = Instant.now().atZone(ZoneId.systemDefault()).getMonthValue();
        int currentDay = Instant.now().atZone(ZoneId.systemDefault()).getDayOfMonth();
        Instant start = Instant.parse(String.format("%d-%d%d-%d%dT00:00:00Z", currentYear, currentMonth / 10,
                currentMonth % 10, currentDay / 10, currentDay % 10));

        //планируем от сегодняшего дня до конца года
        //1 задача - минимум 15 минут
        while (!start.equals(Instant.parse(String.format("%d-01-01T00:00:00Z", currentYear + 1)))) {
            timeBookingTable.put(start, true);
            start = start.plusSeconds(15 * 60);
        }
    }

    protected boolean isValidateTaskTime(Task task) {
        boolean isValidate = true;

        if (!task.isUserSetTime()) {
            return true;
        }
        if (task.getDuration() < MIN_TASK_DURATION_MINUTES) {
            System.out.println("Минимальное время задачи (минут): " + MIN_TASK_DURATION_MINUTES);
            return false;
        }

        Instant startTime = task.getStartTime();
        Instant endTime = task.getEndTime();

        boolean isValidateMinutes = startTime.atZone(ZoneId.systemDefault()).getMinute() % 15 == 0;
        boolean isValidateSeconds = startTime.atZone(ZoneId.systemDefault()).getSecond() == 0;
        boolean isValidateYear = startTime.atZone(ZoneId.systemDefault()).getYear() ==
                Instant.now().atZone(ZoneId.systemDefault()).getYear();

        if (!isValidateSeconds || !isValidateMinutes || !isValidateYear) {
            System.out.println("Время задачи должны быть в этом году и лежать на сетке (минут): " + MIN_TASK_DURATION_MINUTES);
            return false;
        }

        Instant check = task.getStartTime();

        while (check.isBefore(endTime) && isValidate) {
            isValidate = timeBookingTable.get(startTime);
            if (!isValidate) {
                System.out.println("Задача " + task.getTaskName() + ". Время занято: " + startTime);
                break;
            }
            check = check.plusSeconds(60 * MIN_TASK_DURATION_MINUTES);
        }

        if (isValidate) {
            check = task.getStartTime();

            while (check.isBefore(endTime)) {
                timeBookingTable.put(check, false);
                check = check.plusSeconds(60 * MIN_TASK_DURATION_MINUTES);
            }
        } else {
            System.out.println("Время занято: " + startTime);
        }
        return isValidate;
    }
}
