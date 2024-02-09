package service;

import exceptionhandler.NegocioException;
import model.Veiculo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import repository.VeiculoRepository;
import service.impl.VeiculoServiceImpl;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VeiculoServiceTest {

    @Mock
    private VeiculoRepository veiculoRepository;

    @InjectMocks
    private VeiculoServiceImpl veiculoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void listarTodosOsVeiculos_ShouldReturnPageOfVeiculos() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Veiculo veiculo = new Veiculo();
        Page<Veiculo> expectedPage = new PageImpl<>(Collections.singletonList(veiculo));
        when(veiculoRepository.findAll(pageRequest)).thenReturn(expectedPage);

        Page<Veiculo> result = veiculoService.listarTodosOsVeiculos(pageRequest);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(veiculoRepository).findAll(pageRequest);
    }

    @Test
    void listarVeiculoPorId_ExistingId_ShouldReturnVeiculo() {
        Long veiculoId = 1L;
        Optional<Veiculo> expectedVeiculo = Optional.of(new Veiculo());
        when(veiculoRepository.findById(veiculoId)).thenReturn(expectedVeiculo);

        Optional<Veiculo> result = veiculoService.listarVeiculoPorId(veiculoId);

        assertTrue(result.isPresent());
        verify(veiculoRepository).findById(veiculoId);
    }

    @Test
    void cadastrarVeiculo_ShouldSaveAndReturnVeiculo() {
        Veiculo veiculo = new Veiculo();
        when(veiculoRepository.save(veiculo)).thenReturn(veiculo);

        Veiculo savedVeiculo = veiculoService.cadastrarVeiculo(veiculo);

        assertNotNull(savedVeiculo);
        verify(veiculoRepository).save(veiculo);
    }

    @Test
    void modificarVeiculo_ExistingId_ShouldUpdateAndReturnVeiculo() {
        Long veiculoId = 1L;
        Veiculo existingVeiculo = new Veiculo();
        Veiculo updatedVeiculo = new Veiculo();
        updatedVeiculo.setMarca("Updated Marca");

        when(veiculoRepository.findById(veiculoId)).thenReturn(Optional.of(existingVeiculo));
        when(veiculoRepository.save(any(Veiculo.class))).thenReturn(updatedVeiculo);

        Veiculo result = veiculoService.modificarVeiculo(veiculoId, updatedVeiculo);

        assertEquals("Updated Marca", result.getMarca());
        verify(veiculoRepository).save(any(Veiculo.class));
    }

    @Test
    void modificarVeiculo_NonExistingId_ShouldThrowException() {
        Long veiculoId = 99L;
        Veiculo updatedVeiculo = new Veiculo();

        when(veiculoRepository.findById(veiculoId)).thenReturn(Optional.empty());

        assertThrows(NegocioException.class, () -> veiculoService.modificarVeiculo(veiculoId, updatedVeiculo));
    }

    @Test
    void deletarVeiculo_ShouldCallDeleteById() {
        Long veiculoId = 1L;
        doNothing().when(veiculoRepository).deleteById(veiculoId);

        veiculoService.deletarVeiculo(veiculoId);

        verify(veiculoRepository).deleteById(veiculoId);
    }
}
