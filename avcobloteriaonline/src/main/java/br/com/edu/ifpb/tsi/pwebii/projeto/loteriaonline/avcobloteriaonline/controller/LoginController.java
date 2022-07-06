package br.com.edu.ifpb.tsi.pwebii.projeto.loteriaonline.avcobloteriaonline.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/auth")
public class LoginController {

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView getForm(ModelAndView modelAndView) {
        modelAndView.setViewName("auth/login");
        return modelAndView;
    }

}
