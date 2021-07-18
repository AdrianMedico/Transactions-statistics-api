package com.n26.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.n26.data.Statistics;
import com.n26.data.Transaction;
import com.n26.service.StatisticService;
import java.util.concurrent.ExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatisticsController {

  StatisticService statisticService;

  @Autowired
  public StatisticsController(StatisticService statisticService) {
    this.statisticService = statisticService;
  }

  @PostMapping(path = "/transactions", consumes = APPLICATION_JSON_VALUE)
  public ResponseEntity<String> addTransaction(@RequestBody Transaction trx)
      throws ExecutionException, InterruptedException {
    return statisticService.addTransaction(trx).get();
  }

  @GetMapping(path = "/statistics", produces = APPLICATION_JSON_VALUE)
  @ResponseBody
  public ResponseEntity<Statistics> getStatisticsForLast60Seconds() {
    return ResponseEntity.ok(statisticService.geTransactionStatistics());
  }

  @DeleteMapping(path = "/transactions")
  public ResponseEntity<String> deleteTransactionStatistics() {
    return statisticService.deleteTransactionStatistics();
  }

}
