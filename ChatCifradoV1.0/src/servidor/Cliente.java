/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servidor;

import java.io.BufferedReader;
import java.io.PrintWriter;

/**
 *
 * @author Equipo
 */
public class Cliente {

    //ATRIBUTOS
    private String nomCli;
    private BufferedReader entrada;
    private PrintWriter salida;
    private boolean banear;

    //CONSTRUCTOR
    public Cliente(int nomCli, BufferedReader entrada, PrintWriter salida, boolean banear) {
        this.nomCli = "Cliente-" + nomCli;
        this.entrada = entrada;
        this.salida = salida;
        this.banear = banear;
    }

    //GETTERS
    public String getNomCli() {
        return nomCli;
    }

    public PrintWriter getSalida() {
        return salida;
    }

    public BufferedReader getEntrada() {
        return entrada;
    }

    // SETTERS
    public void setNomCli(String nomCli) {
        this.nomCli = nomCli;
    }

    public boolean isBanear() {
        return banear;
    }

    public void setBanear(boolean banear) {
        this.banear = banear;
    }

}
