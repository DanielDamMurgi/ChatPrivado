package servidor;

import cliente.*;
import java.io.Serializable;

/**
 *
 * @author usuario
 */
public class DividirFichero implements Serializable {

    //ATRIBUTOS
    private String nombre;
    private int bytes;
    private boolean ultDivFich;
    private byte[] divFich = new byte[1024];

    //CONSTRUCTOR
    public DividirFichero() {
    }

    // GETTERS 
    public String getNombre() {
        return nombre;
    }

    public int getBytes() {
        return bytes;
    }

    public boolean isUltDivFich() {
        return ultDivFich;
    }

    public byte[] getDivFich() {
        return divFich;
    }

    // SETTERS
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setBytes(int bytes) {
        this.bytes = bytes;
    }

    public void setUltDivFich(boolean ultDivFich) {
        this.ultDivFich = ultDivFich;
    }

    public void setDivFich(byte[] divFich) {
        this.divFich = divFich;
    }

}
