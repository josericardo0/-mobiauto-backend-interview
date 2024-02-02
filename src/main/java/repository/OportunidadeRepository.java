package repository;

import enums.Status;
import model.Oportunidades;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OportunidadeRepository extends JpaRepository <Oportunidades,Long>{
}
