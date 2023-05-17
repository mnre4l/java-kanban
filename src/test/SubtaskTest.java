package test;

import model.Epic;
import model.Subtask;
import model.TaskState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Тест модели сабтаск")
class SubtaskTest {
    Epic epic;
    Subtask subtask;

    @BeforeEach
    void setUp() {
        epic = new Epic("itIsEpic", "itIsEpicDescr");
        epic.setTaskId(100500);
        subtask = new Subtask("subtaskname", "subdescr", TaskState.IN_PROGRESS, epic,
                Instant.parse("2023-06-05T02:00:00Z"), 30);
        subtask.setTaskId(500100);
    }

    @Test
    @DisplayName("Тест строкового представления для сериализации")
    void testToString() {
        assertEquals("500100,SUBTASK,subtaskname,IN_PROGRESS,subdescr,2023-06-05T02:00:00Z,30," +
                "2023-06-05T02:30:00Z,100500", subtask.toString(), "Некорректный " +
                "перевод сабтаска в строку");
    }

    @Test
    @DisplayName("Тест проверки на наличие эпика ")
    void getBelongsToEpicId() {
        assertEquals(100500, subtask.getEpicBelongsId());
    }
}