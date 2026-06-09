package br.com.ccgl.sunharvestbackend.repository;

import br.com.ccgl.sunharvestbackend.entity.Farm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FarmRepository extends JpaRepository<Farm, Long> {
    List<Farm> findByUserEmail(String email);
    Optional<Farm> findByIdAndUserEmail(Long id, String email);
}
