package dev.party.config;

import dev.party.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public UserDetailsService userDetailsService(CustomUserDetailsService customService) {
        return customService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserDetailsService userDetailsService,
                                                            PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   HandlerMappingIntrospector introspector) throws Exception {

        MvcRequestMatcher.Builder mvc = new MvcRequestMatcher.Builder(introspector);

        http
                .authorizeHttpRequests(auth -> auth
                        // 💡 H2-Console & statische Dateien
                        .requestMatchers(
                                mvc.pattern("/h2-console/**"),
                                mvc.pattern("/css/**"),
                                mvc.pattern("/js/**"),
                                mvc.pattern("/images/**"),
                                mvc.pattern("/uploads/**")
                        ).permitAll()

                        // 💡 Öffentliche Seiten
                        .requestMatchers(
                                mvc.pattern("/login"),
                                mvc.pattern("/register"),
                                mvc.pattern("/error"),
                                mvc.pattern("/home")
                        ).permitAll()

                        // 💡 Getränkeübersicht: nur eingeloggte Benutzer
                        .requestMatchers(mvc.pattern("/drinks/**")).hasAnyRole("USER", "ADMIN")

                        // 💡 Bestellungen (Tray, Add, Submit) → für alle erlaubt
                        .requestMatchers(mvc.pattern("/orders/**")).permitAll()

                        // 💡 Admin-Bereich nur für Admins
                        .requestMatchers(mvc.pattern("/admin/**")).hasRole("ADMIN")

                        // 💡 Rest muss eingeloggt sein
                        .anyRequest().authenticated()
                )

                // ✅ Login
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/home", true)
                        .permitAll()
                )

                // ✅ Logout
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )

                // ✅ CSRF deaktivieren (optional für H2 und AJAX)
                .csrf(csrf -> csrf.disable())

                // ✅ Frame-Optionen deaktivieren (H2 Console)
                .headers(headers -> headers.frameOptions(frame -> frame.disable()));

        return http.build();
    }
}
