package com.n26.service;


import com.n26.data.Statistics;
import com.n26.data.Transaction;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class StatisticServiceLast60SecondsTest {

  private StatisticServiceLast60Seconds statisticsService;

  @Before
  public void setUp() {
    statisticsService = new StatisticServiceLast60Seconds();
  }

  @Test
  public void addTransactionShouldReturn204WhenTransactionIsOlderThan60Seconds() {
    // Given
    Transaction trx = new Transaction(new BigDecimal("12345.67"), Instant.now().minusSeconds(65));
    // When
    ResponseEntity response = statisticsService.addTransaction(trx);
    // Then
    Assert.assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
  }

  @Test
  public void addTransactionShouldReturn422WhenTransactionIsFuture() {
    // Given
    Transaction trx = new Transaction(new BigDecimal("12345.67"), Instant.now().plusSeconds(120));
    // When
    ResponseEntity response = statisticsService.addTransaction(trx);
    // Then
    Assert.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
  }

  @Test
  public void addTransactionShouldReturn201WhenTransactionIsCreated() {
    // Given
    Transaction trx = new Transaction(new BigDecimal("12345.67"), Instant.now());
    // When
    ResponseEntity response = statisticsService.addTransaction(trx);
    // Then
    Assert.assertEquals(HttpStatus.CREATED, response.getStatusCode());
  }

  @Test
  public void geTransactionStatisticsShouldReturn0sWhenAllOngoingTransactionsAreOutdated() {
    // Given
    statisticsService.setCurrentTransactions(mockTransactions());
    // When
    Statistics stats = statisticsService.getTransactionStatistics(Instant.now().plusSeconds(3600));
    // Then
    BigDecimal bigDecimal = new BigDecimal("0.00");
    Assert.assertEquals(bigDecimal, stats.getSum());
    Assert.assertEquals(bigDecimal, stats.getAvg());
    Assert.assertEquals(bigDecimal, stats.getMax());
    Assert.assertEquals(bigDecimal, stats.getMin());
    Assert.assertEquals(0, stats.getCount().get());
  }

  @Test
  public void geTransactionStatisticsShouldReturnTheCurrentTransactions() {
    // Given
    statisticsService.setCurrentTransactions(mockTransactions());
    // When
    Statistics stats = statisticsService.getTransactionStatistics(Instant.now());
    // Then
    Assert.assertEquals(new BigDecimal("1201.99"), stats.getSum());
    Assert.assertEquals(new BigDecimal("100.17"), stats.getAvg());
    Assert.assertEquals(new BigDecimal("100.90"), stats.getMax());
    Assert.assertEquals(new BigDecimal("100.00"), stats.getMin());
    Assert.assertEquals(12, stats.getCount().get());
  }

  @Test
  public void deleteTransactionStatisticsShouldReturn204WhenAllTransactionsAreErased() {
    // Given
    statisticsService.addTransaction(new Transaction(new BigDecimal("12345.67"), Instant.now()));
    // When
    ResponseEntity response = statisticsService.deleteTransactionStatistics();
    // Then
    Assert.assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
  }

  @Test
  public void deleteTransactionStatisticsShouldCreateNewVoidConcurrentHashMapInstanceWhenIsCalled() {
    // Given
    statisticsService.addTransaction(new Transaction(new BigDecimal("12345.67"), Instant.now()));
    ConcurrentHashMap originalHashMap = statisticsService.getCurrentTransactions();
    // When
    statisticsService.deleteTransactionStatistics();
    ConcurrentHashMap newHashMap = statisticsService.getCurrentTransactions();

    // Then
    Assert.assertNotSame(newHashMap, originalHashMap);
  }

  private static ConcurrentHashMap mockTransactions() {
    ConcurrentHashMap transactions = new ConcurrentHashMap<>();

    transactions.put(Instant.now().toEpochMilli() - 12, new BigDecimal("100.01"));
    transactions.put(Instant.now().toEpochMilli() - 11, new BigDecimal("100.03"));
    transactions.put(Instant.now().toEpochMilli() - 10, new BigDecimal("100.05"));
    transactions.put(Instant.now().toEpochMilli() - 9, new BigDecimal("100.0599"));
    transactions.put(Instant.now().toEpochMilli() - 8, new BigDecimal("100.09"));
    transactions.put(Instant.now().toEpochMilli() - 7, new BigDecimal("100.0099"));
    transactions.put(Instant.now().toEpochMilli() - 6, new BigDecimal("100.90"));
    transactions.put(Instant.now().toEpochMilli() - 5, new BigDecimal("100.45"));
    transactions.put(Instant.now().toEpochMilli() - 4, new BigDecimal("100.39"));
    transactions.put(Instant.now().toEpochMilli() - 3, new BigDecimal("100.00"));
    transactions.put(Instant.now().toEpochMilli() - 2, new BigDecimal("100.00"));
    transactions.put(Instant.now().toEpochMilli() - 1, new BigDecimal("100.00"));

    return transactions;
  }

}