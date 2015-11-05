package org.easystogu.db.access;

import java.util.List;

import org.easystogu.db.table.StockPriceVO;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public class WeekStockPriceTableHelper extends StockPriceTableHelper {
	private static WeekStockPriceTableHelper instance = null;

	public static WeekStockPriceTableHelper getInstance() {
		if (instance == null) {
			instance = new WeekStockPriceTableHelper();
		}
		return instance;
	}

	protected WeekStockPriceTableHelper() {
		super();
		tableName = "WEEK_STOCKPRICE";
		// please modify this SQL in superClass
		INSERT_SQL = "INSERT INTO "
				+ tableName
				+ " (stockId, date, open, high, low, close, volume, lastclose) VALUES (:stockId, :date, :open, :high, :low, :close, :volume, :lastclose)";
		SELECT_CLOSE_PRICE_SQL = "SELECT close AS rtn FROM " + tableName + " WHERE stockId = :stockId ORDER BY DATE";
		SELECT_LOW_PRICE_SQL = "SELECT low AS rtn FROM " + tableName + " WHERE stockId = :stockId ORDER BY DATE";
		SELECT_HIGH_PRICE_SQL = "SELECT high AS rtn FROM " + tableName + " WHERE stockId = :stockId ORDER BY DATE";
		SELECT_BY_STOCKID_AND_BETWEEN_DATE_SQL = "SELECT * FROM " + tableName
				+ " WHERE stockId = :stockId AND date >= :date1 AND date <= :date2 ORDER BY DATE";
		// macd used this sql
		QUERY_BY_STOCKID_SQL = "SELECT * FROM " + tableName + " WHERE stockId = :stockId ORDER BY DATE";
		// avg price, for example MA5, MA10, MA20, MA30
		AVG_CLOSE_PRICE_SQL = "SELECT avg(close) AS rtn from (SELECT close FROM " + tableName
				+ " WHERE stockId = :stockId ORDER BY date DESC LIMIT :limit) AS myma";
		// avg volume, for example MAVOL5, MAVOL10
		AVG_VOLUME_SQL = "SELECT avg(volume) AS rtn from (SELECT volume FROM " + tableName
				+ " WHERE stockId = :stockId ORDER BY date DESC LIMIT :limit) AS mymavol";
		// kdj used this: Low(n)
		SELECT_LOW_N_PRICE_SQL = "SELECT min(low) AS rtn from (SELECT low FROM " + tableName
				+ " WHERE stockId = :stockId ORDER BY date DESC LIMIT :limit) AS mylown";
		// kdj used this: High(n)
		SELECT_HIGH_N_PRICE_SQL = "SELECT max(high) AS rtn from (SELECT high FROM " + tableName
				+ " WHERE stockId = :stockId ORDER BY date DESC LIMIT :limit) AS myhighn";
		// query price by Id and date
		QUERY_BY_STOCKID_DATE_SQL = "SELECT * FROM " + tableName + " WHERE stockId = :stockId AND date = :date";
		// query the last date
		GET_LATEST_STOCK_DATE = "SELECT date as rtn FROM " + tableName + " ORDER BY DATE DESC limit 1";
		// query the latest N date
		QUERY_LATEST_PRICE_N_DATE_STOCKID_SQL = "SELECT * FROM " + tableName
				+ " WHERE stockId = :stockId ORDER BY date DESC LIMIT :limit";
		// query the low price between date1 and date2
		QUERY_LOW_PRICE_BETWEEN_DATE_SQL = "SELECT min(low) AS rtn from (SELECT low from " + tableName
				+ " WHERE stockId = :stockId AND date >= :startDate AND date <= :endDate) AS mylowQuery";
		// query the high price between date1 and date2
		QUERY_HIGH_PRICE_BETWEEN_DATE_SQL = "SELECT max(high) AS rtn from (SELECT high from " + tableName
				+ " WHERE stockId = :stockId AND date >= :startDate AND date <= :endDate) AS myHighQuery";
		QUERY_HIGH_PRICE_DATE_BETWEEN_DATE_SQL = "SELECT date AS rtn from " + tableName
				+ " WHERE stockId = :stockId AND high = :high AND date > :startDate AND date <= :endDate";
		DELETE_BY_STOCKID_SQL = "DELETE FROM " + tableName + " WHERE stockId = :stockId";
		DELETE_BY_STOCKID_AND_DATE_SQL = "DELETE FROM " + tableName + " WHERE stockId = :stockId AND date = :date";
		COUNT_DAYS_BETWEEN_DATE1_DATE2 = "SELECT COUNT(*) FROM " + tableName
				+ " WHERE stockId = :stockId AND DATE >= :date1 AND DATE <= :date2";
		// only use for weekPrice, query the weekPrice based on date
		QUERY_BY_STOCKID_AND_BETWEEN_DATE = "SELECT * FROM " + tableName
				+ " WHERE stockId = :stockId AND DATE >= :date1 AND DATE <= :date2 ORDER BY DATE";
		QUERY_LATEST_N_DATE_STOCKID_SQL = "SELECT date AS rtn FROM " + tableName
				+ " WHERE stockId = :stockId ORDER BY date DESC LIMIT :limit";

		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		WeekStockPriceTableHelper ins = new WeekStockPriceTableHelper();
		try {
			List<StockPriceVO> list = ins.getStockPriceByIdBetweenDate("002336", "2015-04-03", "2015-04-25");
			System.out.println(list.get(0));
			System.out.println(list.get(list.size() - 1));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
