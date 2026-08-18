package com.edu.taskmanager.services;

import com.edu.taskmanager.model.Task;
import com.edu.taskmanager.model.TaskPriority;
import com.edu.taskmanager.model.TaskStatus;
import com.edu.taskmanager.repository.TaskRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.IntStream;

@Service
public class TaskService {
    private static final Logger logger = LogManager.getLogger(TaskService.class);
    private final TaskRepository taskRepository;
    private final ForkJoinPool forkJoinPool;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
        this.forkJoinPool = new ForkJoinPool();
    }

    // Обычное создание задачи
    public Task createTask(String title, String description, TaskPriority priority, LocalDateTime deadline) {
        Task task = new Task(title, description, priority, deadline);
        return taskRepository.save(task);
    }

    // Получить все задачи
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    // Получить задачу по ID
    public Task getTaskById(String id) {
        return taskRepository.findById(id).orElse(null);
    }

    // Обновить статус задачи
    public Task updateTaskStatus(String id, TaskStatus status) {
        Task task = taskRepository.findById(id).orElse(null);
        if (task != null) {
            task.setStatus(status);
            taskRepository.save(task);
        }
        return task;
    }

    // Удалить задачу
    public void deleteTask(String id) {
        taskRepository.deleteById(id);
    }



    // Параллельная обработка списка задач (изменение статуса и проверка дедлайнов)
    public List<Task> processTasksParallel(List<String> taskIds) {
        long startTime = System.currentTimeMillis();

        List<Task> result = forkJoinPool.submit(() ->
                IntStream.range(0, taskIds.size())
                        .parallel()
                        .mapToObj(i -> {
                            String id = taskIds.get(i);
                            Task task = taskRepository.findById(id).orElse(null);
                            if (task != null) {
                                logger.info("Processing task {} in thread {}", 
                                        task.getTitle(), Thread.currentThread().getName());
                                
                                // Проверка дедлайна
                                if (task.getDeadline() != null && 
                                    LocalDateTime.now().isAfter(task.getDeadline()) &&
                                    task.getStatus() != TaskStatus.COMPLETED) {
                                    task.setStatus(TaskStatus.OVERDUE);
                                    logger.info("Task {} marked as OVERDUE", task.getTitle());
                                }
                                
                                // Если задача PENDING, переводим в IN_PROGRESS
                                if (task.getStatus() == TaskStatus.PENDING) {
                                    task.setStatus(TaskStatus.IN_PROGRESS);
                                    // Симуляция обработки задачи
                                    try {
                                        Thread.sleep(500); // Имитация работы
                                    } catch (InterruptedException e) {
                                        Thread.currentThread().interrupt();
                                    }
                                    task.setStatus(TaskStatus.COMPLETED);
                                    logger.info("Task {} completed", task.getTitle());
                                }
                                
                                taskRepository.save(task);
                                return task;
                            }
                            return null;
                        })
                        .toList()
        ).join();

        long endTime = System.currentTimeMillis();
        logger.info("Parallel processing of {} tasks took {} ms", taskIds.size(), (endTime - startTime));
        return result;
    }

    // Параллельное создание нескольких задач
    public List<Task> createTasksParallel(List<Task> tasksToCreate) {
        long startTime = System.currentTimeMillis();

        List<Task> result = forkJoinPool.submit(() ->
                IntStream.range(0, tasksToCreate.size())
                        .parallel()
                        .mapToObj(i -> {
                            Task task = tasksToCreate.get(i);
                            logger.info("Creating task {} in thread {}", 
                                    task.getTitle(), Thread.currentThread().getName());
                            try {
                                Thread.sleep(100); // Имитация валидации
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                            return taskRepository.save(task);
                        })
                        .toList()
        ).join();

        long endTime = System.currentTimeMillis();
        logger.info("Parallel creation of {} tasks took {} ms", tasksToCreate.size(), (endTime - startTime));
        return result;
    }
}