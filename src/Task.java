public class Task {
    protected String taskName;
    protected String taskDescription;
    protected Integer taskID;
    protected TaskState taskState;

    public Task(String taskName, String taskDescription, TaskState state) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.taskState = state;
    }

    public Integer getID() {
        return taskID;
    }

    public void setTaskState(TaskState state) {
        this.taskState = state;
    }

    @Override
    public String toString() {
        return "Task{" +
                "taskName='" + taskName + '\'' +
                ", taskDescription='" + taskDescription + '\'' +
                ", taskID=" + taskID +
                ", taskState='" + taskState + '\'' +
                '}';
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public void setTaskID(Integer taskID) {
        this.taskID = taskID;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public Integer getTaskID() {
        return taskID;
    }

    public TaskState getTaskState() {
        return taskState;
    }

    public String getTaskName() {
        return taskName;
    }
}
