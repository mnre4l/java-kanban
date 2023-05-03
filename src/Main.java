import model.*;
import service.*;

import java.nio.file.Paths;
import java.time.Instant;

public class Main {

    public static void main(String[] args) {

            FileBackedTasksManager manager = Managers.getFileManager(Paths.get("taskmanager.csv"));

            Task task1 = manager.createTask(new Task("1st task", "1st task descr", TaskState.NEW,
                    Instant.parse("2023-06-05T00:00:00Z"), 60));
            Task task2 = manager.createTask(new Task("2d task", "2d task descr", TaskState.NEW));
            Task task3 = manager.createTask(new Task("3d task", "3d task descr", TaskState.NEW));
            Task task4 = manager.createTask(new Task("4st task", "4st task descr", TaskState.NEW,
                    Instant.parse("2023-06-05T01:00:00Z"), 60));

            Epic epic1 = manager.createEpic(new Epic("1st epic", "1st epic descr"));
            Epic epic2 = manager.createEpic(new Epic("2d epic", "2d epic descr"));

            Subtask sub1 = manager.createSubtask(new Subtask("1st sub", "1st sub descr",
                    TaskState.NEW, epic1,Instant.parse("2023-06-05T02:00:00Z"), 30));
            Subtask sub2 = manager.createSubtask(new Subtask("2d sub", "2d sub descr",
                    TaskState.NEW, epic2));

            System.out.println("Prioritized:");
            System.out.println(manager.getPrioritizedTasks());
    }
}
