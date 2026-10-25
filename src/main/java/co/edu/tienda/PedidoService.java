package co.edu.tienda;

/**
 * Unidad 2: procesa un pedido. Verifica stock, descuenta inventario,
 * calcula el total con descuento y notifica al cliente.
 * Depende de InventarioRepository y NotificacionService (se simulan con mocks).
 */
public class PedidoService {

    private final InventarioRepository inventario;
    private final NotificacionService notificaciones;
    private final CalculadoraDescuento calculadora;

    public PedidoService(InventarioRepository inventario,
                         NotificacionService notificaciones,
                         CalculadoraDescuento calculadora) {
        this.inventario = inventario;
        this.notificaciones = notificaciones;
        this.calculadora = calculadora;
    }

    public double procesarPedido(String email, String codigoProducto, int cantidad,
                                 double precioUnitario, boolean clienteFrecuente) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("El email es obligatorio");
        }
        if (codigoProducto == null || codigoProducto.isBlank()) {
            throw new IllegalArgumentException("El código de producto es obligatorio");
        }
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero");
        }
        if (precioUnitario < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo");
        }
        if (inventario.stockDisponible(codigoProducto) < cantidad) {
            throw new StockInsuficienteException(
                    "Stock insuficiente para " + codigoProducto);
        }

        double total = calculadora.calcularTotal(cantidad * precioUnitario, clienteFrecuente);
        inventario.descontarStock(codigoProducto, cantidad);
        notificaciones.enviar(email, "Pedido confirmado. Total: " + total);
        return total;
    }
}
