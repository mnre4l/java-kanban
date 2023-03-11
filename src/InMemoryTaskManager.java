import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> tasksList = new HashMap<>();
    private final HashMap<Integer, Epic> epicsList = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasksList = new HashMap<>();
    private Integer taskId = 0;
    private Integer epicId = 0;
    private Integer subtaskId = 0;
    HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public List<Task> getHistoryList() {
        return historyManager.getHistoryList();
    }

    @Override
    public Task createTask(Task task) {
        tasksList.put(taskId, task);
        task.taskID = taskId;
        taskId++;
        return task;
    }

    @Override
    public Epic createEpic(Epic epic) {
        epicsList.put(epicId, epic);
        epic.taskID = epicId;
        epicId++;
        return epic;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        subtasksList.put(subtaskId, subtask);
        subtask.taskID = subtaskId;
        subtaskId++;
        Epic epic;
        epic = epicsList.get(subtask.getBelongsToEpicIP());
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
        }
        subtasksList.clear();
    }

    @Override
    public void deleteAllEpics() {
        deleteAllSubTasks();
        epicsList.clear();
    }

    @Override
    public Task getTaskFromId(Integer id) {
        historyManager.addTask(tasksList.get(id));
        return tasksList.get(id);
    }

    @Override
    public Epic getEpicFromId(Integer id) {
        historyManager.addTask(epicsList.get(id));
        return epicsList.get(id);
    }

    @Override
    public Subtask getSubtaskFromId(Integer id) {
        historyManager.addTask(subtasksList.get(id));
        return subtasksList.get(id);
    }

    @Override
    public void removeTaskFromId(Integer id) {
        tasksList.remove(id);
    }

    @Override
    public void removeEpicFromId(Integer id) {
        Epic epic = epicsList.get(id);
        for (Subtask subtask : epic.getSubTasksList()) {
            subtasksList.remove(subtask.getID());
        }
        epicsList.remove(id);
    }

    @Override
    public void removeSubtaskFromId(Integer id) {
        Subtask subtask;
        Epic epic;
        subtask = subtasksList.get(id);
        epic = epicsList.get(subtask.getBelongsToEpicIP());
        epic.deleteSubTask(subtask); //удалили ссылку на суб из эпика
        subtasksList.remove(id);
        epic.setTaskState(setEpicState(epic));
    }

    @Override
    public void updateTask(Task task) {
        tasksList.put(task.getID(), task);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        subtasksList.put(subtask.getID(), subtask);
        Epic epic;
        epic = epicsList.get(subtask.getBelongsToEpicIP());
        epic.setTaskState(setEpicState(epic));
    }

    @Override
    public void updateEpic(Epic epic) {
        epicsList.put(epic.getID(), epic);
    }

    @Override
    public TaskState setEpicState(Epic epic) {
        boolean isDone;
        isDone = true;
        for (Subtask subtask : epic.getSubTasksList()) {
            if (subtask.getTaskState() == TaskState.IN_PROGRESS) {
                return TaskState.IN_PROGRESS; //если хотя бы 1 субтаск из эпика в процессе - возвращаем эпику статус в процессе
            }
            //здесь или DONE или NEW
            if (subtask.getTaskState() == TaskState.NEW) {
                isDone = false;
            }
        }
        if (isDone) {
            return TaskState.DONE;
        } else {
            return TaskState.NEW;
        }
    }
}
