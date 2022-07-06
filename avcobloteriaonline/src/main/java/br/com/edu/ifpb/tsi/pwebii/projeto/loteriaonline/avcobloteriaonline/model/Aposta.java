package br.com.edu.ifpb.tsi.pwebii.projeto.loteriaonline.avcobloteriaonline.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
// import java.util.List;
import java.util.Set;
// import java.util.TreeSet;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
// import javax.validation.constraints.DecimalMax;
// import javax.validation.constraints.DecimalMin;
import javax.persistence.Transient;

import org.springframework.format.annotation.NumberFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Aposta  implements Serializable{
    
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /*@NumberFormat(pattern = "0")
    @DecimalMin(value = "6", message = "Uma aposta não pode ter menos que 6 dezenas")
    @DecimalMax(value = "10", message = "Uma aposta não pode ter mais que 10 dezenas")
    private Integer qtdDezenas;*/

    @ElementCollection(fetch = FetchType.LAZY)
    private Set<Integer> numeros = new HashSet<Integer>();;

    @NumberFormat(pattern = "###,###,##0.00")
    private BigDecimal preco = BigDecimal.ZERO;

    private boolean ehFavorita = false;

    @ManyToOne
    private Sorteio sorteio;

    @Transient
	private Integer numeroSorteio;

    @ManyToOne
    @JoinColumn(name = "idcliente")
    private Cliente cliente;

    public void addNumeros(Integer numero){
        this.numeros.add(numero);
    }
}
