package ru.koreanika.utils.receipt.policy;

import ru.koreanika.common.material.Material;
import ru.koreanika.cutDesigner.Shapes.CutShape;
import ru.koreanika.tableDesigner.item.SinkItem;
import ru.koreanika.tableDesigner.item.TableDesignerItem;
import ru.koreanika.utils.news.NewsCard;
import ru.koreanika.utils.news.NewsCardStockCondition;
import ru.koreanika.utils.news.NewsCardStockItem;
import ru.koreanika.utils.news.NewsController;
import ru.koreanika.utils.receipt.domain.Receipt;
import ru.koreanika.utils.receipt.domain.ReceiptItem;
import ru.koreanika.tableDesigner.TableDesignerSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CalculateItemStocks implements Command {

    public static void calculateItemsStocks() {
        new CalculateItemStocks().execute();
    }

    @Override
    public void execute() {
        List<NewsCard> list = NewsController.getNewsController().getStockItemCards();

        Receipt.getItemStocks().clear();
        //sink
        {
            List<NewsCard> stockSinks = new ArrayList<>();
            for (NewsCard nc : list) {
                if (nc.getStockItem() == NewsCardStockItem.SINK) {
                    stockSinks.add(nc);
                }
            }

            for (TableDesignerItem tableDesignerItem : TableDesignerSession.getTableDesignerAdditionalItemsList()) {
                if (tableDesignerItem instanceof SinkItem sinkItem) {
                    String model = sinkItem.getModel().split(" ")[0];
                    Material sinkMaterial = sinkItem.getMaterial();

                    for (NewsCard nc : stockSinks) {
                        if (nc.getStockCondition() == NewsCardStockCondition.MATERIALCOUNT) {
                            double calcCount = 0;
                            for (Map.Entry<CutShape, ReceiptItem> entry : Receipt.getCutShapesAndReceiptItem().entrySet()) {
                                if (nc.getStockConditionMaterialTypes().contains(entry.getKey().getMaterial().getMainType())) {
                                    calcCount += entry.getKey().getShapeSquare() / 10000.0;
                                }
                            }

                            if (nc.getStockItemModel().contains(model) && nc.getStockConditionMaterialTypes().contains(sinkMaterial.getMainType()) && calcCount >= nc.getStockConditionCount()) {
                                double stockPrice = 0;
                                if (Receipt.getItemStocks().get(nc.getHeader()) == null) {
                                    stockPrice = sinkItem.getOnlySinkPrice() * nc.getStockSize();
                                } else {
                                    stockPrice = Receipt.getItemStocks().get(nc.getHeader()) + sinkItem.getOnlySinkPrice() * nc.getStockSize();
                                }
                                Receipt.getItemStocks().put(nc.getHeader(), stockPrice);
                            }
                        }
                    }
                }
            }
        }
    }

}
