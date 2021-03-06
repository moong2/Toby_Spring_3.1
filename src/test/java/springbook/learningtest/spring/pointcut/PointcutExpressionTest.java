package springbook.learningtest.spring.pointcut;

import org.junit.Test;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class PointcutExpressionTest {
    @Test
    public void methodSignaturePointcut() throws SecurityException, NoSuchMethodException {
        System.out.println(Target.class.getMethod("minus", int.class, int.class));

        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("execution(public int "+"springbook.learningtest.spring.pointcut.Target.minus(int, int) "+"throws java.lang.RuntimeException)");
//        pointcut.setExpression("execution(int minus(int, int))");

        // Target.minus()
        assertThat(pointcut.getClassFilter().matches(Target.class) && pointcut.getMethodMatcher().matches(Target.class.getMethod("minus", int.class, int.class), null), is(true));

        // Target.plus()
        assertThat(pointcut.getClassFilter().matches(Target.class) && pointcut.getMethodMatcher().matches(Target.class.getMethod("plus", int.class, int.class), null), is(false));

        // Bean.method()
        assertThat(pointcut.getClassFilter().matches(Bean.class) && pointcut.getMethodMatcher().matches(Bean.class.getMethod("method"), null), is(false));
    }

    @Test
    public void pointcut() throws Exception {
        targetClassPointcutMatches("execution(* *(..))", true, true, true, true, true, true);
    }
    public void pointcutMathes(String expression, Boolean expected, Class<?> clazz, String methodName, Class<?>... args) throws Exception {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression(expression);

        assertThat(pointcut.getClassFilter().matches(clazz) && pointcut.getMethodMatcher().matches(clazz.getMethod(methodName, args), null), is(expected));
    }
    public void targetClassPointcutMatches(String expression, boolean... expected) throws Exception {
        pointcutMathes(expression, expected[0], Target.class, "hello");
        pointcutMathes(expression, expected[1], Target.class, "hello", String.class);
        pointcutMathes(expression, expected[2], Target.class, "plus", int.class, int.class);
        pointcutMathes(expression, expected[3], Target.class, "minus", int.class, int.class);
        pointcutMathes(expression, expected[4], Target.class, "method");
        pointcutMathes(expression, expected[5], Bean.class, "method");
    }
}
