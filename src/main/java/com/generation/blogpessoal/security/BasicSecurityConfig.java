package com.generation.blogpessoal.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration //indica que a Classe é do tipo configuração, ou seja, define uma Classe como fonte de definições de Beans, além de ser uma das anotações essenciais ao utilizar uma configuração baseada em Java.
@EnableWebSecurity//habilita a segurança de forma Global (toda a aplicação) e sobrescreve os Métodos que irão redefinir as regras de Segurança da sua aplicação.
public class BasicSecurityConfig {

    @Autowired
    private JwtAuthFilter authFilter;

    /*o Método userDetailsService, que retornará uma instância da Classe UserDetailsServiceImpl
     que implementa a Interface userDetailsService. Nós utilizaremos este Método para validar se o usuário que está tentando se 
     autenticar está persistido no Banco de dados da aplicação.*/
    
    
    @Bean//No Spring, os objetos que formam a espinha dorsal da sua aplicação e que são gerenciados pelo Spring são chamados de Beans. Um Bean é um objeto que é instanciado, montado e gerenciado pelo Spring.
    UserDetailsService userDetailsService() {

        return new UserDetailsServiceImpl();
    }

    /*o Método passwordEncoder(), que retornará uma instância da Classe BCryptPasswordEncoder(), que utiliza o algoritmo de criptografia do tipo hash, chamado BCrypt.
     *  utilizaremos este Método para Criptografar e Validar a senha do usuário.*/
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Método authenticationProvider, que retornará uma instância da Classe AuthenticationProvider, informando o Método de autenticação que será utilizado.
    @Bean
    AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    //O Método authenticationManager, implementa a confguração de autenticação. Este Método utiliza o Método authenticationConfiguration.getAuthenticationManager() para procurar uma implementação da Interface UserDetailsService e utilizá-la para identificar se o usuário é válido ou não.
    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /*O Método SecurityFilterChain filterChain(HttpSecurity http), estamos informando ao Spring que a configuração padrão da Spring Security será substituída por uma nova configuração. 
     * Nesta configuração iremos customizar a autenticação da aplicação desabilitando o formulário de login e habilitando a autenticação via HTTP.*/
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http //configuramos a segurança baseada na Web (protocolo HTTP)
                .sessionManagement() //linha 66 , 67 ela deverá ter todas as informações necessárias para o servidor atender à Requisição e a mesma será finalizada com a Resposta HTTP do servidor.
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().csrf().disable() // iremos desabilitar a proteção que vem ativa contra ataques do tipo CSRF 
                .cors(); //vamos liberar o acesso de outras origens (Requisições de outros servidores HTTP), desta forma nossa aplicação poderá ser acessada por outros domínios, ou seja, de outros endereços, além do endereço onde a aplicação está hospedada

        http
                .authorizeHttpRequests((auth) -> auth //Lambda para definir quais endpoints poderão acessar o sistema sem precisar de autenticação. 
                        .requestMatchers("/usuarios/logar").permitAll() //indicaremos os endereços (URI) dos endpoints, que estarão acessíveis sem autenticação.
                        .requestMatchers("/usuarios/cadastrar").permitAll() //indicaremos os endereços (URI) dos endpoints, que estarão acessíveis sem autenticação.
                        .requestMatchers("/error/**").permitAll() //permite que as Exceptions lançadas através do comando throw sejam lançadas. 
                        .requestMatchers(HttpMethod.OPTIONS).permitAll() //permite que o cliente (front-end), possa descobrir quais são as opções permitidas e/ou obrigatórias no cabeçalho da Requisição HTTP. 
                        .anyRequest().authenticated()) //informamos ao sistema que todos os endpoints que não estiverem especificados na lista acima, a autenticação será obrigatória.
                .authenticationProvider(authenticationProvider()) // chamamos o Método autheticationProvider(), para efetuar a autenticação do usuário, através do Banco de dados.
                .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class) //informamos que o Filtro de Servlet JwtAuthFilter, deve ser aplicado antes de efetuar a autenticação do usuário, 
                .httpBasic(); //informamos ao sistema que o servidor irá receber requisições que devem ter o esquema HTTP Basic de autenticação. Ao abrir a sua aplicação no navegador da Internet

        return http.build();

    }

}
