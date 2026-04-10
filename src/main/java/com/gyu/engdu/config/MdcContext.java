package com.gyu.engdu.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MdcContext {

  // MDC에 넣을 Key
  String key();

  String paramName() default "";

  String value() default "";
}
