package integration;

import model.Cliente;
import model.Oportunidades;
import model.Usuario;
import model.Veiculo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.containers.PostgreSQLContainer;
import repository.ClienteRepository;
import repository.UsuarioRepository;
import repository.VeiculoRepository;
import service.OportunidadesService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Testcontainers
public class IntegrationTest {

    @Container
    public static PostgreSQLContainer<?> postgresqlContainer = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    static {
        postgresqlContainer.start();
    }

    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresqlContainer::getUsername);
        registry.add("spring.datasource.password", postgresqlContainer::getPassword);
    }

    @Autowired
    private OportunidadesService oportunidadesService;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private VeiculoRepository veiculoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    void testCadastrarOportunidade() {
        Cliente cliente = new Cliente();
        cliente = clienteRepository.save(cliente);

        Veiculo veiculo = new Veiculo();
        veiculo = veiculoRepository.save(veiculo);

        Usuario usuario = new Usuario();
        usuario = usuarioRepository.save(usuario);

        Oportunidades novaOportunidade = new Oportunidades();
        novaOportunidade.setCliente(cliente);
        novaOportunidade.setVeiculo(veiculo);
        novaOportunidade.setAtendimento(usuario);

        Oportunidades cadastrada = oportunidadesService.cadastrarOportunidade(novaOportunidade);

        assertNotNull(cadastrada.getId());
        assertEquals(cliente.getId(), cadastrada.getCliente().getId());
        assertEquals(veiculo.getId(), cadastrada.getVeiculo().getId());
        assertEquals(usuario.getId(), cadastrada.getAtendimento().getId());
    }
}
