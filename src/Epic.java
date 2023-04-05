import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Subtask> subTasksList; //список субтасков эпика

    public Epic(String taskName, String taskDescription) {
        super(taskName, taskDescription, TaskState.NEW);
        this.subTasksList = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Epic{" +
                "taskName='" + taskName +
                ", taskDescription='" + taskDescription + '\'' +
                ", taskID=" + taskId + (char) 0x2615 +
                ", taskState='" + taskState + '\'' +
                '}';
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
