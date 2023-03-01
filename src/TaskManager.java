import java.util.HashMap;

public class TaskManager {
    private HashMap<Integer, Task> tasksList = new HashMap<>();
    private HashMap<Integer, Epic> epicsList = new HashMap<>();
    private HashMap<Integer, Subtask> subtasksList = new HashMap<>();
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
        return subtask;
    }

    public HashMap<Integer, Task> getTasksList() {
        return tasksList;
    }

    public HashMap<Integer, Epic> getEpicsList() {
        return epicsList;
    }

    public HashMap<Integer, Subtask> getSubtasksList() {
        return subtasksList;
    }

    public void deleteAllTasks() {
        tasksList.clear();
        subtasksList.clear();
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
        epicsList.remove(id);
    }

    public void removeSubtaskFromId(Integer id) {
        Subtask subtask;
        subtask = subtasksList.get(id);
        subtask.getBelongsToEpic().deleteSubTask(subtask); //удалили ссылку на суб из эпика
        subtasksList.remove(id);
    }

    public void updateTask(Task task) {
        tasksList.put(task.getID(), task);
    }

    public void updateSubtask(Subtask subtask) {
        subtasksList.put(subtask.getID(), subtask);
        subtask.getBelongsToEpic().setTaskState(setEpicState(subtask.getBelongsToEpic()));
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
