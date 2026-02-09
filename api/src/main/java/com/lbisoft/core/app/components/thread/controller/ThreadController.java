package com.lbisoft.core.app.components.thread.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lbisoft.core.app.components.thread.model.MessageThread;
import com.lbisoft.core.app.components.thread.model.ThreadCreateForm;
import com.lbisoft.core.app.components.thread.model.ThreadMessage;
import com.lbisoft.core.app.components.thread.model.ThreadMessageAttachment;
import com.lbisoft.core.app.components.thread.service.ThreadGetService;
import com.lbisoft.core.app.components.thread.service.ThreadMessageAttachmentService;
import com.lbisoft.core.app.components.thread.service.ThreadMessageSaveService;
import com.lbisoft.core.app.components.thread.service.ThreadReadStatusSaveService;
import com.lbisoft.core.app.components.thread.service.ThreadSaveService;
import com.lbisoft.core.app.components.useractivities.ActivityTypes;
import com.lbisoft.core.app.components.useractivities.service.UserActivitiesService;
import com.lbisoft.core.framework.useractivities.UserActivityBuilder;
import com.lbisoft.core.framework.utils.RequestHelper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("api/threads/")
@Tag(name = "Threads")
@PreAuthorize("hasAuthority('CAN_READ_THREADS')")
public class ThreadController {
  static final String THREADS = "Threads";

  @Autowired
  private ThreadGetService threadGetService;
  @Autowired
  private ThreadSaveService threadSaveService;
  @Autowired
  private ThreadMessageSaveService messageSaveService;
  @Autowired
  private ThreadReadStatusSaveService threadReadStatusSaveService;
  @Autowired
  private ThreadMessageAttachmentService attachmentService;
  @Autowired
  private UserActivitiesService activitiesService;

  @GetMapping("/{threadId}")
  @Operation(summary = "Get a thread by id", tags = { THREADS })
  public MessageThread getThread(@PathVariable("threadId") Integer threadId, HttpServletRequest request) {
    activitiesService.logActivityById(ActivityTypes.LOAD_THREAD, threadId, request);
    return threadGetService.getThreadById(threadId, RequestHelper.getUser(request)).orElse(null);
  }

  @GetMapping("/by-reference/{referenceTypeLk}/{referenceId}")
  @Operation(summary = "Get threads by reference", tags = { THREADS })
  public List<MessageThread> getThreadsByReference(@PathVariable("referenceTypeLk") String referenceTypeLk,
      @PathVariable("referenceId") Integer referenceId, HttpServletRequest request) {
    activitiesService.logActivity(new UserActivityBuilder(ActivityTypes.LOAD_THREADS_FOR_REFERENCE)
        .relatedString(referenceTypeLk).relatedId(referenceId).build(), request);
    return threadGetService.getThreadsForReference(referenceTypeLk, referenceId, RequestHelper.getUser(request));
  }

  @GetMapping("/mine")
  @Operation(summary = "Get threads for current user", tags = { THREADS })
  public List<MessageThread> getThreadsForParticipant(HttpServletRequest request) {
    var user = RequestHelper.getUser(request);
    activitiesService.logActivityById(ActivityTypes.LOAD_THREADS_FOR_PARTICIPANT, user.getUserId(), request);
    return threadGetService.getThreadsForParticipant(user);
  }

  @PostMapping("/")
  @Operation(summary = "Create a new thread", tags = { THREADS })
  public MessageThread createThread(@RequestBody ThreadCreateForm form, HttpServletRequest request) {
    activitiesService.logActivity(new UserActivityBuilder(ActivityTypes.CREATE_THREAD).relatedObject(form).build(),
        request);
    return threadSaveService.create(form, RequestHelper.getUser(request));
  }

  @PostMapping("/{threadId}")
  @Operation(summary = "Post a message to the thread", tags = { THREADS })
  public ThreadMessage postMessage(@PathVariable("threadId") Integer threadId, @RequestBody ThreadMessage message,
      HttpServletRequest request) {
    activitiesService.logActivity(
        new UserActivityBuilder(ActivityTypes.POST_THREAD_MESSAGE).relatedObject(message).build(),
        request);
    return messageSaveService.post(message, threadId, RequestHelper.getUser(request));
  }

  @PostMapping("/{threadId}/mark-read/{messageId}")
  @Operation(summary = "Mark a message as read", tags = { THREADS })
  public void markMessageAsRead(@PathVariable("threadId") Integer threadId,
      @PathVariable("messageId") Integer messageId, HttpServletRequest request) {
    threadReadStatusSaveService.setLastReadMessageId(threadId, messageId, RequestHelper.getUser(request));
  }

  @PostMapping("/upload")
  @Operation(summary = "Upload an attachment", tags = { THREADS })
  public Integer uploadAttachment(@RequestBody ThreadMessageAttachment attachment, HttpServletRequest request) {
    return attachmentService.save(attachment, RequestHelper.getUser(request));
  }

  @GetMapping("/download/{threadMessageAttachmentId}")
  @Operation(summary = "Download an attachment", tags = { THREADS })
  public void downloadAttachment(@PathVariable("threadMessageAttachmentId") Integer threadMessageAttachmentId,
      HttpServletRequest request, HttpServletResponse response) {
    attachmentService.sendAttachment(threadMessageAttachmentId, response, request, RequestHelper.getUser(request));
  }

}
