package model;

public class Subtask extends Task {
    private final Integer epicBelongsId;

    public Subtask(String taskName, String taskDescription, TaskState state, Epic belongsToEpic) {
        super(taskName, taskDescription, state);
        this.epicBelongsId = belongsToEpic.getTaskId();
        belongsToEpic.addSubTask(this); // добавляем субтаск в список субтасков этого эпика
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "epicBelongsID=" + epicBelongsId +
                ", taskName='" + taskName + '\'' +
                ", taskDescription='" + taskDescription + '\'' +
                ", taskID=" + taskId + (char) 0x2615 +
                ", taskState='" + taskState + '\'' +
                '}';
    }

    public Integer getBelongsToEpicId() {
        return epicBelongsId;
    }

}
