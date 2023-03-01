public class Main {

    public static void main(String[] args) {
        TaskManager manager = new TaskManager();

        Task firstTestTask = new Task("Создать задачу", "Создаем задачу потестировать",
                "NEW");
        manager.createTask(firstTestTask);
        Task secondTestTask = new Task("Создать еще задачу", "Создаем еще задачу " +
                        "потестировать", "NEW");
        manager.createTask(secondTestTask);

        System.out.println(firstTestTask);
        System.out.println(secondTestTask);

        Epic firstEpicTask = new Epic("Сдать 3й спринт", "Сделать работу и сдать");
        manager.createEpic(firstEpicTask);
        Subtask firstSubToFirstEpic = new Subtask("Cделать работу и прислать",
                "ну на ревью", "NEW", firstEpicTask);
        manager.createSubtask(firstSubToFirstEpic);
        Subtask secondSubToFirstEpic = new Subtask("Все переделать xD", "После ревью",
                "NEW", firstEpicTask);
        manager.createSubtask(secondSubToFirstEpic);

        Epic secondEpic = new Epic("Придумать второй эпик", "а то уже сложно");
        manager.createEpic(secondEpic);
        Subtask firstSubToSecondEpic = new Subtask("Придумать суб", "а то тоже сложно",
                "NEW", secondEpic);
        manager.createSubtask(firstSubToSecondEpic);

        System.out.println(firstEpicTask);
        System.out.println(firstSubToFirstEpic);
        System.out.println(secondSubToFirstEpic);


        System.out.println(secondEpic);
        System.out.println(firstSubToSecondEpic);


        System.out.println("Epics list: " + manager.getEpicsList());
        System.out.println("Subtasks list: " + manager.getSubtasksList());
        System.out.println("Tasks list: " + manager.getTasksList());

        System.out.println("firstSubToSecondEpic subtask ID: " + firstSubToSecondEpic.getID());

        firstTestTask.setTaskState("IN_PROGRESS");
        manager.updateTask(firstTestTask);

        firstSubToFirstEpic.setTaskState("DONE");
        manager.updateSubtask(secondSubToFirstEpic);
        secondSubToFirstEpic.setTaskState("DONE");
        manager.updateSubtask(secondSubToFirstEpic);

        secondEpic.setTaskDescription("изменили описание");
        manager.updateEpic(secondEpic);

        System.out.println("updated:");
        System.out.println("Epics list: " + manager.getEpicsList());
        System.out.println("Subtasks list: " + manager.getSubtasksList());
        System.out.println("Tasks list: " + manager.getTasksList());

        System.out.println("удаляем 2ю субтаск из 1 эпика по айди");
        Integer id = 1;
        manager.removeSubtaskFromId(id);
        System.out.println(manager.getSubtasksList());

        System.out.println("удалили все эпики");
        manager.deleteAllEpics();
        System.out.println(manager.getSubtasksList());
        System.out.println(manager.getEpicsList());
    }
}
