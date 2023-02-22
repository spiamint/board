package hello.board.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class LogInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String requestURI = request.getRequestURI();
        String queryString = request.getQueryString();
        String method = request.getMethod();

        if (queryString == null) {
            log.info("{} {}", method, requestURI);
        } else {
            log.info("{} {}?{}", method, requestURI, queryString);
        }

        // 다음 인터셉터 진행
        return true;
    }
}