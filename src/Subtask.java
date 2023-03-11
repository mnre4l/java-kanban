public class Subtask extends Task {
    private final Integer epicBelongsID;

    public Subtask(String taskName, String taskDescription, TaskState state, Epic belongsToEpic) {
        super(taskName, taskDescription, state);
        this.epicBelongsID = belongsToEpic.getID();
        belongsToEpic.addSubTask(this); // добавляем субтаск в список субтасков этого эпика
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "epicBelongsID=" + epicBelongsID +
                ", taskName='" + taskName + '\'' +
                ", taskDescription='" + taskDescription + '\'' +
                ", taskID=" + taskID +
                ", taskState='" + taskState + '\'' +
                '}';
    }

    public Integer getBelongsToEpicIP() {
        return epicBelongsID;
    }

}
