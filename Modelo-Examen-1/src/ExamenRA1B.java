
import javax.swing.text.html.parser.Parser;
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

/** Solo se completan los seis métodos TODO. Consola y coordinación resueltas. */
public final class ExamenRA1B {
    private static final Path DIRECTORIO_ENTRADA = Path.of("datos", "entrada");
    private static final Path FICHERO_1 = DIRECTORIO_ENTRADA.resolve("arboles1.jsonl");
    private static final Path FICHERO_2 = DIRECTORIO_ENTRADA.resolve("arboles2.jsonl");
    private static final Path FICHERO_3 = DIRECTORIO_ENTRADA.resolve("arboles3.jsonl");
    private static final Path FICHERO_ID = DIRECTORIO_ENTRADA.resolve("id_arbol.bin");
    public static final String SALIDA_NOMBRE = "arbol1.txt";
    public static final String SALIDA_FINAL = "arboles_ordenados.jsonl";

    private ExamenRA1B() {
    }

    public static void main(String[] args) {
        System.out.println("=== EXAMEN B: ÁRBOLES Y FICHEROS JSON ===");
        System.out.println("Directorio de entrada: " + DIRECTORIO_ENTRADA.toAbsolutePath());

        try (Scanner teclado = new Scanner(System.in)) {
            System.out.print("Introduce la ruta del directorio de salida: ");
            String ruta = teclado.nextLine().trim();
            if (ruta.isEmpty()) {
                System.err.println("La ruta de salida no puede estar vacía.");
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
                System.err.println("No se puede continuar sin un directorio válido.");
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
        if (Files.notExists(salida)){
            try {
                Files.createDirectories(salida);
                System.out.println("Directorio creado");
                return true;
            } catch (IOException e) {
                System.out.printf("Error al crear el directorio (%s)\n", e.getMessage());
                return false;
            }
        } else if (!Files.isDirectory(salida)) {
            System.out.println("Existe un fichero con ese nombre");
            return false;
        }else{
            System.out.println("El directorio ya existe");
            return true;
        }
    }

    /** Ejercicio 2 (1 punto): leer el árbol y escribir su nombre en arbol1.txt. */
    private static Arbol ejercicio2LeerYEscribirArbol(Path entrada, Path salida) {
        // TODO: leer en UTF-8 y usar ParseadorArboles.parsear(linea).
        // Escribir getNombre() y un salto de línea. Devolver el árbol válido.
        // Si falla la escritura, conservar el árbol leído para el ejercicio 6.
        try (BufferedReader bf = Files.newBufferedReader(entrada, StandardCharsets.UTF_8)) {
            String linea = bf.readLine();
            Arbol a = ParseadorArboles.parsear(linea);
            if (a == null) {
                System.out.println("No se ha encontrado un arbol valido");
                return null;
            }
            try {
                Files.writeString(salida.resolve(SALIDA_NOMBRE), a.getNombre() + System.lineSeparator(),
                        StandardCharsets.UTF_8);
            } catch (IOException e) {
                System.err.println("Error: " + e.getMessage());
            }
            return a;
        } catch (IOException | FormatoJsonIncorrectoException | TipoDatoIncorrectoException e) {
            System.err.println("Error: " + e.getMessage());
            return null;
        }
    }

    /** Ejercicio 3 (2 puntos): leer 200 objetos JSON en un HashMap por ID. */
    private static HashMap<Integer, Arbol> ejercicio3LeerArboles(Path entrada) {
        HashMap<Integer, Arbol> arboles = new HashMap<>();
        String linea;

        try (BufferedReader bf = Files.newBufferedReader(entrada, StandardCharsets.UTF_8)) {
            int numeroLinea = 0;
            while ((linea = bf.readLine()) != null) {
                numeroLinea++;
                try {
                    Arbol a = ParseadorArboles.parsear(linea);
                    if (a == null) {
                        System.err.println("Fichero 2, línea " + numeroLinea + ": valores inválidos.");
                    } else if (arboles.putIfAbsent(a.getId(), a) != null) {
                        System.err.println("Fichero 2, línea " + numeroLinea
                                + ": ID repetido " + a.getId() + ".");
                    }
                } catch (FormatoJsonIncorrectoException | TipoDatoIncorrectoException e) {
                    System.err.println("Fichero 2, línea " + numeroLinea + ": " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("No se pudo leer " + entrada + ": " + e.getMessage());
        }
        System.out.println("Árboles incorporados desde el fichero 2: " + arboles.size());
        return arboles;
    }


    /** Ejercicio 4 (1 punto): leer un entero binario y mostrar el árbol con ese ID. */
    private static void ejercicio4LeerIdYMostrarArbol(
            Path ficheroId, HashMap<Integer, Arbol> arboles) {
        try (DataInputStream dis = new DataInputStream(Files.newInputStream(ficheroId))) {

            int id = dis.readInt();
            Arbol arbol = arboles.get(id);

            if (arbol != null) {
                System.out.println(arbol.toString());
            } else {
                System.err.println("No hay ningún árbol con ID " + id);
            }

        } catch ( EOFException e) {
            System.err.println("Error: " + ficheroId);
        } catch ( IOException e ) {
            System.err.println("Error: " + e.getMessage());
        }

        // TODO: leer un entero binario de 32 bits (formato de DataInputStream.readInt).
        // Buscar por clave y mostrar el árbol con su toString(), o indicar que no existe.
        // Gestionar recursos y errores, incluido un fichero incompleto.
    }

    /** Ejercicio 5 (2 puntos): errores por línea y duplicados dentro del fichero 3. */
    private static HashMap<Integer, Arbol> ejercicio5LeerArbolesConErrores(Path entrada) {
        HashMap<Integer, Arbol> arboles = new HashMap<>();

        int numLinea = 0;
        try (BufferedReader br = Files.newBufferedReader(entrada, StandardCharsets.UTF_8)) {
            String linea;
            while ((linea = br.readLine()) != null) {
                numLinea++;
                try {
                    Arbol arbol = ParseadorArboles.parsear(linea);
                    if (arbol == null) {
                        System.out.println("Línea " + numLinea
                                + ": valores fuera del dominio.");
                    } else if (arboles.containsKey(arbol.getId())) {
                        System.out.println("Línea " + numLinea
                                + ": ID duplicado (" + arbol.getId()
                                + "), se descarta.");
                    } else {
                        arboles.put(arbol.getId(), arbol);
                    }
                } catch (FormatoJsonIncorrectoException e) {
                    System.out.println("Línea " + numLinea
                            + ": formato JSON incorrecto. " + e.getMessage());
                } catch (TipoDatoIncorrectoException e) {
                    System.out.println("Línea " + numLinea
                            + ": tipo de dato incorrecto. " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Error al leer el fichero: " + e.getMessage());
        }
        System.out.println("Total de árboles válidos incorporados: "
                + arboles.size());
        return arboles;
    }

    /** Ejercicio 6 (3 puntos): CREATE_NEW y actualización completa del fichero. */
    private static void ejercicio6FusionarYGenerar(
            HashMap<Integer, Arbol> arboles2, HashMap<Integer, Arbol> arboles3,
            Arbol arbol1, Path salida) {
        // Parte A: combinar los mapas
        HashMap<Integer, Arbol> mapa =
                OperacionesArboles.combinar(arboles2, arboles3);
        // Primera escritura: crear el fichero si no existe
        try (BufferedWriter bw = Files.newBufferedWriter(
                salida.resolve(SALIDA_FINAL),
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE_NEW,
                StandardOpenOption.WRITE)) {
            for (Arbol arbol : OperacionesArboles.ordenar(mapa)) {
                bw.write(arbol.toJson());
                bw.newLine();
            }
        } catch (FileAlreadyExistsException e) {
            System.out.println("El fichero ya existe. No se modifica.");
            return;
        } catch (IOException e) {
            System.err.println("Error en la primera escritura: "
                    + e.getMessage());
            return;
        }
        // Parte B: incorporar el árbol 1 si su ID no existe
        OperacionesArboles.incorporarSiAusente(mapa, arbol1);
        // Reescribir todo el fichero, sustituyendo el contenido anterior
        try (BufferedWriter bw = Files.newBufferedWriter(
                salida.resolve(SALIDA_FINAL),
                StandardCharsets.UTF_8,
                StandardOpenOption.WRITE,
                StandardOpenOption.TRUNCATE_EXISTING)) {
            for (Arbol arbol : OperacionesArboles.ordenar(mapa)) {
                bw.write(arbol.toJson());
                bw.newLine();
            }
            System.out.println("Fichero actualizado correctamente.");
        } catch (IOException e) {
            System.err.println("Error al actualizar el fichero: "
                    + e.getMessage());
        }
    }
}