package br.com.edu.ifpb.tsi.pwebii.projeto.loteriaonline.avcobloteriaonline.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.edu.ifpb.tsi.pwebii.projeto.loteriaonline.avcobloteriaonline.model.Cliente;

@Repository
public interface ClienteRepository  extends JpaRepository<Cliente, Integer>{
    
    @Query("SELECT C FROM Cliente C JOIN FETCH C.user U WHERE U.username = :USERNAME")
	Optional<Cliente> findByUser(@Param("USERNAME") String username);
}
