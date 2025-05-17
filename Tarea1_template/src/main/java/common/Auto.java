package common;

import java.io.Serializable;

public class Auto implements Serializable{

    private String patente;
    private String conductor;
    private String tipoCombustible;


    public Auto(String patente, String conductor, String tipoCombustible) {
        setPatente(patente);
        setConductor(conductor);
        setTipoCombustible(tipoCombustible);
    }

    private void setTipoCombustible(String tipoCombustible) {
        this.tipoCombustible = tipoCombustible;
    }

    private void setConductor(String conductor) {
        this.conductor = conductor;
    }

    private void setPatente(String patente) {
        this.patente = patente;
    }

    public String getPatente() {
        return patente;
    }

    public String getConductor() {
        return conductor;
    }

    public String getTipoCombustible() {
        return tipoCombustible;
    }
}