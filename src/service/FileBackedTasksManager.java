package service;

import model.*;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.*;

public class FileBackedTasksManager extends InMemoryTaskManager implements TaskManager {
    private final Path file;
    private final static String FILE_TABLE_HEADER = "id,type,name,status,description,startTime,duration,endTime,epic";

    public FileBackedTasksManager(Path file) throws ManagerSaveException {
        try {
            Files.deleteIfExists(file);
            this.file = Files.createFile(file);
        } catch (IOException e) {
            throw new ManagerSaveException("IO ex при создании менеджера");
        }
    }

    public static FileBackedTasksManager loadFromFile(Path path) {
        try {
            String content = Files.readString(path);
            FileBackedTasksManager manager = new FileBackedTasksManager(path);

            String[] fileContent = content.split("\n");
            int i = 1;

            while (i < fileContent.length && !fileContent[i].isEmpty()) {
                manager.fromString(fileContent[i++]);
            }

            try {
                List<Integer> historyFromList = historyFromString(fileContent[fileContent.length - 1]);
                Collections.reverse(historyFromList);
                for (Integer id : historyFromList) {
                    if (manager.tasksList.containsKey(id)) {
                        manager.historyManager.add(manager.getTaskById(id));
                    } else if (manager.epicsList.containsKey(id)) {
                        manager.historyManager.add(manager.getEpicById(id));
                    } else {
                        manager.historyManager.add(manager.getSubtaskById(id));
                    }
                }
            } catch (ManagerSaveException e) {
                System.out.println("Не получилось создать историю нового менеджера. Причина: " + e.getMessage() +
                        " или пустой файл (отсутствует история)");
            }
            return manager;
        } catch (IOException ex) {
            System.out.println("IO ex при загрузке");
            return null;
        } catch (ManagerSaveException e) {
            System.out.println("Ошибка: " + e.getMessage());
            return null;
        }
    }

    private void writeMapToList(Map map) throws IOException {
        for (Object obj : map.values()) {
            try (FileWriter fileWriter = new FileWriter(file.toString(), true)) {
                fileWriter.write("\n" + obj.toString());
            }
        }
    }

    private static String historyToString(HistoryManager manager) {
        StringBuilder history = new StringBuilder("\n\n");
        for (Task task : manager.getHistory()) {
            history.append(task.getTaskId() + ",");
        }
        history.deleteCharAt(history.length() - 1);
        return history.toString();
    }

    private static boolean isValidateHistoryString(String value) {
        boolean isValidate = true;
        String[] valueSplit = value.split(",");

        if (value.isEmpty() || value.isBlank() || valueSplit.length == 1) {
            isValidate = false;
        }

        for (String item : valueSplit) {
            for (int i = 0; i < item.length() && isValidate; i++) {
                if (!Character.isDigit(item.charAt(i))) {
                    isValidate = false;
                }
            }
        }
        return isValidate;

    }

    private static List<Integer> historyFromString(String value) throws ManagerSaveException {
        List<Integer> historyIdList = new ArrayList<>();
        if (!isValidateHistoryString(value)) {
            throw new ManagerSaveException("Неверный формат записи истории в файле");
        }
        for (String stringId : value.split(",")) {
            historyIdList.add(Integer.parseInt(stringId));
        }
        return historyIdList;
    }

    private void save() throws ManagerSaveException {
        try (FileWriter fileWriter = new FileWriter(file.toString())) {
            fileWriter.write(FILE_TABLE_HEADER);
        } catch (IOException e) {
            throw new ManagerSaveException("IO ex при сохранении");
        }

        try {
            writeMapToList(tasksList);
            writeMapToList(epicsList);
            writeMapToList(subtasksList);
        } catch (IOException e) {
            throw new ManagerSaveException("IO ex при сохранении");
        }

        try (FileWriter fileWriter = new FileWriter(file.toString(), true)) {
            fileWriter.write(historyToString(historyManager));
        } catch (IOException e) {
            throw new ManagerSaveException("IO ex при сохранении");
        }
    }



