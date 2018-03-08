/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servidor;

import static java.lang.Thread.sleep;
import java.net.Socket;
import java.util.ArrayList;
import static java.util.Arrays.copyOf;
import java.util.Base64;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author Equipo
 */
public class HiloEnviar implements Runnable {

    ArrayList<Cliente> cliente;

    Socket s;

    String contraseña = "";
    String mensaje = "";
    String fraseFinalCifrada;
    byte[] fraseBytes;
    byte[] contraseñaBytes = null;
    byte[] contraseñaFinalBytes;
    byte[] fraseCifrada;
    SecretKey claveSimetrica;
    Cipher cifrar;

    String clienteBaneado;
    boolean encontrado = false;

    Secreto clave;

    Scanner teclado = new Scanner(System.in);

    public HiloEnviar(ArrayList<Cliente> cliente, Socket s, String clave) {
        this.cliente = cliente;
        this.s = s;
        this.contraseña = clave;
    }

    public static String codBase64(byte[] a) {
        Base64.Encoder encoder = Base64.getEncoder();
        String b = encoder.encodeToString(a);
        return b;
    }

// ------------------------------------------------------------------------------------------
    // METODO PARA ENCCRIPTAR LOS MENSAJES QUE SE ENVIAN A LOS CLIENTES.
    public String encriptar(String frase) {

        try {
            contraseñaBytes = contraseña.getBytes("UTF8");
            contraseñaFinalBytes = copyOf(contraseñaBytes, 24);
            claveSimetrica = new SecretKeySpec(contraseñaFinalBytes, "DESede");
            cifrar = Cipher.getInstance("DESede");
            fraseBytes = frase.getBytes("UTF8");

            cifrar.init(Cipher.ENCRYPT_MODE, claveSimetrica);
            fraseCifrada = cifrar.doFinal(fraseBytes);
            fraseFinalCifrada = codBase64(fraseCifrada);

        } catch (Exception ex) {
            System.out.println("Error: "+ex.getMessage());
        }

        return fraseFinalCifrada;
    } // FIN METODO ENCRIPTAR

// ------------------------------------------------------------------------------------------
    // hilo que envia los mensajes escrtitos desde el servidor a los clientes y la desconexion del servidor
    @Override
    public void run() {

        while (mensaje != null) {
            mensaje = teclado.nextLine();
            encontrado = false;

            if (mensaje.trim().equals("/x") || mensaje.trim().equals("/ban") || mensaje.trim().equals("/ext")) {
                if (mensaje.trim().equals("/x")) {
                    System.out.println("Servidor => El servidor se desconectará en 120 seg ");
                    for (int i = 120; i >= 0; i = i - 10) {
                        try {
                            sleep(10000);
                            System.out.println("Servidor => El servidor se desconectará en " + i + " seg");
                            for (Cliente c : cliente) {

                                c.getSalida().println("Servidor => El servidor se desconectará en " + i + " seg");
                            }
                        } catch (InterruptedException ex) {
                            Logger.getLogger(HiloEnviar.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    try {
                        s.close();
                        System.exit(0);
                    } catch (Exception ex) {
                        System.out.println("Error: "+ex.getMessage());
                    }
                }

                if (mensaje.trim().equals("/ban")) {
                    System.out.println("Inserta el cliente a banear: \n");
                    for (Cliente c : cliente) {
                        System.out.println(c.getNomCli());
                    }

                    clienteBaneado = teclado.nextLine();

                    for (Cliente c : cliente) {
                        if (clienteBaneado.trim().equals(c.getNomCli())) {
                            c.getSalida().println(encriptar("/ban"));
                            encontrado = true;
                            break;
                        }
                    }
                    if (!encontrado) {
                        System.out.println("No existe el cliente");
                    }
                }

                if (mensaje.trim().equals("/ext")) {
                    System.out.println("Inserta el cliente a expulsar: \n");
                    for (Cliente c : cliente) {
                        System.out.println(c.getNomCli());
                    }

                    clienteBaneado = teclado.nextLine();

                    for (Cliente c : cliente) {
                        if (clienteBaneado.trim().equals(c.getNomCli())) {
                            c.getSalida().println(encriptar("/ext"));
                            cliente.remove(c);
                            encontrado = true;
                            break;
                        }
                    }
                    if (!encontrado) {
                        System.out.println("No existe el cliente");
                    }
                }

            } else {
                for (Cliente c : cliente) {
                    c.getSalida().println(encriptar("Servidor => " + mensaje));
                }
            }
        }
    } // FIN METODO RUN

}
