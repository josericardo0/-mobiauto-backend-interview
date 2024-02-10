package controller;

import model.Oportunidades;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import service.OportunidadesService;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@ExtendWith(MockitoExtension.class)
public class OportunidadeControllerTest {

    @Mock
    private OportunidadesService oportunidadesService;

    @InjectMocks
    private OportunidadeController oportunidadeController;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = standaloneSetup(oportunidadeController).build();
    }

    @Test
    void listarOportunidades_retornaTodasAsOportunidades() throws Exception {
        mockMvc.perform(get("/oportunidades")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void listarOportunidadePorId_bemSucedido_retornaOportunidadePeloId() throws Exception {
        Oportunidades oportunidade = new Oportunidades();
        given(oportunidadesService.listarOportunidadesporId(1L)).willReturn(Optional.of(oportunidade));

        mockMvc.perform(get("/oportunidades/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
        ;
    }

    @Test
    void cadastrarOportunidade_retornaOportunidadeCadastrada() throws Exception {
        Oportunidades toSave = new Oportunidades();
        Oportunidades saved = new Oportunidades();

        given(oportunidadesService.cadastrarOportunidade(toSave)).willReturn(saved);

        mockMvc.perform(post("/oportunidades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(toSave)))
                .andExpect(status().isOk())
        ;
    }


}
