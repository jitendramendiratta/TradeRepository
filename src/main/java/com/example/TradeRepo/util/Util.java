package com.example.TradeRepo.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

public class Util {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getCanonicalName());

    public static Date toJavaDate(java.sql.Date sqlDate){
        return new Date(sqlDate.getTime());
    }

    public static java.sql.Date toSQLDate(Date date){
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        int year  = localDate.getYear();
        int month = localDate.getMonthValue();
        int day   = localDate.getDayOfMonth();
        return java.sql.Date.valueOf(LocalDate.of(year,month,day));
    }

    public static ZonedDateTime toZonedDatetime(Timestamp timestamp){
        return ZonedDateTime.ofInstant(timestamp.toInstant(), ZoneId.systemDefault());
    }

    public static Timestamp toSQLTimeStamp(ZonedDateTime zonedDateTime){
        return Timestamp.from(zonedDateTime.toInstant());
    }
}
