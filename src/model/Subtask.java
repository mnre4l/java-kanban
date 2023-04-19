package model;

public class Subtask extends Task {
    private final Integer epicBelongsId;

    public Subtask(String taskName, String taskDescription, TaskState state, Epic belongsToEpic) {
        super(taskName, taskDescription, state);
        this.epicBelongsId = belongsToEpic.getTaskId();
        this.taskType = TasksType.SUBTASK;
        belongsToEpic.addSubTask(this); // добавляем субтаск в список субтасков этого эпика
    }

    @Override
    public String toString() {
        return super.toString() + this.epicBelongsId;
    }

    public Integer getBelongsToEpicId() {
        return epicBelongsId;
    }

}
