public class HuffmanTreeNode implements Comparable<HuffmanTreeNode> {
    public int frequency;
    public HuffmanTreeNode left;
    public HuffmanTreeNode right;

    public HuffmanTreeNode(int frequency) {
        this.frequency = frequency;
    }

    // In order to be inserted inside a priority queue
    @Override
    public int compareTo(HuffmanTreeNode node) {
        return Integer.compare(frequency, node.frequency);
    }

}
