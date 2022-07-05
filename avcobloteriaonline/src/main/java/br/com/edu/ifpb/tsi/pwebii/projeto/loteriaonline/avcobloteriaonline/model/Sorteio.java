package br.com.edu.ifpb.tsi.pwebii.projeto.loteriaonline.avcobloteriaonline.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;


import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.NumberFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Sorteio {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @DecimalMin(value = "1")
    private Integer numeroSorteio;

    private Boolean estado = false;

    @NumberFormat(pattern = "###,###,###,##0.00")
    private BigDecimal valorPremio = BigDecimal.ZERO;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @NotNull(message = "Campo obrigatório!")
    @Future(message = "Data deve ser futura")
    /*@Temporal(TemporalType.TIMESTAMP)*/
    private LocalDateTime dataHoraSorteio;

    @ElementCollection(fetch = FetchType.LAZY)
    private Set<Integer> dezenasSorteadas= new TreeSet<Integer>();

    @ElementCollection(fetch = FetchType.LAZY)
    @OneToMany()
    private List<Aposta> apostasRealizadas = new ArrayList<Aposta>();

    //@ElementCollection(fetch = FetchType.LAZY)
    //@OneToMany
    @Transient
    private List<Cliente> vencedores = new ArrayList<Cliente>();

    @Transient
	private Map<Integer, Aposta> vencedoresComAcertos = new TreeMap<Integer, Aposta>();

    public void sorteia(){
        Random r = new Random();

        while (dezenasSorteadas.size() < 6) {
            Integer dezena =  r.nextInt(61);
            this.dezenasSorteadas.add(dezena != 0 ? dezena : 1);  
        }

        this.setEstado(true);  
    }

    public String listaSorteados() {
        return Arrays.toString(this.dezenasSorteadas.toArray());
    }
    
    public void addApostaAoSorteio(Aposta aposta) {
        if (aposta != null) {
            this.apostasRealizadas.add(aposta);
        }
    }

    public void encontraVencedores() {
        this.vencedores.addAll(this.apostasRealizadas.stream()
                                                     .filter(a -> a.getNumeros().containsAll(this.dezenasSorteadas))
                                                     .map(aposta -> aposta.getCliente())
                                                     .collect(Collectors.toList()));     

        if (this.vencedores.isEmpty()){
            Integer contador = 5;

            while (this.vencedoresComAcertos.isEmpty() && contador>0){ 
                for (Aposta aposta : this.apostasRealizadas){
                    
                    List<Integer> apostAuxiliar = new ArrayList<Integer>();
                    apostAuxiliar.addAll(aposta.getNumeros());
                    apostAuxiliar.retainAll(this.dezenasSorteadas);

                    if (apostAuxiliar.size() == contador){
                        this.vencedoresComAcertos.put(apostAuxiliar.size(), aposta);
                    }
                }

                contador-= 1;
            } 
        }
    }

    public List<Cliente> listaVencedores(){
        return this.vencedoresComAcertos.isEmpty()? this.vencedores : this.vencedoresComAcertos.values().stream()
                                                                                                        .map(aposta -> aposta.getCliente())
                                                                                                        .collect(Collectors.toList());
    }

    public void entregaPremio(){
        if (this.vencedores.isEmpty()){ 
            BigDecimal divisao = this.valorPremio.divide(BigDecimal.valueOf(this.vencedoresComAcertos.size()));
            this.vencedoresComAcertos.values().stream()
                                                .map(aposta -> aposta.getCliente())
                                                .collect(Collectors.toList())
                                                .forEach(vencedor -> vencedor.setGanhos(vencedor.getGanhos()
                                                                                                .add(divisao)));
        } else {
            BigDecimal divisao = this.valorPremio.divide(BigDecimal.valueOf(this.vencedores.size()));
            this.vencedores.forEach(vencedor -> vencedor.setGanhos(vencedor.getGanhos().add(divisao)));
        }
    }

}
