package model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import enums.Funcoes;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome_usuarios")
    @NotEmpty(message = "{campo.obrigatorio}")
    private String nome;

    @Column(name = "email", unique = true)
    @Email
    private String email;

    @Column(name = "senha")
    @JsonIgnore
    @NotEmpty(message = "{campo.obrigatorio}")
    private String senha;

    @Enumerated(EnumType.STRING)
    private Funcoes funcoes;

    @ManyToMany
    @JoinTable(
            name = "revenda_veiculo_usuario",
            joinColumns = @JoinColumn(name = "id_usuario"),
            inverseJoinColumns = @JoinColumn(name = "id_revenda"))
    private Set<RevendaVeiculos> revendaVeiculos = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Funcoes getFuncoes() {
        return funcoes;
    }

    public void setFuncoes(Funcoes funcoes) {
        this.funcoes = funcoes;
    }

    public Set<RevendaVeiculos> getRevendaVeiculos() {
        return revendaVeiculos;
    }

    public void setRevendaVeiculos(Set<RevendaVeiculos> revendaVeiculos) {
        this.revendaVeiculos = revendaVeiculos;
    }
}

