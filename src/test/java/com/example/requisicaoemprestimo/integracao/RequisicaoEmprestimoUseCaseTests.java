package com.example.requisicaoemprestimo.integracao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.requisicaoemprestimo.domain.models.Emprestimo;
import com.example.requisicaoemprestimo.domain.models.ResultadoAnalise;
import com.example.requisicaoemprestimo.domain.models.ResultadoTesouraria;
import com.example.requisicaoemprestimo.domain.ports.IAnaliseProxy;
import com.example.requisicaoemprestimo.domain.ports.ITesourariaProxy;
import com.example.requisicaoemprestimo.domain.usecases.RequisicaoEmprestimoUseCase;

public class RequisicaoEmprestimoUseCaseTests {

    private IAnaliseProxy analiseProxy;
    private ITesourariaProxy tesourariaProxy;
    private RequisicaoEmprestimoUseCase requisicao;

    @BeforeEach
    public void Setup(){
        analiseProxy = mock(IAnaliseProxy.class);
        tesourariaProxy = mock(ITesourariaProxy.class);
    }

    @Test
    public void testeRequisicaoValida(){
        when(analiseProxy.solicitarAnaliseDeCredito(any(Emprestimo.class))).thenReturn(new ResultadoAnalise(true, new String[0]));
        when(tesourariaProxy.solicitarLiberacaoDaTesouraria(any(Emprestimo.class))).thenReturn(new ResultadoTesouraria(true, "OK"));

        requisicao = new RequisicaoEmprestimoUseCase(analiseProxy, tesourariaProxy);

        Emprestimo emprestimo = requisicao.executar(UUID.randomUUID(), 100, 12);

        assertTrue(emprestimo.isEmprestimoFoiAprovado());
    }

    @Test
    public void testeRequisicaoAnaliseInvalida(){
        when(analiseProxy.solicitarAnaliseDeCredito(any(Emprestimo.class))).thenReturn(new ResultadoAnalise(false, new String[0]));
        when(tesourariaProxy.solicitarLiberacaoDaTesouraria(any(Emprestimo.class))).thenReturn(new ResultadoTesouraria(true, "OK"));

        requisicao = new RequisicaoEmprestimoUseCase(analiseProxy, tesourariaProxy);

        Emprestimo emprestimo = requisicao.executar(UUID.randomUUID(), 100, 12);
        
        assertEquals(false, emprestimo.isEmprestimoFoiAprovado());
    }

    @Test
    public void testeRequisicaoTesourariaInvalida(){
        when(analiseProxy.solicitarAnaliseDeCredito(any(Emprestimo.class))).thenReturn(new ResultadoAnalise(true, new String[0]));
        when(tesourariaProxy.solicitarLiberacaoDaTesouraria(any(Emprestimo.class))).thenReturn(new ResultadoTesouraria(false, "NOT OK"));

        requisicao = new RequisicaoEmprestimoUseCase(analiseProxy, tesourariaProxy);

        Emprestimo emprestimo = requisicao.executar(UUID.randomUUID(), 100, 12);
        
        assertEquals(false, emprestimo.isEmprestimoFoiAprovado());
    }
}
