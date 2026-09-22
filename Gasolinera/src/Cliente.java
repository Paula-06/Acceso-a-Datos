public class Cliente implements Comparable<Cliente> {
    //Atributos
   // private static int contador = 1;
    private int id;
    private String nombre;
    private String telefono;
    private String matricula;

    //Constructores
    //Crea un nuevo cliente
    public Cliente(int id, String nombre, String telefono, String matricula) {
        this.id = id;
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

    //Setters
    //Necesarios o no hacen falta, habrá modificaciones?
    /*
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }
    */

    @Override
    public String toString() {
        return "Opción: " + id +
        "\tNombre: " + nombre +
        "\tTeléfono: " + telefono +
        "\tMatrícula: " + matricula;

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
