/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cliente;

import java.io.BufferedReader;
import java.net.Socket;
import static java.util.Arrays.copyOf;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author Equipo
 */
/* Hilo que recoge los mensajes del servidor y los demas usuarios conectados */
public class HiloCliente implements Runnable {

    // ATRIBUTOS
    private BufferedReader lectura;
    private String contraseña, fraseBuena;
    private Socket socket;
    private byte[] contraseñaBytes;
    private byte[] contraseñaFinalBytes;
    private SecretKey claveSimetrica;
    private Cipher cifrar;

    // CONSTRUCTOR
    public HiloCliente(BufferedReader l, String clave, Socket socket) {
        lectura = l;
        this.contraseña = clave;
        this.socket = socket;
    }

// --------------------------------------------------------------------------------------
    // METODO QUE LEE LOS MENSAJES QUE SON ENVIADOS POR EL SERVIDOR Y OTROS USUARIOS
    @Override
    public void run() {
        String cad = " ";

        while (cad != null) {
            try {
                cad = lectura.readLine();
                String aux = desencriptar(cad);
                if (aux.trim().equals("/ban") || aux.trim().equals("/ext")) {
                    if (aux.trim().equals("/ban")) {
                        System.out.println("Estas baneado");
                        Cliente.setBaneado(true);
                    }
                    if (aux.trim().equals("/ext")) {
                        System.exit(0);
                    }

                } else {

                    System.out.println(aux);
                }
            } catch (Exception ex) {
                System.out.println("Error: "+ex.getMessage());
            }
        }
    } // FIN METODO RUN

// --------------------------------------------------------------------------------------   
    public byte[] decodBase64(String a) {
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] decodedByteArray = decoder.decode(a);
        return decodedByteArray;
    }

    // METODO QUE DESENCRIPTA LOS MENSAJES QUE LE LLEGAN AL CLIENTE
    public String desencriptar(String frase) {

        try {
            contraseñaBytes = contraseña.getBytes("UTF8");
            contraseñaFinalBytes = copyOf(contraseñaBytes, 24);
            claveSimetrica = new SecretKeySpec(contraseñaFinalBytes, "DESede");
            cifrar = Cipher.getInstance("DESede");

            byte[] frasecifradabytes1 = decodBase64(frase);
            cifrar.init(Cipher.DECRYPT_MODE, claveSimetrica);
            byte[] frasebytedescifrada = cifrar.doFinal(frasecifradabytes1);

            fraseBuena = new String(frasebytedescifrada);

        } catch (Exception ex) {
            System.out.println("Error: "+ex.getMessage());
        }

        return fraseBuena;
    } // FIN METODO DESENCRIPTAR

}
