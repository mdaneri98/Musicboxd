package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.models.Notification;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.services.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/notifications")
public class NotificationController {
    private final NotificationService notificationService;
    
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }
    
    @RequestMapping
    public ModelAndView getNotifications(@RequestParam(defaultValue = "1") int page,
                                 @RequestParam(defaultValue = "10") int pageSize,
                                 @ModelAttribute("loggedUser") User loggedUser) {
        ModelAndView mav = new ModelAndView("notifications");
        final List<Notification> notifications = notificationService.getUserNotifications(loggedUser.getId(), page, pageSize);
        mav.addObject("notifications", notifications);
        mav.addObject("unreadCount", notificationService.getUnreadCount(loggedUser.getId()));
        return mav;
    }
    
    @RequestMapping("/{id:\\d+}/read")
    @ResponseBody
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }
    
    @RequestMapping("/read-all")
    @ResponseBody
    public ResponseEntity<Void> markAllAsRead(@ModelAttribute("loggedUser") User loggedUser) {
        notificationService.markAllAsRead(loggedUser.getId());
        return ResponseEntity.ok().build();
    }
}