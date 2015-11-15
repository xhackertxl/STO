package org.easystogu.indicator.runner;

import java.util.List;

import org.easystogu.db.access.ChuQuanChuXiPriceHelper;
import org.easystogu.db.access.IndShenXianTableHelper;
import org.easystogu.db.access.StockPriceTableHelper;
import org.easystogu.db.table.ShenXianVO;
import org.easystogu.db.table.StockPriceVO;
import org.easystogu.file.access.CompanyInfoFileHelper;
import org.easystogu.indicator.ShenXianHelper;
import org.easystogu.indicator.runner.utils.StockPriceFetcher;
import org.easystogu.multirunner.MultThreadRunner;
import org.easystogu.utils.Strings;

public class DailyShenXianCountAndSaveDBRunner implements Runnable {
	protected StockPriceTableHelper stockPriceTable = StockPriceTableHelper.getInstance();
	protected IndShenXianTableHelper shenXianTable = IndShenXianTableHelper.getInstance();
	private ShenXianHelper shenXianHelper = new ShenXianHelper();
	protected ChuQuanChuXiPriceHelper chuQuanChuXiPriceHelper = new ChuQuanChuXiPriceHelper();
	protected CompanyInfoFileHelper stockConfig = CompanyInfoFileHelper.getInstance();
	protected MultThreadRunner parentRunner;

	public DailyShenXianCountAndSaveDBRunner() {

	}

	public DailyShenXianCountAndSaveDBRunner(MultThreadRunner parentRunner) {
		this.parentRunner = parentRunner;
		this.parentRunner.newTaskInfo(this.getClass().getSimpleName());
	}

	public void deleteShenXian(String stockId, String date) {
		shenXianTable.delete(stockId, date);
	}

	public void deleteShenXian(String stockId) {
		shenXianTable.delete(stockId);
	}

	public void deleteShenXian(List<String> stockIds) {
		int index = 0;
		for (String stockId : stockIds) {
			System.out.println("Delete ShenXian for " + stockId + " " + (++index) + "/" + stockIds.size());
			this.deleteShenXian(stockId);
		}
	}

	public void countAndSaved(String stockId) {
		List<StockPriceVO> priceList = stockPriceTable.getStockPriceById(stockId);

		if (priceList.size() <= 108) {
			// System.out.println("StockPrice data is less than 108, skip " +
			// stockId);
			return;
		}

		// update price based on chuQuanChuXi event
		chuQuanChuXiPriceHelper.updatePrice(stockId, priceList);

		List<Double> close = StockPriceFetcher.getClosePrice(priceList);

		double[][] shenXian = shenXianHelper.getShenXianList(close.toArray(new Double[0]));

		int length = shenXian[0].length;

		ShenXianVO vo = new ShenXianVO();
		vo.setH1(Strings.convert2ScaleDecimal(shenXian[0][length - 1]));
		vo.setH2(Strings.convert2ScaleDecimal(shenXian[1][length - 1]));
		vo.setH3(Strings.convert2ScaleDecimal(shenXian[2][length - 1]));
		vo.setStockId(stockId);
		vo.setDate(priceList.get(length - 1).date);

		this.deleteShenXian(stockId, vo.date);
		shenXianTable.insert(vo);

	}

	public void countAndSaved(List<String> stockIds) {
		int index = 0;
		for (String stockId : stockIds) {
			if (index++ % 500 == 0) {
				System.out.println("ShenXian countAndSaved: " + stockId + " " + (index) + "/" + stockIds.size());
			}
			this.countAndSaved(stockId);
		}
	}

	public void run() {
		this.parentRunner.startTaskInfo(this.getClass().getSimpleName());
		countAndSaved(stockConfig.getAllStockId());
		this.parentRunner.stopTaskInfo(this.getClass().getSimpleName());
	}

	// TODO Auto-generated method stub
	public static void main(String[] args) {
		CompanyInfoFileHelper stockConfig = CompanyInfoFileHelper.getInstance();
		List<String> stockIds = stockConfig.getAllStockId();
		DailyShenXianCountAndSaveDBRunner runner = new DailyShenXianCountAndSaveDBRunner();
		runner.countAndSaved(stockIds);
		// runner.countAndSaved("002194");
	}
}
