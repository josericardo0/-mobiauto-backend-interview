import com.mobiauto.backendinterview.MobiautoChallengeApplication;
import model.Veiculo;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import service.VeiculoService;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;



@SpringBootTest(classes = MobiautoChallengeApplication.class)
@AutoConfigureMockMvc
public class VeiculoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VeiculoService veiculoService;

    @Test
    public void testListarTodosOsVeiculos() throws Exception {
        Page<Veiculo> veiculos = new PageImpl<>(Collections.singletonList(new Veiculo()));
        Mockito.when(veiculoService.listarTodosOsVeiculos(any(PageRequest.class))).thenReturn(veiculos);

        mockMvc.perform(get("/veiculos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").exists());
    }

    @Test
    public void testObterVeiculoPorIdSuccess() throws Exception {
        Veiculo veiculo = new Veiculo();
        Mockito.when(veiculoService.listarVeiculoPorId(anyLong())).thenReturn(java.util.Optional.of(veiculo));

        mockMvc.perform(get("/veiculos/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists());
    }

    @Test
    public void testCriarVeiculo() throws Exception {
        Veiculo veiculo = new Veiculo();
        Mockito.when(veiculoService.cadastrarVeiculo(any(Veiculo.class))).thenReturn(veiculo);

        mockMvc.perform(post("/veiculos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Test Veiculo\",\"tipo\":\"Carro\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").exists());
    }

    @Test
    public void testModificarVeiculoSuccess() throws Exception {
        Veiculo veiculo = new Veiculo();
        Mockito.when(veiculoService.modificarVeiculo(anyLong(), any(Veiculo.class))).thenReturn(veiculo);

        mockMvc.perform(put("/veiculos/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Updated Veiculo\",\"tipo\":\"Carro\"}"))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeletarVeiculoSuccess() throws Exception {
        Mockito.doNothing().when(veiculoService).deletarVeiculo(anyLong());

        mockMvc.perform(delete("/veiculos/{id}", 1L))
                .andExpect(status().isNoContent());
    }
}
