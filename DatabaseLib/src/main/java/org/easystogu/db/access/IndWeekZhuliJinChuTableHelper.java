package org.easystogu.db.access;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public class IndWeekZhuliJinChuTableHelper extends IndZhuliJinChuTableHelper {
	private static IndWeekZhuliJinChuTableHelper instance = null;

	public static IndWeekZhuliJinChuTableHelper getInstance() {
		if (instance == null) {
			instance = new IndWeekZhuliJinChuTableHelper();
		}
		return instance;
	}

	protected IndWeekZhuliJinChuTableHelper() {
		super();
		tableName = "IND_WEEK_ZHULIJINCHU";
		// please modify this SQL in superClass
		INSERT_SQL = "INSERT INTO " + tableName
				+ " (stockId, date, duofang, kongfang) VALUES (:stockId, :date, :duofang, :kongfang)";
		QUERY_BY_ID_AND_DATE_SQL = "SELECT * FROM " + tableName + " WHERE stockId = :stockId AND date = :date";
		QUERY_ALL_BY_ID_SQL = "SELECT * FROM " + tableName + " WHERE stockId = :stockId ORDER BY date";
		QUERY_LATEST_N_BY_ID_SQL = "SELECT * FROM " + tableName
				+ " WHERE stockId = :stockId ORDER BY date DESC LIMIT :limit";
		DELETE_BY_STOCKID_SQL = "DELETE FROM " + tableName + " WHERE stockId = :stockId";
		DELETE_BY_STOCKID_AND_DATE_SQL = "DELETE FROM " + tableName + " WHERE stockId = :stockId AND date = :date";
		DELETE_BY_DATE_SQL = "DELETE FROM " + tableName + " WHERE date = :date";

		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		IndWeekZhuliJinChuTableHelper ins = new IndWeekZhuliJinChuTableHelper();
		try {
			System.out.println(ins.getAllZhuliJinChu("600359").size());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
