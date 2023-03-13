public class Task {
    protected String taskName;
    protected String taskDescription;
    protected Integer taskId;
    protected TaskState taskState;

    public Task(String taskName, String taskDescription, TaskState state) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.taskState = state;
    }

    public void setTaskState(TaskState state) {
        this.taskState = state;
    }

    @Override
    public String toString() {
        return "Task{" +
                "taskName='" + taskName + '\'' +
                ", taskDescription='" + taskDescription + '\'' +
                ", taskID=" + taskId +
                ", taskState='" + taskState + '\'' +
                '}';
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public TaskState getTaskState() {
        return taskState;
    }

    public String getTaskName() {
        return taskName;
    }
}
