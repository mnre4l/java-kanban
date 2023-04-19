package service;

import java.nio.file.Path;

public abstract class Managers {
    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static FileBackedTasksManager getFileManager(Path file) {
        try {
            return new FileBackedTasksManager(file);
        } catch (ManagerSaveException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
        return null;
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
