package service.impl;

import exceptionhandler.NegocioException;
import javax.transaction.Transactional;
import model.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import repository.ClienteRepository;
import service.ClienteService;

import java.util.Optional;

@Service
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteServiceImpl(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }


    public Optional<Cliente> listarClientesPorId(Long id) {
        return this.clienteRepository.findById(id);
    }

    @Transactional
    public Cliente cadastarCliente(Cliente cliente) {
        return this.clienteRepository.save(cliente);
    }

    @Transactional
    public Cliente modificarCliente(Long id, Cliente clienteAtualizado) {
        return listarClientesPorId(id)
                .map(clienteExistente -> {
                    clienteExistente.setNome(clienteAtualizado.getNome());
                    clienteExistente.setEmail(clienteAtualizado.getEmail());
                    clienteExistente.setTelefone(clienteAtualizado.getTelefone());

                    return this.cadastarCliente(clienteExistente);
                })
                .orElseThrow(() -> new NegocioException("Não foi possível encontrar um cliente com o id: " + id));
    }

    @Transactional
    public void deletarCliente(Long id) {
        this.clienteRepository.deleteById(id);
    }

    @Override
    public Page<Cliente> listarTodosOsClientes(Pageable pageable) {
        return this.clienteRepository.findAll(pageable);
    }
}
