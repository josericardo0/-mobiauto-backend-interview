package service;

import model.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ClienteService {

    Page<Cliente> listarTodosOsClientes(Pageable pageable);
    Optional<Cliente> listarClientesPorId(Long id);
    Cliente cadastarCliente(Cliente cliente);
    Cliente modificarCliente(Long id, Cliente clienteAtualizado);
    void deletarCliente(Long id);
}
