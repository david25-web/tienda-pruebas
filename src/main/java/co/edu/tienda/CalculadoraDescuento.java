package co.edu.tienda;

/**
 * Unidad 1: calcula el valor final de una compra aplicando descuentos por monto.
 * No tiene dependencias externas (lógica pura).
 *
 * Reglas: monto &lt; 100.000 -> 0 %; 100.000 a 499.999 -> 5 %;
 *         500.000 o más -> 10 %. El cliente frecuente suma 3 % adicional.
 */
public class CalculadoraDescuento {

    public static final double UMBRAL_MEDIO = 100_000;
    public static final double UMBRAL_ALTO = 500_000;

    public double porcentajeDescuento(double monto, boolean clienteFrecuente) {
        if (Double.isNaN(monto) || monto < 0) {
            throw new IllegalArgumentException("El monto no puede ser negativo ni NaN");
        }
        double porcentaje = 0;
        if (monto >= UMBRAL_ALTO) {
            porcentaje = 10;
        } else if (monto >= UMBRAL_MEDIO) {
            porcentaje = 5;
        }
        if (clienteFrecuente) {
            porcentaje += 3;
        }
        return porcentaje;
    }

    public double calcularTotal(double monto, boolean clienteFrecuente) {
        double porcentaje = porcentajeDescuento(monto, clienteFrecuente);
        return monto - (monto * porcentaje / 100);
    }
}
