package br.com.edu.ifpb.tsi.pwebii.projeto.loteriaonline.avcobloteriaonline.controller;

import java.security.Principal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.edu.ifpb.tsi.pwebii.projeto.loteriaonline.avcobloteriaonline.model.Aposta;
import br.com.edu.ifpb.tsi.pwebii.projeto.loteriaonline.avcobloteriaonline.model.Cliente;
import br.com.edu.ifpb.tsi.pwebii.projeto.loteriaonline.avcobloteriaonline.model.PrecoAposta;
import br.com.edu.ifpb.tsi.pwebii.projeto.loteriaonline.avcobloteriaonline.model.Sorteio;
import br.com.edu.ifpb.tsi.pwebii.projeto.loteriaonline.avcobloteriaonline.repository.ApostaRepository;
import br.com.edu.ifpb.tsi.pwebii.projeto.loteriaonline.avcobloteriaonline.repository.ClienteRepository;
import br.com.edu.ifpb.tsi.pwebii.projeto.loteriaonline.avcobloteriaonline.repository.SorteioRepository;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;

@Controller
@RequestMapping("/apostas")
public class ApostaController {

    @Autowired
    ApostaRepository apostaRepository;

    @Autowired
    ClienteRepository clienteRepository;

    @Autowired
    SorteioRepository sorteioRepository;

    @RequestMapping(method = RequestMethod.GET)
	public ModelAndView getForm(Aposta aposta, ModelAndView mav, Principal auth) {
		mav.addObject("apostaNv", new Aposta());
		mav.addObject("apostasFavoritas", apostaRepository.findByClienteAndEhFavoritaTrue(auth.getName()).get());
        mav.addObject("sorteioNv", new Sorteio());
		mav.addObject("sorteiosAtivos", sorteioRepository.findByEstadoFalse()); // ainda não realizados
		mav.addObject("aposta", aposta);
		mav.setViewName("/apostas/form");

		return mav;
	}

	@RequestMapping(method = RequestMethod.POST)
	@Transactional
	public ModelAndView save(Aposta aposta, ModelAndView mav, Principal auth, RedirectAttributes attrs, @RequestParam(name = "cbx", required = false) String valoresdocheck) {
        
        Set<Integer> valores;
        
        if(aposta.getNumeros() == null || aposta.getNumeros().isEmpty()) {
            Set<String> valoresstring = new HashSet<>(Arrays.asList(valoresdocheck.split(",")));
            valores = valoresstring.stream().map(elemento -> Integer.valueOf(elemento)).collect(Collectors.toSet());

            aposta.setNumeros(valores);
            
        } else {
            valores = new HashSet<>(aposta.getNumeros());
            aposta.setNumeros(valores);
        }

        if(valores != null) {
            
            if(valores.size() >= 6  && valores.size() <= 10) {

                Sorteio sorteio = sorteioRepository.findById(aposta.getNumeroSorteio()).get();
                System.out.println(sorteio.getId());
                Cliente cliente = clienteRepository.findByUser(auth.getName()).get();
                System.out.println(cliente.getId());

                aposta.setSorteio(sorteio);
                aposta.setCliente(cliente);
                aposta.setPreco(PrecoAposta.valueOfQtdDezenas(valores.size()).getPreco());

                apostaRepository.save(aposta);
                
                cliente.setDespesas(cliente.getDespesas().add(PrecoAposta.valueOfQtdDezenas(valores.size()).getPreco()));
                clienteRepository.save(cliente);


                sorteio.addApostaAoSorteio(aposta);
                sorteioRepository.save(sorteio);

                mav.setViewName("redirect:/apostas/minhasapostas");
                attrs.addFlashAttribute("mensagem", "Aposta cadastrada com sucesso!");

                return mav;
            }
        }
         
        mav.setViewName("redirect:/apostas/form");
        attrs.addFlashAttribute("mensagem", "Por favor, digite no mínimo 6 valores e no máximo 10");
        return mav;
		
	}

    @RequestMapping("/minhasapostas")
	public ModelAndView listaApostas(ModelAndView mav, Principal auth) {
		mav.addObject("minhasApostas", apostaRepository.findByCliente(auth.getName()).get());
		mav.addObject("meusSorteiosAtivos", sorteioRepository.findByUserAndByEstadoFalse(auth.getName()).get());
        mav.addObject("esseUser", clienteRepository.findByUser(auth.getName()).get());
        mav.setViewName("/apostas/list");
		return mav;
	}

    @RequestMapping("/apostasfavoritas")
	public ModelAndView listaApostasFavoritas(ModelAndView mav, Principal auth) {
		mav.addObject("minhasApostas", apostaRepository.findByClienteAndEhFavoritaTrue(auth.getName()).get());
		mav.setViewName("/apostas/aposta");
		return mav;
	}
    
}
