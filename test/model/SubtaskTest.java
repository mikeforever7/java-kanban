package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {

    @Test
    public void subtasksMustBeEqualIfIdEqual(){
        Subtask subtask = new Subtask(2, "Первая задача", "эпик", TaskStatus.NEW, 3);
        Subtask subtask2 = new Subtask(2, "Еще подзадача", "эпик", TaskStatus.NEW, 3);
        assertEquals(subtask, subtask2, "Ошибка, задачи должны быть равны, если равен их ID");
    }
}