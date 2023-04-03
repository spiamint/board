package hello.board.aop;

import hello.board.log.trace.LogTrace;
import hello.board.log.trace.TraceStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.aop.ProxyMethodInvocation;
import org.springframework.aop.aspectj.MethodInvocationProceedingJoinPoint;

@RequiredArgsConstructor
@Slf4j
@Aspect
public class LogTraceAspect {

    private final LogTrace logTrace;

    // Pointcut 표현식 분리
    @Pointcut("execution(* hello.board.controller..*(..))")
    public void allController() {};

    @Pointcut("execution(* hello.board.service..*(..))")
    public void allService() {};

    @Pointcut("execution(* hello.board.repository..*(..))")
    public void allRepository() {};

    @Around("allController() || allService() || allRepository()")
    public Object execute(ProceedingJoinPoint joinPoint) throws Throwable {
        TraceStatus status = null;  // catch 문 사용을 위해 외부에서 선언
        try {
            String message = joinPoint.getSignature().toShortString();
            status = logTrace.begin(message);

            // Reflection invoke(Method), returns Method.return
            Object result = joinPoint.proceed();

            logTrace.end(status);
            return result;
        } catch (Exception e) {
            logTrace.exception(status, e);
            throw e; //예외를 처리하진 않음
        }
    }

}