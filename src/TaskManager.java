import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TaskManager {
    private final HashMap<Integer, Task> tasksList = new HashMap<>();
    private final HashMap<Integer, Epic> epicsList = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasksList = new HashMap<>();
    private Integer taskId = 0;
    private Integer epicId = 0;
    private Integer subtaskId = 0;

    Task createTask(Task task) {
        tasksList.put(taskId, task);
        task.taskID = taskId;
        taskId++;
        return task;
    }

    Epic createEpic(Epic epic) {
        epicsList.put(epicId, epic);
        epic.taskID = epicId;
        epicId++;
        return epic;
    }

    Subtask createSubtask(Subtask subtask) {
        subtasksList.put(subtaskId, subtask);
        subtask.taskID = subtaskId;
        subtaskId++;
        Epic epic;
        epic = epicsList.get(subtask.getBelongsToEpicIP());
        epic.setTaskState(setEpicState(epic));
        return subtask;
    }

    public List<Task> getTasksList() {
        return new ArrayList<>(tasksList.values());
    }

    public List<Epic> getEpicsList() {
        return new ArrayList<>(epicsList.values());
    }

    public List<Subtask> getSubtasksList() {
        return new ArrayList<>(subtasksList.values());
    }

    public void deleteAllTasks() {
        tasksList.clear();
    }

    public void deleteAllSubTasks() {
        for (Epic epic : epicsList.values()) {
            epic.getSubTasksList().clear();
        }
        subtasksList.clear();
    }

    public void deleteAllEpics() {
        deleteAllSubTasks();
        epicsList.clear();
    }

    public Task getTaskFromId(Integer id) {
        return tasksList.get(id);
    }

    public Epic getEpicFromId(Integer id) {
        return epicsList.get(id);
    }

    public Subtask getSubtaskFromId(Integer id) {
        return subtasksList.get(id);
    }

    public void removeTaskFromId(Integer id) {
        tasksList.remove(id);
    }

    public void removeEpicFromId(Integer id) {
        Epic epic = epicsList.get(id);
        for (Subtask subtask : epic.getSubTasksList()) {
            subtasksList.remove(subtask.getID());
        }
        epicsList.remove(id);
    }

    public void removeSubtaskFromId(Integer id) {
        Subtask subtask;
        Epic epic;
        subtask = subtasksList.get(id);
        epic = epicsList.get(subtask.getBelongsToEpicIP());
        epic.deleteSubTask(subtask); //удалили ссылку на суб из эпика
        subtasksList.remove(id);
        epic.setTaskState(setEpicState(epic));
    }

    public void updateTask(Task task) {
        tasksList.put(task.getID(), task);
    }

    public void updateSubtask(Subtask subtask) {
        subtasksList.put(subtask.getID(), subtask);
        Epic epic;
        epic = epicsList.get(subtask.getBelongsToEpicIP());
        epic.setTaskState(setEpicState(epic));
    }

    public void updateEpic(Epic epic) {
        epicsList.put(epic.getID(), epic);
    }

    public String setEpicState(Epic epic) {
        boolean isDone;
        isDone = true;
        for (Subtask subtask : epic.getSubTasksList()) {
            if (subtask.getTaskState().equals("IN_PROGRESS")) {
                return "IN_PROGRESS"; //если хотя бы 1 субтаск из эпика в процессе - возвращаем эпику статус в процессе
            }
            //здесь или DONE или NEW
            if (subtask.getTaskState().equals("NEW")) {
                isDone = false;
            }
        }
        if (isDone) {
            return "DONE";
        } else {
            return "NEW";
        }
    }
}
