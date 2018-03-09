/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servidor;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Base64;
import javax.crypto.Cipher;

/**
 *
 * @author usuario2
 */

/*
    CLASE PARA CIFRAR LA CLAVE QUE SE ENVIA A LOS CLIENTES DE FORMA ASIMETRICA
 */
public class AsimetricoClave {

    // ATRIBUTOS
    private String claveAlmacen = "prueba2", keystore ="/home/prueba2/.keystore" , clave, claveb64 = "";
    private static int cli;

    private char[] passAlmacen =claveAlmacen.toCharArray();;

    ArrayList<Asimetrico> claves = new ArrayList<>();

    // CONSTRUCTOR
    public AsimetricoClave(String c, int cli) {
        claves();
        clave = c;
        this.cli = cli;
    }

    public AsimetricoClave() {

    }

    public void claves() {
        if (claves.isEmpty()) {
            claves.add(new Asimetrico(" ", " "));
            claves.add(new Asimetrico("Cliente-1", "prueba1"));
            claves.add(new Asimetrico("Cliente-2", "prueba3"));

        }
    }

    // METODO PARA CIFRAR LA CLAVE CON CIFRADO ASIMETRICO
    public String cifrar() {
        try (
                FileInputStream fis = new FileInputStream(keystore);) {
            
            KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
            ks.load(fis, passAlmacen);

            PublicKey pk = ks.getCertificate(claves.get(cli).getUsuario()).getPublicKey();

            Cipher cifrar = Cipher.getInstance(pk.getAlgorithm());
            cifrar.init(Cipher.ENCRYPT_MODE, pk);

            byte[] bClave = clave.getBytes();
            byte[] claveCifrada = cifrar.doFinal(bClave);

            this.claveb64 = Base64.getEncoder().encodeToString(claveCifrada);

        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
        }

        return this.claveb64;

    }

}
