package org.easystogu.sina.runner;

import java.util.List;

import org.easystogu.config.StockListConfigurationService;
import org.easystogu.db.access.ChuQuanChuXiPriceHelper;
import org.easystogu.db.access.StockPriceTableHelper;
import org.easystogu.db.access.WeekStockPriceTableHelper;
import org.easystogu.db.table.StockPriceVO;
import org.easystogu.utils.WeekdayUtil;

//每日stockprice入库之后计算本周的stockprice，入库
public class DailyWeeklyStockPriceCountAndSaveDBRunner implements Runnable {
	private StockPriceTableHelper stockPriceTable = StockPriceTableHelper.getInstance();
	private WeekStockPriceTableHelper weekStockPriceTable = WeekStockPriceTableHelper.getInstance();
	private String latestDate = stockPriceTable.getLatestStockDate();
	protected ChuQuanChuXiPriceHelper chuQuanChuXiPriceHelper = new ChuQuanChuXiPriceHelper();
	protected StockListConfigurationService stockConfig = StockListConfigurationService.getInstance();

	public void deleteWeekStockPrice(String stockId, String date) {
		weekStockPriceTable.delete(stockId, date);
	}

	public void countAndSave(List<String> stockIds) {
		int index = 0;
		for (String stockId : stockIds) {
			if (index++ % 500 == 0) {
				System.out.println("Process weekly price " + (index) + "/" + stockIds.size());
			}
			this.countAndSaved(stockId);
		}
	}

	public void countAndSaved(String stockId) {
		// first clean one tuple in week_stockprice table
		// loop all this week's date, in fact, only one tuple match and
		// del
		List<String> dates = WeekdayUtil.getWeekWorkingDates(latestDate);
		for (String date : dates) {
			this.deleteWeekStockPrice(stockId, date);
		}

		if ((dates != null) && (dates.size() >= 1)) {
			String firstDate = dates.get(0);
			String lastDate = dates.get(dates.size() - 1);
			List<StockPriceVO> spList = stockPriceTable.getStockPriceByIdAndBetweenDate(stockId, firstDate, lastDate);
			if ((spList != null) && (spList.size() >= 1)) {

				// update price based on chuQuanChuXi event
				chuQuanChuXiPriceHelper.updatePrice(stockId, spList);

				int last = spList.size() - 1;
				// first day
				StockPriceVO mergeVO = spList.get(0).copy();
				// last day
				mergeVO.close = spList.get(last).close;
				mergeVO.date = spList.get(last).date;

				if (spList.size() > 1) {
					for (int j = 1; j < spList.size(); j++) {
						StockPriceVO vo = spList.get(j);
						mergeVO.volume += vo.volume;
						if (mergeVO.high < vo.high) {
							mergeVO.high = vo.high;
						}
						if (mergeVO.low > vo.low) {
							mergeVO.low = vo.low;
						}
					}
				}
				// System.out.println(mergeVO);
				weekStockPriceTable.insert(mergeVO);
			}
		}
	}

	public void run() {
		countAndSave(stockConfig.getAllStockId());
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		StockListConfigurationService stockConfig = StockListConfigurationService.getInstance();
		DailyWeeklyStockPriceCountAndSaveDBRunner runner = new DailyWeeklyStockPriceCountAndSaveDBRunner();
		runner.countAndSave(stockConfig.getAllStockId());
		// runner.countAndSaved("002327");
	}
}
