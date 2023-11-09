package ru.koreanika.utils.receipt.policy;

import ru.koreanika.tableDesigner.item.*;
import ru.koreanika.utils.receipt.domain.ReceiptItem;
import ru.koreanika.tableDesigner.TableDesignerSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TableDesignerItemMapper {

    public static List<ReceiptItem> getEdgesReceiptList() {
        ItemMapper<EdgeItem> mapper = new EdgeItemMapper();
        return TableDesignerSession.getTableDesignerAdditionalItemsList().stream()
                .filter(i -> i instanceof EdgeItem).map(i -> mapper.apply((EdgeItem) i)).filter(Objects::nonNull).toList();
    }

    public static List<ReceiptItem> getLeakGroovesReceiptItems() {
        ItemMapper<LeakGrooveItem> mapper = new LeakGrooveItemMapper();
        return TableDesignerSession.getTableDesignerMainWorkItemsList().stream()
                .filter(i -> i instanceof LeakGrooveItem).map(i -> mapper.apply((LeakGrooveItem) i)).filter(Objects::nonNull).toList();
    }

    public static List<ReceiptItem> getBordersReceiptList() {
        ItemFlatMapper<BorderItem> flatMapper = new BorderItemFlatMapper();
        return TableDesignerSession.getTableDesignerAdditionalItemsList().stream()
                .filter(i -> i instanceof BorderItem).flatMap(i -> flatMapper.apply((BorderItem) i).stream()).filter(Objects::nonNull).toList();
    }

    public static List<ReceiptItem> getSinkAcrylReceiptList() {
        ItemMapper<SinkItem> mapper = new SinkItemMapper();
        return TableDesignerSession.getTableDesignerAdditionalItemsList().stream()
                .filter(i -> i instanceof SinkItem).map(i -> mapper.apply((SinkItem) i)).filter(Objects::nonNull).toList();
    }

    public static List<ReceiptItem> getSinkQuarzReceiptList() {
        ItemMapper<SinkItem> mapper = new QuartzSinkItemMapper();
        return TableDesignerSession.getTableDesignerAdditionalItemsList().stream()
                .filter(i -> i instanceof SinkItem).map(i -> mapper.apply((SinkItem) i)).filter(Objects::nonNull).toList();
    }

    public static List<ReceiptItem> getSinkInstallReceiptList() {
        ItemFlatMapper<SinkItem> mapper = new InstallSinkItemFlatMapper();
        return TableDesignerSession.getTableDesignerAdditionalItemsList().stream()
                .filter(i -> i instanceof SinkItem).flatMap(i -> mapper.apply((SinkItem) i).stream()).filter(Objects::nonNull).toList();
    }

    public static List<ReceiptItem> getJointsReceiptList() {
        ItemMapper<JointItem> mapper = new JointItemMapper();
        return TableDesignerSession.getTableDesignerMainWorkItemsList().stream()
                .filter(i -> i instanceof JointItem).map(i -> mapper.apply((JointItem) i)).filter(Objects::nonNull).toList();
    }

    public static List<ReceiptItem> getCutoutsReceiptList() {
        List<ReceiptItem> cutoutsReceiptItems = new ArrayList<>();

        ItemMapper<CutoutItem> cutoutItemMapper = new CutoutItemMapper();
        List<ReceiptItem> list1 = TableDesignerSession.getTableDesignerAdditionalItemsList().stream()
                .filter(i -> i instanceof CutoutItem).map(i -> cutoutItemMapper.apply((CutoutItem) i))
                .filter(Objects::nonNull).toList();
        cutoutsReceiptItems.addAll(list1);

        RadiatorGroovesItemMapper radiatorGroovesItemMapper = new RadiatorGroovesItemMapper();
        List<ReceiptItem> list2 = TableDesignerSession.getTableDesignerAdditionalItemsList().stream()
                .filter(i -> i instanceof RadiatorGroovesItem).map(i -> radiatorGroovesItemMapper.apply((RadiatorGroovesItem) i))
                .filter(Objects::nonNull).toList();
        cutoutsReceiptItems.addAll(list2);

        return cutoutsReceiptItems;
    }

    public static List<ReceiptItem> getGroovesReceiptList() {
        ItemMapper<GroovesItem> mapper = new GroovesItemMapper();
        return TableDesignerSession.getTableDesignerAdditionalItemsList().stream()
                .filter(i -> i instanceof GroovesItem).map(i -> mapper.apply((GroovesItem) i)).filter(Objects::nonNull).toList();
    }

    public static List<ReceiptItem> getRodsReceiptList() {
        ItemMapper<RodsItem> mapper = new RodsItemMapper();
        return TableDesignerSession.getTableDesignerAdditionalItemsList().stream()
                .filter(i -> i instanceof RodsItem).map(i -> mapper.apply((RodsItem) i)).filter(Objects::nonNull).toList();
    }

    public static List<ReceiptItem> getRadiusReceiptList() {
        ItemMapper<RadiusItem> mapper = new RadiusItemMapper();
        return TableDesignerSession.getTableDesignerMainWorkItemsList().stream()
                .filter(i -> i instanceof RadiusItem).map(i-> mapper.apply((RadiusItem) i)).filter(Objects::nonNull).toList();
    }

    public static List<ReceiptItem> getMetalFootingReceiptList() {
        ItemMapper<MetalFootingItem> mapper = new MetalFootingItemMapper();
        return TableDesignerSession.getTableDesignerAdditionalWorkItemsList().stream()
                .filter(i -> i instanceof MetalFootingItem)
                .map(i -> mapper.apply((MetalFootingItem) i))
                .filter(Objects::nonNull)
                .toList();
    }

    public static List<ReceiptItem> getPlywoodReceiptList() {
        ItemMapper<PlywoodItem> mapper = new PlywoodItemMapper();
        return TableDesignerSession.getTableDesignerAdditionalWorkItemsList().stream()
                .filter(i -> i instanceof PlywoodItem)
                .map(i -> mapper.apply((PlywoodItem) i))
                .filter(Objects::nonNull)
                .toList();
    }

    public static List<ReceiptItem> getStonePolishingReceiptList() {
        ItemMapper<StonePolishingItem> mapper = new StonePolishingItemMapper();
        return TableDesignerSession.getTableDesignerAdditionalWorkItemsList().stream()
                .filter(item -> item instanceof StonePolishingItem)
                .map(item -> mapper.apply((StonePolishingItem) item))
                .filter(Objects::nonNull)
                .toList();
    }

    public static List<ReceiptItem> getSiphonReceiptList() {
        ItemMapper<SiphonItem> mapper = new SiphonItemMapper();
        return TableDesignerSession.getTableDesignerAdditionalItemsList().stream().filter(i -> i instanceof SiphonItem)
                .map(i -> mapper.apply((SiphonItem) i))
                .filter(Objects::nonNull)
                .toList();
    }

    public static List<ReceiptItem> getMeasurerReceiptList() {
        ItemMapper<MeasurerItem> mapper = new MeasurerItemMapper();
        return TableDesignerSession.getTableDesignerMainWorkItemsList().stream().filter(i -> i instanceof MeasurerItem)
                .map(i -> mapper.apply((MeasurerItem) i))
                .filter(Objects::nonNull)
                .toList();
    }

    public static List<ReceiptItem> getDeliveryReceiptList() {
        ItemFlatMapper<DeliveryItem> flatMapper = new DeliveryItemFlatMapper();
        return TableDesignerSession.getTableDesignerMainWorkItemsList().stream().filter(i -> i instanceof DeliveryItem deliveryItem)
                .flatMap(i -> flatMapper.apply((DeliveryItem) i).stream())
                .filter(Objects::nonNull)
                .toList();
    }

    public static List<ReceiptItem> getMountingReceiptList() {
        ItemMapper<MountingItem> mapper = new MountingItemMapper();
        return TableDesignerSession.getTableDesignerMainWorkItemsList().stream().filter(i -> i instanceof MountingItem)
                .map(i -> mapper.apply((MountingItem) i))
                .filter(Objects::nonNull)
                .toList();
    }

    public static List<ReceiptItem> getPlumbingAlveusReceiptList() {
        ItemMapper<PlumbingAlveusItem> mapper = new PlumbingAlveusItemMapper();
        return TableDesignerSession.getTableDesignerAdditionalItemsList().stream().filter(i -> i instanceof PlumbingAlveusItem)
                .map(i -> mapper.apply((PlumbingAlveusItem) i))
                .filter(Objects::nonNull)
                .toList();
    }

    public static List<ReceiptItem> getPlumbingReceiptList() {
        ItemMapper<PlumbingItem> mapper = new PlumbingItemMapper();
        return TableDesignerSession.getTableDesignerAdditionalItemsList().stream().filter(i -> i instanceof PlumbingItem plumbingItem)
                .map(i -> mapper.apply((PlumbingItem) i))
                .filter(Objects::nonNull)
                .toList();
    }

    public static List<ReceiptItem> getPalletReceiptList() {
        ItemMapper<PalletItem> mapper = new PalletItemMapper();
        return TableDesignerSession.getTableDesignerAdditionalItemsList().stream().filter(i -> i instanceof PalletItem)
                .map(i -> mapper.apply((PalletItem) i))
                .filter(Objects::nonNull)
                .toList();
    }

    public static List<ReceiptItem> getCustomReceiptList() {
        ItemMapper<CustomItem> mapper = new CustomItemMapper();
        return TableDesignerSession.getTableDesignerMainWorkItemsList().stream().filter(i -> i instanceof CustomItem customItem)
                .map(i -> mapper.apply((CustomItem) i))
                .filter(Objects::nonNull)
                .toList();
    }

    public static List<ReceiptItem> getDiscountReceiptList() {
        ItemMapper<DiscountItem> mapper = new DiscountItemMapper();
        return TableDesignerSession.getTableDesignerMainWorkItemsList().stream().filter(i -> i instanceof DiscountItem)
                .map(i -> mapper.apply((DiscountItem) i))
                .filter(Objects::nonNull)
                .toList();
    }
}
