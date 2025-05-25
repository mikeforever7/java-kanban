package manager;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;

import java.util.List;
import java.util.Map;

public interface TaskManager {

    List<Task> getHistory();

    //Проверка выполнения подзадач
    TaskStatus checkEpicStatus(int epicId);

    //Обновление задач
    void updateTask(int id, String newName, String newDescription, TaskStatus newStatus);

    void updateEpic(int id, String newName, String newDescription);

    void updateSubtask(int id, String newName, String newDescription, TaskStatus newStatus);

    //Получить по id
    Task getTask(int id);

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

    Map<Integer, Task> getTasks();
}
