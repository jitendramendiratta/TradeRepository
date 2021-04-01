package com.example.tradeRepo;


import com.example.tradeRepo.datafabric.TradeDataFabricLayer;
import com.example.tradeRepo.dataobject.Trade;
import com.example.tradeRepo.processor.TradeProcessor;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

@SpringBootTest
public class TradeRepoApplicationTests {


	private static TradeDataFabricLayer tradeDataFabricLayer;
	private TradeProcessor processor = new TradeProcessor();
	private static EmbeddedDatabase db;

	@BeforeClass
	public static void dataSource() {

		EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
		db = builder
				.setType(EmbeddedDatabaseType.H2)
				.addScript("schema.sql")
				.build();
		JdbcTemplate template = new JdbcTemplate(db);
		tradeDataFabricLayer = new TradeDataFabricLayer();
		tradeDataFabricLayer.jdbctemplate = template;
	}

	@Test
	public void testNewTradeInsert() {
		int tradeid = 11234;
		Trade t = new Trade();
		t.setTradeId(1234);
		t.setVersion(0);
		t.setCounterPartyId("CP-1");
		t.setBookId("BK-1");
		t.setMaturityDate(new java.util.Date());
		t.setCreatedDate(ZonedDateTime.now());
		t.setExpired(false);
		int count = tradeDataFabricLayer.insert(t);
		assertTrue(count==1);
	}

	@Test
	public void testTradeWithInvalidMaturity() {
		boolean willBeDiscarded = false;
		DateFormat dateformatter = new SimpleDateFormat("yyyy-MM-dd");

		int tradeid = 11234;
		Trade t = new Trade();
		t.setTradeId(1234);
		t.setVersion(0);
		t.setCounterPartyId("CP-1");
		t.setBookId("BK-1");
		try {
			t.setMaturityDate(dateformatter.parse("2020-12-31"));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		t.setCreatedDate(ZonedDateTime.now());
		t.setExpired(false);
		if(!processor.isValidMaturityDate(t))
			willBeDiscarded = true;
		assertTrue(willBeDiscarded);
	}

	@Test
	public void testFindTradeById() {
		int tradeid = 11235;
		Trade t = new Trade();
		t.setTradeId(tradeid);
		t.setVersion(0);
		t.setCounterPartyId("CP-1");
		t.setBookId("BK-1");
		t.setMaturityDate(new java.util.Date());
		t.setCreatedDate(ZonedDateTime.now());
		t.setExpired(false);
		int count = tradeDataFabricLayer.insert(t);
		Optional<Trade> trade = tradeDataFabricLayer.findbyid(tradeid);
		assertThat(trade.get()).isNotNull();
	}

	@Test
	public void testTradeUpdateForSameVersion() {
		int tradeid = 11235;
		Trade t = new Trade();
		t.setTradeId(tradeid);
		t.setVersion(0);
		t.setCounterPartyId("CP-1");
		t.setBookId("BK-1");
		t.setMaturityDate(new java.util.Date());
		t.setCreatedDate(ZonedDateTime.now());
		t.setExpired(false);
		tradeDataFabricLayer.insert(t);

		Trade newTrade = new Trade();
		newTrade.setTradeId(tradeid);
		newTrade.setVersion(0);
		newTrade.setCounterPartyId("CP-1");
		newTrade.setBookId("BK-1");
		newTrade.setMaturityDate(new java.util.Date());
		newTrade.setCreatedDate(ZonedDateTime.now());
		newTrade.setExpired(false);

		int count=0;
		if(processor.isSameVersion(t,newTrade))
			count = tradeDataFabricLayer.update(newTrade);
		assertThat(count==1);
	}

	@Test
	public void testTradeUpdateforHigherVersion() {
		int tradeid = 11235;
		Trade t = new Trade();
		t.setTradeId(tradeid);
		t.setVersion(11);
		t.setCounterPartyId("CP-1");
		t.setBookId("BK-1");
		t.setMaturityDate(new java.util.Date());
		t.setCreatedDate(ZonedDateTime.now());
		t.setExpired(false);
		tradeDataFabricLayer.insert(t);

		Trade newTrade = new Trade();
		newTrade.setTradeId(tradeid);
		newTrade.setVersion(10);
		newTrade.setCounterPartyId("CP-1");
		newTrade.setBookId("BK-1");
		newTrade.setMaturityDate(new java.util.Date());
		newTrade.setCreatedDate(ZonedDateTime.now());
		newTrade.setExpired(false);

		int count =0;
		if(processor.isNewerVersion(t,newTrade))
			 count = tradeDataFabricLayer.update(t);
		assertThat(count==0);
	}

}
