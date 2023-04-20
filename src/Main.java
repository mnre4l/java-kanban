import model.*;
import service.*;

import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) {

            FileBackedTasksManager manager = Managers.getFileManager(Paths.get("taskmanager.csv"));

            Task task1 = manager.createTask(new Task("1st task", "1st task descr", TaskState.NEW));
            Epic epic1 = manager.createEpic(new Epic("1st epic", "1st epic descr"));
            Epic epic2 = manager.createEpic(new Epic("2d epic", "2d epic descr"));

            manager.getEpicById(2);
            manager.getTaskById(0);

            Subtask sub1 = manager.createSubtask(new Subtask("1st sub", "1st sub descr",
                                                TaskState.NEW, epic1));

            manager.getSubtaskById(3);
            System.out.println("История до: " + manager.getHistoryList());
            Epic epic3 = manager.createEpic(new Epic("3d epic", "3d epic descr"));
            FileBackedTasksManager manager2 = FileBackedTasksManager.loadFromFile(Paths.get("taskmanager.csv"));
            System.out.println(manager2.getEpicsList());
            System.out.println(manager2.getSubtasksList());
            System.out.println(manager2.getTasksList());
            System.out.println("История после: " + manager2.getHistoryList());
    }
}
