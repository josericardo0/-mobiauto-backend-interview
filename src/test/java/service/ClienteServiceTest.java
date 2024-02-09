package service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import model.Cliente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import repository.ClienteRepository;
import service.impl.ClienteServiceImpl;

import java.util.Arrays;
import java.util.Optional;

public class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ClienteServiceImpl clienteService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testListarClientesPorId() {
        Long id = 1L;
        Cliente cliente = new Cliente();
        cliente.setId(id);
        when(clienteRepository.findById(id)).thenReturn(Optional.of(cliente));

        Optional<Cliente> result = clienteService.listarClientesPorId(id);

        assertTrue(result.isPresent());
        assertEquals(id, result.get().getId());
    }

    @Test
    void testCadastarCliente() {
        Cliente cliente = new Cliente();
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);

        Cliente savedCliente = clienteService.cadastarCliente(cliente);

        assertNotNull(savedCliente);
    }

    @Test
    void testModificarCliente() {
        Long id = 1L;
        Cliente clienteAtualizado = new Cliente();
        clienteAtualizado.setNome("Novo Nome");
        when(clienteRepository.findById(id)).thenReturn(Optional.of(new Cliente()));
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteAtualizado);

        Cliente updatedCliente = clienteService.modificarCliente(id, clienteAtualizado);

        assertEquals("Novo Nome", updatedCliente.getNome());
    }

    @Test
    void testDeletarCliente() {
        Long id = 1L;
        doNothing().when(clienteRepository).deleteById(id);

        assertDoesNotThrow(() -> clienteService.deletarCliente(id));
    }

    @Test
    void testListarTodosOsClientes() {
        PageRequest pageable = PageRequest.of(0, 10);
        Cliente cliente = new Cliente();
        Page<Cliente> page = new PageImpl<>(Arrays.asList(cliente));
        when(clienteRepository.findAll(pageable)).thenReturn(page);

        Page<Cliente> resultPage = clienteService.listarTodosOsClientes(pageable);

        assertEquals(1, resultPage.getContent().size());
    }
}
