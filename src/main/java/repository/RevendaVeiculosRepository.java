package repository;

import model.RevendaVeiculos;
import org.springframework.data.jpa.repository.JpaRepository;


public interface RevendaVeiculosRepository extends JpaRepository<RevendaVeiculos,Long> {
    boolean existsByCnpj(String cnpj);
}
