package service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> tasksList = new HashMap<>();
    private final HashMap<Integer, Epic> epicsList = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasksList = new HashMap<>();
    private Integer taskId = 0;
    private final HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public List<Task> getHistoryList() {
        return historyManager.getHistory();
    }

    @Override
    public Task createTask(Task task) {
        tasksList.put(taskId, task);
        task.taskId = taskId;
        taskId++;
        historyManager.add(task);
        return task;
    }

    @Override
    public Epic createEpic(Epic epic) {
        epicsList.put(taskId, epic);
        epic.taskId = taskId;
        taskId++;
        historyManager.add(epic);
        return epic;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        subtasksList.put(taskId, subtask);
        subtask.taskId = taskId;
        taskId++;
        Epic epic;
        epic = epicsList.get(subtask.getBelongsToEpicId());
        epic.setTaskState(setEpicState(epic));
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public List<Task> getTasksList() { //не очень понятно, нужно ли в этом случае обновлять историю - пока не стал
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
        for (Task task : tasksList.values()) {
            historyManager.remove(taskId);
        }
        tasksList.clear();
    }

    @Override
    public void deleteAllSubTasks() {
        for (Epic epic : epicsList.values()) {
            for (Subtask subtask : epic.getSubTasksList()) {
                historyManager.remove(subtask.getTaskId());
            }
            epic.getSubTasksList().clear();
            epic.setTaskState(TaskState.NEW);
        }
        subtasksList.clear();
    }

    @Override
    public void deleteAllEpics() {
        deleteAllSubTasks();
        for (Epic epic : epicsList.values()) {
            historyManager.remove(epic.getTaskId());
        }
        epicsList.clear();
    }

    @Override
    public Task getTaskById(Integer id) {
        boolean isTaskInList = false;

        for (Task task : tasksList.values()) {
            if (task.getTaskId() == id) {
                isTaskInList = true;
            }
        }
        if (!isTaskInList) {
            System.out.println("Таска с таким id нет");
            return null;
        }
        historyManager.add(tasksList.get(id));
        return tasksList.get(id);
    }

    @Override
    public Epic getEpicById(Integer id) {
        boolean isTaskInList = false;

        for (Epic epic : epicsList.values()) {
            if (epic.getTaskId() == id) {
                isTaskInList = true;
            }
        }
        if (!isTaskInList) {
            System.out.println("Эпика с таким id нет");
            return null;
        }
        historyManager.add(epicsList.get(id));
        return epicsList.get(id);
    }

    @Override
    public Subtask getSubtaskById(Integer id) {
        boolean isTaskInList = false;

        for (Subtask subtask : subtasksList.values()) {
            if (subtask.getTaskId() == id) {
                isTaskInList = true;
            }
        }
        if (!isTaskInList) {
            System.out.println("Субтаска с таким id у этого эпика нет");
            return null;
        }
        historyManager.add(subtasksList.get(id));
        return subtasksList.get(id);
    }

    @Override
    public void removeTaskById(Integer id) {
        boolean isTaskInList = false;

        for (Task task : tasksList.values()) {
            if (task.getTaskId() == id) {
                isTaskInList = true;
            }
        }
        if (!isTaskInList) {
            System.out.println("Таска с таким id нет");
            return;
        }
        tasksList.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void removeEpicById(Integer id) {
        boolean isTaskInList = false;

        for (Epic epic : epicsList.values()) {
            if (epic.getTaskId() == id) {
                isTaskInList = true;
            }
        }
        if (!isTaskInList) {
            System.out.println("Эпика с таким id нет");
            return;
        }
        Epic epic = epicsList.get(id);
        for (Subtask subtask : epic.getSubTasksList()) {
            subtasksList.remove(subtask.getTaskId());
            historyManager.remove(subtask.getTaskId());
        }
        epicsList.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void removeSubtaskById(Integer id) {
        boolean isTaskInList = false;

        for (Subtask subtask : subtasksList.values()) {
            if (subtask.getTaskId() == id) {
                isTaskInList = true;
            }
        }
        if (!isTaskInList) {
            System.out.println("Субтаска с таким id у этого эпика нет");
            return;
        }
        Subtask subtask;
        Epic epic;
        subtask = subtasksList.get(id);
        epic = epicsList.get(subtask.getBelongsToEpicId());
        epic.deleteSubTask(subtask); //удалили ссылку на суб из эпика
        subtasksList.remove(id);
        epic.setTaskState(setEpicState(epic));
        historyManager.remove(id);
    }

    @Override
    public void updateTask(Task task) {
        tasksList.put(task.getTaskId(), task);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        subtasksList.put(subtask.getTaskId(), subtask);
        Epic epic;
        epic = epicsList.get(subtask.getBelongsToEpicId());
        epic.setTaskState(setEpicState(epic));
    }

    @Override
    public void updateEpic(Epic epic) {
        epicsList.put(epic.getTaskId(), epic);
    }

    public TaskState setEpicState(Epic epic) {
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
}
