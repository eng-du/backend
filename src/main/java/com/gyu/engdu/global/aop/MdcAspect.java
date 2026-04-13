package com.gyu.engdu.global.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class MdcAspect {

  // 메서드 실행 시점에 MDC에 값을 넣고, 메서드 종료 시점에 MDC에서 값을 제거한다.
  @Around("@annotation(mdcContext)")
  public Object around(ProceedingJoinPoint joinPoint, MdcContext mdcContext) throws Throwable {
    String mdcKey = mdcContext.key();
    String mdcValue = resolveMdcValue(joinPoint, mdcContext);

    if (mdcValue != null) {
      MDC.put(mdcKey, mdcValue);
    }

    try {
      return joinPoint.proceed();
    } finally {
      MDC.remove(mdcKey);
    }
  }

  // value()가 지정되어 있으면 고정 값을 사용하고, 없으면 paramName()에 해당하는 메서드 파라미터에서 값을 추출한다.
  private String resolveMdcValue(ProceedingJoinPoint joinPoint, MdcContext mdcContext) {
    if (!mdcContext.value().isEmpty()) {
      return mdcContext.value();
    }
    return extractFromParam(joinPoint, mdcContext.paramName());
  }

  // joinpoint 메서드의 파라미터에 해당하는 인자값을 추출한다.
  private String extractFromParam(ProceedingJoinPoint joinPoint, String paramName) {
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    String[] parameterNames = signature.getParameterNames();
    Object[] args = joinPoint.getArgs();

    for (int i = 0; i < parameterNames.length; i++) {
      if (paramName.equals(parameterNames[i])) {
        return args[i].toString();
      }
    }

    return null;
  }
}
