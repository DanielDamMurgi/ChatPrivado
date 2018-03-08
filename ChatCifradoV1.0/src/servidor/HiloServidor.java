package servidor;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import static java.util.Arrays.copyOf;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author Equipo
 */
public class HiloServidor implements Runnable {

    //ATRIBUTOS
    Cliente cli;
    BufferedReader entrada;
    PrintWriter salida;
    Socket socket;
    int nomCli;
    String mensaje = "";
    ArrayList<Cliente> cliente;
    Secreto clave;
    String nomFich = "normas.zip";
    File fichero = null;

    //ATRIBUTOS DESENCRIPTAR
    String contraseña = "";
    String fraseDesencriptada;
    byte[] contraBytes;
    byte[] contraseñaBytes;

    //ATRIBUTOS ENCRIPTAR
    String fraseFinalCifrada;
    byte[] fraseBytes;
    byte[] contraseñaByteEncrip = null;
    byte[] contraseñafinalBytesEncrip;
    byte[] fraseCifrada;

    SecretKey claveSimetrica;
    Cipher cifrar;

    AsimetricoClave asimetricoClave;

    //CONSTRUCTOR
    public HiloServidor(Socket socket, int nomCli, ArrayList<Cliente> cliente, Secreto clave) {
        this.socket = socket;
        this.nomCli = nomCli;
        this.cliente = cliente;
        this.clave = clave;
    }

// ------------------------------------------------------------------------------------------
    //metodo que agrega los clientes al arraylist de la clase Cliente
    public void agregarCliente(int numCli, BufferedReader ent, PrintWriter sal, boolean ban) {
        cli = new Cliente(numCli, ent, sal, ban);
        cliente.add(cli);
    }

// ------------------------------------------------------------------------------------------
    //metodo para enviar los mensajes a los clientes conectados menos en que los envia
    public void enviarMensaje(String m) {
        for (Cliente c : cliente) {
            if (c != cli) {
                c.getSalida().println(encriptar(cli.getNomCli() + " => " + m));
            }
        }
    }

// ------------------------------------------------------------------------------------------
    //metodo que muestra en el servidor y al resto de clientes en cliente que se acaba de conectar o desconectar
    public void conexion(String m) {
        for (Cliente c : cliente) {
            if (c != cli) {
                c.getSalida().println(encriptar("\n\t\tServidor => El " + cli.getNomCli() + " se ha " + m + " chat\n"));

            }
        }
        System.out.println("\n\t\tServidor => El " + cli.getNomCli() + " se ha " + m + " chat\n");
    }

