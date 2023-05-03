package test;

import model.Epic;
import model.Subtask;
import model.TaskState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    Epic epic = null;
    @BeforeEach
    void createEpic() {
        epic = new Epic("itIsEpic", "itIsEpicDescr");
    }

    @Test
    void addSubTask() {
        Subtask subtask = new Subtask("subname", "subdescr", TaskState.IN_PROGRESS, epic);
        epic.addSubTask(subtask);
        assertTrue(epic.getSubTasksList().contains(subtask), "Некорректно добавляется сабтаск в " +
                "список эпика");
    }

    @Test
    void deleteSubTask() {
        Subtask subtask = new Subtask("subname", "subdescr", TaskState.IN_PROGRESS, epic);
        epic.deleteSubTask(subtask);
        assertTrue(!epic.getSubTasksList().contains(subtask), "Некорректно удаляется сабтаск");
    }

    @Test
    void getSubTasksList() {
        Subtask subtask = new Subtask("subname", "subdescr", TaskState.IN_PROGRESS, epic);
        assertEquals(new ArrayList<Subtask>(List.of(subtask)), epic.getSubTasksList());
    }
}