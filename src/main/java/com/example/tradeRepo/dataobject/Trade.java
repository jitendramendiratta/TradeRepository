package com.example.tradeRepo.dataobject;


import java.time.ZonedDateTime;
import java.util.Date;


import lombok.*;
import lombok.ToString;

/**
 * This is Trade POJO to communicate trade data between layers.
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Trade {

    @EqualsAndHashCode.Include
    long tradeId;
    @EqualsAndHashCode.Include
    int version;
    @EqualsAndHashCode.Include
    String counterPartyId; // alphanumeric
    @EqualsAndHashCode.Include
    String bookId; //alphanumeric
    @EqualsAndHashCode.Include
    Date maturityDate;
    @EqualsAndHashCode.Include
    ZonedDateTime createdDate;
    @EqualsAndHashCode.Include
    boolean expired;

}
