package repository;

import model.RevendaVeiculos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface RevendaVeiculosRepository extends JpaRepository<RevendaVeiculos,Long> {
    boolean existsByCnpj(String cnpj);
}
