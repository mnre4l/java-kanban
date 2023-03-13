import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final static int HISTORY_LIST_SIZE = 10;
    private final List<Task> historyList = new ArrayList<>();

    @Override
    public List<Task> getHistoryList() {
        return historyList;
    }

    @Override
    public void addTask(Task task) {
        historyList.add(task);
        if (historyList.size() <= HISTORY_LIST_SIZE) {
        } else {
            historyList.remove(0);
        }
    }

}
