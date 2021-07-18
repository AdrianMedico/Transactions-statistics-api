package com.n26.service;

import com.n26.data.Statistics;
import com.n26.data.Transaction;
import java.util.concurrent.CompletableFuture;
import org.springframework.http.ResponseEntity;

public interface StatisticService {

  CompletableFuture<ResponseEntity> addTransaction(Transaction trx);

  Statistics geTransactionStatistics();

  ResponseEntity deleteTransactionStatistics();

  public void eraseTransactionStatistics();

}
