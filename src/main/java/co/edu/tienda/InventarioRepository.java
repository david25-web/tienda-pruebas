package co.edu.tienda;

/** Dependencia externa (en producción sería una base de datos). */
public interface InventarioRepository {
    int stockDisponible(String codigoProducto);
    void descontarStock(String codigoProducto, int cantidad);
}
