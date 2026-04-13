package com.gyu.engdu.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.gyu.engdu.IntegrationTestSupport;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@SpringBootTest
class MdcAspectTest extends IntegrationTestSupport {

  private static final String MDC_TEST_KEY = "mdcTestKey";
  private static final String MDC_TEST_VALUE = "mdcTestValue";

  @Autowired
  MethodWithFixedValue methodWithFixedValue;

  @Autowired
  MethodWithParamValue methodWithParamValue;

  @AfterEach
  void clearMdc() {
    MDC.clear();
  }

  @DisplayName("@MdcContext에 value를 지정하면, 메서드 실행 중 MDC에 고정값이 세팅된다.")
  @Test
  void fixedValue_mdcIsSetDuringExecution() {
    // given
    String expectedValue = MDC_TEST_VALUE;

    // when
    String result = methodWithFixedValue.captureMdcValue();

    // then
    assertThat(result).isEqualTo(expectedValue);
  }

  @DisplayName("@MdcContext에 value를 지정하면, 메서드 종료 후 MDC 값이 제거된다.")
  @Test
  void fixedValue_mdcIsRemovedAfterExecution() {
    // given
    String mdcKey = MDC_TEST_KEY;

    // when
    methodWithFixedValue.run();

    // then
    assertThat(MDC.get(mdcKey)).isNull();
  }

  @DisplayName("@MdcContext에 value를 지정하면, 메서드에서 예외가 발생해도 MDC 값이 제거된다.")
  @Test
  void fixedValue_mdcIsRemovedEvenOnException() {
    // given
    String mdcKey = MDC_TEST_KEY;

    // when & then
    assertThatThrownBy(() -> methodWithFixedValue.runThrowing())
        .isInstanceOf(RuntimeException.class);
    assertThat(MDC.get(mdcKey)).isNull();
  }

  @DisplayName("@MdcContext에 param을 지정하면, 메서드 실행 시 파라미터 값이 MDC에 세팅된다.")
  @Test
  void paramValue_mdcIsSetDuringExecution() {
    // given
    String traceId = MDC_TEST_VALUE;

    // when
    String result = methodWithParamValue.captureMdcValue(traceId);

    // then
    assertThat(result).isEqualTo(traceId);
  }

  @DisplayName("@MdcContext에 param을 지정하면, 메서드 종료 후 MDC 값이 제거된다.")
  @Test
  void paramValue_mdcIsRemovedAfterExecution() {
    // given
    String mdcKey = MDC_TEST_KEY;
    String traceId = MDC_TEST_VALUE;

    // when
    methodWithParamValue.run(traceId);

    // then
    assertThat(MDC.get(mdcKey)).isNull();
  }

  @DisplayName("@MdcContext에 param을 지정하면, 메서드에서 예외가 발생해도 MDC 값이 제거된다.")
  @Test
  void paramValue_mdcIsRemovedEvenOnException() {
    // given
    String mdcKey = MDC_TEST_KEY;
    String traceId = MDC_TEST_VALUE;

    // when & then
    assertThatThrownBy(() -> methodWithParamValue.runThrowing(traceId))
        .isInstanceOf(RuntimeException.class);
    assertThat(MDC.get(mdcKey)).isNull();
  }

  @TestConfiguration
  static class TestConfig {

    @Bean
    public MethodWithFixedValue methodWithFixedValue() {
      return new MethodWithFixedValue();
    }

    @Bean
    public MethodWithParamValue methodWithParamValueValue() {
      return new MethodWithParamValue();
    }
  }

  static class MethodWithFixedValue {

    @MdcContext(key = MDC_TEST_KEY, value = MDC_TEST_VALUE)
    public void run() {
    }

    @MdcContext(key = MDC_TEST_KEY, value = MDC_TEST_VALUE)
    public String captureMdcValue() {
      return MDC.get(MDC_TEST_KEY);
    }

    @MdcContext(key = MDC_TEST_KEY, value = MDC_TEST_VALUE)
    public void runThrowing() {
      throw new RuntimeException();
    }
  }

  static class MethodWithParamValue {

    @MdcContext(key = MDC_TEST_KEY, paramName = "paramValue")
    public void run(String paramValue) {
    }

    @MdcContext(key = MDC_TEST_KEY, paramName = "paramValue")
    public String captureMdcValue(String paramValue) {
      return MDC.get(MDC_TEST_KEY);
    }

    @MdcContext(key = MDC_TEST_KEY, paramName = "paramValue")
    public void runThrowing(String paramValue) {
      throw new RuntimeException();
    }
  }

}
