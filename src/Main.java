public class Main {

    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();




        Task firstTestTask = new Task("Создать задачу", "Создаем задачу потестировать",
                TaskState.NEW);
        manager.createTask(firstTestTask);
        Task secondTestTask = new Task("Создать еще задачу", "Создаем еще задачу " +
                "потестировать", TaskState.NEW);
        manager.createTask(secondTestTask);

        System.out.println(firstTestTask);
        System.out.println(secondTestTask);

        Epic firstEpicTask = new Epic("Сдать 3й спринт", "Сделать работу и сдать");
        manager.createEpic(firstEpicTask);
        Subtask firstSubToFirstEpic = new Subtask("Cделать работу и прислать",
                "ну на ревью", TaskState.NEW, firstEpicTask);
        manager.createSubtask(firstSubToFirstEpic);
        Subtask secondSubToFirstEpic = new Subtask("Все переделать xD", "После ревью",
                TaskState.NEW, firstEpicTask);
        manager.createSubtask(secondSubToFirstEpic);

        Epic secondEpic = new Epic("Придумать второй эпик", "а то уже сложно");
        manager.createEpic(secondEpic);
        Subtask firstSubToSecondEpic = new Subtask("Придумать суб", "а то тоже сложно",
                TaskState.NEW, secondEpic);
        manager.createSubtask(firstSubToSecondEpic);

        System.out.println(firstEpicTask);
        System.out.println(firstSubToFirstEpic);
        System.out.println(secondSubToFirstEpic);


        System.out.println(secondEpic);
        System.out.println(firstSubToSecondEpic);


        System.out.println("Epics list: " + manager.getEpicsList());
        System.out.println("Subtasks list: " + manager.getSubtasksList());
        System.out.println("Tasks list: " + manager.getTasksList());

        System.out.println("firstSubToSecondEpic subtask ID: " + firstSubToSecondEpic.getTaskId());

        firstTestTask.setTaskState(TaskState.IN_PROGRESS);
        manager.updateTask(firstTestTask);

        firstSubToFirstEpic.setTaskState(TaskState.NEW);
        manager.updateSubtask(secondSubToFirstEpic);
        secondSubToFirstEpic.setTaskState(TaskState.IN_PROGRESS);
        manager.updateSubtask(secondSubToFirstEpic);

        secondEpic.setTaskDescription("изменили описание");
        manager.updateEpic(secondEpic);

        System.out.println("updated:");
        System.out.println("Epics list: " + manager.getEpicsList());
        System.out.println("Subtasks list: " + manager.getSubtasksList());
        System.out.println("Tasks list: " + manager.getTasksList());

        System.out.println("удаляем 2ю субтаск из 1 эпика по айди");
        Integer id = 1;
        manager.removeSubtaskById(id);
        id = 4;
        manager.removeSubtaskById(id);
        System.out.println(manager.getSubtasksList());

        System.out.println("удалили все эпики");
        System.out.println(manager.getSubtasksList());
        System.out.println(manager.getEpicsList());


        Task task1 = manager.getTaskById(0);
        System.out.println(manager.getHistoryList());
        Task task2 = manager.getEpicById(2);
        System.out.println(manager.getHistoryList());


    }
}
