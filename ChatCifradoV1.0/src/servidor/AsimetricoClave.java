/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servidor;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.PublicKey;
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
    private String claveAlmacen = "prueba2", keystore = "/home/prueba2/.keystore", clave, claveb64="";
    private char[] passAlmacen = claveAlmacen.toCharArray();

    // CONSTRUCTOR
    public AsimetricoClave(String c) {
        clave = c;
        
    }
    
    // METODO PARA CIFRAR LA CLAVE CON CIFRADO ASIMETRICO
    public String cifrar() {
        try (
                FileInputStream fis = new FileInputStream(keystore);) {

            KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
            ks.load(fis, passAlmacen);

            PublicKey pk = ks.getCertificate("prueba1").getPublicKey();

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
