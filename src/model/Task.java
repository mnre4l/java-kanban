package model;

import java.time.Instant;
import java.util.Objects;

public class Task {

    protected String taskName;
    protected String taskDescription;
    protected Integer taskId;
    protected TaskState taskState;
    protected TasksType taskType;
    protected Integer duration;
    protected Instant startTime;
    protected boolean isUserSetTime = false;
    public static final int FILE_COLUM_NUM_ID = 0;
    public static final int FILE_COLUM_NUM_TYPE = 1;
    public static final int FILE_COLUM_NUM_NAME = 2;
    public static final int FILE_COLUM_NUM_STATE = 3;
    public static final int FILE_COLUM_NUM_DESCR = 4;
    public static final int FILE_COLUM_NUM_EPICBELONGS = 8;
    public static final int FILE_COLUM_NUM_STARTTIME = 5;
    public static final int FILE_COLUM_NUM_DURATION = 6;
    public static final int FILE_COLUM_NUM_ENDTIME = 7;

    public Task(String taskName, String taskDescription, TaskState state) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.taskState = state;
        this.taskType = TasksType.TASK;
        this.startTime = Instant.now();
        this.duration = 0;
    }

    public Task(String taskName, String taskDescription, TaskState state, Instant startTime, int duration) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.taskState = state;
        this.taskType = TasksType.TASK;
        this.startTime = startTime;
        this.duration = duration;
        this.isUserSetTime = true;
    }

    public void setTaskState(TaskState state) {
        this.taskState = state;
    }

    @Override
    public String toString() {
        return this.taskId + "," + this.taskType + "," + this.taskName + "," + this.taskState + "," +
                this.taskDescription + "," + this.startTime.toString() + "," + this.duration + "," +
                this.getEndTime().toString() + ",";
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

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
        this.isUserSetTime = true;
    }

    public void setDuration(int duration) {
        this.duration = duration;
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

    public Instant getStartTime() {
        return startTime;
    }

    public Instant getEndTime() {
        return startTime.plusSeconds(60 * duration);
    }

    public int getDuration() {
        return duration;
    }

    public boolean isUserSetTime() {
        return isUserSetTime;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (this.getClass() != o.getClass()) return false;
        if (this == o) return true;
        Task otherTask = (Task) o;
        return Objects.equals(this.getTaskId(), otherTask.getTaskId()) &&
                Objects.equals(this.getTaskName(), otherTask.getTaskName()) &&
                Objects.equals(this.getStartTime(), otherTask.getStartTime()) &&
                Objects.equals(this.getTaskDescription(), otherTask.getTaskDescription()) &&
                Objects.equals(this.getTaskState(), otherTask.getTaskState()) &&
                Objects.equals(this.getEndTime(), otherTask.getEndTime()) &&
                Objects.equals(this.getDuration(), otherTask.getDuration());
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskId, taskName, startTime, taskDescription, taskState, getEndTime(), duration);
    }
}
