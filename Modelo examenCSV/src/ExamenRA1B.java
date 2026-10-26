import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Scanner;

/** Solo se completan los seis metodos TODO. Consola y coordinacion resueltas. */
public final class ExamenRA1B {
    private static final Path DIRECTORIO_ENTRADA = Path.of("datos", "entrada");
    private static final Path FICHERO_1 = DIRECTORIO_ENTRADA.resolve("arboles1.csv");
    private static final Path FICHERO_2 = DIRECTORIO_ENTRADA.resolve("arboles2.csv");
    private static final Path FICHERO_3 = DIRECTORIO_ENTRADA.resolve("arboles3.csv");
    private static final Path FICHERO_ID = DIRECTORIO_ENTRADA.resolve("id_arbol.bin");
    public static final String SALIDA_NOMBRE = "arbol1.txt";
    public static final String SALIDA_FINAL = "arboles_ordenados.csv";

    private ExamenRA1B() {
    }

    public static void main(String[] args) {
        System.out.println("=== EXAMEN B: ARBOLES Y FICHEROS CSV ===");
        System.out.println("Directorio de entrada: " + DIRECTORIO_ENTRADA.toAbsolutePath());

        try (Scanner teclado = new Scanner(System.in)) {
            System.out.print("Introduce la ruta del directorio de salida: ");
            String ruta = teclado.nextLine().trim();
            if (ruta.isEmpty()) {
                System.err.println("La ruta de salida no puede estar vacia.");
                return;
            }
            Path salida;
            try {
                salida = Path.of(ruta);
            } catch (InvalidPathException e) {
                System.err.println("Ruta de salida incorrecta: " + e.getMessage());
                return;
            }
            if (!prepararDirectorioSalida(salida)) {
                System.err.println("No se puede continuar sin un directorio valido.");
                return;
            }

            Arbol arbol1 = ejercicio2LeerYEscribirArbol(FICHERO_1, salida);
            HashMap<Integer, Arbol> arboles2 = ejercicio3LeerArboles(FICHERO_2);
            ejercicio4LeerIdYMostrarArbol(FICHERO_ID, arboles2);
            HashMap<Integer, Arbol> arboles3 = ejercicio5LeerArbolesConErrores(FICHERO_3);
            ejercicio6FusionarYGenerar(arboles2, arboles3, arbol1, salida);
        }
    }

    /** Ejercicio 1 (1 punto): comprobar/crear directorio e informar de errores. */
    private static boolean prepararDirectorioSalida(Path salida) {
        try {
            if (Files.exists(salida)) {
                if (Files.isDirectory(salida)) {
                    System.out.println("El directorio de salida ya existia: " + salida);
                    return true;
                }
                System.err.println("La ruta existe, pero no es un directorio: " + salida);
                return false;
            }
            Files.createDirectories(salida);
            System.out.println("Directorio de salida creado: " + salida);
            return true;
        } catch (IOException | SecurityException e) {
            System.err.println("No se pudo preparar " + salida + ": " + e.getMessage());
            return false;
        }
    }

