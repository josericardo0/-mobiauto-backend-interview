package controller;

import model.RevendaVeiculos;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import service.RevendaVeiculosService;
import org.springframework.data.domain.Pageable;
import java.util.Arrays;
import java.util.Optional;
import exceptionhandler.NegocioException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@ExtendWith(MockitoExtension.class)
public class RevendaControllerTest {

    @Mock
    private RevendaVeiculosService revendaVeiculosService;

    @InjectMocks
    private RevendaController revendaController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = standaloneSetup(revendaController).build();
    }

    @Test
    void buscarRevendaPorId_bemSucedido_retornaRevendaPeloId() throws Exception {
        RevendaVeiculos revenda = new RevendaVeiculos();
        given(revendaVeiculosService.listarRevendasPorId(1L)).willReturn(Optional.of(revenda));

        mockMvc.perform(get("/revendas/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void cadastrarRevenda_retornaRevendaCadastrada() throws Exception {
        RevendaVeiculos revendaToSave = new RevendaVeiculos();
        RevendaVeiculos savedRevenda = new RevendaVeiculos();

        given(revendaVeiculosService.cadastroDeRevenda(any(RevendaVeiculos.class))).willReturn(savedRevenda);

        mockMvc.perform(post("/revendas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(revendaToSave)))
                .andExpect(status().isCreated());
    }

    @Test
    void buscarTodasRevendas_retornaTodasAsRevendas() throws Exception {
        Page<RevendaVeiculos> revendasPage = new PageImpl<>(Arrays.asList(new RevendaVeiculos(), new RevendaVeiculos()));
        given(revendaVeiculosService.listarRevendas(any(Pageable.class))).willReturn(revendasPage);

        mockMvc.perform(get("/revendas")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void excluirRevenda_bemSucedido_retornaSemConteudo() throws Exception {
        Long revendaId = 1L;
        RevendaVeiculos revendaVeiculos = new RevendaVeiculos();
        revendaVeiculos.setId(revendaId);

        doNothing().when(revendaVeiculosService).deletarRevendasVeiculos(revendaVeiculos);

        mockMvc.perform(delete("/revendas/{id}", revendaId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        Mockito.verify(revendaVeiculosService).deletarRevendasVeiculos(revendaVeiculos);
    }


    @Test
    void alterarRevenda_semExistir_retornaNotFound() throws Exception {
        Long revendaId = 1L;
        RevendaVeiculos revendaToUpdate = new RevendaVeiculos();

        given(revendaVeiculosService.modificarRevenda(eq(revendaId), any(RevendaVeiculos.class))).willThrow(NegocioException.class);

        mockMvc.perform(put("/revendas/{id}", revendaId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(revendaToUpdate)))
                .andExpect(status().isNotFound());
    }
    @Test
    void alterarRevenda_QuandoEncontrada_RetornaRevendaAtualizada() throws Exception {
        Long revendaId = 1L;
        RevendaVeiculos revendaToUpdate = new RevendaVeiculos();
        RevendaVeiculos updatedRevenda = new RevendaVeiculos();

        given(revendaVeiculosService.modificarRevenda(eq(revendaId), any(RevendaVeiculos.class))).willReturn(updatedRevenda);

        mockMvc.perform(put("/revendas/{id}", revendaId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(revendaToUpdate)))
                .andExpect(status().isOk());
    }





}
