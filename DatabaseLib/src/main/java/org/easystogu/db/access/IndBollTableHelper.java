package org.easystogu.db.access;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.easystogu.db.ds.PostgreSqlDataSourceFactory;
import org.easystogu.db.table.BollVO;
import org.easystogu.log.LogHelper;
import org.slf4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public class IndBollTableHelper {
	private static Logger logger = LogHelper.getLogger(IndBollTableHelper.class);
	private static IndBollTableHelper instance = null;
	protected DataSource dataSource = PostgreSqlDataSourceFactory.createDataSource();
	protected String tableName = "IND_BOLL";
	protected String INSERT_SQL = "INSERT INTO " + tableName
			+ " (stockId, date, mb, up, dn) VALUES (:stockId, :date, :mb, :up, :dn)";
	protected String QUERY_BY_ID_AND_DATE_SQL = "SELECT * FROM " + tableName
			+ " WHERE stockId = :stockId AND date = :date";
	protected String QUERY_ALL_BY_ID_SQL = "SELECT * FROM " + tableName + " WHERE stockId = :stockId ORDER BY date";
	protected String QUERY_LATEST_N_BY_ID_SQL = "SELECT * FROM " + tableName
			+ " WHERE stockId = :stockId ORDER BY date DESC LIMIT :limit";
	protected String DELETE_BY_STOCKID_SQL = "DELETE FROM " + tableName + " WHERE stockId = :stockId";
	protected String DELETE_BY_STOCKID_AND_DATE_SQL = "DELETE FROM " + tableName
			+ " WHERE stockId = :stockId AND date = :date";
	protected String DELETE_BY_DATE_SQL = "DELETE FROM " + tableName + " WHERE date = :date";

	protected NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public static IndBollTableHelper getInstance() {
		if (instance == null) {
			instance = new IndBollTableHelper();
		}
		return instance;
	}

	protected IndBollTableHelper() {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	private static final class BollVOMapper implements RowMapper<BollVO> {
		public BollVO mapRow(ResultSet rs, int rowNum) throws SQLException {
			BollVO vo = new BollVO();
			vo.setStockId(rs.getString("stockId"));
			vo.setDate(rs.getString("date"));
			vo.setMb(rs.getDouble("mb"));
			vo.setUp(rs.getDouble("up"));
			vo.setDn(rs.getDouble("dn"));
			return vo;
		}
	}

	private static final class DefaultPreparedStatementCallback implements PreparedStatementCallback<Integer> {
		public Integer doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
			return ps.executeUpdate();
		}
	}

	public void insert(BollVO vo) {
		logger.debug("insert for {}", vo);

		try {
			MapSqlParameterSource namedParameters = new MapSqlParameterSource();
			namedParameters.addValue("stockId", vo.getStockId());
			namedParameters.addValue("date", vo.getDate());
			namedParameters.addValue("mb", vo.getMb());
			namedParameters.addValue("up", vo.getUp());
			namedParameters.addValue("dn", vo.getDn());

			namedParameterJdbcTemplate.execute(INSERT_SQL, namedParameters, new DefaultPreparedStatementCallback());
		} catch (Exception e) {
			logger.error("exception meets for insert vo: " + vo, e);
			e.printStackTrace();
		}
	}

	public void insert(List<BollVO> list) throws Exception {
		for (BollVO vo : list) {
			this.insert(vo);
		}
	}

	public void delete(String stockId) {
		try {
			MapSqlParameterSource namedParameters = new MapSqlParameterSource();
			namedParameters.addValue("stockId", stockId);
			namedParameterJdbcTemplate.execute(DELETE_BY_STOCKID_SQL, namedParameters,
					new DefaultPreparedStatementCallback());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void delete(String stockId, String date) {
		try {
			MapSqlParameterSource namedParameters = new MapSqlParameterSource();
			namedParameters.addValue("stockId", stockId);
			namedParameters.addValue("date", date);
			namedParameterJdbcTemplate.execute(DELETE_BY_STOCKID_AND_DATE_SQL, namedParameters,
					new DefaultPreparedStatementCallback());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void deleteByDate(String date) {
		try {
			MapSqlParameterSource namedParameters = new MapSqlParameterSource();
			namedParameters.addValue("date", date);
			namedParameterJdbcTemplate.execute(DELETE_BY_DATE_SQL, namedParameters,
					new DefaultPreparedStatementCallback());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public BollVO getBoll(String stockId, String date) {
		try {

			MapSqlParameterSource namedParameters = new MapSqlParameterSource();
			namedParameters.addValue("stockId", stockId);
			namedParameters.addValue("date", date);

			BollVO vo = this.namedParameterJdbcTemplate.queryForObject(QUERY_BY_ID_AND_DATE_SQL, namedParameters,
					new BollVOMapper());

			return vo;
		} catch (EmptyResultDataAccessException ee) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<BollVO> getAllBoll(String stockId) {
		try {

			MapSqlParameterSource namedParameters = new MapSqlParameterSource();
			namedParameters.addValue("stockId", stockId);

			List<BollVO> list = this.namedParameterJdbcTemplate.query(QUERY_ALL_BY_ID_SQL, namedParameters,
					new BollVOMapper());

			return list;
		} catch (Exception e) {
			logger.error("exception meets for getAllKDJ stockId=" + stockId, e);
			e.printStackTrace();
			return new ArrayList<BollVO>();
		}
	}

	// 最近几天的，必须使用时间倒序的SQL
	public List<BollVO> getNDateBoll(String stockId, int day) {
		try {

			MapSqlParameterSource namedParameters = new MapSqlParameterSource();
			namedParameters.addValue("stockId", stockId);
			namedParameters.addValue("limit", day);

			List<BollVO> list = this.namedParameterJdbcTemplate.query(QUERY_LATEST_N_BY_ID_SQL, namedParameters,
					new BollVOMapper());

			return list;
		} catch (Exception e) {
			logger.error("exception meets for getAvgClosePrice stockId=" + stockId, e);
			e.printStackTrace();
			return new ArrayList<BollVO>();
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		IndBollTableHelper ins = new IndBollTableHelper();
		try {
			// just for UT
			BollVO vo = new BollVO();
			vo.setMb(1.11);
			vo.setUp(2.22);
			vo.setDn(3.33);
			vo.setStockId("000333");
			vo.setDate("2015-01-29");
			// ins.insert(vo);
			System.out.println(ins.getBoll("000333", "2015-01-29"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
