package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.models.Notification;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.services.NotificationService;
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
    public ModelAndView getNotifications(@RequestParam(defaultValue = "1") int pageNum,
                                 @ModelAttribute("loggedUser") User loggedUser) {
        final int pageSize = 10;

        ModelAndView mav = new ModelAndView("notifications");
        final List<Notification> notifications = notificationService.getUserNotifications(loggedUser.getId(), pageNum, pageSize);

        mav.addObject("notifications", notifications);
        mav.addObject("unreadCount", notificationService.getUnreadCount(loggedUser.getId()));
        mav.addObject("showNext", notifications.size() == pageSize);
        mav.addObject("showPrevious", pageNum > 1);
        mav.addObject("pageNum", pageNum);
        return mav;
    }
    
    @RequestMapping("/{id:\\d+}/read")
    @ResponseBody
    public ModelAndView markAsRead(@PathVariable Long id, @ModelAttribute("loggedUser") User loggedUser) {
        notificationService.markAsRead(id);
        loggedUser.setUnreadNotificationCount(notificationService.getUnreadCount(loggedUser.getId()));
        return new ModelAndView("redirect:/notifications");
    }
    
    @RequestMapping("/read-all")
    @ResponseBody
    public ModelAndView markAllAsRead(@ModelAttribute("loggedUser") User loggedUser) {
        loggedUser.setUnreadNotificationCount(0);
        notificationService.markAllAsRead(loggedUser.getId());
        return new ModelAndView("redirect:/notifications");
    }
}