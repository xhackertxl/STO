package org.easystogu.indicator.runner.history;

import java.util.List;

import org.easystogu.db.access.ChuQuanChuXiPriceHelper;
import org.easystogu.db.access.IndZhuliJinChuTableHelper;
import org.easystogu.db.access.StockPriceTableHelper;
import org.easystogu.db.table.StockPriceVO;
import org.easystogu.db.table.ZhuliJinChuVO;
import org.easystogu.file.access.CompanyInfoFileHelper;
import org.easystogu.indicator.ZhuliJinChuHelper;
import org.easystogu.utils.Strings;

public class HistoryZhuliJinChuCountAndSaveDBRunner {

	protected StockPriceTableHelper stockPriceTable = StockPriceTableHelper.getInstance();
	protected IndZhuliJinChuTableHelper zhuliJinChuTable = IndZhuliJinChuTableHelper.getInstance();
	private ZhuliJinChuHelper zhuliJinChuHelper = new ZhuliJinChuHelper();
	protected ChuQuanChuXiPriceHelper chuQuanChuXiPriceHelper = new ChuQuanChuXiPriceHelper();

	public void deleteZhuliJinChu(String stockId) {
		zhuliJinChuTable.delete(stockId);
	}

	public void deleteZhuliJinChu(List<String> stockIds) {
		int index = 0;
		for (String stockId : stockIds) {
			System.out.println("Delete ZhuliJinChu for " + stockId + " " + (++index) + " of " + stockIds.size());
			this.deleteZhuliJinChu(stockId);
		}
	}

	public void countAndSaved(String stockId) {
		List<StockPriceVO> priceList = stockPriceTable.getStockPriceById(stockId);

		if (priceList.size() <= 34) {
			System.out.println("StockPrice data is less than 34, skip " + stockId);
			return;
		}

		// update price based on chuQuanChuXi event
		chuQuanChuXiPriceHelper.updatePrice(stockId, priceList);

		// list is order by date
		int length = priceList.size();
		double[] var1 = new double[length];
		int index = 0;
		for (StockPriceVO vo : priceList) {
			var1[index++] = (2 * vo.close + vo.high + vo.low) / 4;
		}

		double[][] zhuliJinChu = zhuliJinChuHelper.getZhuliJinChuList(var1);

		for (int i = 0; i < zhuliJinChu[0].length; i++) {
			ZhuliJinChuVO vo = new ZhuliJinChuVO();
			vo.setDuofang(Strings.convert2ScaleDecimal(zhuliJinChu[0][i]));
			vo.setKongfang(Strings.convert2ScaleDecimal(zhuliJinChu[1][i]));
			vo.setStockId(stockId);
			vo.setDate(priceList.get(i).date);

			try {
				// if (vo.date.compareTo("2015-06-29") >= 0)
				if (zhuliJinChuTable.getZhuliJinChu(vo.stockId, vo.date) == null) {
					zhuliJinChuTable.insert(vo);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void countAndSaved(List<String> stockIds) {
		int index = 0;
		for (String stockId : stockIds) {
			if (index++ % 100 == 0)
				System.out.println("ZhuliJinChu countAndSaved: " + stockId + " " + (index) + " of " + stockIds.size());
			this.countAndSaved(stockId);
		}
	}

	// TODO Auto-generated method stub
	// 一次性计算数据库中所有ShenXian数据，入库
	public static void main(String[] args) {
		CompanyInfoFileHelper stockConfig = CompanyInfoFileHelper.getInstance();
		HistoryZhuliJinChuCountAndSaveDBRunner runner = new HistoryZhuliJinChuCountAndSaveDBRunner();
		runner.countAndSaved(stockConfig.getAllStockId());
		// runner.countAndSaved("600000");
	}

}
