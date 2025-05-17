package common;

import java.time.LocalDateTime;

public class RegistroCompra {
    private int id;
    private String patente;
    private double litros;
    private double costo;
    // private LocalDateTime fecha;


    public RegistroCompra(int id, String patente, double litros, double costo) {
        setId(id);
        setPatente(patente);
       // setFecha(fecha);
        setLitros(litros);
        setCosto(costo);
    }

    private void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    private void setPatente(String patente) {
        this.patente = patente;
    }

    public String getPatente() {
        return patente;
    }
/*
    private void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }
*/
    private void setLitros(double litros) {
        this.litros = litros;
    }

    public double getLitros() {
        return litros;
    }

    private void setCosto(double costo) {
        this.costo = costo;
    }

    public double getCosto() {
        return costo;
    }
}
