public class Subtask extends Task {
    private Epic belongsToEpic; //ссылка на эпик, которому принадлежит субтаск

    public Subtask(String taskName, String taskDescription, String taskState, Epic belongsToEpic) {
        super(taskName, taskDescription, taskState);
        this.belongsToEpic = belongsToEpic; // при создании субтаска сохраняем ссылку на соответствующий эпик..
        belongsToEpic.addSubTask(this); //и добавляем субтаск в список субтасков этого эпика
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "belongsToEpic=" + belongsToEpic +
                ", taskName='" + taskName + '\'' +
                ", taskDescription='" + taskDescription + '\'' +
                ", taskID=" + taskID +
                ", taskState='" + taskState + '\'' +
                '}';
    }

    public Epic getBelongsToEpic() {
        return belongsToEpic;
    }

}
