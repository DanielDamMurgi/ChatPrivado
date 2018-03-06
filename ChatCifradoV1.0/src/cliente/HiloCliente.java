/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cliente;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import static java.util.Arrays.copyOf;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import servidor.Secreto;

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
            } catch (IOException ex) {

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

        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(HiloCliente.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(HiloCliente.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchPaddingException ex) {
            Logger.getLogger(HiloCliente.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeyException ex) {
            Logger.getLogger(HiloCliente.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalBlockSizeException ex) {
            Logger.getLogger(HiloCliente.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadPaddingException ex) {
            Logger.getLogger(HiloCliente.class.getName()).log(Level.SEVERE, null, ex);
            //}catch(IllegalArgumentException e){

        }

        return fraseBuena;
    } // FIN METODO DESENCRIPTAR

}
