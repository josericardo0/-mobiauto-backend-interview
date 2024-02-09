import model.Veiculo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import service.VeiculoService;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class VeiculoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VeiculoService veiculoService;

    @Test
    public void testListarTodosOsVeiculos() throws Exception {
        Page<Veiculo> veiculos = new PageImpl<>(List.of(new Veiculo()));
        Mockito.when(veiculoService.listarTodosOsVeiculos(Mockito.any(PageRequest.class))).thenReturn(veiculos);

        mockMvc.perform(get("/veiculos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").exists());
    }

    @Test
    public void testObterVeiculoPorIdSuccess() throws Exception {
        Veiculo veiculo = new Veiculo();
        Mockito.when(veiculoService.listarVeiculoPorId(Mockito.anyLong())).thenReturn(java.util.Optional.of(veiculo));

        mockMvc.perform(get("/veiculos/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists());
    }

    @Test
    public void testCriarVeiculo() throws Exception {
        Veiculo veiculo = new Veiculo();
        Mockito.when(veiculoService.cadastrarVeiculo(Mockito.any(Veiculo.class))).thenReturn(veiculo);

        mockMvc.perform(post("/veiculos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").exists());
    }

    @Test
    public void testModificarVeiculoSuccess() throws Exception {
        Veiculo veiculo = new Veiculo();
        Mockito.when(veiculoService.modificarVeiculo(Mockito.eq(1L), Mockito.any(Veiculo.class))).thenReturn(veiculo);

        mockMvc.perform(put("/veiculos/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeletarVeiculoSuccess() throws Exception {
        Mockito.doNothing().when(veiculoService).deletarVeiculo(Mockito.anyLong());

        mockMvc.perform(delete("/veiculos/{id}", 1L))
                .andExpect(status().isNoContent());
    }
}
