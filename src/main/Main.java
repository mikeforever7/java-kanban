package main;

import manager.FileBackedTaskManager;
import manager.TaskManager;
import model.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        Path path = Paths.get("c:\\Users\\user\\IdeaProjects\\java-kanban\\taskManager.csv");
        FileBackedTaskManager taskManager = new FileBackedTaskManager(path.toFile());
        // Доп задание к этому ФЗ в классе FileBackedTaskManager
        // Здесь не стал удалять, чтобы периодически смотреть как работает программа
        // Создаем задачи.
        taskManager.addTask("Задача один", "Создана");
        taskManager.addTask("Задача Два", "Создана");
        taskManager.addEpic("Первый эпик", "С ID 3");
        taskManager.addEpic("Второй эпик", "С ID 4");
        taskManager.addSubtask("Первая подзадача c ID 5", "Для эпика с ID 3", 3);
        taskManager.addSubtask("Вторая подзадача c ID 6", "Для эпика с ID 3", 3);
        taskManager.addSubtask("Третья подзадача прям новая c ID 7", "Для эпика с ID 3", 3);
        // Вызываем задачи в различном порядке и выводим историю без повторов
        taskManager.getTask(2);
        taskManager.getTask(1);
        taskManager.getSubtask(7);
        taskManager.getSubtask(6);
        printHistory(taskManager);
        taskManager.getTask(1);
        printHistory(taskManager);
        taskManager.getSubtask(5);
        printHistory(taskManager);
        taskManager.getSubtask(6);
        printHistory(taskManager);
        taskManager.getEpic(4);
        printHistory(taskManager);
        // Удаляем задачу и проверяем, что и в истории ее нет
        taskManager.deleteTaskById(1);
        printHistory(taskManager);
        // Удаляем эпик с тремя подзадачами и проверяем, что в истории они также удалены
        taskManager.deleteAllEpics();
        printHistory(taskManager);
    }

    private static void printHistory(TaskManager taskManager) {
        List<Task> history = taskManager.getHistory();
        if (history.isEmpty()) {
            System.out.println("История просмотров пуста.");
            return;
        }
        System.out.println("История просмотров:");
        System.out.println(history.size());
        int index = 1;
        for (Task task : history) {
            System.out.println(index + ". " + task);
            index++;
        }
    }
}
