/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servidor;

/**
 *
 * @author usuario
 */
public class Asimetrico {
    
    private String id,claveAlmacen, keystore, usuario;

    public Asimetrico(String id, String usuario) {
        this.id = id;
        
        this.usuario = usuario;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClaveAlmacen() {
        return claveAlmacen;
    }

    public void setClaveAlmacen(String claveAlmacen) {
        this.claveAlmacen = claveAlmacen;
    }

    public String getKeystore() {
        return keystore;
    }

    public void setKeystore(String keystore) {
        this.keystore = keystore;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }
    
    
    
}
