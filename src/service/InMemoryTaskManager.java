package service;

import model.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    protected final HashMap<Integer, Task> tasksList = new HashMap<>();
    protected final HashMap<Integer, Epic> epicsList = new HashMap<>();
    protected final HashMap<Integer, Subtask> subtasksList = new HashMap<>();
    protected Integer taskId = 0;
    protected final HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public List<Task> getHistoryList() {
        return historyManager.getHistory();
    }

    @Override
    public Task createTask(Task task) {
        tasksList.put(taskId, task);
        task.setTaskId(taskId);
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
        subtasksList.put(taskId, subtask);
        subtask.setTaskId(taskId);
        taskId++;
        Epic epic;
        epic = epicsList.get(subtask.getBelongsToEpicId());
        epic.setTaskState(calculateEpicState(epic));
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
        for (Integer id : tasksList.keySet()) {
            historyManager.remove(id);
        }
        tasksList.clear();
    }

    @Override
    public void deleteAllSubTasks() {
        for(Integer id : subtasksList.keySet()) {
            historyManager.remove(id);

            Integer epicId = subtasksList.get(id).getBelongsToEpicId();

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
            System.out.println("Сабтаска с таким id у этого эпика нет");
            return null;
        }
    }

    @Override
    public void removeTaskById(Integer id) {
        if (tasksList.containsKey(id)) {
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
        epic = epicsList.get(subtask.getBelongsToEpicId());
        epic.deleteSubTask(subtask); //удалили ссылку на суб из эпика
        subtasksList.remove(id);
        epic.setTaskState(calculateEpicState(epic));
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
        epic.setTaskState(calculateEpicState(epic));
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
}
