package common;

import java.io.Serializable;

public class Auto implements Serializable{

	private int patente;
	private String conductor;
	private String tipoCombustible;
	
	
	public Auto(int patente, String conductor, String tipoCombustible) {
		setPatente(patente);
		setConductor(conductor);
		setTipoCombustible(tipoCombustible);
	}
	
	private void setTipoCombustible(String tipoCombustible) {
		// TODO Auto-generated method stub
		this.tipoCombustible = tipoCombustible;
	}

	private void setConductor(String conductor) {
		// TODO Auto-generated method stub
		this.conductor = conductor;
	}

	private void setPatente(int patente) {
		// TODO Auto-generated method stub
		this.patente = patente;
	}

	public int getPatente() {
		return patente;
	}
	
	public String getConductor() {
		return conductor;
	}
	
	public String getTipoCombustible() {
		return tipoCombustible;
	}
}
