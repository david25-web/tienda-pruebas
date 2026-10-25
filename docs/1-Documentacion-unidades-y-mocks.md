# Documentación: selección de unidades y simulación de dependencias

**Proyecto:** `tienda-pruebas` (Java 17, Maven, JUnit 5 y Mockito 5)

## 1. Preparación del entorno
- Proyecto nuevo: un módulo de tienda con tres clases y dos interfaces.
- Framework de pruebas: **JUnit 5** (`junit-jupiter`) y **Mockito** (`mockito-junit-jupiter`), declarados en `pom.xml` con alcance `test`.
- Las pruebas se ejecutan con `mvn test` (plugin Surefire) o desde cualquier IDE con soporte Maven.

## 2. Unidades seleccionadas

### Unidad 1: `CalculadoraDescuento`
Lógica pura, sin dependencias. Es autónoma y pequeña, así que se prueba de forma aislada.
- `porcentajeDescuento(monto, clienteFrecuente)`: 0 % si el monto es menor de 100.000; 5 % de 100.000 a 499.999; 10 % desde 500.000. El cliente frecuente suma 3 %. Lanza `IllegalArgumentException` si el monto es negativo o NaN.
- `calcularTotal(monto, clienteFrecuente)`: devuelve el monto menos el descuento.

### Unidad 2: `PedidoService`
Orquesta el procesamiento de un pedido. Valida los datos, verifica el stock, calcula el total, descuenta inventario y notifica al cliente.
- Depende de `InventarioRepository` y `NotificacionService`, que en producción serían una base de datos y un servicio de correo. Por eso se simulan.
- Lanza `IllegalArgumentException` ante datos inválidos y `StockInsuficienteException` si falta stock.

## 3. Casos de prueba

| Unidad | Válido | Inválido | Límite |
|---|---|---|---|
| `CalculadoraDescuento` | 200.000 sin frecuente da 190.000; 600.000 con frecuente da 13 % | Monto negativo y NaN lanzan excepción | Montos 0, 99.999,99, 100.000, 499.999,99, 500.000 y monto 0 con frecuente (parametrizado) |
| `PedidoService` | Pedido correcto: total, descuento de stock y notificación | Cantidad 0, email vacío y stock insuficiente | Cantidad igual al stock y precio 0 |

Cada prueba usa aserciones (`assertEquals`, `assertThrows`) para comparar el resultado obtenido con el esperado.

## 4. Simulación de dependencias (Mockito)
- `@ExtendWith(MockitoExtension.class)` activa Mockito en JUnit 5.
- `@Mock` crea dobles de `InventarioRepository` y `NotificacionService`. No hay base de datos ni envío real de correos.
- `when(...).thenReturn(...)` fija el stock que "responde" el repositorio (stub).
- `verify(...)`, `verify(..., never())` y `verifyNoInteractions(...)` comprueban las interacciones. Por ejemplo, ante un stock insuficiente **no** se descuenta inventario ni se notifica.
- `@Spy` sobre `CalculadoraDescuento` usa la implementación real, que es lógica pura, pero permite observarla.
- `@InjectMocks` construye `PedidoService` inyectando los dobles por constructor.

## 5. Ejecución y validación
Comando: `mvn test`. Resultado esperado: 16 pruebas ejecutadas y 0 fallos (las 6 filas del `@CsvSource` cuentan como casos independientes).

> **Completar tras ejecutar en tu equipo:** pega aquí el resumen de Maven (`Tests run: …, Failures: 0`) y anota cualquier corrección que hayas hecho.

## 6. Automatización en CI/CD
Se configuró **GitHub Actions** en `.github/workflows/ci.yml`:
1. Se activa con cada `push` y cada `pull_request`.
2. Descarga el código, instala JDK 17 y ejecuta `mvn -B test`.
3. Sube los reportes de Surefire como artefacto.

Para verificarlo: sube el proyecto a un repositorio de GitHub, haz un commit y revisa la pestaña **Actions**. Debe aparecer el flujo "CI - Pruebas unitarias" en verde.

> **Completar tras subir al repositorio:** agrega una captura de la ejecución exitosa.
