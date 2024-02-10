package controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.Cliente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import service.ClienteService;
import org.springframework.data.domain.PageRequest;
import java.util.Arrays;


public class ClienteControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ClienteService clienteService;

    @InjectMocks
    private ClienteController clienteController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = standaloneSetup(clienteController).build();
    }

    @Test
    public void listarClientes_retornaTodosOsClientes() throws Exception {
        Cliente clienteExample = new Cliente(/* set properties as needed */);
        Page<Cliente> page = new PageImpl<>(Arrays.asList(clienteExample), PageRequest.of(0, 5), 1);

        when(clienteService.listarTodosOsClientes(any())).thenReturn(page);

        mockMvc.perform(get("/clientes")
                        .param("page", "0")
                        .param("size", "5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").exists()) // Checks that content array exists
                .andExpect(jsonPath("$.content[0].id").value(clienteExample.getId())) // Example of checking a field
                .andExpect(jsonPath("$.totalPages").value(1)) // Checks totalPages
                .andExpect(jsonPath("$.totalElements").value(1)); // Checks totalElements
    }


    @Test
    public void listarClientePorId_retornaClientePorId() throws Exception {
        Cliente cliente = new Cliente(/* set properties */);
        when(clienteService.listarClientesPorId(1L)).thenReturn(java.util.Optional.of(cliente));

        mockMvc.perform(get("/clientes/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(cliente.getId()));
    }

    @Test
    public void cadastrarCliente_retornaClienteCadastrado() throws Exception {
        Cliente clienteToSave = new Cliente(/* set properties */);
        Cliente savedCliente = new Cliente(/* set properties */);
        savedCliente.setId(1L);
        when(clienteService.cadastarCliente(any(Cliente.class))).thenReturn(savedCliente);

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clienteToSave)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists()); // Now this should pass
    }


    @Test
    public void modificarCliente_quandoEncontrado_retornaClienteModificado() throws Exception {
        Cliente updatedCliente = new Cliente(/* set properties */);
        when(clienteService.modificarCliente(eq(1L), any(Cliente.class))).thenReturn(updatedCliente);

        mockMvc.perform(put("/clientes/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedCliente)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(updatedCliente.getId()));
    }

    @Test
    public void deletarCliente_bemSucedido_retornaNaoEncontrado() throws Exception {
        doNothing().when(clienteService).deletarCliente(1L);

        mockMvc.perform(delete("/clientes/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}
