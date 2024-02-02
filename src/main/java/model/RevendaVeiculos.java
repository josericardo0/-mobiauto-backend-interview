package model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.List;
import org.hibernate.validator.constraints.br.CNPJ;
@Table(name = "revenda_veiculos")
@Entity
public class RevendaVeiculos {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CNPJ
    @NotBlank
    @Column(unique = true)
    private String cnpj;

    @NotBlank
    private String nomeSocial;

    @OneToMany(mappedBy = "revenda_veiculo", cascade = CascadeType.ALL)
    private List<Oportunidades> oportunidades;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getNomeSocial() {
        return nomeSocial;
    }

    public void setNomeSocial(String nomeSocial) {
        this.nomeSocial = nomeSocial;
    }

    public List<Oportunidades> getOportunidades() {
        return oportunidades;
    }

    public void setOportunidades(List<Oportunidades> oportunidades) {
        this.oportunidades = oportunidades;
    }
}
