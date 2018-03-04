/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servidor;

import cliente.*;
import java.io.Serializable;

/**
 *
 * @author usuario
 */
public class DividirFichero implements Serializable{
    
    //ATRIBUTOS
    private String nombreFich;
    private boolean ultimoTrozo;
    private int bytesValidos;
    private byte [] trozo=new byte[1024];
    
    //CONSTRUCTOR

    public DividirFichero() {
    }

    // GETTERS 
    public String getNombreFich() {
        return nombreFich;
    }

    public boolean isUltimoTrozo() {
        return ultimoTrozo;
    }

    public int getBytesValidos() {
        return bytesValidos;
    }

    public byte[] getTrozo() {
        return trozo;
    }
    
    // SETTERS

    public void setNombreFich(String nombreFich) {
        this.nombreFich = nombreFich;
    }

    public void setUltimoTrozo(boolean ultimoTrozo) {
        this.ultimoTrozo = ultimoTrozo;
    }

    public void setBytesValidos(int bytesValidos) {
        this.bytesValidos = bytesValidos;
    }

    public void setTrozo(byte[] trozo) {
        this.trozo = trozo;
    }
    
    
    
}
