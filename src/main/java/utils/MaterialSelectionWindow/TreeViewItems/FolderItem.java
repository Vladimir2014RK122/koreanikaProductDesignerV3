package utils.MaterialSelectionWindow.TreeViewItems;

public class FolderItem extends MaterialTreeCellItem {

    String name = "";

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
