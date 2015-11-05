package org.easystogu.db.access;

import java.util.ArrayList;
import java.util.List;

import org.easystogu.db.table.BollVO;
import org.easystogu.db.table.KDJVO;
import org.easystogu.db.table.MacdVO;
import org.easystogu.db.table.Mai1Mai2VO;
import org.easystogu.db.table.ShenXianVO;
import org.easystogu.db.table.StockPriceVO;
import org.easystogu.db.table.StockSuperVO;
import org.easystogu.db.table.YiMengBSVO;

public class WeekStockSuperVOHelper extends StockSuperVOHelper {
    public WeekStockSuperVOHelper() {
        stockPriceTable = WeekStockPriceTableHelper.getInstance();
        macdTable = IndWeekMacdTableHelper.getInstance();
        kdjTable = IndWeekKDJTableHelper.getInstance();
        bollTable = IndWeekBollTableHelper.getInstance();
        xueShi2Table = IndWeekXueShi2TableHelper.getInstance();
        mai1mai2Table = IndWeekMai1Mai2TableHelper.getInstance();
        yiMengBSTable = IndWeekYiMengBSTableHelper.getInstance();
        shenXianTable = IndWeekShenXianTableHelper.getInstance();
    }

    @Override
    public List<StockSuperVO> getLatestNStockSuperVO(String stockId, int day) {
        // merge them into one overall VO
        List<StockSuperVO> overList = new ArrayList<StockSuperVO>();

        List<StockPriceVO> spList = stockPriceTable.getNdateStockPriceById(stockId, day);
        List<MacdVO> macdList = macdTable.getNDateMacd(stockId, day);
        List<KDJVO> kdjList = kdjTable.getNDateKDJ(stockId, day);
        List<BollVO> bollList = bollTable.getNDateBoll(stockId, day);
        List<Mai1Mai2VO> mai1mai2List = mai1mai2Table.getNDateMai1Mai2(stockId, day);
        List<YiMengBSVO> yiMengBSList = yiMengBSTable.getNDateYiMengBS(stockId, day);
        List<ShenXianVO> shenXianList = shenXianTable.getNDateShenXian(stockId, day);

        if ((spList.size() != day) || (macdList.size() != day) || (kdjList.size() != day)
                || (mai1mai2List.size() != day) || (yiMengBSList.size() != day) || (bollList.size() != day)) {
            return overList;
        }

        if (!spList.get(0).date.equals(macdList.get(0).date) || !spList.get(0).date.equals(kdjList.get(0).date)
                || !spList.get(0).date.equals(mai1mai2List.get(0).date)
                || !spList.get(0).date.equals(yiMengBSList.get(0).date)
                || !spList.get(0).date.equals(shenXianList.get(0).date)
                || !spList.get(0).date.equals(bollList.get(0).date)) {
            return overList;
        }

        if (!spList.get(day - 1).date.equals(macdList.get(day - 1).date)
                || !spList.get(day - 1).date.equals(kdjList.get(day - 1).date)
                || !spList.get(day - 1).date.equals(mai1mai2List.get(day - 1).date)
                || !spList.get(day - 1).date.equals(yiMengBSList.get(day - 1).date)
                || !spList.get(day - 1).date.equals(shenXianList.get(day - 1).date)
                || !spList.get(day - 1).date.equals(bollList.get(day - 1).date)) {
            return overList;
        }

        for (int index = 0; index < spList.size(); index++) {
            StockSuperVO superVO = new StockSuperVO(spList.get(index), macdList.get(index), kdjList.get(index),
                    bollList.get(index));
            superVO.setMai1Mai2VO(mai1mai2List.get(index));
            superVO.setShenXianVO(shenXianList.get(index));
            superVO.setYiMengBSVO(yiMengBSList.get(index));
            overList.add(superVO);
        }

        return overList;
    }

