package test;

import model.Epic;
import model.Subtask;
import model.TaskState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

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
    void testToString() {
        assertEquals("500100,SUBTASK,subtaskname,IN_PROGRESS,subdescr,2023-06-05T02:00:00Z,30," +
                "2023-06-05T02:30:00Z,100500", subtask.toString(), "Некорректный " +
                "перевод сабтаска в строку");
    }

    @Test
    void getBelongsToEpicId() {
        assertEquals(100500, subtask.getBelongsToEpicId());
    }
}