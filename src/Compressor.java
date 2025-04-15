import java.io.*;
import java.util.*;

public class Compressor {
    private HuffmanTree huffmanTree;
    public void compressFile(String filePath, int n) {
        try {
            long start = System.currentTimeMillis();
            int bufferSize = 1024 * n * 16;
            FileInputStream inputStream = new FileInputStream(filePath);
            this.huffmanTree = new HuffmanTree(inputStream, n);
            inputStream.close();
            File file = new File(filePath);
            String parentDir = file.getParent();
            String fileName = file.getName();
            String newFileName = "21011554." + String.valueOf(n) + "." + fileName + ".hc";
            String newFilePath = new File(parentDir, newFileName).getPath();
            try (FileInputStream in = new FileInputStream(filePath);
                 OutputStream outputStream = new FileOutputStream(newFilePath, true);
                 ObjectOutputStream out = new ObjectOutputStream(outputStream)) {
                out.writeObject(this.huffmanTree.codes);
                int bytesRead;
                byte[] bytes = new byte[bufferSize];
                while ((bytesRead = in.read(bytes)) != -1) {
                    byte[] encodedBytes = encodeBytes(bytes, n, bytesRead);
                    out.writeObject(encodedBytes);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            File input = new File(filePath);
            File output = new File(newFilePath);
            System.out.println("compression ratio:" + (double) output.length() / (double) input.length());
            System.out.println("time in ms:" + (end - start));
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private byte[] encodeBytes(byte[] bytes, int n, int bytesRead) {
        StringBuilder encodedBytesString = new StringBuilder();
        StringBuilder binaryString = new StringBuilder();
        int counter = 0;
        for(byte b : bytes) {
            for (int i = 7; i >= 0; i--) {
                int bit = (b >> i) & 1;
                binaryString.append(bit);
            }
            counter++;
            if(counter == n) {
                String key = binaryString.toString();
                encodedBytesString.append(this.huffmanTree.codes.get(key));
                counter = 0;
                binaryString.setLength(0);
            }
            bytesRead--;
            if(bytesRead == 0) {
                if(counter < n && counter > 0) {
                    String key = binaryString.toString();
                    encodedBytesString.append(this.huffmanTree.codes.get(key));
                    binaryString.setLength(0);
                }
                break;
            }
        }
        List<Byte> byteList = new ArrayList<>();
        int idx = 0;
        while(idx < encodedBytesString.length()) {
            if(idx + 8 >= encodedBytesString.length()) {
                // adding padding to the byte
                while(encodedBytesString.length() % 8 != 0) {
                    encodedBytesString.append("0");
                }
            }
            byteList.add((byte) Integer.parseInt(
                    encodedBytesString.substring(idx, idx + 8),
                    2));
            idx += 8;
        }
        byte[] byteArray = new byte[byteList.size()];

        for (int i = 0; i < byteList.size(); i++) {
            byteArray[i] = byteList.get(i);
        }
        return byteArray;
    }

    public void decompressFile(String filePath) {
        File file = new File(filePath);
        String parentDir = file.getParent();
        String fileName = file.getName();
        String newFileName = "extracted." + fileName;
        String newFilePath = new File(parentDir, newFileName).getPath();
        try (FileInputStream in = new FileInputStream(filePath);
             ObjectInputStream objectIn = new ObjectInputStream(in);
             FileOutputStream outStream = new FileOutputStream(newFilePath.substring(0, newFilePath.length() - 3), true)){
            long start = System.currentTimeMillis();
            Map<String, String> codes = (Map<String, String>) objectIn.readObject();
            Map<String, String> codesToBytes = new HashMap<>();
            for(Map.Entry<String, String> byteToCode: codes.entrySet()) {
                codesToBytes.put(byteToCode.getValue(), byteToCode.getKey());
            }
            while(true) {
                try {
                    byte[] bytes = (byte[]) objectIn.readObject();
                    byte[] decodedBytes = decodeBytes(bytes, codesToBytes);
                    outStream.write(decodedBytes);
                } catch (EOFException e) {
                    break;
                }
            }
            long end = System.currentTimeMillis();
            System.out.println("time in ms:" + (end - start));
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private byte[] decodeBytes(byte[] bytes, Map<String, String> codesToBytes) {
        StringBuilder decodedBytesString = new StringBuilder();
        for (byte b : bytes) {
            for (int i = 7; i >= 0; i--) {
                decodedBytesString.append((b >> i) & 1);
            }
        }
        List<Byte> decodedBytesList = new ArrayList<>();
        StringBuilder prefix = new StringBuilder();
        for (int i = 0; i < decodedBytesString.length(); i++) {
            prefix.append(decodedBytesString.charAt(i));
            if (codesToBytes.containsKey(prefix.toString())) {
                String byteString = codesToBytes.get(prefix.toString());
                for(int j = 0; j < byteString.length(); j += 8) {
                    decodedBytesList.add((byte) Integer.parseInt(byteString.substring(j, j + 8), 2));
                }
                prefix.setLength(0);
            }
        }
        byte[] decodedBytes = new byte[decodedBytesList.size()];
        for (int i = 0; i < decodedBytesList.size(); i++) {
            decodedBytes[i] = decodedBytesList.get(i);
        }
        return decodedBytes;
    }
}
