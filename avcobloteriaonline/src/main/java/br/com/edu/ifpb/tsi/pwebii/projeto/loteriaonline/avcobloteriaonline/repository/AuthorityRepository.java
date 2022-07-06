package br.com.edu.ifpb.tsi.pwebii.projeto.loteriaonline.avcobloteriaonline.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.edu.ifpb.tsi.pwebii.projeto.loteriaonline.avcobloteriaonline.model.Authority;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, Integer> {

    public Authority findByAuthority(String authorityString);

}