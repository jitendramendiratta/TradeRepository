package com.example.TradeRepo.datafabric;

import com.example.TradeRepo.dataobject.Trade;
import com.example.TradeRepo.util.Util;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

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
