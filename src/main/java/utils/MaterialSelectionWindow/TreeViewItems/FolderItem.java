package utils.MaterialSelectionWindow.TreeViewItems;

public class FolderItem implements MaterialTreeCellItem {

    private final String name;

    public FolderItem(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getFullName() {
        return name;
    }
}
