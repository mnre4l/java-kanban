package model;

import java.time.Instant;

public class Subtask extends Task {
    private Integer epicBelongsId;

    public Subtask(String taskName, String taskDescription, TaskState state, int epicId) {
        super(taskName, taskDescription, state);
        this.epicBelongsId = epicId;
        this.taskType = TasksType.SUBTASK;
    }

    public Subtask(String taskName, String taskDescription, TaskState state, Epic epic) {
        super(taskName, taskDescription, state);
        this.epicBelongsId = epic.getTaskId();
        this.taskType = TasksType.SUBTASK;
    }

    public Subtask(String taskName, String taskDescription, TaskState state, Epic belongsToEpic, Instant startTime,
                   int duration) {
        super(taskName, taskDescription, state, startTime, duration);
        this.epicBelongsId = belongsToEpic.getTaskId();
        this.taskType = TasksType.SUBTASK;
        belongsToEpic.addSubTask(this); // добавляем субтаск в список субтасков этого эпика
        this.isUserSetTime = true;
    }

    @Override
    public String toString() {
        return super.toString() + this.epicBelongsId;
    }

    public Integer getEpicBelongsId() {
        return epicBelongsId;
    }

    public void setEpicBelongsId(int id) {
        this.epicBelongsId = id;
    }
}
