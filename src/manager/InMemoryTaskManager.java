package manager;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;

public class  InMemoryTaskManager implements TaskManager {

    private int counter = 0;

    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();

    HistoryManager historyManager = new InMemoryHistoryManager();

    //Получить историю
    public ArrayList<Task> getHistory() {
        return historyManager.getHistory();
    }

    //Проверка выполнения подзадач
    @Override
    public TaskStatus checkEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
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
    @Override
    public void updateTask(int id, String newName, String newDescription, TaskStatus newStatus) {
        Task task = getTask(id);
        task.setName(newName);
        task.setDescription(newDescription);
        task.setStatus(newStatus);
    }

    @Override
    public void updateEpic(int id, String newName, String newDescription) {
        Epic epic = getEpic(id);
        epic.setName(newName);
        epic.setDescription(newDescription);
    }

    @Override
    public void updateSubtask(int id, String newName, String newDescription, TaskStatus newStatus) {
        Subtask subtask = getSubtask(id);
        subtask.setName(newName);
        subtask.setDescription(newDescription);
        subtask.setStatus(newStatus);
        Epic epic = getEpic(subtask.getEpicId());
        epic.setStatus(checkEpicStatus(subtask.getEpicId()));
    }

    //Получить по id
    @Override
    public Task getTask(int id) {
        historyManager.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public Epic getEpic(int id) {
        historyManager.add(epics.get(id));
        return epics.get(id);
    }

    @Override
    public Subtask getSubtask(int id) {
        historyManager.add(subtasks.get(id));
        return subtasks.get(id);
    }

    //Добавление задач
    @Override
    public void addTask(String name, String description) {
        getID();
        Task task = new Task(counter ,name, description);
        tasks.put(counter,task);
    }

    @Override
    public void addEpic(String name, String description) {
        getID();
        Epic epic = new Epic(counter, name, description);
        epics.put(counter,epic);
    }

    @Override
    public void addSubtask(String name, String description, int epicId) {
        getID();
        Subtask subtask = new Subtask(counter, name, description, epicId);
        subtasks.put(counter,subtask);
        Epic epic = getEpic(epicId);
        epic.getSubtasksInEpic().add(subtask);
        epic.setStatus(checkEpicStatus(subtask.getEpicId()));
    }

    //Очистка списков
    @Override
    public void deleteAllTasks() {
        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        epics.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        subtasks.clear();
        for (Integer key : epics.keySet()) {
            Epic epic = epics.get(key);
            epic.getSubtasksInEpic().clear();
            epic.setStatus(checkEpicStatus(key));
        }

    }


    private  void getID() {
        counter++;
    }

    //Удаление по id
    @Override
    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    @Override
    public void deleteEpicById(int id) {
        epics.remove(id);
    }

    @Override
    public void deleteSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        Epic epic = getEpic(subtask.getEpicId());
        int epicId = subtask.getEpicId();
        subtasks.remove(id);
        epic.getSubtasksInEpic().remove(subtask);
        epic.setStatus(checkEpicStatus(epicId));
    }

    //Печать списков задач
    @Override
    public void printTasks() {
        for (Integer task : tasks.keySet()) {
            System.out.println("Task " + task + ", Задача " + tasks.get(task));
        }
    }

    @Override
    public void printEpics() {
        for (Integer epic : epics.keySet()) {
            System.out.println("Epic " + epic + ", Задача " + epics.get(epic));
        }
    }

    @Override
    public void printSubtasks() {
        for (Integer subtask : subtasks.keySet()) {
            System.out.println("Subtask " + subtask + ", Задача " + subtasks.get(subtask));
        }
    }

    //Печать списка подзадач в эпике
    @Override
    public void printSubtasksByEpic(int epicId) {
        Epic epic = getEpic(epicId);
        for (Subtask subtask: epic.getSubtasksInEpic()) {
            System.out.println(subtask);
        }
    }

    @Override
    public HashMap<Integer, Task> getTasks() {
        return tasks;
    }
}

