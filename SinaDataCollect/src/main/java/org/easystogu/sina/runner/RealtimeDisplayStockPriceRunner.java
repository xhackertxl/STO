package org.easystogu.sina.runner;

import java.util.List;

import org.easystogu.config.FileConfigurationService;
import org.easystogu.sina.common.RealTimePriceVO;
import org.easystogu.sina.helper.SinaDataDownloadHelper;

public class RealtimeDisplayStockPriceRunner {

	public String printRealTimeOutput() {
		// 显示实时数据(指定的stockIds)
		StringBuffer sb = new StringBuffer();
		FileConfigurationService configure = FileConfigurationService
				.getInstance();
		SinaDataDownloadHelper ins = new SinaDataDownloadHelper();

		String strList = configure.getString("realtime.display.stock.list")
				+ "," + configure.getString("analyse.select.stock.list");

		sb.append("============Main Selected===========\n");
		// System.out.println("============Main Selected===========");
		List<RealTimePriceVO> list = ins.fetchDataFromWeb(strList);
		for (RealTimePriceVO vo : list) {
			// System.out.println(vo);
			sb.append(vo.toString() + "\n");
		}
		return sb.toString();
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		RealtimeDisplayStockPriceRunner runner = new RealtimeDisplayStockPriceRunner();
		System.out.println(runner.printRealTimeOutput());
	}
}
