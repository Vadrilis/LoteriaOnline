package br.com.edu.ifpb.tsi.pwebii.projeto.loteriaonline.avcobloteriaonline.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
// import java.util.HashSet;
import java.util.List;
// import java.util.Set;
// import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
// import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.br.CPF;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.NumberFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Cliente implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotEmpty(message = "O nome é obrigatório!")
    private String nome;

    @Past(message = "Por favor, informe uma data válida!")
    @NotNull(message = "Data de nascimento é obrigatório!")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate dataNascimento;

    @CPF
    @NotBlank(message = "CPF é obrigatório!")
    @Digits(integer = 11, fraction = 0, message = "Informe um CPF válido")
    private String cpf;

    /* @Lob
    private byte[] imagem; */

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    private List<Aposta> apostas = new ArrayList<Aposta>();

    @Transient
    private String login;
    
    @Transient
    private String senha;

    @OneToOne
    @JoinColumn(name = "username")
    private User user;
    
    @NumberFormat(pattern = "###,###,###,##0.00")
    private BigDecimal ganhos = BigDecimal.valueOf(0.00); 

    @NumberFormat(pattern = "###,###,###,##0.00")
    private BigDecimal despesas = BigDecimal.valueOf(0.00); 

    private boolean controlador = false;

    public void addAposta(Aposta aposta) {
        if (aposta != null) {
            this.apostas.add(aposta);
        }
    }

}
