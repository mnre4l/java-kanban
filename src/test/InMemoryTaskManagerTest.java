package test;

import org.junit.jupiter.api.BeforeEach;
import service.InMemoryTaskManager;
import service.Managers;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    void setUp() {
        manager = Managers.getInMemoryManager();
    }
}