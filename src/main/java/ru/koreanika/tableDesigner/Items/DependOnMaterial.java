package ru.koreanika.tableDesigner.Items;

import ru.koreanika.Common.Material.Material;

public interface DependOnMaterial {

    Material getMaterial();

    void autoUpdateMaterial();
}
