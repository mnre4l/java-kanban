package test;

import model.Epic;
import model.Subtask;
import model.TaskState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Тест модели - эпик")
class EpicTest {
    Epic epic = null;
    @BeforeEach
    void createEpic() {
        epic = new Epic("itIsEpic", "itIsEpicDescr");
    }

    @Test
    @DisplayName("Проверка добавление сабтаска к эпику")
    void addSubTask() {
        Subtask subtask = new Subtask("subname", "subdescr", TaskState.IN_PROGRESS, epic);

        assertNotNull(epic.getSubTasksList());
        assertEquals(epic.getSubTasksList().size(), 1, "Размер списка сохраненных сабтасков неверный");
        assertTrue(epic.getSubTasksList().contains(subtask), "Некорректно добавляется сабтаск в " +
                "список эпика");
    }

    @Test
    @DisplayName("Проверка удаления сабтаска из эпика")
    void deleteSubTask() {
        Subtask subtask = new Subtask("subname", "subdescr", TaskState.IN_PROGRESS, epic);

        assertNotNull(epic.getSubTasksList());
        assertEquals(epic.getSubTasksList().size(), 1, "Число сабтасков неверное");
        epic.deleteSubTask(subtask);
        assertNotNull(epic.getSubTasksList());
        assertFalse(epic.getSubTasksList().contains(subtask), "Некорректно удаляется сабтаск");
    }

    @Test
    @DisplayName("Получение списка эпиков")
    void getSubTasksList() {
        Subtask subtask = new Subtask("subname", "subdescr", TaskState.IN_PROGRESS, epic);

        assertNotNull(epic.getSubTasksList());
        assertEquals(epic.getSubTasksList().size(), 1, "Число сабтасков неверное");
        assertEquals(new ArrayList<>(List.of(subtask)), epic.getSubTasksList());
    }
}