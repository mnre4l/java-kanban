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
        return historyManager.getHistoryList();
    }

    @Override
    public Task createTask(Task task) {
        tasksList.put(taskId, task);
        task.taskId = taskId;
        taskId++;
        return task;
    }

    @Override
    public Epic createEpic(Epic epic) {
        epicsList.put(taskId, epic);
        epic.taskId = taskId;
        taskId++;
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
        return subtask;
    }

    @Override
    public List<Task> getTasksList() {
        return new ArrayList<>(tasksList.values());
    }

    @Override
    public List<Epic> getEpicsList() {
        return new ArrayList<>(epicsList.values());
    }

    @Override
    public List<Subtask> getSubtasksList() {
        return new ArrayList<>(subtasksList.values());
    }

    @Override
    public void deleteAllTasks() {
        tasksList.clear();
    }

    @Override
    public void deleteAllSubTasks() {
        for (Epic epic : epicsList.values()) {
            epic.getSubTasksList().clear();
            epic.setTaskState(TaskState.NEW);
        }
        subtasksList.clear();
    }

    @Override
    public void deleteAllEpics() {
        deleteAllSubTasks();
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
            return;
        }
        historyManager.addTask(tasksList.get(id));
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
            return;
        }
        historyManager.addTask(epicsList.get(id));
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
            return;
        }
        historyManager.addTask(subtasksList.get(id));
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
        }
        epicsList.remove(id);
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
        boolean isDone;
        boolean isNew;

        isDone = true;
        for (Subtask subtask : epic.getSubTasksList()) {
            if (subtask.getTaskState() == TaskState.IN_PROGRESS) {
                return TaskState.IN_PROGRESS; //если хотя бы 1 субтаск из эпика в процессе - возвращаем эпику статус
                // в процессе
            }
            if (subtask.getTaskState() == TaskState.NEW) {
                isDone = false;
            } else if (subtask.getTaskState() == TaskState.DONE) {
                isNew = false;
            }
        }
        if (isDone) {
            return TaskState.DONE;
        } else if (isNew) {
            return TaskState.NEW
        } else {
            return TaskState.IN_PROGRESS;
        }
    }
}
