package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EpicTest {
    @Test
    public void epicsMustBeEqualIfIdEqual(){
        Epic epic = new Epic(2, "Первая задача", "эпик", TaskStatus.NEW);
        Epic epic2 = new Epic(2, "Первя задача", "эпик", TaskStatus.NEW);
        assertEquals(epic, epic2, "Ошибка, задачи должны быть равны, если равен их ID");
    }
}