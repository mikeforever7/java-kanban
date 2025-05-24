package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    public void tasksMustBeEqualIfIdEqual(){
        Task task = new Task(2, "Первая задача", "проверка", TaskStatus.NEW);
        Task task2 = new Task(2, "Вторая задача", "проверка", TaskStatus.DONE);
        assertEquals(task, task2, "Ошибка, задачи должны быть равны, если равен их ID");
    }
}