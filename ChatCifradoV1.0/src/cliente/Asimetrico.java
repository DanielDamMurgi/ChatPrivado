/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cliente;

/**
 *
 * @author usuario
 */
public class Asimetrico {
    
    private String cliente, keystore , claveAlmacen , keyClaves, usuario;

    public Asimetrico(String cliente, String keystore, String claveAlmacen, String keyClaves, String usuario) {
        this.cliente = cliente;
        this.keystore = keystore;
        this.claveAlmacen = claveAlmacen;
        this.keyClaves = keyClaves;
        this.usuario = usuario;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getKeystore() {
        return keystore;
    }

    public void setKeystore(String keystore) {
        this.keystore = keystore;
    }

    public String getClaveAlmacen() {
        return claveAlmacen;
    }

    public void setClaveAlmacen(String claveAlmacen) {
        this.claveAlmacen = claveAlmacen;
    }

    public String getKeyClaves() {
        return keyClaves;
    }

    public void setKeyClaves(String keyClaves) {
        this.keyClaves = keyClaves;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }
    
    
    
}
