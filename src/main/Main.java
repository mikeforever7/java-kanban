package main;

import manager.Managers;
import manager.TaskManager;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();
        //Дополнительное задание:
        // Создаем задачи.
        taskManager.addTask("Задача один", "Создана");
        taskManager.addTask("Задача Два", "Создана");
        taskManager.addEpic("Первый эпик", "С ID 3");
        taskManager.addEpic("Второй эпик", "С ID 4");
        taskManager.addSubtask("Первая подзадача c ID 5", "Для эпика с ID 3", 3);
        taskManager.addSubtask("Вторая подзадача c ID 6", "Для эпика с ID 3", 3);
        taskManager.addSubtask("Третья подзадача c ID 7", "Для эпика с ID 3", 3);
        // Вызываем задачи в различном порядке и выводим историю без повторов
        taskManager.getTask(2);
        taskManager.getTask(1);
        taskManager.getSubtask(7);
        taskManager.getSubtask(6);
        taskManager.printHistory();
        taskManager.getTask(1);
        taskManager.printHistory();
        taskManager.getSubtask(5);
        taskManager.printHistory();
        taskManager.getSubtask(6);
        taskManager.printHistory();
        taskManager.getEpic(4);
        taskManager.printHistory();
        // Удаляем задачу и проверяем, что и в истории ее нет
        taskManager.deleteTaskById(1);
        taskManager.printHistory();
        // Удаляем эпик с тремя подзадачами и проверяем, что в истории они также удалены
        taskManager.deleteAllEpics();
        taskManager.printHistory();
    }
}
