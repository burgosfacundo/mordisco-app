package utn.back.mordiscoapi.service.impl;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
@Service
public class PinService {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    public String generarPin() {
        StringBuilder pin = new StringBuilder(5);
          for (int i = 0; i < 5; i++) {
               int index = RANDOM.nextInt(CHARACTERS.length());
               pin.append(CHARACTERS.charAt(index));
          }
            return pin.toString();
    }
}


