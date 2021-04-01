package com.example.tradeRepo.datafabric;

import com.example.tradeRepo.dataobject.Trade;
import com.example.tradeRepo.util.Util;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This class is to map a result row to Trade object
 */
public class TradeRowMapper implements RowMapper<Trade> {

    @Override
    public Trade mapRow(ResultSet rs, int rowNum) throws SQLException {
        Trade trade = new Trade();
        trade.setTradeId(rs.getInt("TRADE_ID"));
        trade.setVersion(rs.getInt("VERSION"));
        trade.setCounterPartyId(rs.getString("COUNTERPARTYID"));
        trade.setCounterPartyId(rs.getString("BOOKID"));
        trade.setMaturityDate(Util.toJavaDate(rs.getDate("MATURITYDATE")));
        trade.setCreatedDate(Util.toZonedDatetime(rs.getTimestamp("MATURITYDATE")));
        trade.setExpired(rs.getBoolean("EXPIRED"));
        return trade;

    }
}
