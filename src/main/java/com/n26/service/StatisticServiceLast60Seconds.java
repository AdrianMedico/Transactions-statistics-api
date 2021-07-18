package com.n26.service;

import com.n26.data.Statistics;
import com.n26.data.Transaction;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class StatisticServiceLast60Seconds implements StatisticService {

  @Autowired
  Statistics statistics;

  @Override
  @Async("asyncExecutor")
  public CompletableFuture<ResponseEntity> addTransaction(Transaction trx) {
    if (trx.getTimestamp().isBefore(Instant.now().minusSeconds(60))) {
      // transactions older then 60 seconds
      return CompletableFuture.completedFuture(ResponseEntity.noContent().build());
    } else if (trx.getTimestamp().isAfter(Instant.now())) {
      // if any of the fields are not parsable or the transaction date is in the future
      return CompletableFuture.completedFuture(ResponseEntity.unprocessableEntity().build());
    }

    statistics.getCount().incrementAndGet();
    statistics.getSum().accumulateAndGet(trx.getAmount(), BigDecimal::add);
    statistics.getMax().accumulateAndGet(trx.getAmount(), BigDecimal::max);
    statistics.getMin().accumulateAndGet(trx.getAmount(), BigDecimal::min);

    return CompletableFuture.completedFuture(ResponseEntity.accepted().build());
  }

  @Override
  public Statistics geTransactionStatistics() {
    if (statistics.getCount().get() != 0){
      statistics.getAvg()
          .set(statistics.getSum().get().divide(BigDecimal.valueOf(statistics.getCount().get()),
              RoundingMode.HALF_UP));
    } else {
      statistics.getMin().set(new BigDecimal("0.00"));
    }

    return statistics;
  }

  @Override
  public ResponseEntity deleteTransactionStatistics() {
    eraseTransactionStatistics();
    return ResponseEntity.noContent().build();
  }

  @Scheduled(fixedRate = 60000)
  public void eraseTransactionStatistics(){
    log.debug("flushed transaction statistic data");
    statistics = new Statistics();
  }

}
