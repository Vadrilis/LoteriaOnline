package br.com.edu.ifpb.tsi.pwebii.projeto.loteriaonline.avcobloteriaonline.controller;

// import java.security.Principal;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
// import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// import br.com.edu.ifpb.tsi.pwebii.projeto.loteriaonline.avcobloteriaonline.model.Aposta;
import br.com.edu.ifpb.tsi.pwebii.projeto.loteriaonline.avcobloteriaonline.model.Sorteio;
import br.com.edu.ifpb.tsi.pwebii.projeto.loteriaonline.avcobloteriaonline.repository.ClienteRepository;
import br.com.edu.ifpb.tsi.pwebii.projeto.loteriaonline.avcobloteriaonline.repository.SorteioRepository;


@Controller
@RequestMapping("/sorteios")
public class SorteioController {

    @Autowired
    SorteioRepository sorteioRepository;

    @Autowired
    ClienteRepository clienteRepository;
    
    @RequestMapping(method = RequestMethod.GET)
	public ModelAndView getForm(Sorteio sorteio, ModelAndView mav) {
		mav.addObject("sorteio", sorteio);
		mav.setViewName("/sorteios/form");
		return mav;
	}

    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView save(@Valid Sorteio sorteio, BindingResult validation, ModelAndView mav, RedirectAttributes attrs) {
        if (validation.hasErrors()) {
            mav.setViewName("/sorteios/form");
        } else {
            List<Sorteio> controle = sorteioRepository.findAll();  
            if (controle.isEmpty()){
                sorteioRepository.save(sorteio);
                mav.setViewName("redirect:/sorteios/sorteioaberto");
                attrs.addFlashAttribute("mensagem", "Sorteio cadastrado com sucesso!");
            } else {

                DateTimeFormatter formatador1 = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                LocalDateTime novoSorteio = sorteio.getDataHoraSorteio();
                LocalDateTime ultimoSorteio = sorteioRepository.findTopByOrderByDataHoraSorteioDesc().getDataHoraSorteio();
                

                Duration duracao = Duration.between(novoSorteio,ultimoSorteio);
                long dias = duracao.toDays();
        
                if (!(novoSorteio.isAfter(ultimoSorteio))){
                    mav.setViewName("redirect:/sorteios");
                    attrs.addFlashAttribute("mensagem", String.format("É necessário que seja posterior ao último sorteio: %s", ultimoSorteio.format(formatador1).toString()));
                    
                } else if(!(Math.abs(dias) >= 7)){
                    mav.setViewName("redirect:/sorteios");
                    attrs.addFlashAttribute("mensagem", String.format("É necessário que haja pelo menos 1 semana entre este sorteio e o último %s", ultimoSorteio.format(formatador1).toString()));
                } else{
                    sorteioRepository.save(sorteio);
                    mav.setViewName("redirect:/sorteios/sorteioaberto");
                    attrs.addFlashAttribute("mensagem", "Sorteio cadastrado com sucesso!");
                }
            }
        }
        
        return mav;
    }

    @RequestMapping("/sorteioaberto")
	public ModelAndView listarSorteiosAbertos(ModelAndView mav) {
		mav.addObject("sorteiosAbertos", sorteioRepository.findByEstadoFalse()); //Lembrar de mostrar as apostas daquele sorteio
        mav.addObject("menu", "sorteiosabertos");
		mav.setViewName("/sorteios/listabertos");
		return mav;
	}

	@RequestMapping("/sorteiotodos")
	public ModelAndView listarTodosSorteios(ModelAndView mav) {
		mav.addObject("todosSorteios", sorteioRepository.findAll());
        mav.addObject("menu", "sorteiostodos");
		mav.setViewName("/sorteios/list");
		return mav;
	}
    
	@RequestMapping(value = "/sorteando/{id}", method = RequestMethod.GET)
	public ModelAndView sortearManualmente(ModelAndView mav, @PathVariable("id") Integer id) {
		mav.addObject("sorteio", sorteioRepository.findById(id).get());
		mav.setViewName("/sorteios/sorteiomanual");

		return mav;
	}

    @RequestMapping(value = "/sorteando/{id}", method = RequestMethod.POST)
    @ResponseBody
	@Transactional
    public ModelAndView sorteando(ModelAndView mav, RedirectAttributes attrs, @PathVariable("id") Integer id, @RequestParam(value = "cbx", required = false) String numeros) {
        Set<Integer> listaDeNumeros;

        Sorteio sorteio = sorteioRepository.findById(id).get();

        if(numeros != null) {
            Set<String> listaVStr = new HashSet<>(Arrays.asList(numeros.split(",")));
            listaDeNumeros = listaVStr.stream().map(elemento -> Integer.valueOf(elemento)).collect(Collectors.toSet());

            if(listaDeNumeros.size() == 6) {
                sorteio.setDezenasSorteadas(listaDeNumeros);
            } else {
                sorteio.sorteia();
            }
        } else {
            sorteio.sorteia();
        }

        sorteio.setEstado(true);

        sorteio.encontraVencedores();
        if(sorteio.listaVencedores().isEmpty()){
            mav.setViewName("redirect:/sorteios/sorteioaberto");
            attrs.addFlashAttribute("mensagem", "O Sorteio realizado não possui vencedores");

            sorteioRepository.save(sorteio);

        } else {
            sorteio.entregaPremio();
        
            sorteioRepository.save(sorteio);

            clienteRepository.saveAll(sorteio.listaVencedores()); //update dos clientes p receberem os valores

            mav.setViewName("redirect:/sorteios/sorteio");
            attrs.addFlashAttribute("mensagem", "Sorteio realizado com sucesso!");
        }
		return mav;
	}

}
