public class HuffmanTreeLeefNode extends HuffmanTreeNode{
    public String data;
    public HuffmanTreeLeefNode(int frequency, String data) {
        super(frequency);
        this.data = data;
        this.left = null;
        this.right = null;
    }
}
