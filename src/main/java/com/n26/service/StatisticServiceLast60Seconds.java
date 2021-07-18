package com.n26.service;
import com.n26.data.Statistics;
import com.n26.data.Transaction;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class StatisticServiceLast60Seconds implements StatisticService {

  ConcurrentHashMap<Long, BigDecimal> transactions = new ConcurrentHashMap<>();

  @Override
  public ResponseEntity addTransaction(Transaction trx) {
    if (trx.getTimestamp().isBefore(Instant.now().minusSeconds(60))) {
      // transactions older then 60 seconds
      return ResponseEntity.noContent().build();
    } else if (trx.getTimestamp().isAfter(Instant.now())) {
      // if any of the fields are not parsable or the transaction date is in the future
      return ResponseEntity.unprocessableEntity().build();
    }
    transactions.put(trx.getTimestamp().toEpochMilli(), trx.getAmount());

    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @Override
  public synchronized Statistics geTransactionStatistics() {
    Statistics stats = new Statistics();
    Instant currentTime = Instant.now();
    transactions.entrySet().forEach(entry -> {
      if (Instant.ofEpochMilli(entry.getKey()).isBefore(currentTime.minusSeconds(60))) {
        transactions.remove(entry.getKey());
      } else {
        stats.addData(entry.getValue());
      }
    });
    stats.averageValue();

    if (stats.getCount().get() == 0){
      stats.setMin(new BigDecimal("0.00"));
    }

    return stats;
  }

  @Override
  public ResponseEntity deleteTransactionStatistics() {
    transactions = new ConcurrentHashMap<>();
    return ResponseEntity.noContent().build();
  }

}
