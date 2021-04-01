package com.example.tradeRepo.datafabric;

import com.example.tradeRepo.dataobject.Trade;
import com.example.tradeRepo.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.lang.invoke.MethodHandles;
import java.sql.Types;
import java.util.List;
import java.util.Optional;

/**
 * This class is to provide access to database
 *
 */

@Repository
public class TradeDataFabricLayer {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getCanonicalName());

    final String UPDATE_QUERY = "UPDATE trade_repo SET " +
            "counterPartyId=?" +
            ",bookId=?" +
            ",maturityDate=?" +
            ",createdDate=?," +
            "expired=? " +
            "where trade_id=? " +
            "and version =?";
    int[] data_types_for_update_query = {Types.VARCHAR, Types.VARCHAR, Types.DATE, Types.TIMESTAMP, Types.BOOLEAN, Types.INTEGER, Types.INTEGER};


    @Autowired
    public JdbcTemplate jdbctemplate;

    /**
     * This method returns the list of trades which have been matured but have expired flag set as false.
     * @return
     */
    public Optional<List<Trade>> findMatureTrades() {
        List<Trade> tradeList = null;
        try {
            String sql = "SELECT * FROM TRADE_REPO where MATURITYDATE < ? and EXPIRED=FALSE";
            tradeList = jdbctemplate.query(sql, new TradeRowMapper(), new Object[]{Util.toSQLDate(new java.util.Date())});
        } catch (Exception e) {
            logger.error("Error while trying to find mature trades. Exception cause -{}", e.getMessage());
        }
        return Optional.ofNullable(tradeList);
    }

    /**
     * This method returns the Trade from TRADE_REPO table for a given trade id.
     * @param id
     * @return
     */
    public Optional<Trade> findbyid(long id) {
        Trade trade = null;
        try {
            String sql = "SELECT Top 1 * FROM TRADE_REPO where TRADE_ID=? order by version desc";
            trade = jdbctemplate.queryForObject(sql, new Object[]{id}, new TradeRowMapper());
        } catch (EmptyResultDataAccessException e) {
            logger.error("Error while trying to find record for {} . Exception cause -{}", id, e.getMessage());
        }
        return Optional.ofNullable(trade);
    }

    /**
     * This method is to insert a trade object into TRADE_REPO
     * @param trade
     * @return
     */
    public int insert(Trade trade) {

        return jdbctemplate.update("INSERT INTO trade_repo (trade_id,version,counterPartyId,bookId,maturityDate,createdDate,expired)  " +
                        "VALUES(?,?,?,?,?,?,?)", trade.getTradeId(), trade.getVersion(), trade.getCounterPartyId(), trade.getBookId(),
                Util.toSQLDate(trade.getMaturityDate()), Util.toSQLTimeStamp(trade.getCreatedDate()), trade.isExpired());
    }

    /**
     * This method is to update an existing trade
     * @param trade
     * @return
     */
    public int update(Trade trade) {

        Object[] params = {trade.getCounterPartyId(), trade.getBookId(),
                Util.toSQLDate(trade.getMaturityDate()), Util.toSQLTimeStamp(trade.getCreatedDate()), trade.isExpired(), trade.getTradeId(), trade.getVersion()};

        int rows = jdbctemplate.update(UPDATE_QUERY, params, data_types_for_update_query);
        logger.info(" {} row(s) updated for trade id {}.", rows, trade.getTradeId());
        return rows;
    }

}
