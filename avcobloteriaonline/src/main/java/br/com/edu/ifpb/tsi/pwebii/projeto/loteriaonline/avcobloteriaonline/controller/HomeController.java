package br.com.edu.ifpb.tsi.pwebii.projeto.loteriaonline.avcobloteriaonline.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;


@Controller
public class HomeController {

    @RequestMapping("/home")
    public ModelAndView showHomePage(ModelAndView mav) {
        mav.setViewName("index");
        mav.addObject("menu", "home");
        return mav;
        
    }

}
