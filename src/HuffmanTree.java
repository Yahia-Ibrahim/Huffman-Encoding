import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class HuffmanTree {
    private HuffmanTreeNode root;
    private Map<String, Integer> frequencies;
    public Map<String, String> codes;

    public HuffmanTree(FileInputStream inputStream, int n) throws IOException {
        codes = new HashMap<>();
        frequencies = new HashMap<>();
        int bufferSize = 1024 * 1024 * n * 10;   // a chunk of n * 10MB
        byte[] bytes = new byte[bufferSize];
        int bytesRead;
        // reading the file in chunks to construct the frequencies map, for memory considerations
        while ((bytesRead = inputStream.read(bytes)) != -1) {
            this.constructFrequencies(bytes, bytesRead, n);
        }
        this.root = this.constructTree();
        this.mapBytesToCodes(root, "");
    }

    public void constructFrequencies(byte[] data, int bytesRead, int n) {
        StringBuilder binaryString = new StringBuilder();
        int counter = 0;
        for(int i = 0; i < bytesRead; i++) {
            for(int j = 7; j >= 0; j--) {
                int bit = (data[i] >> j) & 1;
                binaryString.append(bit);
            }
            counter++;
            if(counter == n) {
                String key = binaryString.toString();
                frequencies.put(key, frequencies.getOrDefault(binaryString.toString(), 0) + 1);
                counter = 0;
                binaryString.setLength(0);
            }
            else if(i == bytesRead - 1 && counter < n) {
                String key = binaryString.toString();
                frequencies.put(key, frequencies.getOrDefault(binaryString.toString(), 0) + 1);
                counter = 0;
                binaryString.setLength(0);
            }
        }
    }
    private HuffmanTreeNode constructTree() {
        Queue<HuffmanTreeNode> priorityQueue = new PriorityQueue<>();
        Iterator freqIterator = frequencies.entrySet().iterator();
        while (freqIterator.hasNext()) {
            Map.Entry mapElement = (Map.Entry)freqIterator.next();
            priorityQueue.add(new HuffmanTreeLeefNode((int) mapElement.getValue(), (String) mapElement.getKey()));
        }
        while(priorityQueue.size() > 1) {
            HuffmanTreeNode firstChild = priorityQueue.poll();
            HuffmanTreeNode secondChild = priorityQueue.poll();
            assert secondChild != null;
            HuffmanTreeNode parent = new HuffmanTreeNode(firstChild.frequency + secondChild.frequency);
            if(firstChild.frequency > secondChild.frequency) {
                parent.right = firstChild;
                parent.left = secondChild;
            }
            else {
                parent.right = firstChild;
                parent.left = secondChild;
            }
            priorityQueue.add(parent);
        }
        return priorityQueue.poll();
    }
    private void mapBytesToCodes(HuffmanTreeNode node, String code) {
        if(node.left == null && node.right == null) {
            codes.put(((HuffmanTreeLeefNode) node).data, code);
            return;
        }
        assert node.left != null;
        mapBytesToCodes(node.left, code + "0");
        mapBytesToCodes(node.right, code + "1");
    }


}
