package model;

import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Subtask> subTasksList; //список субтасков эпика

    public Epic(String taskName, String taskDescription) {
        super(taskName, taskDescription, TaskState.NEW);
        this.taskType = TasksType.EPIC;
        this.subTasksList = new ArrayList<>();
    }

    public void addSubTask(Subtask subTask) {
        subTasksList.add(subTask);
    }

    public void deleteSubTask(Subtask subtask) {
        subTasksList.remove(subtask);
    }

    public ArrayList<Subtask> getSubTasksList() {
        return subTasksList;
    }
}
