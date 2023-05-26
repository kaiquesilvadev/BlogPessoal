package com.generation.blogpessoal.security;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component //@Component, o que indica que esta Classe é uma Classe de Componente. Classe de Componente é uma Classe gerenciada pelo Spring, que permite Injetar e Instanciar qualquer Dependência especificada na implementação da Classe, em qualquer outra Classe, sempre que necessário.
public class JwtService {
	
	//Para gerar esta chave, utilizamos o site All Keys Generator (https://www.allkeysgenerator.com/), que permite gerar chaves encriptadas aleatórias de diversos formatos e tamanhos:
	
	public static final String SECRET = "655468576D5A7134743777217A25432A462D4A614E645267556A586E32723575"; 

	//O Método Key getSignKey() é responsável por codificar a SECRET em Base 64 e gerar a Assinatura (Signature) do Token JWT, codificada pelo Algoritmo HMAC SHA256.
	private Key getSignKey() {
		byte[] keyBytes = Decoders.BASE64.decode(SECRET);
		return Keys.hmacShaKeyFor(keyBytes);
	}
	
	// Método extractAllClaims(String token) retorna todas as claims, inseridas no Payload do Token JWT.
	private Claims extractAllClaims(String token) {
		return Jwts.parserBuilder()
				.setSigningKey(getSignKey()).build()
				.parseClaimsJws(token).getBody();
	}
	
	//O Método extractClaim(String token, Function< Claims, T > claimsResolver) retorna uma claim específica, inserida no Payload do Token JWT.
	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}
	
	//O Método extractUsername(String token) recupera os dados da Claim sub, onde se encontra o usuario (e-mail),
	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}
	
	//O Método extractExpiration(String token) recupera os dados da Claim exp, onde se encontra a data e o horário de expiração do Token JWT,
	public Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}
	
	//O Método isTokenExpired(String token) recupera os dados da Claim exp, onde se encontra a data e o horário de expiração do Token JWT,
	private Boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}
	
	//O Método validateToken(String token, UserDetails userDetails) valida se o Token JWT pertence ao usuário que enviou o token através do Cabeçalho de uma requisição HTTP
	public Boolean validateToken(String token, UserDetails userDetails) {
		final String username = extractUsername(token);
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}

	//O Método createToken(Map<String, Object> claims, String userName) cria o Token JWT. 
	//O Método recebe 2 parâmetros: uma Collection Map, chamada claims, que será utilizada para receber Claims personalizadas e um Objeto da Classe String,
	//chamado userName, contendo o usuário autenticado (e-mail).
	private String createToken(Map<String, Object> claims, String userName) {
		return Jwts.builder()
					.setClaims(claims)
					.setSubject(userName)
					.setIssuedAt(new Date(System.currentTimeMillis()))
					.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
					.signWith(getSignKey(), SignatureAlgorithm.HS256).compact();
	}

    //O Método generateToken(String userName) é responsável por gerar um novo Token a partir do usuario (e-mail), que será recebido através do parâmetro username.
	public String generateToken(String userName) {
		Map<String, Object> claims = new HashMap<>();
		return createToken(claims, userName);
	}

}