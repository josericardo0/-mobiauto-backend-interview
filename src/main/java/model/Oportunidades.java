package model;

import enums.Status;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import jakarta.persistence.*;


@Entity
@Table(name = "oportunidades")
public class Oportunidades {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Status status;

    private String motivoConclusao;

    @ManyToOne
    @JoinColumn(name = "id_cliente")
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "id_veiculo")
    private Veiculo veiculo;

    @ManyToOne
    @JoinColumn(name = "id_revenda")
    private RevendaVeiculos revendaVeiculos;

    @ManyToOne
    @JoinColumn(name = "id_atendimento")
    private Usuario atendimento;

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDateTime dataAtribuicao;

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDateTime dataCriacao;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getMotivoConclusao() {
        return motivoConclusao;
    }

    public void setMotivoConclusao(String motivoConclusao) {
        this.motivoConclusao = motivoConclusao;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Veiculo getVeiculo() {
        return veiculo;
    }

    public void setVeiculo(Veiculo veiculo) {
        this.veiculo = veiculo;
    }

    public RevendaVeiculos getRevendaVeiculos() {
        return revendaVeiculos;
    }

    public void setRevendaVeiculos(RevendaVeiculos revendaVeiculos) {
        this.revendaVeiculos = revendaVeiculos;
    }

    public Usuario getAtendimento() {
        return atendimento;
    }

    public void setAtendimento(Usuario atendimento) {
        this.atendimento = atendimento;
    }

    public LocalDateTime getDataAtribuicao() {
        return dataAtribuicao;
    }

    public void setDataAtribuicao(LocalDateTime dataAtribuicao) {
        this.dataAtribuicao = dataAtribuicao;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }
}
