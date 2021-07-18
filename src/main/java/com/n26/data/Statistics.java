package com.n26.data;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.atomic.AtomicLong;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Statistics {

  private static final int SCALE = 2;

  @JsonSerialize(using = ToStringSerializer.class)
  BigDecimal sum = new BigDecimal("0.00");
  @JsonSerialize(using = ToStringSerializer.class)
  BigDecimal avg = new BigDecimal("0.00");
  @JsonSerialize(using = ToStringSerializer.class)
  BigDecimal max = new BigDecimal("0.00");
  @JsonSerialize(using = ToStringSerializer.class)
  BigDecimal min = new BigDecimal(Long.MAX_VALUE);
  AtomicLong count = new AtomicLong();

  public  void addData(BigDecimal amount) {
    count.incrementAndGet();
    sum = sum.add(amount).setScale(SCALE, RoundingMode.HALF_UP);

    if (max.compareTo(amount) < 0) {
      max = amount.setScale(SCALE, RoundingMode.HALF_UP);
    }
    if (min.compareTo(amount) > 0 || count.get() == 1) {
      min = amount.setScale(SCALE, RoundingMode.HALF_UP);
    }
  }

  public void averageValue() {
    avg = (count.get() > 0) ? sum.divide(BigDecimal.valueOf(count.get()), SCALE, RoundingMode.HALF_UP) : new BigDecimal("0.00");
  }

}
