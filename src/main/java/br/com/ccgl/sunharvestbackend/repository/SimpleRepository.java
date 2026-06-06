package br.com.ccgl.sunharvestbackend.repository;

import br.com.ccgl.sunharvestbackend.entity.SimpleObject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SimpleRepository extends JpaRepository<SimpleObject, Integer> {
    SimpleObject getSimpleObjectById(Long id);
}
