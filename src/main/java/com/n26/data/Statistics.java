package com.n26.data;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import lombok.Getter;

@Getter
public class Statistics {

  AtomicReference<BigDecimal> sum = new AtomicReference<>(new BigDecimal("0.00"));
  AtomicReference<BigDecimal> avg = new AtomicReference<>(new BigDecimal("0.00"));
  AtomicReference<BigDecimal> max = new AtomicReference<>(new BigDecimal("0.00"));
  AtomicReference<BigDecimal> min = new AtomicReference<>(new BigDecimal(Long.MAX_VALUE));
  AtomicLong count = new AtomicLong();
}