    @Override
    public List<StockSuperVO> getAllStockSuperVO(String stockId) {
        // merge them into one overall VO
        List<StockSuperVO> overList = new ArrayList<StockSuperVO>();

        List<StockPriceVO> spList = stockPriceTable.getStockPriceById(stockId);
        List<MacdVO> macdList = macdTable.getAllMacd(stockId);
        List<KDJVO> kdjList = kdjTable.getAllKDJ(stockId);
        List<Mai1Mai2VO> mai1mai2List = mai1mai2Table.getAllMai1Mai2(stockId);
        List<BollVO> bollList = bollTable.getAllBoll(stockId);
        List<YiMengBSVO> yiMengBSList = yiMengBSTable.getAllYiMengBS(stockId);
        List<ShenXianVO> shenXianList = shenXianTable.getAllShenXian(stockId);

        /*
        for (int index = 0; index < spList.size(); index++) {
            StockPriceVO spVO = spList.get(index);

            MacdVO macdVO = macdTable.getMacd(stockId, spVO.date);
            if (macdVO == null) {
                //System.out.println(stockId + " missing week macd at " + spVO.date);
                return overList;
            }
            KDJVO kdjVO = kdjTable.getKDJ(stockId, spVO.date);
            if (kdjVO == null) {
                //System.out.println(stockId + " missing week kdj at " + spVO.date);
                return overList;
            }
            BollVO bollVO = bollTable.getBoll(stockId, spVO.date);
            if (bollVO == null) {
                //System.out.println(stockId + " missing week boll at " + spVO.date);
                return overList;
            }
            Mai1Mai2VO mai1mai2VO = mai1mai2Table.getMai1Mai2(stockId, spVO.date);
            if (mai1mai2VO == null) {
                //System.out.println(stockId + " missing week mai1mai2 at " + spVO.date);
                return overList;
            }

            ShenXianVO shenXianVO = shenXianTable.getShenXian(stockId, spVO.date);
            if (shenXianVO == null) {
                //System.out.println(stockId + " missing week shenXian at " + spVO.date);
                return overList;
            }

            YiMengBSVO yiMengBSVO = yiMengBSTable.getYiMengBS(stockId, spVO.date);
            if (yiMengBSVO == null) {
                //System.out.println(stockId + " missing week yiMengBS at " + spVO.date);
                return overList;
            }

            //
            StockSuperVO superVO = new StockSuperVO(spVO, macdVO, kdjVO, bollVO);
            superVO.setMai1Mai2VO(mai1mai2VO);
            superVO.setShenXianVO(shenXianVO);
            superVO.setYiMengBSVO(yiMengBSVO);
            overList.add(superVO);
        }
        */

        if ((spList.size() != macdList.size()) || (spList.size() != kdjList.size())
                || (spList.size() != bollList.size()) || (spList.size() != mai1mai2List.size())
                || (spList.size() != yiMengBSList.size()) || (spList.size() != shenXianList.size())) {
            return overList;
        }

        if ((spList.size() == 0) || (macdList.size() == 0) || (kdjList.size() == 0) || (bollList.size() == 0)
                || (mai1mai2List.size() == 0) || (yiMengBSList.size() == 0) || (shenXianList.size() == 0)) {
            return overList;
        }

        if (!spList.get(0).date.equals(macdList.get(0).date) || !spList.get(0).date.equals(kdjList.get(0).date)
                || !spList.get(0).date.equals(bollList.get(0).date)
                || !spList.get(0).date.equals(mai1mai2List.get(0).date)
                || !spList.get(0).date.equals(yiMengBSList.get(0).date)
                || !spList.get(0).date.equals(shenXianList.get(0).date)) {
            return overList;
        }

        for (int index = 0; index < spList.size(); index++) {
            StockSuperVO superVO = new StockSuperVO(spList.get(index), macdList.get(index), kdjList.get(index),
                    bollList.get(index));
            superVO.setMai1Mai2VO(mai1mai2List.get(index));
            superVO.setShenXianVO(shenXianList.get(index));
            superVO.setYiMengBSVO(yiMengBSList.get(index));
            overList.add(superVO);
        }

        return overList;
    }
}
