package ar.edu.itba.paw.webapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;


@RequestMapping("/error")
@Controller()
public class ErrorController {

    @RequestMapping("/404")
    public ModelAndView notFound() {
        return new ModelAndView("errors/404");
    }

    @RequestMapping("/500")
    public ModelAndView general() {
        return new ModelAndView("errors/500");
    }


}
