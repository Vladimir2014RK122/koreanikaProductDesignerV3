package cutDesigner;

public class MaterialSheetInfoRow {

    private String material;
    private String depth;
    private String percent;

    public MaterialSheetInfoRow(String material, String depth, String percent) {
        this.material = material;
        this.depth = depth;
        this.percent = percent;
    }

    public String getMaterial() {
        return material;
    }

    public String getDepth() {
        return depth;
    }

    public String getPercent() {
        return percent;
    }
}
