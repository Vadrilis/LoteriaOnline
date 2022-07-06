package br.com.edu.ifpb.tsi.pwebii.projeto.loteriaonline.avcobloteriaonline.controller;

// import java.io.IOException;
import java.security.Principal;
// import java.util.List;
// import java.util.Optional;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.ModelAttribute;
// import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.bind.annotation.ResponseBody;
// import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// import br.com.edu.ifpb.tsi.pwebii.projeto.loteriaonline.avcobloteriaonline.model.Aposta;
import br.com.edu.ifpb.tsi.pwebii.projeto.loteriaonline.avcobloteriaonline.model.Cliente;
import br.com.edu.ifpb.tsi.pwebii.projeto.loteriaonline.avcobloteriaonline.model.User;
// import br.com.edu.ifpb.tsi.pwebii.projeto.loteriaonline.avcobloteriaonline.repository.ApostaRepository;
import br.com.edu.ifpb.tsi.pwebii.projeto.loteriaonline.avcobloteriaonline.repository.ClienteRepository;
import br.com.edu.ifpb.tsi.pwebii.projeto.loteriaonline.avcobloteriaonline.repository.SorteioRepository;
import br.com.edu.ifpb.tsi.pwebii.projeto.loteriaonline.avcobloteriaonline.repository.UserRepository;


import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import br.com.edu.ifpb.tsi.pwebii.projeto.loteriaonline.avcobloteriaonline.model.Authority;
// import br.com.edu.ifpb.tsi.pwebii.projeto.loteriaonline.avcobloteriaonline.model.User;
import br.com.edu.ifpb.tsi.pwebii.projeto.loteriaonline.avcobloteriaonline.model.Authority.AuthorityId;
import br.com.edu.ifpb.tsi.pwebii.projeto.loteriaonline.avcobloteriaonline.repository.AuthorityRepository;
// import br.com.edu.ifpb.tsi.pwebii.projeto.loteriaonline.avcobloteriaonline.repository.UserRepository;


@Controller
@RequestMapping("/clientes")
public class ClienteController {
    
    @Autowired
    ClienteRepository clienteRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    AuthorityRepository authorityRepository;

    @Autowired
    SorteioRepository sorteioRepository;
    

    private User formaUser(String nome, String senha){
        User usuario = new User();
        usuario.setUsername(nome);
        usuario.setPassword(new BCryptPasswordEncoder().encode(senha));
        usuario.setEnabled(true);

        return userRepository.save(usuario);
    }

    private Authority formaAuthority(User usuario, String role) {
        AuthorityId authorityId = new AuthorityId();
        authorityId.setUsername(usuario.getUsername());
        authorityId.setAuthority(role);
        
        Authority authority = new Authority();
        authority.setId(authorityId);
        authority.setAuthority(role);
        authority.setUsername(usuario);

        return authorityRepository.save(authority);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView getForm(Cliente cliente, ModelAndView mav) {
        mav.addObject("cliente", cliente);
        mav.setViewName("/clientes/form");
        return mav;
    }

    @RequestMapping(method = RequestMethod.POST)
    @Transactional
    public ModelAndView save(@Valid Cliente cliente, BindingResult validation, ModelAndView mav, RedirectAttributes attrs, @RequestParam(value="controlads", required=false) boolean controlads) {
        
        if (validation.hasErrors()) {
            mav.setViewName("/clientes/form");
            return mav;
        } else {
            if (!controlads){
                controlads = false;
            }

            System.out.println(controlads);

            cliente.setControlador(controlads);
            String role = controlads ? "ROLE_ADMIN" : "ROLE_CLIENTE";
            User usuarin = formaUser(cliente.getLogin(), cliente.getSenha());
            formaAuthority(usuarin, role);

            cliente.setUser(usuarin);

            System.out.println(clienteRepository.save(cliente));

            mav.setViewName("redirect:/auth");
            attrs.addFlashAttribute("mensagem", "Cliente cadastrado com sucesso!");
            
            return mav;
        }
        
    }

    @RequestMapping("/sorteiosativosporusuario")
    public ModelAndView getSorteiosAtivosPorUsuaio(Principal auth, ModelAndView mav) {
        mav.addObject("sorteiosativosporusuario", sorteioRepository.findByUserAndByEstadoTrue(auth.getName()).get());
        mav.setViewName("clientes/telinhaclienteapostador");
        return mav;
    }

    
}
