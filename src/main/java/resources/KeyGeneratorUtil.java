package resources;

import java.security.*;
import java.util.Base64;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;

public class KeyGeneratorUtil {

    public static void main(String[] args) throws NoSuchAlgorithmException, IOException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        KeyPair pair = keyGen.generateKeyPair();

        // Convertir a formato PEM
        String privateKeyPEM = encodeToPEM("PRIVATE KEY", pair.getPrivate().getEncoded());
        String publicKeyPEM = encodeToPEM("PUBLIC KEY", pair.getPublic().getEncoded());

        // Crear un directorio 'src/resources/META-INF.resources' si no existe
        File directorio = new File("src/resources/META-INF/resources");
        if (!directorio.exists()) {
            directorio.mkdirs();  // Crea la carpeta y subcarpetas si no existen
        }

        // Guardar en archivos dentro de 'src/resources/META-INF/resources'
        try (FileWriter privateWriter = new FileWriter("src/resources/META-INF/resources/privateKey.pem");
             FileWriter publicWriter = new FileWriter("src/resources/META-INF/resources/publicKey.pem")) {
            privateWriter.write(privateKeyPEM);
            publicWriter.write(publicKeyPEM);
        }

        System.out.println("Claves RSA generadas correctamente en formato PEM.");
    }

    private static String encodeToPEM(String type, byte[] keyBytes) {
        String base64 = Base64.getEncoder().encodeToString(keyBytes);
        return "-----BEGIN " + type + "-----\n" + base64 + "\n-----END " + type + "-----\n";
    }
}
