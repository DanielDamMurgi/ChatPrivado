/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servidor;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Equipo
 */
public class Servidor {

    private static Secreto clave;
    private static AsimetricoClave asimetricoClave ;

    private static final int[] NUMEROS = {1, 2, 3, 4, 5, 6, 7, 8, 9};
    private static final char[] MINUSCULAS = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
    private static final char[] MAYUSCULAS = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
    private static final char[] CARACTERES_ESP = {'=', '+', '@', '/', '-'};

    public static void informacion() {//Metodo que muestra la ip del servidor y el puerto en el servidor

        try {
            System.out.println("////////////  SERVIDOR ACTIVO  ////////////");

            System.out.println("/INFORMACION DEL SERVIDOR ==>");

            System.out.println("/\tIP DEL SERVIDOR ==> " + InetAddress.getLocalHost().getHostAddress());
            System.out.println("/\tPUERTO ==> 23000");
            System.out.println("/\t/ban ==> silenciar");
            System.out.println("/\t/ext ==> expulsar");
            System.out.println("///////////////////////////////////////////");
        } catch (UnknownHostException ex) {
            System.out.println("Error al obtener la ip");
        }
    }

// ------------------------------------------------------------------------------------------
    public static void main(String[] args) {
        int numeroCliente = 1;
        int puerto = 23000;
        ArrayList<ClienteServ> cliente = new ArrayList<>();
        
        

        try (
                ServerSocket socket = new ServerSocket(puerto);) {
            informacion();
            clave = new Secreto(generarClave());
            

            while (true) {
                HiloServidor hiloServidor = new HiloServidor(socket.accept(), numeroCliente++, cliente, clave);
                Thread hiloS = new Thread(hiloServidor);
                hiloS.start();
            }

        } catch (IOException ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    // ------------------------------------------------------------------------------------------
    private static String generarClave() {
        String key = "";
        int op = 0;
        int n = 0;
        int s = 0;

        do {
            op = aleatorio(1, 4);
            switch (op) {
                case 1:
                    int nu = NUMEROS[aleatorio(0, NUMEROS.length)];
                    //System.out.println(nu);
                    key += nu;

                    break;

                case 2:
                    char min = MINUSCULAS[aleatorio(0, MINUSCULAS.length)];
                    //System.out.println(min);
                    key += min;
                    break;

                case 3:
                    char mayu = MAYUSCULAS[aleatorio(0, MAYUSCULAS.length)];
                    //System.out.println(mayu);
                    key += mayu;
                    break;

                case 4:
                    char esp = CARACTERES_ESP[aleatorio(0, CARACTERES_ESP.length)];
                    //System.out.println(esp);
                    key += esp;

                    break;
            }
        } while (key.length() != 8);

        //System.out.println(key);
        return key;
    }

// ------------------------------------------------------------------------------------------
    public static int aleatorio(int minimo, int maximo) {

        int num = (int) Math.floor(Math.random() * (maximo - (minimo + 1)) + (minimo));
        //System.out.println("numero aleatroio"+num);

        return num;
    }

}
