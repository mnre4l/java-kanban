package model;

import java.time.Instant;
import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Subtask> subTasksList; //список субтасков эпика
    private Instant endTime = Instant.now();

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

    public void setEndTime(int duration) {
        endTime = startTime.plusSeconds(60 * duration);
    }

    @Override
    public Instant getEndTime() {
        return endTime;
    }
}
