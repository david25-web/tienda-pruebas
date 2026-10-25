# Informe de reflexión: pruebas unitarias en un proyecto de software

**Asignatura:** Lenguaje de Programación Avanzado II · **Ejercicio:** 2.4

## Desafíos encontrados y cómo se superaron
El primer desafío fue elegir unidades realmente aisladas. Separar la regla de descuentos (`CalculadoraDescuento`) de la orquestación del pedido (`PedidoService`) permitió probar la lógica sin ninguna infraestructura. El segundo fue la dependencia de inventario y notificaciones: no se puede enviar un correo ni modificar una base de datos en cada prueba. Se resolvió definiendo interfaces e inyectándolas por constructor, lo que permitió reemplazarlas con mocks de Mockito. El tercero fue pensar los casos límite, porque los errores suelen aparecer justo en los umbrales (100.000 y 500.000, o cantidad igual al stock). Usar pruebas parametrizadas con `@CsvSource` hizo ese análisis ordenado y fácil de ampliar.

> **Añade aquí tu experiencia real** durante la ejecución: errores de configuración, fallos que corregiste, etc.

## Importancia de las pruebas unitarias
Las pruebas unitarias dan retroalimentación inmediata: un cambio que rompe una regla de negocio se detecta en segundos y no en producción. También funcionan como documentación ejecutable, porque los nombres y casos describen el comportamiento esperado. Al verificar interacciones (por ejemplo, que no se descuente stock cuando falta inventario) protegen reglas que serían difíciles de revisar manualmente. Junto con el pipeline de CI, cada commit se valida de forma automática, lo que reduce errores humanos y facilita refactorizar con confianza.

## Recomendaciones para mejorar la cobertura
1. Medir la cobertura con **JaCoCo** y fijar un mínimo (por ejemplo, 80 %) que haga fallar el pipeline.
2. Agregar casos para entradas poco frecuentes: valores muy grandes, precisión decimal (usar `BigDecimal` para dinero) y valores nulos en todos los parámetros.
3. Incorporar pruebas de integración (Spring Boot Test) para validar los componentes reales juntos, ya que los mocks no detectan fallos de configuración.
4. Aplicar TDD en las nuevas funcionalidades: escribir primero la prueba.
5. Revisar periódicamente las pruebas para eliminar duplicados y mantenerlas rápidas y legibles.
