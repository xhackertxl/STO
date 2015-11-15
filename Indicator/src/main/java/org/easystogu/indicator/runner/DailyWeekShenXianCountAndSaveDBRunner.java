package org.easystogu.indicator.runner;

import java.util.List;

import org.easystogu.db.access.IndWeekShenXianTableHelper;
import org.easystogu.db.access.WeekStockPriceTableHelper;
import org.easystogu.file.access.CompanyInfoFileHelper;
import org.easystogu.multirunner.MultThreadRunner;
import org.easystogu.utils.WeekdayUtil;

public class DailyWeekShenXianCountAndSaveDBRunner extends DailyShenXianCountAndSaveDBRunner {
    public DailyWeekShenXianCountAndSaveDBRunner(MultThreadRunner parentRunner) {
        super(parentRunner);
    }
    
	public DailyWeekShenXianCountAndSaveDBRunner() {
		stockPriceTable = WeekStockPriceTableHelper.getInstance();
		shenXianTable = IndWeekShenXianTableHelper.getInstance();
	}

	@Override
	public void deleteShenXian(String stockId, String date) {
		List<String> dates = WeekdayUtil.getWeekWorkingDates(date);
		// first clean one tuple in week_stockprice table
		// loop all this week's date, in fact, only one tuple match and del
		for (String d : dates) {
			shenXianTable.delete(stockId, d);
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		CompanyInfoFileHelper stockConfig = CompanyInfoFileHelper.getInstance();
		DailyWeekShenXianCountAndSaveDBRunner runner = new DailyWeekShenXianCountAndSaveDBRunner();
		runner.countAndSaved(stockConfig.getAllStockId());
		// runner.countAndSaved("600825");
	}

}
