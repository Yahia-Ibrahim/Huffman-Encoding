import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
public class Main {

    public static String generateSHA256Hash(byte[] input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] encodedhash = digest.digest(input);

            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedhash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws IOException {
        Compressor comp = new Compressor();
        if (args.length < 2) {
            System.out.println("Error");
            return;
        }
        String operation = args[0];
        String filePath = args[1];

        switch (operation) {
            case "c":
                if (args.length < 3) {
                    System.out.println("Error");
                    return;
                }
                int n;
                try {
                    n = Integer.parseInt(args[2]);
                } catch (NumberFormatException e) {
                    System.out.println("Error");
                    return;
                }
                comp.compressFile(filePath, n);
                break;

            case "d":
                comp.decompressFile(filePath);
                break;

            default:
                System.out.println("Error: Invalid operation.");
        }

    }
}
