package dominio.personal;

import java.time.LocalDate;

/** Expendedor: acreditación para manipular combustibles con número y caducidad. */
public final class Expendedor extends Funcion {

    private final String numeroAcreditacion;
    private final LocalDate caducidad;

    public Expendedor(String numeroAcreditacion, LocalDate caducidad) {
        super("EXPENDEDOR", "Expendedor");
        if (numeroAcreditacion == null || numeroAcreditacion.isBlank()) {
            throw new IllegalArgumentException("El número de acreditación es obligatorio.");
        }
        if (caducidad == null) {
            throw new IllegalArgumentException("La caducidad de la acreditación es obligatoria.");
        }
        this.numeroAcreditacion = numeroAcreditacion;
        this.caducidad = caducidad;
    }

    public boolean estaVigente(LocalDate fecha) {
        return !fecha.isAfter(caducidad);
    }

    public String getNumeroAcreditacion() {
        return numeroAcreditacion;
    }

    public LocalDate getCaducidad() {
        return caducidad;
    }
}
