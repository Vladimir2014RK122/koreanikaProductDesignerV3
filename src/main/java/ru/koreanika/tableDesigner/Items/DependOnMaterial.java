package ru.koreanika.tableDesigner.Items;

import ru.koreanika.common.material.Material;

public interface DependOnMaterial {

    Material getMaterial();

    void autoUpdateMaterial();
}
