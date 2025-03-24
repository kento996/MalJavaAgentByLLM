package com.jas.quickstart.core.plugins;

import com.jas.quickstart.core.aop.Pointcut;
import com.jas.quickstart.core.aop.advice.Advice;
import com.jas.quickstart.core.aop.support.AbstractAspect;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;

import static net.bytebuddy.matcher.ElementMatchers.*;
import static net.bytebuddy.matcher.ElementMatchers.isInterface;

@Plugin
@Slf4j
public class CommonPlugin extends AbstractAspect {

    @Override
    public Pointcut getPointcut() {
        return new Pointcut() {
            @Override
            public ElementMatcher.Junction<MethodDescription> getMethodMatcher() {
                return any();
            }

            @Override
            public ElementMatcher.Junction<TypeDescription> getTypeMatcher() {
                return any();
            }
        };
    }

    @Override
    public Advice getAdvice() {
        return null;
    }

    @Override
    public String getAspectName() {
        return "commonAspect";
    }
}
