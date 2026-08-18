package com.edu.taskmanager.controllers;

import com.edu.taskmanager.model.Task;
import com.edu.taskmanager.model.TaskPriority;
import com.edu.taskmanager.model.TaskStatus;
import com.edu.taskmanager.services.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private static final Logger logger = LogManager.getLogger(TaskController.class);
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

 

    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Map<String, String> request) {
        try {
            String title = request.get("title");
            String description = request.get("description");
            TaskPriority priority = TaskPriority.valueOf(request.getOrDefault("priority", "MEDIUM").toUpperCase());
            LocalDateTime deadline = null;
            if (request.containsKey("deadline")) {
                deadline = LocalDateTime.parse(request.get("deadline"), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            }

            Task task = taskService.createTask(title, description, priority, deadline);
            return new ResponseEntity<>(task, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Error creating task", e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks() {
        return ResponseEntity.ok(taskService.getAllTasks());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable String id) {
        Task task = taskService.getTaskById(id);
        if (task != null) {
            return ResponseEntity.ok(task);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Task> updateTaskStatus(@PathVariable String id, @RequestBody Map<String, String> request) {
        try {
            TaskStatus status = TaskStatus.valueOf(request.get("status").toUpperCase());
            Task task = taskService.updateTaskStatus(id, status);
            if (task != null) {
                return ResponseEntity.ok(task);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable String id) {
        taskService.deleteTask(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

  

    @Operation(summary = "Process tasks in parallel", 
               description = "Параллельная обработка списка задач: проверка дедлайнов и изменение статусов")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tasks processed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping("/process-parallel")
    public ResponseEntity<List<Task>> processTasksParallel(@RequestBody Map<String, List<String>> request) {
        List<String> taskIds = request.get("taskIds");
        if (taskIds == null || taskIds.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        logger.info("Starting parallel processing of {} tasks", taskIds.size());
        List<Task> processedTasks = taskService.processTasksParallel(taskIds);
        return ResponseEntity.ok(processedTasks);
    }

    @Operation(summary = "Create multiple tasks in parallel",
               description = "Параллельное создание нескольких задач из списка")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tasks created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping("/create-parallel")
    public ResponseEntity<List<Task>> createTasksParallel(@RequestBody List<Map<String, String>> requests) {
        if (requests == null || requests.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        List<Task> tasksToCreate = requests.stream().map(req -> {
            String title = req.get("title");
            String description = req.get("description");
            TaskPriority priority = TaskPriority.valueOf(req.getOrDefault("priority", "MEDIUM").toUpperCase());
            LocalDateTime deadline = null;
            if (req.containsKey("deadline")) {
                deadline = LocalDateTime.parse(req.get("deadline"), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            }
            return new Task(title, description, priority, deadline);
        }).toList();

        logger.info("Starting parallel creation of {} tasks", tasksToCreate.size());
        List<Task> createdTasks = taskService.createTasksParallel(tasksToCreate);
        return new ResponseEntity<>(createdTasks, HttpStatus.CREATED);
    }
}