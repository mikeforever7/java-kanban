package manager;

import model.*;
import java.util.HashMap;

public class TaskManager {

    private static int counter = 0;
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();

    //Проверка выполнения подзадач
    public TaskStatus checkEpicStatus(int epicId) {
        Epic epic = getEpic(epicId);
        for (Subtask subtask : epic.getSubtasksInEpic()) {
            if (!subtask.getStatus().equals(TaskStatus.NEW)) {
                for (Subtask otherSubtask : epic.getSubtasksInEpic()) {
                    if (!otherSubtask.getStatus().equals(TaskStatus.DONE)){
                        return TaskStatus.IN_PROGRESS;
                    }
                }
                return TaskStatus.DONE;
            }
        }
        return TaskStatus.NEW;
    }

    //Обновление задач
    public void updateTask(int id, String newName, String newDescription, TaskStatus newStatus) {
        Task task = tasks.get(id);
        task.setName(newName);
        task.setDescription(newDescription);
        task.setStatus(newStatus);
    }

    public void updateEpic(int id, String newName, String newDescription) {
        Epic epic = epics.get(id);
        epic.setName(newName);
        epic.setDescription(newDescription);
    }

    public void updateSubtask(int id, String newName, String newDescription, TaskStatus newStatus) {
        Subtask subtask = subtasks.get(id);
        subtask.setName(newName);
        subtask.setDescription(newDescription);
        subtask.setStatus(newStatus);
        Epic epic = getEpic(subtask.getEpicId());
        epic.setStatus(checkEpicStatus(subtask.getEpicId()));
    }

    //Получить по id
    public Task getTask(int id) {
        return tasks.get(id);
    }

    public Epic getEpic(int id) {
        return epics.get(id);
    }

    public Subtask getSubtask(int id) {
        return subtasks.get(id);
    }

    //Добавление задач
    public void addTask(String name, String description) {
        getID();
        Task task = new Task(counter ,name, description);
        tasks.put(counter,task);
    }

    public void addEpic(String name, String description) {
        getID();
        Epic epic = new Epic(counter, name, description);
        epics.put(counter,epic);
    }

    public void addSubtask(String name, String description, int epicId) {
        getID();
        Subtask subtask = new Subtask(counter, name, description, epicId);
        subtasks.put(counter,subtask);
        Epic epic = getEpic(epicId);
        epic.getSubtasksInEpic().add(subtask);
        epic.setStatus(checkEpicStatus(subtask.getEpicId()));
    }

    //Очистка списков
    public void deleteAllTasks() {
        tasks.clear();
    }

    public void deleteAllEpics() {
        epics.clear();
    }

    public void deleteAllSubtasks() {
        subtasks.clear();
    }

    private static void getID() {
        counter++;
    }

    //Удаление по id
    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    public void deleteEpicById(int id) {
        epics.remove(id);
    }

    public void deleteSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        Epic epic = getEpic(subtask.getEpicId());
        int epicId = subtask.getEpicId();
        subtasks.remove(id);
        epic.getSubtasksInEpic().remove(subtask);
        epic.setStatus(checkEpicStatus(epicId));
    }

    //Печать списков задач
    public void printTasks() {
        for (Integer task : tasks.keySet()) {
            System.out.println("model.Task " + task + ", Задача " + tasks.get(task));
        }
    }

    public void printEpics() {
        for (Integer epic : epics.keySet()) {
            System.out.println("model.Epic " + epic + ", Задача " + epics.get(epic));
        }
    }

    public void printSubtasks() {
        for (Integer subtask : subtasks.keySet()) {
            System.out.println("model.Subtask " + subtask + ", Задача " + subtasks.get(subtask));
        }
    }

    //Печать списка подзадач в эпике
    public void printSubtasksByEpic(int epicId) {
        Epic epic = getEpic(epicId);
        for (Subtask subtask: epic.getSubtasksInEpic()) {
            System.out.println(subtask);
        }
    }
}
