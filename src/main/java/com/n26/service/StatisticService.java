package com.n26.service;

import com.n26.data.Statistics;
import com.n26.data.Transaction;
import java.time.Instant;
import org.springframework.http.ResponseEntity;

public interface StatisticService {

  ResponseEntity addTransaction(Transaction trx);

  Statistics getTransactionStatistics(Instant now);

  ResponseEntity deleteTransactionStatistics();

}
