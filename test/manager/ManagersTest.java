package manager;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {
    TaskManager taskManager;
    InMemoryHistoryManager historyManager;

    @Test
    public void notNull_WhenTaskManagerCreated() {
        taskManager = Managers.getDefault();
        assertNotNull(taskManager);
    }

    @Test
    public void notNull_WhenHistoryManagerCreated() {
        historyManager = Managers.getDefaultHistory();
        assertNotNull(historyManager);
    }

}