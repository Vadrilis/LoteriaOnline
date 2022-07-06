package br.com.edu.ifpb.tsi.pwebii.projeto.loteriaonline.avcobloteriaonline.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

// import br.com.edu.ifpb.tsi.pwebii.projeto.loteriaonline.avcobloteriaonline.model.Aposta;
import br.com.edu.ifpb.tsi.pwebii.projeto.loteriaonline.avcobloteriaonline.model.Sorteio;

@Repository
public interface SorteioRepository extends JpaRepository<Sorteio, Integer>{

    public Optional<Sorteio> findById(Integer id);
    
    public Sorteio findTopByOrderByDataHoraSorteioDesc(); //max

    public Sorteio findByDataHoraSorteioGreaterThan(LocalDate dataComHora);

    public List<Sorteio> findByEstadoTrue(); //todos os sorteios realizados

    public List<Sorteio> findByEstadoFalse(); //todos os sorteios ainda nao realizados

    @Query("SELECT S FROM Sorteio S JOIN FETCH S.apostasRealizadas AR JOIN FETCH AR.cliente C JOIN FETCH C.user U WHERE U.username = :USERNAME AND S.estado = 1")
    Optional<List<Sorteio>> findByUserAndByEstadoTrue(@Param("USERNAME") String username);

    @Query("SELECT S FROM Sorteio S JOIN FETCH S.apostasRealizadas AR JOIN FETCH AR.cliente C JOIN FETCH C.user U WHERE U.username = :USERNAME AND S.estado = 0")
    Optional<List<Sorteio>> findByUserAndByEstadoFalse(@Param("USERNAME") String username);
    
}
