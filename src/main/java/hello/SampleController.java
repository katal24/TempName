package hello;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;
import products.Product;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


@Controller
@EnableAutoConfiguration
public class SampleController {

    @Bean(name = "dataSource")
    public DriverManagerDataSource dataSource() {
        DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();
        driverManagerDataSource.setDriverClassName("com.mysql.jdbc.Driver");
        driverManagerDataSource.setUrl("jdbc:mysql://sql4.freesqldatabase.com:3306/sql497139");
        driverManagerDataSource.setUsername("sql497139");//
        driverManagerDataSource.setPassword("7lwadeYgm6");
        return driverManagerDataSource;
    }

    @ResponseBody  //TU DALEM RESPONSEBODY BO BYLO CIRCULAR VIEW PATH [user] na /user
    @RequestMapping("/user")
    public Principal user(Principal user) {
        return user;
    }

    @ResponseBody
    @RequestMapping("/getResources/")
    public Map<String, Object> home() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName();
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("content", "Hello World");
        model.put("current",name);
        return model;
    }

//    @RequestMapping("/")
//    @ResponseBody
//    String home2() {
//        return "Hello World!";
//    }


    @RequestMapping("/getIndex/")
    @ResponseBody
    String index() {
        return "witaj na stronie index!";
    }

    @RequestMapping("/getProducts/")
    @ResponseBody
    List<Product> getProducts() {
        List<Product> lista= new LinkedList<Product>();
        Product nutella = new Product();
        Product lizak = new Product();

        nutella.setId(2);
        nutella.setCarbo(200);
        nutella.setFat(43);
        nutella.setName("nutella");
        nutella.setGrade(10);
        nutella.setKcal(200);
        nutella.setWhey(30);


        lista.add(nutella);
        lizak.setId(1);
        lizak.setCarbo(100);
        lizak.setFat(33);
        lizak.setName("lziaczek");
        lizak.setGrade(10);
        lizak.setKcal(200);
        lizak.setWhey(30);
        lista.add(lizak);
        return lista;
    }


    public static void main(String[] args) throws Exception {
        SpringApplication.run(SampleController.class, args);
    }



    @RequestMapping(value = "/addNewAccount", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void receiveNewConfiguration(@RequestBody Configuration configuration)
    {

    }

    @Configuration
    @Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
    protected static class SecurityConfiguration extends WebSecurityConfigurerAdapter {

        @Autowired //javax.sql.dataSource
                DataSource dataSource;

        @Autowired
        public void configAuthentication(AuthenticationManagerBuilder auth) throws Exception {

            auth.jdbcAuthentication().dataSource(dataSource)
                    .usersByUsernameQuery(
                            "select username,password, enabled from users where username=?")
                    .authoritiesByUsernameQuery(
                            "select username, role from user_roles where username=?")
                    .and()
                    .inMemoryAuthentication()
                    .withUser("uname@gmail.com").password("pass").roles("USER").and()
                    .withUser("admin@gmail.com").password("pass").roles("ADMIN").and()
                    .withUser("expert@gmail.com").password("pass").roles("EXPERT");
        }

        @Bean
        public PasswordEncoder passwordEncoder() {//z pakietu krypto cos tam bo bylo wiele opcji
            PasswordEncoder encoder = new BCryptPasswordEncoder();
            return encoder;
        }


        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.httpBasic().and().authorizeRequests()
                    .antMatchers("/index.html", "/home.html", "/login.html", "/","/products.html").permitAll().anyRequest()
                    .authenticated().and().csrf()
                    .csrfTokenRepository(csrfTokenRepository()).and()
                    .addFilterAfter(csrfHeaderFilter(), CsrfFilter.class);
        }

        private Filter csrfHeaderFilter() {
            return new OncePerRequestFilter() {
                @Override
                protected void doFilterInternal(HttpServletRequest request,
                                                HttpServletResponse response, FilterChain filterChain)
                        throws ServletException, IOException {
                    CsrfToken csrf = (CsrfToken) request.getAttribute(CsrfToken.class
                            .getName());
                    if (csrf != null) {
                        Cookie cookie = WebUtils.getCookie(request, "XSRF-TOKEN");
                        String token = csrf.getToken();
                        if (cookie == null || token != null
                                && !token.equals(cookie.getValue())) {
                            cookie = new Cookie("XSRF-TOKEN", token);
                            cookie.setPath("/");
                            response.addCookie(cookie);
                        }
                    }
                    filterChain.doFilter(request, response);
                }
            };
        }

        private CsrfTokenRepository csrfTokenRepository() {
            HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
            repository.setHeaderName("X-XSRF-TOKEN");
            return repository;
        }
    }
}