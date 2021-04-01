package com.example.tradeRepo.dataobject;


import java.time.ZonedDateTime;
import java.util.Date;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.ToString;

/**
 * This is Trade POJO to communicate trade data between layers.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Trade {

    long tradeId;
    int version;
    String counterPartyId; // alphanumeric
    String bookId; //alphanumeric
    Date maturityDate;
    ZonedDateTime createdDate;
    boolean expired;

}
