# Style.md — Mis reglas de programación

Reglas de estilo que sigo al programar (Java, proyectos de DAM).

Estas reglas se aplican a **mi código**. Las carpetas `src/dominio/**` no son
mías y no se revisan con ellas.

## 1. Nombres

- Todo en español: variables, métodos y clases.
- Mi convención: **primera letra en mayúscula y las demás en minúscula**,
  siempre. Ejemplos: `Preciocombustible`, `Clientes`, `Calculartotal()`.
- Base de datos: tablas y columnas en español.
  - Tablas: primera mayúscula y resto en minúscula (p. ej. `Cliente`).
  - Columnas: `snake_case` (minúsculas separadas con `_`), p. ej.
    `precio_combustible`.

### Convenciones que aparecen en código ajeno

- **camelCase**: todo junto, primera palabra en minúscula y cada palabra
  siguiente empieza con mayúscula (`precioCombustible`, `calcularTotal()`).
- **snake_case**: todo en minúsculas y las palabras separadas con guion bajo
  (`precio_combustible`, `fecha_venta`).

## 2. Comentarios

Comentarios breves y solo en lo importante.

## 3. Organización del proyecto

- Arquitectura por capas: modelo, DAO, servicio y vista.
- A veces separo la clase de datos de la lógica:
  - Una clase solo con los atributos (y lo básico: constructores,
    getters/setters y métodos como `toString` o `Comparator`).
  - Los demás métodos van en otras clases.

## 4. Orden dentro de una clase

1. Atributos
2. Constructores
3. Getters y setters
4. Métodos

## 5. Constantes y valores fijos

- Se declaran como `static final` y en MAYÚSCULAS.
- Recordatorio: no olvidarme del `final` (si no lo pongo, deja de ser una
  constante).
- Arriba de la clase: las constantes y atributos que necesitan todos los
  métodos.
- Dentro del método: las constantes específicas de ese método.
- Si una constante solo la usan 2 clases de 5, la declaro dentro de cada
  método, para que no pueda dar errores ni cargar el código.

## 6. Formato

- Llave de apertura en la misma línea (estilo K&R).
- Los métodos tienen la longitud que haga falta, sin límite fijo.

## 7. Errores

- Uso `try-catch` y muestro un mensaje por consola.
- Algunos errores los resuelvo con `System.out.println` (`sout`) en vez de
  `try-catch`, pero intento usar más `try-catch`.

## 8. Recursos (conexiones, ficheros, hilos)

El cierre depende del caso.

## 9. Hilos

- Por defecto extiendo `Thread`.
- Uso `Runnable` solo cuando sea necesario (por ejemplo, si la clase ya
  extiende otra).

## 10. Git

- Quiero hacer commits más explicativos (decir qué cambia y por qué).

## Pendiente de decidir

- JDBC: cómo meter los valores en las consultas SQL (aún no lo tengo claro).
