package br.com.edu.ifpb.tsi.pwebii.projeto.loteriaonline.avcobloteriaonline.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.edu.ifpb.tsi.pwebii.projeto.loteriaonline.avcobloteriaonline.model.User;


@Repository
public interface UserRepository extends JpaRepository<User, String> {

    public List<User> findByEnabledTrue();

}

