package ru.koreanika.tableDesigner.item;

import ru.koreanika.common.material.Material;

public interface DependOnMaterial {

    Material getMaterial();

    void autoUpdateMaterial();
}
