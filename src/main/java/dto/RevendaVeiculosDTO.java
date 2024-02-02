package dto;

import model.Oportunidades;
import model.RevendaVeiculos;

import java.util.List;

public class RevendaVeiculosDTO {

    private String cnpj;
    private String nomeSocial;
    private List<Oportunidades> oportunidades;

    public RevendaVeiculosDTO(RevendaVeiculos entity) {
        this.cnpj = entity.getCnpj();
        this.nomeSocial = entity.getNomeSocial();
        this.oportunidades = entity.getOportunidades();
    }


}
