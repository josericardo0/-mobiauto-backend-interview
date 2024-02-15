package dto;

import enums.Funcoes;
import model.RevendaVeiculos;
import model.Usuario;

import java.util.HashSet;
import java.util.Set;

public class UsuarioDTO {
    private String nome;
    private String email;
    private String senha;
    private Funcoes funcoes;

    public String login;

    public void setLogin(String login) {
        this.login = login;
    }

    private Set<RevendaVeiculos> revendaVeiculos = new HashSet<>();

    public UsuarioDTO(Usuario usuario) {
    }

    public Usuario toEntity() {
        Usuario entity = new Usuario();
        entity.setNome(this.nome);
        entity.setEmail(this.email);
        entity.setFuncoes(this.funcoes);
        entity.setSenha(this.senha);
        entity.setRevendaVeiculos(this.revendaVeiculos);
        return entity;
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

    public String getLogin() {
        return null;
    }
}
