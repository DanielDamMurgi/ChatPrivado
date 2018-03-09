package cliente;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.util.ArrayList;
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
    private static String claveBuena;
    private static char[] claveAlmacenc;
    private static char[] keyClavesc;
    private static byte[] clavec;
    private static ArrayList<Asimetrico> claves = new ArrayList<>();
    private static int cli;

    // CONSTRUCCTORES
    public AsimetricoClave() {

    }

    public AsimetricoClave(String c, int cli) {

        clavec = Base64.getDecoder().decode(c);
        this.cli = cli;
        claves();
        descifrar();
    }

    public static void claves() {
        
        claves.clear();
        
 
        if (claves.isEmpty()) {
            claves.add(new Asimetrico(" ", " ", " ", " ", " "));
            claves.add(new Asimetrico("Cliente-1", "/home/prueba1/.keystore", "prueba1", "claveprueba1", "prueba1"));
            claves.add(new Asimetrico("Cliente-2", "/home/prueba3/.keystore", "prueba3", "claveprueba3", "prueba3"));
        }

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
                FileInputStream fis = new FileInputStream(claves.get(cli).getKeystore());) {

            claveAlmacenc = claves.get(cli).getClaveAlmacen().toCharArray();
            keyClavesc = claves.get(cli).getKeyClaves().toCharArray();

            KeyStore ks1 = KeyStore.getInstance(KeyStore.getDefaultType());
            ks1.load(fis, claveAlmacenc);

            PrivateKey privk = (PrivateKey) ks1.getKey(claves.get(cli).getUsuario(), keyClavesc);

            Cipher cifrado = Cipher.getInstance(privk.getAlgorithm());
            cifrado.init(Cipher.DECRYPT_MODE, privk);

            byte[] frasedesb = cifrado.doFinal(clavec);

            claveBuena = new String(frasedesb);

        } catch (Exception ex) {
            System.err.println("Error: " + ex.getMessage());
        }
    }// FIN METODO DESCIFRAR

}
