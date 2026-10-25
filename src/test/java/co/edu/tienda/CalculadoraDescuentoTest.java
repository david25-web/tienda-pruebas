package co.edu.tienda;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class CalculadoraDescuentoTest {

    private CalculadoraDescuento calc;

    @BeforeEach
    void setUp() {
        calc = new CalculadoraDescuento();
    }

    // ---------- Escenario 1: entradas válidas ----------
    @Test
    @DisplayName("Válido: monto medio sin cliente frecuente aplica 5 %")
    void montoMedioAplicaCincoPorCiento() {
        assertEquals(190_000.0, calc.calcularTotal(200_000, false), 0.001);
    }

    @Test
    @DisplayName("Válido: monto alto con cliente frecuente aplica 13 %")
    void montoAltoClienteFrecuenteAplicaTrecePorCiento() {
        assertEquals(13.0, calc.porcentajeDescuento(600_000, true), 0.001);
        assertEquals(522_000.0, calc.calcularTotal(600_000, true), 0.001);
    }

    // ---------- Escenario 2: entradas inválidas ----------
    @Test
    @DisplayName("Inválido: monto negativo lanza IllegalArgumentException")
    void montoNegativoLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> calc.calcularTotal(-1, false));
    }

    @Test
    @DisplayName("Inválido: NaN lanza IllegalArgumentException")
    void montoNaNLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> calc.porcentajeDescuento(Double.NaN, false));
    }

    // ---------- Escenario 3: casos límite ----------
    @ParameterizedTest(name = "Límite: monto={0}, frecuente={1} -> {2} %")
    @CsvSource({
            "0,          false, 0",
            "99999.99,   false, 0",
            "100000,     false, 5",
            "499999.99,  false, 5",
            "500000,     false, 10",
            "0,          true,  3"
    })
    void casosLimite(double monto, boolean frecuente, double esperado) {
        assertEquals(esperado, calc.porcentajeDescuento(monto, frecuente), 0.001);
    }
}
