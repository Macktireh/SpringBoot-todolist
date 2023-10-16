package com.asynclearning.todolist.gateway;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.asynclearning.todolist.domain.Label;
import com.asynclearning.todolist.domain.Task;
import com.asynclearning.todolist.entity.LabelEntity;
import com.asynclearning.todolist.entity.TaskEntity;
import com.asynclearning.todolist.entity.TaskListEntity;
import com.asynclearning.todolist.repository.LabelRepository;
import com.asynclearning.todolist.repository.TaskRepository;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;

@Service
public class TaskGatewayImpl implements TaskGatewayInterface {

    private final TaskListGatewayInterface taskListGatewayInterface;

    private final TaskRepository taskRepository;

    private final LabelRepository labelRepository;

    public TaskGatewayImpl(TaskListGatewayInterface taskListGatewayInterface, TaskRepository taskRepository,
            LabelRepository labelRepository) {

        this.taskListGatewayInterface = taskListGatewayInterface;
        this.taskRepository = taskRepository;
        this.labelRepository = labelRepository;

    }

    /**
     * Creates a new task list.
     *
     * @param task the task to be added to the list
     * @return the created task
     */
    @Override
    public Task createTaskList(Task task) {

        if (this.taskRepository.findByTitle(task.getTitle()).isPresent()) {
            throw new EntityExistsException("Task already exists");
        }

        return this.entityToDomain(this.taskRepository.save(domainToEntity(task)));

    }

    @Override
    public List<Task> getAllTaskLists() {

        return this.taskRepository.findAll().stream().map((taskEntity) -> this.entityToDomain(taskEntity)).toList();

    }

    /**
     * Retrieves a Task by its unique identifier.
     *
     * @param id the unique identifier of the Task
     * @return the Task with the specified id
     */
    @Override
    public Task getTaskListById(Long id) {

        return this.entityToDomain(this.taskRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Task not found")));

    }

    /**
     * Updates a task in the task list with the given ID.
     *
     * @param id   the ID of the task to be updated
     * @param task the updated task object with new values
     */
    @Override
    public void updateTaskList(Long id, Task task) {

        TaskEntity taskEntity = this.taskRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Task not found"));

        List<LabelEntity> labels = task.getLabels().stream()
                .map((label) -> this.labelRepository.findByName(label.getName())
                        .orElseThrow(() -> new EntityNotFoundException(label.getName() + " Label not found")))
                .toList();

        taskEntity.setTitle(task.getTitle());
        taskEntity.setDescription(task.getDescription());
        taskEntity.setDueDate(task.getDueDate());
        taskEntity.setStatus(task.getStatus());
        taskEntity.setPriority(task.getPriority());
        taskEntity.setUpdatedAt(LocalDateTime.now());
        taskEntity.setLabel(labels);

        this.taskRepository.save(taskEntity);

    }

    /**
     * Deletes a task list with the specified ID.
     *
     * @param id the ID of the task list to delete
     */
    @Override
    public void deleteTaskList(Long id) {

        this.taskRepository.deleteById(id);

    }

    /**
     * Creates a new label.
     *
     * @param label the label to be created
     * @return the created label
     */
    @Override
    public Label createLabel(Label label) {

        if (this.labelRepository.findByName(label.getName()).isPresent()) {
            throw new EntityExistsException("Label already exists");
        }

        if (this.labelRepository.findByColor(label.getColor()).isPresent()) {
            throw new EntityExistsException("Label color already exists");
        }

        LabelEntity labelEntity = this.labelRepository.save(new LabelEntity(label.getName(), label.getColor(), LocalDateTime.now()));
        
        return new Label(labelEntity.getName(), labelEntity.getColor());

    }

    /**
     * Retrieves all labels from the label repository and maps them to a list of
     * Label objects.
     *
     * @return a list of Label objects representing all the labels
     */
    @Override
    public List<Label> getAllLabels() {

        return this.labelRepository.findAll().stream().map((labelEntity) -> new Label(labelEntity.getName(), labelEntity.getColor())).toList();

    }

    /**
     * Adds a label to a task.
     *
     * @param taskId the ID of the task
     * @param name   the name of the label
     */
    @Override
    public void addLabelToTask(Long taskId, String name) {

        TaskEntity taskEntity = this.taskRepository.findById(taskId).orElseThrow(() -> new EntityNotFoundException("Task not found"));
        
        LabelEntity labelEntity = this.labelRepository.findByName(name).orElseThrow(() -> new EntityNotFoundException("Label not found"));
        
        taskEntity.getLabel().add(labelEntity);
        
        this.taskRepository.save(taskEntity);

    }

    /**
     * Converts a Task object from the domain model to a TaskEntity object for
     * persistence.
     *
     * @param task the Task object to be converted
     * @return the converted TaskEntity object
     */
    private TaskEntity domainToEntity(Task task) {

        List<LabelEntity> labels = task.getLabels().stream()
                .map((label) -> this.labelRepository.findByName(label.getName())
                        .orElseThrow(() -> new EntityNotFoundException(label.getName() + " Label not found")))
                .toList();

        TaskListEntity taskList = this.taskListGatewayInterface.getTaskListByIdJPA(task.getTaskList().getId());

        return new TaskEntity(
                task.getTitle(),
                task.getDescription(),
                task.getDueDate(),
                task.getStatus(),
                task.getPriority(),
                task.getUpdatedAt(),
                taskList,
                labels);
    }

    /**
     * Converts a TaskEntity object to a Task object.
     *
     * @param taskEntity the TaskEntity object to be converted
     * @return the converted Task object
     */
    private Task entityToDomain(TaskEntity taskEntity) {

        List<Label> labels = taskEntity.getLabel().stream().map((label) -> new Label(label.getName(), label.getColor())).toList();

        return new Task(
                taskEntity.getId(),
                taskEntity.getTitle(),
                taskEntity.getDescription(),
                taskEntity.getDueDate(),
                taskEntity.getStatus(),
                taskEntity.getPriority(),
                this.taskListGatewayInterface.entityToDomain(taskEntity.getTaskList()),
                taskEntity.getUpdatedAt(),
                labels);
    }

}
