package main;

import manager.InMemoryTaskManager;
import manager.TaskManager;
import model.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class Main {

    public static void main(String[] args) {
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
