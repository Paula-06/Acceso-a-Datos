public class Cliente implements Comparable<Cliente> {
    //Atributos
    private int id;
    private String nombre;
    private String telefono;
    private String matricula;

    //Constructores
    //Crea un nuevo cliente
    public Cliente(String nombre, String telefono, String matricula) {
        this.nombre = nombre;
        this.telefono = telefono;
        this.matricula = matricula;
    }

    //Getters
    public int getId() {//Devuelve id
        return id;
    }
    public String getNombre() { //Devuelve nombre
        return nombre;
    }
    public String getTelefono() { //Devuelve telefono
        return telefono;
    }
    public String getMatricula() { // Devuelve matricula
        return matricula;
    }

    @Override
    public String toString() {
        return "ID: " + id +
                ", Nombre: " + nombre +
                ", Teléfono: " + telefono +
                ", Matrícula: " + matricula;
    }

    // Implementación de la comparación
    @Override
    public int compareTo(Cliente otro) {
        // 1. Comparar nombres ignorando mayúsculas/minúsculas
        int comparacionNombre = this.nombre.compareToIgnoreCase(otro.nombre);

        // 2. Si los nombres NO son iguales, devolvemos el resultado de esa comparación
        if (comparacionNombre != 0) {
            return comparacionNombre;
        }

        // 3. Si los nombres coinciden, desempatamos por id ascendente
        return Integer.compare(this.id, otro.id);
    }
}
