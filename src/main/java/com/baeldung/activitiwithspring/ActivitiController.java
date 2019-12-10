package com.baeldung.activitiwithspring;

import org.activiti.engine.impl.context.Context;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.impl.context.Context;
// import org.activiti.engine.impl.runtime.ExecutionEntity;
import org.activiti.engine.runtime.ProcessInstance;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class ActivitiController {
    private static final Logger logger = LoggerFactory.getLogger(ActivitiController.class);

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @GetMapping("/start-process")
    public String startProcess() {
		// my-process is defined in file my-process.bpmn20.xml in src\main\resources\processes
        ProcessInstance pi = runtimeService.startProcessInstanceByKey("my-process");
		String processInstanceId = pi.getId() ;
		
        logger.info("processInstanceId : " + processInstanceId);

        return "Process id " + processInstanceId + ". Process started. Number of currently running process instances = " + runtimeService.createProcessInstanceQuery()
          .count();
		  
    }

    @GetMapping("/get-tasks/{processInstanceId}")
    public List<TaskRepresentation> getTasks(@PathVariable String processInstanceId) {
        List<Task> usertasks = taskService.createTaskQuery()
          .processInstanceId(processInstanceId)
          .list();

        return usertasks.stream()
          .map(task -> new TaskRepresentation(task.getId(), task.getName(), task.getProcessInstanceId()))
          .collect(Collectors.toList());
    }

    @GetMapping("/complete-task-A/{processInstanceId}")
    public void completeTaskA(@PathVariable String processInstanceId) {
        Task task = taskService.createTaskQuery()
          .processInstanceId(processInstanceId)
          .singleResult();
        taskService.complete(task.getId());
        logger.info("Task completed");
    }
}
