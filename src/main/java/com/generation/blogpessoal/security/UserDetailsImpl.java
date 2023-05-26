package com.generation.blogpessoal.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.generation.blogpessoal.model.Usuario;

public class UserDetailsImpl implements UserDetails {

	private static final long serialVersionUID = 1L;

	private String userName;  // Define os Atributos username, que receberá o atributo usuario  (e-mail)
	private String password; //password, que receberá o atributo senha
	private List<GrantedAuthority> authorities; //Define o Atributo authorities como uma Collection List do tipo GrantedAuthority.

	/*Método Construtor da Classe UserDetailsImpl, com os atributos username e password, 
    * que terão os seus valores preenchidos através de um Objeto da Classe Usuario*/
	
	
	public UserDetailsImpl(Usuario user) {
		this.userName = user.getUsuario();
		this.password = user.getSenha();
	}
    //Método Construtor da Classe UserDetailsImpl sem parâmetros, que será utilizado eventualmente para gerar Objetos com os atributos não preenchidos.
	
	public UserDetailsImpl() {	}

	//Método getAuthorities(), responsável por retornar os Direitos de Acesso do Usuário 
	//Como não iremos implementar os Direitos de Acesso do Usuário, o Método sempre retornará uma Collection vazia. 
	
	// (?). Este sinal significa que o Método pode receber um Objeto de qualquer Classe. 
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {

		return authorities;
	}

	@Override
	public String getPassword() {

		return password;
	}

	@Override
	public String getUsername() {

		return userName;
	}
	
	//Indica se o acesso do usuário expirou (tempo de acesso). Uma conta expirada não pode ser autenticada (return false).
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}
	//Indica se o usuário está bloqueado ou desbloqueado. Um usuário bloqueado não pode ser autenticado (return false).
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}
	//Indica se as credenciais do usuário (senha) expiraram (precisa ser trocada). Senha expirada impede a autenticação (return false).
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}
	//Indica se o usuário está habilitado ou desabilitado. Um usuário desabilitado não pode ser autenticado (return false).
	@Override
	public boolean isEnabled() {
		return true;
	}

}