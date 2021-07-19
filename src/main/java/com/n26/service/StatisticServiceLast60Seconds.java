package com.n26.service;

import com.n26.data.Statistics;
import com.n26.data.Transaction;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class StatisticServiceLast60Seconds implements StatisticService {

  @Getter
  @Setter
  private ConcurrentHashMap<Long, BigDecimal> currentTransactions = new ConcurrentHashMap<>();

  @Override
  public ResponseEntity addTransaction(Transaction trx) {
    if (trx.getTimestamp().isBefore(Instant.now().minusSeconds(60))) {
      // transactions older then 60 seconds
      return ResponseEntity.noContent().build();
    }
    if (trx.getTimestamp().isAfter(Instant.now())) {
      // if any of the fields are not parsable or the transaction date is in the future
      return ResponseEntity.unprocessableEntity().build();
    }
    currentTransactions.put(trx.getTimestamp().toEpochMilli(), trx.getAmount());

    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @Override
  public synchronized Statistics getTransactionStatistics(Instant now) {
    Statistics stats = new Statistics();

    currentTransactions.entrySet().forEach(entry -> {
      if (Instant.ofEpochMilli(entry.getKey()).isBefore(now.minusSeconds(60))) {
        currentTransactions.remove(entry.getKey());
      } else {
        stats.addData(entry.getValue());
      }
    });
    stats.averageValue();

    if (stats.getCount().get() == 0) {
      stats.setMin(new BigDecimal("0.00"));
    }

    return stats;
  }

  @Override
  public ResponseEntity deleteTransactionStatistics() {
    currentTransactions = new ConcurrentHashMap<>();
    return ResponseEntity.noContent().build();
  }

}
