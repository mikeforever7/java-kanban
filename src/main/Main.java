package main;

import manager.Managers;
import manager.TaskManager;
import model.TaskStatus;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();
        //Создаем
        taskManager.addTask("Первая задача", "Создана");
        taskManager.getTask(1);
        taskManager.addTask("Вторая задача", "Тоже создана");
        taskManager.addEpic("Первый эпик", "Создан");
        taskManager.addSubtask("Первая подзадача" ,"Первого эпика", 3);
        taskManager.addSubtask("Вторая подзадача", "Первого эпика", 3);
        taskManager.addEpic("Второй эпик", "Создан");
        taskManager.addSubtask("Первая подзадача", "Второго эпика", 6);
        //Печатаем
        taskManager.printTasks();
        taskManager.printEpics();
        taskManager.printSubtasks();
        //Изменяем
        taskManager.updateTask(1, "Первая задача", "В работе" , TaskStatus.IN_PROGRESS);
        taskManager.updateTask(2, "Вторая задача", "Выполнена" , TaskStatus.DONE);
        taskManager.updateEpic(3, "Первый эпик", "Изменен");
        taskManager.updateSubtask(4, "Первая подзадача первого эпика", "В процессе",
                TaskStatus.IN_PROGRESS);
        taskManager.updateSubtask(5,"Вторая подзадача первого эпика", "Выполнена",
                TaskStatus.DONE);
        taskManager.updateEpic(6, "Второй эпик", "Выполнен(так как выполнена подзадача)");
        taskManager.updateSubtask(7, "Первая подзадача", "Выполнена", TaskStatus.DONE);

        //Удаляем единственную подзадачу, статус эпика также меняется на New, так как пустой
        taskManager.updateEpic(6, "Второй эпик", "Новый, так как подзадач нет");
        taskManager.deleteSubtaskById(7);
        //Вызываем просто для истории
        taskManager.getTask(2);
        taskManager.getSubtask(5);
        taskManager.getEpic(3);
//        //Печатаем
        taskManager.printTasks();         //Задачи
        taskManager.printEpics();         //Эпики
        taskManager.printSubtasks();      //Все подзадачи
        taskManager.printSubtasksByEpic(3); //Подзадачи отдельно эпика
        taskManager.deleteAllSubtasks();
        taskManager.printSubtasks();  //Пустота после удаление всех подзадач
        taskManager.printEpics();     //Эпики с измененными статусами из-за удаления подзадач
        taskManager.getHistory();     //История просмотра
    }
}
