package com.generation.blogpessoal.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.generation.blogpessoal.model.Usuario;
import com.generation.blogpessoal.model.UsuarioLogin;
import com.generation.blogpessoal.repository.UsuarioRepository;
import com.generation.blogpessoal.service.UsuarioService;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)//A anotação cria e inicializa o nosso ambiente de testes.
@TestInstance(TestInstance.Lifecycle.PER_CLASS)//A anotação permite modificar o ciclo de vida da Classe de testes.
public class UsuarioControllerTest {

	@Autowired
	private TestRestTemplate testRestTemplate;//injetado, um objeto da Classe TestRestTemplate para enviar as requisições para a nossa aplicação.
	
	@Autowired
	private UsuarioService usuarioService;
	
	@Autowired
	private UsuarioRepository usuariorepository;
	
	@BeforeAll//A anotação indica que o Método deve ser executado antes de cada Método da Classe, para criar pré-condições necessárias para cada teste 
	void start() {//apaga todos os dados da tabela e cria o usuário root@root.com para testar os Métodos protegidos por autenticação.
		usuariorepository.deleteAll();
		
		usuarioService.cadastrarUsuario(new Usuario(0L, "root", "root@root.com","rootroot"," "));
	}
	
	@Test
	@DisplayName("cadastrar um usuário")//mensagem que será exibida ao invés do nome do Método.
	public void deveCriarUmUsuario() {
		
		HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(new Usuario(0L,"paulo","paulo@email.com.br","12345678","-"));
		
		ResponseEntity<Usuario> corpoResposta = testRestTemplate
				.exchange("/usuarios/cadastrar", HttpMethod.POST, corpoRequisicao, Usuario.class);
		
		assertEquals(HttpStatus.CREATED,corpoResposta.getStatusCode());
	}
	
	@Test
	@DisplayName("não deve permitir duplicação do usuário")
	public void naoDeveDuplicarUsuario() {
		
		usuarioService.cadastrarUsuario(new Usuario(0L,"maria","maria@email.com.br","12345678","-"));
		
		HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(new Usuario(0L,"maria","maria@email.com.br","12345678","-"));
		
		ResponseEntity<Usuario> corpoResposta = testRestTemplate
				.exchange("/usuarios/cadastrar", HttpMethod.POST, corpoRequisicao, Usuario.class);
		
		assertEquals(HttpStatus.BAD_REQUEST,corpoResposta.getStatusCode());
	}
	
	@Test
	@DisplayName("atualizar um usuário")
	public void deveAtualizarUmUsuario() {
		
		Optional<Usuario> usuarioCadastardo = usuarioService.cadastrarUsuario(new Usuario(0L,"maria","maria@email.com.br","12345678","-"));
	
		Usuario usuarioUpdata = new Usuario(usuarioCadastardo.get().getId(),"mariaNova","mariaNovo@email.com.br","12345678","-");
		
		HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(usuarioUpdata);		
		
		ResponseEntity<Usuario> corpoResposta = testRestTemplate
				.withBasicAuth("root@root.com","rootroot")
				.exchange("/usuarios/atualizar", HttpMethod.PUT, corpoRequisicao, Usuario.class);
		
		assertEquals(HttpStatus.OK,corpoResposta.getStatusCode());
	}
	
	@Test
    @DisplayName("Listar todos os Usuários")//Personaliza o nome do teste permitindo inserir um Emoji (tecla Windows + . ) e texto.
    public void deveMostrarTodosUsuarios() {

        usuarioService.cadastrarUsuario(new Usuario(0L,"Guilherme", "guilherme@email.com.br", "12345678", "-"));

        usuarioService.cadastrarUsuario(new Usuario(0L,"Jeniffer", "jeniffer@email.com.br", "12345678", "-"));

        ResponseEntity<String> resposta = testRestTemplate
                .withBasicAuth("root@root.com", "rootroot")
                .exchange("/usuarios/all", HttpMethod.GET, null, String.class);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());

    }

    @Test
    @DisplayName("Listar Um Usuário Específico")
    public void deveListarApenasUmUsuario() {

        Optional<Usuario> usuarioBusca = usuarioService.cadastrarUsuario(new Usuario(0L,
                "Laura Santolia", "laura_santolia@email.com.br", "laura12345", "-"));

        ResponseEntity<String> resposta = testRestTemplate
                .withBasicAuth("root@root.com", "rootroot")
                .exchange("/usuarios/" + usuarioBusca.get().getId(), HttpMethod.GET, null, String.class);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
    }

    @Test
    @DisplayName("Login do Usuário")
    public void deveAutenticarUsuario() {

        usuarioService.cadastrarUsuario(new Usuario(0L,
                "Marisa Souza", "marisa_souza@email.com.br", "13465278", "-"));

        HttpEntity<UsuarioLogin> corpoRequisicao = new HttpEntity<UsuarioLogin>(new UsuarioLogin(0L,
                "", "marisa_souza@email.com.br", "13465278", "", ""));

        ResponseEntity<UsuarioLogin> corpoResposta = testRestTemplate
                .exchange("/usuarios/logar", HttpMethod.POST, corpoRequisicao, UsuarioLogin.class);

        assertEquals(HttpStatus.OK, corpoResposta.getStatusCode());

    }
}