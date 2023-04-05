public class Main {

    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();

        Epic firstEpic = new Epic("1st epic", "1st epic descr");
        manager.createEpic(firstEpic);

        Subtask firstSubOfFirstEpic = new Subtask("1st sub of 1st epic", "1st sub 1st epic descr",
                TaskState.NEW, firstEpic);
        Subtask secondSubOfFirstEpic = new Subtask("2d sub of 1st epic", "2d sub 1st epic descr",
                TaskState.NEW, firstEpic);
        Subtask thirdSubOfFirstEpic = new Subtask("3d sub of 1st epic", "3d sub 1st epic descr",
                TaskState.NEW, firstEpic);
        manager.createSubtask(firstSubOfFirstEpic);
        manager.createSubtask(secondSubOfFirstEpic);
        manager.createSubtask(thirdSubOfFirstEpic);

        Epic secondEpic = new Epic("2d epic", "2d epic descr");
        manager.createEpic(secondEpic);

        System.out.println("1 история");
        System.out.println(manager.getHistoryList());

        manager.getEpicById(0);
        System.out.println("2я история, запрашивали эпик айди=0");
        System.out.println(manager.getHistoryList());

        manager.getEpicById(4);
        manager.getSubtaskById(2);
        manager.getEpicById(0);
        System.out.println("3я история, запрашивали эпик айди=4, сабтаск айди=2, эпик айди=0");
        System.out.println(manager.getHistoryList());

        manager.removeSubtaskById(2);
        System.out.println("удалили сабтаск айди=2");
        System.out.println("История:");
        System.out.println(manager.getHistoryList());

        manager.removeEpicById(0);
        System.out.println("удалили эпик айди=0, у которого осталось 2 сабтаска");
        System.out.println("История:");
        System.out.println(manager.getHistoryList());

    }
}
