package dominio.estacion;

import dominio.comunes.EstadoSurtidor;
import dominio.comunes.TipoCombustible;

/** Equipo de una estación: número visible único dentro de ella, no en la empresa. */
public class Surtidor {

    private Integer id;
    private final String codigoEquipo;      // único en toda la empresa
    private final int numeroVisible;        // único solo dentro de la estación
    private final TipoCombustible tipoCombustible;
    private EstadoSurtidor estado;
    private Estacion estacion;

    public Surtidor(Integer id, String codigoEquipo, int numeroVisible,
                    TipoCombustible tipoCombustible, EstadoSurtidor estado) {
        if (codigoEquipo == null || codigoEquipo.isBlank()) {
            throw new IllegalArgumentException("El código de equipo es obligatorio.");
        }
        if (numeroVisible <= 0) {
            throw new IllegalArgumentException("El número visible debe ser positivo.");
        }
        if (tipoCombustible == null || estado == null) {
            throw new IllegalArgumentException("El tipo y el estado son obligatorios.");
        }
        this.id = id;
        this.codigoEquipo = codigoEquipo;
        this.numeroVisible = numeroVisible;
        this.tipoCombustible = tipoCombustible;
        this.estado = estado;
    }

    void enlazar(Estacion estacion) {
        this.estacion = estacion;
    }

    public void marcarAveriado() {
        this.estado = EstadoSurtidor.AVERIADO;
    }

    public void reparar() {
        this.estado = EstadoSurtidor.OPERATIVO;
    }

    public Integer getId() {
        return id;
    }

    public String getCodigoEquipo() {
        return codigoEquipo;
    }

    public int getNumeroVisible() {
        return numeroVisible;
    }

    public TipoCombustible getTipoCombustible() {
        return tipoCombustible;
    }

    public EstadoSurtidor getEstado() {
        return estado;
    }

    public Estacion getEstacion() {
        return estacion;
    }

    @Override
    public String toString() {
        return "Surtidor " + numeroVisible + " (" + tipoCombustible + ", " + estado + ")";
    }
}
