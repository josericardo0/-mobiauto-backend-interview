package service;

import exceptionhandler.NegocioException;
import model.RevendaVeiculos;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import repository.RevendaVeiculosRepository;
import service.impl.RevendaVeiculosServiceImpl;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RevendaServiceTest {

    @Mock
    private RevendaVeiculosRepository revendaVeiculosRepository;

    @InjectMocks
    private RevendaVeiculosServiceImpl revendaVeiculosService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void listarRevendas_ShouldReturnPageOfRevendas() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        when(revendaVeiculosRepository.findAll(pageRequest))
                .thenReturn(new PageImpl<>(Collections.singletonList(new RevendaVeiculos())));

        Page<RevendaVeiculos> result = revendaVeiculosService.listarRevendas(pageRequest);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
    }

    @Test
    void cadastroDeRevenda_ValidCnpj_ShouldReturnSavedRevenda() {
        RevendaVeiculos revendaVeiculos = new RevendaVeiculos();
        revendaVeiculos.setCnpj("validCnpj");
        when(revendaVeiculosRepository.existsByCnpj(revendaVeiculos.getCnpj())).thenReturn(false);
        when(revendaVeiculosRepository.save(any(RevendaVeiculos.class))).thenReturn(revendaVeiculos);

        RevendaVeiculos savedRevendaVeiculos = revendaVeiculosService.cadastroDeRevenda(revendaVeiculos);

        assertNotNull(savedRevendaVeiculos);
        verify(revendaVeiculosRepository).save(revendaVeiculos);
    }

    @Test
    void cadastroDeRevenda_CnpjAlreadyExists_ShouldThrowNegocioException() {
        RevendaVeiculos revendaVeiculos = new RevendaVeiculos();
        revendaVeiculos.setCnpj("existingCnpj");
        when(revendaVeiculosRepository.existsByCnpj(revendaVeiculos.getCnpj())).thenReturn(true);

        assertThrows(NegocioException.class, () -> revendaVeiculosService.cadastroDeRevenda(revendaVeiculos));
    }

    @Test
    void deletarRevendasVeiculos_ShouldCallDelete() {
        RevendaVeiculos revendaVeiculos = new RevendaVeiculos();
        revendaVeiculos.setId(1L);

        doNothing().when(revendaVeiculosRepository).delete(revendaVeiculos);

        assertDoesNotThrow(() -> revendaVeiculosService.deletarRevendasVeiculos(revendaVeiculos));
        verify(revendaVeiculosRepository).delete(revendaVeiculos);
    }

}
