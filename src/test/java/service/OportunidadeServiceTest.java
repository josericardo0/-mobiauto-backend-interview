package service;

import exceptionhandler.NegocioException;
import model.Oportunidades;
import model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import repository.OportunidadeRepository;
import service.impl.OportunidadesServiceImpl;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class OportunidadeServiceTest {

    @Mock
    private OportunidadeRepository oportunidadeRepository;

    @InjectMocks
    private OportunidadesServiceImpl oportunidadesService;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void listarOportunidades_ShouldReturnPageOfOportunidades() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        when(oportunidadeRepository.findAll(pageRequest))
                .thenReturn(new PageImpl<>(Collections.singletonList(new Oportunidades())));

        Page<Oportunidades> result = oportunidadesService.listarOportunidades(pageRequest);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
    }

    @Test
    void cadastrarOportunidade_ShouldReturnSavedOportunidade() {
        Oportunidades oportunidade = new Oportunidades();
        when(oportunidadeRepository.save(any(Oportunidades.class))).thenReturn(oportunidade);

        Oportunidades savedOportunidade = oportunidadesService.cadastrarOportunidade(oportunidade);

        assertNotNull(savedOportunidade);
    }

    @Test
    void modificarOportunidade_WhenOportunidadeNotFound_ShouldThrowException() {
        Long oportunidadeId = 1L;
        when(oportunidadeRepository.findById(oportunidadeId)).thenReturn(Optional.empty());

        assertThrows(NegocioException.class, () ->
                oportunidadesService.modificarOportunidade(oportunidadeId, new Oportunidades(), new Usuario()));
    }

    @Test
    void deletarOportunidade_ShouldDeleteOpportunity() {
        Long oportunidadeId = 1L;
        doNothing().when(oportunidadeRepository).deleteById(oportunidadeId);

        assertDoesNotThrow(() -> oportunidadesService.deletarOportunidade(oportunidadeId));
        verify(oportunidadeRepository, times(1)).deleteById(oportunidadeId);
    }



}
