package controller;

public enum Endpoints {
    UNKNOW,
    POST_TASK, // POST /tasks/task/
    POST_EPIC, // POST /tasks/epic/
    POST_SUBTASK, // POST /tasks/subtask/
    GET_TASKS, // GET /tasks/task/
    GET_EPICS, // GET /tasks/epic/
    GET_SUBTASKS, // GET /tasks/subtask/
    DELETE_TASKS, // DELETE /tasks/task/
    DELETE_SUBTASKS, // DELETE /tasks/subtask/
    DELETE_EPICS, // DELETE /tasks/epic/
    GET_TASK_BY_ID, // GET /tasks/task/?id=/
    GET_EPIC_BY_ID, // GET /tasks/epic/?id=/
    GET_SUBTASK_BY_ID, // GET /tasks/subtask/?id=/
    DELETE_TASK_BY_ID, // DELETE /tasks/task/?id=/
    DELETE_EPIC_BY_ID, // DELETE /tasks/epic/?id=/
    DELETE_SUBTASK_BY_ID, // DELETE /tasks/subtask/?id=/
    POST_UPDATE_TASK, // POST /tasks/task/?id=/
    POST_UPDATE_SUBTASK, // POST /tasks/subtask/?id=/
    POST_UPDATE_EPIC, // POST /tasks/epic/?id=/
    GET_HISTORY, // GET /tasks/history/
    GET_PRIORITIZED_TASKS // GET /tasks/prioritized/
}
