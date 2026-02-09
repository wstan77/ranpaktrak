package com.lbisoft.core.app.settings;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.boot.autoconfigure.condition.ConditionMessage;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.type.AnnotatedTypeMetadata;

import com.lbisoft.core.app.Constants.AuthMethod;

public class OnAuthMethodCondition extends SpringBootCondition {
  @Retention(RUNTIME)
  @Target({ TYPE, METHOD })
  @Conditional(OnAuthMethodCondition.class)
  public @interface ConditionalOnAuthMethod {
    AuthMethod value() default AuthMethod.LDAP;
  }

  private static final String AUTH_METHOD_PROPERTY = "di.auth.method";

  @Override
  public ConditionOutcome getMatchOutcome(
      ConditionContext context, AnnotatedTypeMetadata metadata) {
    AuthMethod method = context.getEnvironment().getProperty(AUTH_METHOD_PROPERTY, AuthMethod.class);
    AuthMethod targetMethod = null;

    var attrs = metadata.getAnnotationAttributes(ConditionalOnAuthMethod.class.getName());
    if (attrs != null) {
      targetMethod = (AuthMethod) attrs.get("value");
    }

    if (method == targetMethod) {
      return ConditionOutcome.match(
          ConditionMessage.forCondition(ConditionalOnAuthMethod.class, "expected", targetMethod)
              .because("matched auth method"));
    } else {
      return ConditionOutcome.noMatch(
          ConditionMessage.forCondition(ConditionalOnAuthMethod.class, "expected", targetMethod)
              .found("different auth method")
              .items(method));
    }
  }
}
