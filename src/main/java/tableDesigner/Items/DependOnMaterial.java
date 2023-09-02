package tableDesigner.Items;

import Common.Material.Material;

public interface DependOnMaterial {

    Material getMaterial();

    void autoUpdateMaterial();
}
