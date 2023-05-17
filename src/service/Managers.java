package service;

import java.nio.file.Path;

public abstract class Managers {
    public static HttpTaskManager getDefault(String url) {
        try {
            return new HttpTaskManager(url);
        } catch (ManagerSaveException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public static FileBackedTasksManager getFileManager(Path file) {
        try {
            return new FileBackedTasksManager(file);
        } catch (ManagerSaveException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
        return null;
    }

    public static InMemoryTaskManager getInMemoryManager() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