    // ------------------------------------------------------------------------------------------
    //metodo para mostrar kos clientes conectados
    public void mostrarConectados() {
        cli.getSalida().println(encriptar("Clientes conectados =>"));

        for (Cliente c : cliente) {
            if (c != cli) {
                String conect = c.getNomCli();
                cli.getSalida().println(encriptar("\t" + conect + "\n"));
            }

        }
    }

// ------------------------------------------------------------------------------------------
    //metodo que muestra los comandos del servidor
    public void mostrarAyuda() {
        cli.getSalida().println(encriptar("\n\t/l - mostrar los clientes conectados al chat"));
        cli.getSalida().println(encriptar("\n\t/v - version del servidor"));
        //cli.getSalida().println(encriptar("\n\t/n - cambiar nombre"));
        cli.getSalida().println(encriptar("\n\t/x - Salir\n"));
    }

// ------------------------------------------------------------------------------------------
    //metodo que muestra una introduccion a un cliente que se acaba de conectar con el nombre del cliente y los comandos del servidor
    public void logIn() {
        cli.getSalida().println(encriptar("//////////// " + cli.getNomCli() + " bienvenido al servidor de chat ////////////"));
        cli.getSalida().println(encriptar("/////////////////////// COMANDOS DEL SERVIDOR ////////////////////"));
        cli.getSalida().println(encriptar("\n\t/h - mostrar ayuda"));
        mostrarAyuda();
        cli.getSalida().println(encriptar("//////////////////////////////////////////////////////////////////"));

    }

// ------------------------------------------------------------------------------------------
    // metodo que sirve para que un usuario se pueda cambiar el nombre por defecto
    public void cambiarNombre() throws IOException {
        cli.getSalida().println(encriptar("Inserta en nuevo nombre"));

        String nombre = entrada.readLine();
        
        

        String nombreDefecto = cli.getNomCli();
        cli.setNomCli(nombre);
        cli.getSalida().println(encriptar("Nombre cambiado a " + cli.getNomCli()));
        System.out.println("El " + nombreDefecto + " se ha cambiado el nombre a " + cli.getNomCli());
        for (Cliente c : cliente) {
            if (c != cli) {
                c.getSalida().println(encriptar("\n\t\tServidor => El " + nombreDefecto + " se ha cambiado el nombre a " + cli.getNomCli() + "\n"));

            }
        }
    }

// ------------------------------------------------------------------------------------------
    @Override
    public void run() {
        try (
                BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter salida = new PrintWriter(socket.getOutputStream(), true);
                ObjectOutputStream OOS = new ObjectOutputStream(socket.getOutputStream());) {

            agregarCliente(nomCli, entrada, salida, false);

            contraseña = clave.getSecretKey();
            asimetricoClave = new AsimetricoClave(clave.getSecretKey()); // cifra la clave ccon cifrado asimetrico
            clave.setSecretKey(asimetricoClave.cifrar());

            OOS.writeObject(clave); // envia el objeto que contiene la clave cifrada al cliente

            enviarFichero(OOS);

            conexion("conectado al");

            logIn();

            HiloEnviar hiloEnviar = new HiloEnviar(cliente, socket, contraseña);
            Thread hiloE = new Thread(hiloEnviar);
            hiloE.start();

            /*lee los monsajes escritos por los clientes para poder enviarlos al resto de clientes conectados y además contine los comandos
            disponibles en el servidor*/
            while (mensaje != null) {

                mensaje = entrada.readLine();

                mensaje = desencriptar(mensaje);

                if (mensaje.trim().equals("/x") || mensaje.trim().equals("/n") || mensaje.trim().equals("/l") || mensaje.trim().equals("/v") || mensaje.trim().equals("/h")) {

                    if (mensaje.trim().equals("/x")) {//salida del chat
                        conexion("desconectado del");
                        break;
                    } else if (mensaje.trim().equals("/l")) {//mostrar los clientes conectados
                        mostrarConectados();
                    } else if (mensaje.trim().equals("/h")) {//mostrar la ayuda 
                        mostrarAyuda();
                    } else if (mensaje.trim().equals("/n")) {//cambiar el nombre por defecto
                        cambiarNombre();
                    } else if (mensaje.trim().equals("/v")) {//muestra la version de chat
                        cli.getSalida().println(encriptar("Chat Versión V1.3"));
                    }

                } else {

                    System.out.println(cli.getNomCli() + " => " + mensaje);
                    enviarMensaje(mensaje);
                }

            }

            cliente.remove(cli);//elimina a un cliente cuando este sale

        } catch (IOException ex) {
            cliente.remove(cli);//elimina a un cliente cuando este sale
        }
    }

// ------------------------------------------------------------------------------------    
    public static byte[] decodBase64(String a) {
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] decodedByteArray = decoder.decode(a);
        return decodedByteArray;
    }

    public String desencriptar(String frase) {

        try {
            contraBytes = contraseña.getBytes("UTF8");
            contraseñaBytes = copyOf(contraBytes, 24);
            claveSimetrica = new SecretKeySpec(contraseñaBytes, "DESede");
            cifrar = Cipher.getInstance("DESede");

            byte[] frasecifradabytes1 = decodBase64(frase);
            cifrar.init(Cipher.DECRYPT_MODE, claveSimetrica);
            byte[] frasebytedescifrada = cifrar.doFinal(frasecifradabytes1);

            fraseDesencriptada = new String(frasebytedescifrada);

        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
        }

        return fraseDesencriptada;
    }
// --------------------------------------------------------------------------------------    

    public static String codBase64(byte[] a) {
        Base64.Encoder encoder = Base64.getEncoder();
        String b = encoder.encodeToString(a);
        return b;
    }

    public String encriptar(String frase) {

        try {
            contraseñaByteEncrip = contraseña.getBytes("UTF8");
            contraseñafinalBytesEncrip = copyOf(contraseñaByteEncrip, 24);
            claveSimetrica = new SecretKeySpec(contraseñafinalBytesEncrip, "DESede");
            cifrar = Cipher.getInstance("DESede");
            fraseBytes = frase.getBytes("UTF8");

            cifrar.init(Cipher.ENCRYPT_MODE, claveSimetrica);
            fraseCifrada = cifrar.doFinal(fraseBytes);
            fraseFinalCifrada = codBase64(fraseCifrada);

        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
        }

        return fraseFinalCifrada;
    }

    private void enviarFichero(ObjectOutputStream OOS) {

        int leido = 0;
        DividirFichero dividirFich;
        fichero = new File(nomFich);

        try (
                InputStream isFich = new FileInputStream(fichero);
                BufferedInputStream bisFich = new BufferedInputStream(isFich);) {

            do {
                dividirFich = new DividirFichero();
                dividirFich.setNombre(nomFich);
                leido = bisFich.read(dividirFich.getDivFich());

                if (leido < 0) {

                    break;
                }

                dividirFich.setBytes(leido);

                if (leido < 1024) {
                    dividirFich.setUltDivFich(true);
                } else {
                    dividirFich.setUltDivFich(false);
                }
                OOS.writeObject(dividirFich);

            } while (!dividirFich.isUltDivFich());
            System.out.println("Archivo enviado correctamente");

        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

}
