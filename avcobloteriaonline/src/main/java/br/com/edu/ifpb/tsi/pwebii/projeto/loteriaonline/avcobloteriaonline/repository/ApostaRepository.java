package br.com.edu.ifpb.tsi.pwebii.projeto.loteriaonline.avcobloteriaonline.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.edu.ifpb.tsi.pwebii.projeto.loteriaonline.avcobloteriaonline.model.Aposta;
// import br.com.edu.ifpb.tsi.pwebii.projeto.loteriaonline.avcobloteriaonline.model.Cliente;

@Repository
public interface ApostaRepository  extends JpaRepository<Aposta, Integer>{

    @Query("SELECT A FROM Aposta A JOIN FETCH A.cliente AA JOIN FETCH AA.user U WHERE U.username = :USERNAME AND A.ehFavorita = 1")
    Optional<List<Aposta>> findByClienteAndEhFavoritaTrue(@Param("USERNAME") String username);

    @Query("SELECT A FROM Aposta A JOIN FETCH A.cliente AA JOIN FETCH AA.user U WHERE U.username = :USERNAME")
    Optional<List<Aposta>> findByCliente(@Param("USERNAME") String username);

    

}