    /** Ejercicio 2 (1 punto): leer el arbol y escribir su nombre en arbol1.txt. */
    private static Arbol ejercicio2LeerYEscribirArbol(Path entrada, Path salida) {
        Arbol arbol;
        try (BufferedReader lector = Files.newBufferedReader(entrada, StandardCharsets.UTF_8)) {
            String linea = lector.readLine();
            arbol = ParseadorArboles.parsear(linea);   // si linea es null devuelve null
            if (arbol == null) {
                System.err.println("No hay un arbol valido en " + entrada);
                return null;
            }
        } catch (FormatoCSVIncorrectoException | TipoDatoIncorrectoException e) {
            System.err.println("No se pudo interpretar el arbol 1: " + e.getMessage());
            return null;
        } catch (IOException e) {
            System.err.println("No se pudo leer " + entrada + ": " + e.getMessage());
            return null;
        }
        Path ficheroSalida = salida.resolve(SALIDA_NOMBRE);   // directorio + arbol1.txt
        try {
            Files.writeString(ficheroSalida, arbol.getNombre() + System.lineSeparator(),
                    StandardCharsets.UTF_8, StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
        } catch (IOException e) {
            System.err.println("No se pudo escribir " + ficheroSalida + ": " + e.getMessage());
        }
        return arbol;   // se devuelve aunque falle la escritura
    }

    /** Ejercicio 3 (2 puntos): leer 200 objetos CSV en un HashMap por ID. */
    private static HashMap<Integer, Arbol> ejercicio3LeerArboles(Path entrada) {
        HashMap<Integer, Arbol> arboles = new HashMap<>();
        try (BufferedReader lector = Files.newBufferedReader(entrada, StandardCharsets.UTF_8)) {
            String linea;
            int numeroLinea = 0;
            while ((linea = lector.readLine()) != null) {
                numeroLinea++;
                try {
                    Arbol arbol = ParseadorArboles.parsear(linea);
                    if (arbol == null) {
                        System.err.println("Fichero 2, linea " + numeroLinea + ": valores invalidos.");
                    } else if (arboles.putIfAbsent(arbol.getId(), arbol) != null) {
                        System.err.println("Fichero 2, linea " + numeroLinea
                                + ": ID repetido " + arbol.getId() + ".");
                    }
                } catch (FormatoCSVIncorrectoException | TipoDatoIncorrectoException e) {
                    System.err.println("Fichero 2, linea " + numeroLinea + ": " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("No se pudo leer " + entrada + ": " + e.getMessage());
        }
        System.out.println("Arboles incorporados desde el fichero 2: " + arboles.size());
        return arboles;
    }

    /** Ejercicio 4 (1 punto): leer un entero binario y mostrar el arbol con ese ID. */
    private static void ejercicio4LeerIdYMostrarArbol(
            Path ficheroId, HashMap<Integer, Arbol> arboles) {
        try (DataInputStream entrada = new DataInputStream(Files.newInputStream(ficheroId))) {
            int id = entrada.readInt();            // 4 bytes, big-endian
            Arbol arbol = arboles.get(id);         // null si no existe
            if (arbol == null) {
                System.out.println("No hay ningun arbol con ID " + id + ".");
            } else {
                System.out.println("Arbol con ID " + id + ": " + arbol);   // usa toString()
            }
        } catch (EOFException e) {                 // ANTES que IOException
            System.err.println("El fichero binario no contiene un entero completo: " + ficheroId);
        } catch (IOException e) {
            System.err.println("No se pudo leer " + ficheroId + ": " + e.getMessage());
        }
    }

    /** Ejercicio 5 (2 puntos): errores por linea y duplicados dentro del fichero 3. */
    private static HashMap<Integer, Arbol> ejercicio5LeerArbolesConErrores(Path entrada) {
        HashMap<Integer, Arbol> arboles = new HashMap<>();
        try (BufferedReader lector = Files.newBufferedReader(entrada, StandardCharsets.UTF_8)) {
            String linea;
            int numeroLinea = 0;
            while ((linea = lector.readLine()) != null) {
                numeroLinea++;
                try {
                    Arbol arbol = ParseadorArboles.parsear(linea);
                    if (arbol == null) {
                        System.err.println("Fichero 3, linea " + numeroLinea
                                + ": valores de dominio invalidos; registro descartado.");
                    } else if (arboles.putIfAbsent(arbol.getId(), arbol) != null) {
                        System.err.println("Fichero 3, linea " + numeroLinea
                                + ": ID repetido " + arbol.getId() + "; registro descartado.");
                    }
                } catch (FormatoCSVIncorrectoException e) {
                    System.err.println("Fichero 3, linea " + numeroLinea
                            + ": formato CSV incorrecto (" + e.getMessage() + ").");
                } catch (TipoDatoIncorrectoException e) {
                    System.err.println("Fichero 3, linea " + numeroLinea
                            + ": tipo de dato incorrecto (" + e.getMessage() + ").");
                }
            }
        } catch (IOException e) {
            System.err.println("No se pudo leer " + entrada + ": " + e.getMessage());
        }
        System.out.println("Arboles incorporados desde el fichero 3: " + arboles.size());
        return arboles;
    }

    /** Ejercicio 6 (3 puntos): CREATE_NEW y actualizacion completa del fichero. */
    private static void ejercicio6FusionarYGenerar(
            HashMap<Integer, Arbol> arboles2, HashMap<Integer, Arbol> arboles3,
            Arbol arbol1, Path salida) {
        HashMap<Integer, Arbol> mapa = OperacionesArboles.combinar(arboles2, arboles3);
        Path ficheroSalida = salida.resolve(SALIDA_FINAL);   // la misma ruta en A y en B

        // ---------- PARTE A: primera escritura, no sobrescribe ----------
        try (BufferedWriter escritor = Files.newBufferedWriter(ficheroSalida,
                StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW,
                StandardOpenOption.WRITE)) {
            for (Arbol arbol : OperacionesArboles.ordenar(mapa)) {
                escritor.write(arbol.toCsv());
                escritor.newLine();
            }
        } catch (FileAlreadyExistsException e) {   // ANTES que IOException
            System.out.println("El fichero final ya existe; se conserva intacto: " + ficheroSalida);
            return;                                // no se hace la Parte B
        } catch (IOException e) {
            System.err.println("Fallo la primera escritura de " + ficheroSalida
                    + "; no se realiza la actualizacion: " + e.getMessage());
            return;
        }
        System.out.println("Primera generacion completada con " + mapa.size() + " arboles.");

        // ---------- PARTE B: el escritor anterior ya esta cerrado ----------
        OperacionesArboles.incorporarSiAusente(mapa, arbol1);   // acepta null
        try (BufferedWriter escritor = Files.newBufferedWriter(ficheroSalida,
                StandardCharsets.UTF_8, StandardOpenOption.WRITE,
                StandardOpenOption.TRUNCATE_EXISTING)) {
            for (Arbol arbol : OperacionesArboles.ordenar(mapa)) {
                escritor.write(arbol.toCsv());
                escritor.newLine();
            }
            System.out.println("Fichero final actualizado con " + mapa.size() + " arboles.");
        } catch (IOException e) {
            System.err.println("No se pudo actualizar " + ficheroSalida + ": " + e.getMessage());
        }
    }
}