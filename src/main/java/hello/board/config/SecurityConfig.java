package hello.board.config;

import hello.board.auth.oauth.PrincipalOauth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
//@EnableWebSecurity(debug = true)  // 스프링 시큐리티 로그
@Configuration
@EnableWebSecurity // 스프링 시큐리티 필터를 스프링 필터체인에 등록
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationFailureHandler customFailureHandler;
    private final PrincipalOauth2UserService principalOauth2UserService;

    // 패스워드 암호화 인코더 등록
    @Bean
    public BCryptPasswordEncoder pwEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    // SecurityFilterChain 을 리턴하는 메서드를 @Bean 으로 등록 -> component 방식으로 스프링 컨테이너가 관리
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf().disable();

        // 요청 인증 방식 설정
        http.authorizeRequests()

                .antMatchers("/member/add", "/member/add-oauth").permitAll() // 회원가입
                .antMatchers("/board/write").authenticated() // 글쓰기
                .antMatchers("/board/*").permitAll() // 글조회
                .antMatchers("/*/*/edit", "/*/*/delete").authenticated() // 정보변경, 삭제
                .anyRequest().permitAll() // 나머지

                .and()

                .formLogin()
                .usernameParameter("loginId") // username 대신 loginId
                .loginPage("/loginForm")
                .loginProcessingUrl("/loginProc") // loginForm.action, POST /login 하면 에러남.
                .defaultSuccessUrl("/boards")
                .failureHandler(customFailureHandler)

                .and()

                .logout()
                .permitAll()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/boards")
//                .deleteCookies("JSESSIONID") // 로그아웃 시 JSESSIONID 제거
                .invalidateHttpSession(true) // 로그아웃 시 세션 종료
                .clearAuthentication(true) // 로그아웃 시 권한 제거

                .and()

                .oauth2Login()
                .loginPage("/loginForm")
                .defaultSuccessUrl("/login/check")
                .userInfoEndpoint().userService(principalOauth2UserService); // 인증한 사용자 정보 설정 및 처리

        return http.build();
    }

}
