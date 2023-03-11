import java.util.List;

public interface TaskManager {
    Task createTask(Task task);

    Epic createEpic(Epic epic);

    Subtask createSubtask(Subtask subtask);

    List<Task> getTasksList();

    List<Epic> getEpicsList();

    List<Subtask> getSubtasksList();

    void deleteAllTasks();

    TaskState setEpicState(Epic epic);

    void deleteAllSubTasks();

    void deleteAllEpics();

    Task getTaskFromId(Integer id);

    Epic getEpicFromId(Integer id);

    Subtask getSubtaskFromId(Integer id);

    void removeTaskFromId(Integer id);

    void removeEpicFromId(Integer id);

    void removeSubtaskFromId(Integer id);

    void updateTask(Task task);

    void updateSubtask(Subtask subtask);

    void updateEpic(Epic epic);
    List<Task> getHistoryList();
}
