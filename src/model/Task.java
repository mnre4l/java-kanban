package model;

/**
 * Возвращает объект задачи. Задача имеет: имя, описание, статус, тип и идентефикатор.
 * В полях также сохраняются константы, устанавливающее соответствие номер столбцов -
 * параметр задачи (в файле CSV).
 * Класс является родителем для задач других типов.
 */
public class Task {
    /**
     *
     */
    protected String taskName;
    protected String taskDescription;
    protected Integer taskId;
    protected TaskState taskState;
    protected TasksType taskType;
    public static final int FILE_COLUM_NUM_ID = 0;
    public static final int FILE_COLUM_NUM_TYPE = 1;
    public static final int FILE_COLUM_NUM_NAME = 2;
    public static final int FILE_COLUM_NUM_STATE = 3;
    public static final int FILE_COLUM_NUM_DESCR = 4;
    public static final int FILE_COLUM_NUM_EPICBELONGS = 5;

    public Task(String taskName, String taskDescription, TaskState state) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.taskState = state;
        this.taskType = TasksType.TASK;
    }

    public void setTaskState(TaskState state) {
        this.taskState = state;
    }

    @Override
    public String toString() {
        return this.taskId + "," + this.taskType + "," + this.taskName + "," + this.taskState + "," +
                this.taskDescription + ",";
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
