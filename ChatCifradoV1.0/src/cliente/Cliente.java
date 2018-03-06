/*github version*/
package cliente;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import static java.lang.Thread.sleep;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import static java.util.Arrays.copyOf;
import java.util.Base64;
import java.util.Scanner;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import servidor.Secreto;
import servidor.DividirFichero;

/**
 *
 * @author Equipo
 */
public class Cliente {

    // ATRIBUTOS
    static String contraseña = "";
    static String fraseFinalCifrada;
    static byte[] fraseBytes;
    static byte[] contraseñaBytes = null;
    static byte[] contraseñaFinalBytes;
    static byte[] fraseCifrada;
    static SecretKey claveSimetrica;
    static Cipher cifrar;
    private static Secreto clave;
    private static boolean baneado = false;
    private static AsimetricoClave asimetricoClave;

    //ATRIBUTOS FICHERO
    private static File fichero;
    private static Object fichObject = null;
    private static String directorio;
    private static boolean generado = false;
    private static DividirFichero divFich;

    // GETTERS
    public static boolean isBaneado() {
        return baneado;
    }

    //SETTERS
    public static void setBaneado(boolean baneado) {
        Cliente.baneado = baneado;
    }

//------------------------------------------------------------------------------------------------- 
    //hilo para leer y mostrar los mensajes que envia el servidor
    public static void leerMensajes(BufferedReader entrada, String clave, Socket socket) {
        HiloCliente hiloMostrar = new HiloCliente(entrada, clave, socket);
        Thread hilo = new Thread(hiloMostrar);
        hilo.start();
    }
//-------------------------------------------------------------------------------------------------   

    public static void main(String[] args) throws ClassNotFoundException, InterruptedException {
        InetAddress direccion = null;
        int puerto = 0;
        Scanner teclado = new Scanner(System.in);
        String mensaje = "";

        if (args.length < 2) { // comprueba que se ha insertado la ip y el puerto
            System.out.println("No has puestro correctamente la Ip o el Puerto");
            System.exit(0);
        }

        try {

            direccion = InetAddress.getByName(args[0]); //recoge la ip que se ha insertado
            puerto = Integer.parseInt(args[1]);// recoge el puerto que se ha insertado

        } catch (UnknownHostException e) {
            System.out.println("Error al insertar la ip");
            System.exit(0);

        } catch (NumberFormatException ex) {
            System.out.println("Error al insertar el puerto");
            System.exit(0);
        }
//-------------------------------------------------------------------------------------------------

        try (
                Socket tuberia = new Socket(direccion, puerto);//Se habre la conexión con el servidor a través de la direccion y puerto
                BufferedReader entrada = new BufferedReader(new InputStreamReader(tuberia.getInputStream()));//lee los mensajes que se envian por la tuberia
                PrintWriter salida = new PrintWriter(tuberia.getOutputStream(), true);//envia mensajes por la tuberia
                ObjectInputStream OIS = new ObjectInputStream(tuberia.getInputStream());) {

            Object c = OIS.readObject();
            if (c instanceof Secreto) {
                clave = (Secreto) c; // recibe el objeto que contiene la clave cifrada
            }
            recibirFichero(OIS, fichero);

            asimetricoClave = new AsimetricoClave(clave.getSecretKey()); // descifra la clave cifrada con cifrado asimetrico
            contraseña = asimetricoClave.getClaveBuena();

            leerMensajes(entrada, contraseña, tuberia); //metodo que contiene un hilo para leer y mostrar los mensajes que envia el servidor

            while (mensaje != null) { //while que envia los mensajes que escribe el cliente al servidor.
                if (baneado) {
                    System.out.println("......");
                    sleep(420000);
                    baneado = false;

                } else {
                    mensaje = teclado.nextLine();

                    String aux = encriptar(mensaje);
                    salida.println(aux);

                    if (mensaje.equals("/x")) {

                        System.exit(0);
                    }

                }

            }

        } catch (IOException ex) {

        }

    } //FIN METODO PRINCIPAL

//-------------------------------------------------------------------------------------------------
    public static String codBase64(byte[] a) {
        Base64.Encoder encoder = Base64.getEncoder();
        String b = encoder.encodeToString(a);
        return b;
    } //FIN METODO CODBASE64

//-------------------------------------------------------------------------------------------------
    // METODO QUE ENCRIPTA LOS MENAJES QUE ENVIA EL CLIENTE AL RESTO DE CLIENTES
    public static String encriptar(String frase) {

        try {
            contraseñaBytes = contraseña.getBytes("UTF8");
            contraseñaFinalBytes = copyOf(contraseñaBytes, 24);
            claveSimetrica = new SecretKeySpec(contraseñaFinalBytes, "DESede");
            cifrar = Cipher.getInstance("DESede");

            fraseBytes = frase.getBytes("UTF8");
            cifrar.init(Cipher.ENCRYPT_MODE, claveSimetrica);

            fraseCifrada = cifrar.doFinal(fraseBytes);

            fraseFinalCifrada = codBase64(fraseCifrada);

        } catch (UnsupportedEncodingException ex) {

        } catch (NoSuchAlgorithmException ex) {

        } catch (NoSuchPaddingException ex) {

        } catch (InvalidKeyException ex) {

        } catch (IllegalBlockSizeException ex) {

        } catch (BadPaddingException ex) {

        }

        return fraseFinalCifrada;
    } //FIN METODO ENCRIPTAR

    //-------------------------------------------------------------------------------------------------
    public static void recibirFichero(ObjectInputStream in, File fich) {
        try {

            FileOutputStream fos = null;
            do {
                divFich = new DividirFichero();

                fichObject = in.readObject();

                directorio = System.getProperty("user.home");

                if (fichObject instanceof DividirFichero) {
                    divFich = (DividirFichero) fichObject;

                    if (!generado) {
                        fich = new File(directorio + "/" + divFich.getNombre());
                        try {
                            fos = new FileOutputStream(fich);
                            generado = true;

                        } catch (Exception ex) {
                            System.out.println("Error: " + ex.getMessage());
                        };
                    }

                    fos.write(divFich.getDivFich(), 0, divFich.getBytes());
                    System.out.println("Archivo de normas descargado en: " + fich.getPath());

                } else {
                    System.out.println("Error con el tipo de fichero");
                }
                {
                    break;
                }
            } while (!divFich.isUltDivFich());
        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());

        }
    }
}
