package model;

import java.time.Instant;

public class Subtask extends Task {
    private final Integer epicBelongsId;

    public Subtask(String taskName, String taskDescription, TaskState state, Epic belongsToEpic) {
        super(taskName, taskDescription, state);
        this.epicBelongsId = belongsToEpic.getTaskId();
        this.taskType = TasksType.SUBTASK;
        belongsToEpic.addSubTask(this); // добавляем субтаск в список субтасков этого эпика
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

    public Integer getBelongsToEpicId() {
        return epicBelongsId;
    }

}
