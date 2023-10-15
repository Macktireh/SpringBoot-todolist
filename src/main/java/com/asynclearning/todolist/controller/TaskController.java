package com.asynclearning.todolist.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.List;

import com.asynclearning.todolist.DTO.LabelDTO;
import com.asynclearning.todolist.DTO.TaskRequestDTO;
import com.asynclearning.todolist.DTO.TaskResponseDTO;
import com.asynclearning.todolist.service.TaskServiceInterface;

@RestController
@RequestMapping("/task")
public class TaskController {

    private final TaskServiceInterface taskServiceInterface;

    public TaskController(TaskServiceInterface taskServiceInterface) {
        this.taskServiceInterface = taskServiceInterface;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    public TaskResponseDTO create(@RequestBody TaskRequestDTO taskRequestDTO) {
        return taskServiceInterface.createTaskList(taskRequestDTO);
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public List<TaskResponseDTO> getAll() {
        return taskServiceInterface.getAllTaskLists();
    }

    @GetMapping(path = "/{id}", produces = APPLICATION_JSON_VALUE)
    public TaskResponseDTO getById(@PathVariable Long id) {
        return taskServiceInterface.getTaskListById(id);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping(path = "/{id}", consumes = APPLICATION_JSON_VALUE)
    public void update(@PathVariable Long id, @RequestBody TaskRequestDTO taskRequestDTO) {
        taskServiceInterface.updateTaskList(id, taskRequestDTO);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(path = "/{id}")
    public void delete(@PathVariable Long id) {
        taskServiceInterface.deleteTaskList(id);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(path = "/labels", consumes = APPLICATION_JSON_VALUE)
    public LabelDTO createLabel(@RequestBody LabelDTO labelDTO) {
        return taskServiceInterface.createLabel(labelDTO);
    }

    @GetMapping(path = "/labels", produces = APPLICATION_JSON_VALUE)
    public List<LabelDTO> getLabels() {
        return taskServiceInterface.getAllLabels();
    }

    @PostMapping(path = "/{taskId}/add-label", consumes = APPLICATION_JSON_VALUE)
    public void addLabelToTask(@PathVariable Long taskId, @PathVariable String labelName) {
        taskServiceInterface.addLabelToTask(taskId, labelName);
    }

}
