package cliente;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.util.Base64;
import javax.crypto.Cipher;

/**
 *
 * @author usuario2
 */

/*
    CLASE QUE DESCIFRA LA CLAVE INICIAL QUE ENVIA EL SERVIDOR AL CLIENTE A TRAVÉS DE CIFRADO
    ASIMETRICO.
 */
public class AsimetricoClave {

    // ATRIBUTOS
    private static String keystore = "/home/prueba1/.keystore", claveAlmacen = "prueba1", keyClaves = "claveprueba1", claveBuena;
    private static char[] claveAlmacenc = claveAlmacen.toCharArray();
    private static char[] keyClavesc = keyClaves.toCharArray();
    private static byte[] clavec = null;

    // CONSTRUCCTORES
    public AsimetricoClave() {

    }

    public AsimetricoClave(String c) {
        clavec = Base64.getDecoder().decode(c);
        descifrar();
    }

    //GETTERS
    public static String getClaveBuena() {
        return claveBuena;
    }

    //SETTERS
    public void setClaveBuena(String claveBuena) {
        AsimetricoClave.claveBuena = claveBuena;
    }

//-------------------------------------------------------------------------------------------------
    // METODO PARA DESCIFRAR DE FORMA ASIMETRICA
    public static void descifrar() {
        try (
                FileInputStream fis = new FileInputStream(keystore);) {

            KeyStore ks1 = KeyStore.getInstance(KeyStore.getDefaultType());
            ks1.load(fis, claveAlmacenc);

            PrivateKey privk = (PrivateKey) ks1.getKey("prueba1", keyClavesc);

            Cipher cifrado = Cipher.getInstance(privk.getAlgorithm());
            cifrado.init(Cipher.DECRYPT_MODE, privk);

            byte[] frasedesb = cifrado.doFinal(clavec);

            claveBuena = new String(frasedesb);

        } catch (Exception ex) {
            System.err.println("Error: " + ex.getMessage());
        }
    }// FIN METODO DESCIFRAR

}
