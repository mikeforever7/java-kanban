package manager;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;

public interface TaskManager {

    ArrayList<Task> getHistory();

    //Проверка выполнения подзадач
    TaskStatus checkEpicStatus(int epicId);

    //Обновление задач
    void updateTask(int id, String newName, String newDescription, TaskStatus newStatus);

    void updateEpic(int id, String newName, String newDescription);

    void updateSubtask(int id, String newName, String newDescription, TaskStatus newStatus);

    //Получить по id
    <T> Task getTask(int id);

    Epic getEpic(int id);

    Subtask getSubtask(int id);

    //Добавление задач
    void addTask(String name, String description);

    void addEpic(String name, String description);

    void addSubtask(String name, String description, int epicId);

    //Очистка списков
    void deleteAllTasks();

    void deleteAllEpics();

    void deleteAllSubtasks();

    //Удаление по id
    void deleteTaskById(int id);

    void deleteEpicById(int id);

    void deleteSubtaskById(int id);

    //Печать списков задач
    void printTasks();

    void printEpics();

    void printSubtasks();

    //Печать списка подзадач в эпике
    void printSubtasksByEpic(int epicId);

    HashMap<Integer, Task> getTasks();
}
