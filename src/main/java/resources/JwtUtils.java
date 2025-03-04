package resources;

import io.smallrye.jwt.build.Jwt;
import java.time.Duration;
import java.util.Set;

public class JwtUtils {

    public static String generateToken(String correo, Set<String> roles) {
        return Jwt.issuer("http://localhost")  // Emisor en local
                .subject(correo)
                .groups(roles)  // Asigna roles al usuario
                .expiresIn(Duration.ofHours(2))  // Expira en 2 horas
                .sign();
    }
}
