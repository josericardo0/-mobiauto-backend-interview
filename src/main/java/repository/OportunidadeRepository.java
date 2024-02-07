package repository;

import model.Oportunidades;
import org.springframework.data.jpa.repository.JpaRepository;


public interface OportunidadeRepository extends JpaRepository <Oportunidades,Long>{
}