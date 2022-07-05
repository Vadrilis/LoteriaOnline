package br.com.edu.ifpb.tsi.pwebii.projeto.loteriaonline.avcobloteriaonline.model;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum PrecoAposta {
    SEIS_DEZENAS(6, BigDecimal.valueOf(3.00)), 
    SETE_DEZENAS(7, BigDecimal.valueOf(15.00)), 
    OITO_DEZENAS(8, BigDecimal.valueOf(90.00)), 
    NOVE_DEZENAS(9, BigDecimal.valueOf(300.00)), 
    DEZ_DEZENAS(10, BigDecimal.valueOf(1200.00)); 

    private static Map<Integer, PrecoAposta> BY_QUANTITY = new HashMap<>();
    
    static {
        for (PrecoAposta e : values()) {
            BY_QUANTITY.put(e.qtdDezenas, e);
        }
    }

    public Integer qtdDezenas;
    public BigDecimal preco;

    public static PrecoAposta valueOfQtdDezenas(Integer qtdDezenas) {
        return BY_QUANTITY.get(qtdDezenas);
    }



}
