package manager;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;

import java.util.List;
import java.util.Map;
import java.util.TreeSet;

public interface TaskManager {

    List<Task> getHistory();

    //Проверка выполнения подзадач
    TaskStatus checkEpicStatus(int epicId);

    //Вычисление и установка временных параметров у эпика
    void setEpicDurationAndTime(int epicId);

    //Обновление задач
    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    //Получить по id
    Task getTask(int id);

    Epic getEpic(int id);

    Subtask getSubtask(int id);

    //Добавление задач
    void addTask(Task task);

    void addEpic(Epic epic);

    void addSubtask(Subtask subtask);

    //Очистка списков
    void deleteAllTasks();

    void deleteAllEpics();

    void deleteAllSubtasks();

    //Удаление по id
    void deleteTaskById(int id);

    void deleteEpicById(int id);

    void deleteSubtaskById(int id);

    Map<Integer, Task> getTasks();

    Map<Integer, Epic> getEpics();

    Map<Integer, Subtask> getSubtasks();

    TreeSet<Task> getPrioritizedTasks();
}
