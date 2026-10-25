package co.edu.tienda;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private InventarioRepository inventario;

    @Mock
    private NotificacionService notificaciones;

    @Spy
    private CalculadoraDescuento calculadora = new CalculadoraDescuento();

    @InjectMocks
    private PedidoService servicio;

    // ---------- Escenario 1: entradas válidas ----------
    @Test
    @DisplayName("Válido: procesa pedido, descuenta stock y notifica")
    void procesaPedidoValido() {
        when(inventario.stockDisponible("P1")).thenReturn(10);

        double total = servicio.procesarPedido("ana@mail.com", "P1", 4, 50_000, false);

        assertEquals(190_000.0, total, 0.001); // 200.000 - 5 %
        verify(inventario).descontarStock("P1", 4);
        verify(notificaciones).enviar(eq("ana@mail.com"), contains("190000"));
    }

    // ---------- Escenario 2: entradas inválidas ----------
    @Test
    @DisplayName("Inválido: cantidad cero lanza excepción y no toca dependencias")
    void cantidadCeroLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> servicio.procesarPedido("ana@mail.com", "P1", 0, 1000, false));
        verifyNoInteractions(inventario, notificaciones);
    }

    @Test
    @DisplayName("Inválido: email vacío lanza excepción")
    void emailVacioLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> servicio.procesarPedido(" ", "P1", 1, 1000, false));
        verifyNoInteractions(inventario, notificaciones);
    }

    @Test
    @DisplayName("Inválido: sin stock suficiente no descuenta ni notifica")
    void stockInsuficienteLanzaExcepcion() {
        when(inventario.stockDisponible("P1")).thenReturn(2);

        assertThrows(StockInsuficienteException.class,
                () -> servicio.procesarPedido("ana@mail.com", "P1", 3, 1000, false));

        verify(inventario, never()).descontarStock(anyString(), anyInt());
        verifyNoInteractions(notificaciones);
    }

    // ---------- Escenario 3: casos límite ----------
    @Test
    @DisplayName("Límite: cantidad exactamente igual al stock se acepta")
    void cantidadIgualAlStockSeAcepta() {
        when(inventario.stockDisponible("P1")).thenReturn(5);

        double total = servicio.procesarPedido("ana@mail.com", "P1", 5, 20_000, false);

        assertEquals(95_000.0, total, 0.001); // 100.000 -> exactamente el umbral del 5 %
        verify(inventario).descontarStock("P1", 5);
    }

    @Test
    @DisplayName("Límite: precio cero es válido y el total es cero")
    void precioCeroEsValido() {
        when(inventario.stockDisponible("P1")).thenReturn(1);

        assertEquals(0.0, servicio.procesarPedido("ana@mail.com", "P1", 1, 0, false), 0.001);
        verify(notificaciones).enviar(eq("ana@mail.com"), anyString());
    }
}