    private Task fromString(String value) {
        String[] taskParams = value.split(",");
        int id = Integer.parseInt(taskParams[Task.FILE_COLUM_NUM_ID]);
        String state = taskParams[Task.FILE_COLUM_NUM_STATE];
        String name = taskParams[Task.FILE_COLUM_NUM_NAME];
        String description = taskParams[Task.FILE_COLUM_NUM_DESCR];
        String type = taskParams[Task.FILE_COLUM_NUM_TYPE];

        String stringStartTime = taskParams[Task.FILE_COLUM_NUM_STARTTIME];
        //если пустой то now, если нет то в число
        Instant startTime;
        if (stringStartTime.isEmpty()) {
            startTime = Instant.now();
        } else {
            startTime = Instant.parse(stringStartTime);
        }

        int duration = Integer.parseInt(taskParams[Task.FILE_COLUM_NUM_DURATION]);
        String stringEndTime = taskParams[Task.FILE_COLUM_NUM_ENDTIME];
        //если пустой то now, если нет то в число
        Instant endTime;
        if (stringEndTime.isEmpty()) {
            endTime = Instant.now();
        } else {
            endTime = Instant.parse(stringEndTime);
        }

        Integer lastTaskId = taskId;

        if (type.equals(TasksType.TASK.toString())) {
            Task task = new Task(name, description, TaskState.valueOf(state), startTime, duration);
            taskId = id;
            super.createTask(task);
            taskId = Math.max(taskId, lastTaskId);
            return task;
        }

        if (type.equals(TasksType.EPIC.toString())) {
            Epic epic = new Epic(name, description);
            taskId = id;
            super.createEpic(epic);
            taskId = Math.max(taskId, lastTaskId);
            return epic;
        }

        if (type.equals(TasksType.SUBTASK.toString())) {
            int epicBelongs = Integer.parseInt(taskParams[Task.FILE_COLUM_NUM_EPICBELONGS]);
            Subtask subtask = new Subtask(name, description, TaskState.valueOf(state), epicsList.get(epicBelongs),
                    startTime, duration);
            taskId = id;
            super.createSubtask(subtask);
            taskId = Math.max(taskId, lastTaskId);
            return subtask;
        }
        return null;
    }

    @Override
    public Task createTask(Task task) {
        Task createdTask = super.createTask(task);
        try {
            save();
        } catch (ManagerSaveException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
        return createdTask;
    }

    @Override
    public Epic createEpic(Epic epic) {
        Epic createdEpic = super.createEpic(epic);
        try {
            save();
        } catch (ManagerSaveException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
        return createdEpic;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        Subtask createdSubtask = super.createSubtask(subtask);
        try {
            save();
        } catch (ManagerSaveException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
        return createdSubtask;
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        try {
            save();
        } catch (ManagerSaveException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    @Override
    public void deleteAllSubTasks() {
        super.deleteAllSubTasks();
        try {
            save();
        } catch (ManagerSaveException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        try {
            save();
        } catch (ManagerSaveException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    @Override
    public Task getTaskById(Integer id) {
        Task task = super.getTaskById(id);
        try {
            save();
        } catch (ManagerSaveException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
        return task;
    }

    @Override
    public Epic getEpicById(Integer id) {
        Epic epic = super.getEpicById(id);
        try {
            save();
        } catch (ManagerSaveException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
        return epic;
    }

    @Override
    public Subtask getSubtaskById(Integer id) {
        Subtask subtask = super.getSubtaskById(id);
        try {
            save();
        } catch (ManagerSaveException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
        return subtask;
    }

    @Override
    public void removeTaskById(Integer id) {
        super.removeTaskById(id);
        try {
            save();
        } catch (ManagerSaveException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    public void removeEpicById(Integer id) {
        super.removeEpicById(id);
        try {
            save();
        } catch (ManagerSaveException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    @Override
    public void removeSubtaskById(Integer id) {
        super.removeSubtaskById(id);
        try {
            save();
        } catch (ManagerSaveException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    public void updateTask(Task task) {
        super.updateTask(task);
        try {
            save();
        } catch (ManagerSaveException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        try {
            save();
        } catch (ManagerSaveException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        try {
            save();
        } catch (ManagerSaveException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }
}

