package com.generation.blogpessoal.service;
 
import java.util.List;
import java.util.Optional;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
 
import com.generation.blogpessoal.model.Usuario;
import com.generation.blogpessoal.model.UsuarioLogin;
import com.generation.blogpessoal.repository.UsuarioRepository;
import com.generation.blogpessoal.security.JwtService;
 
@Service
public class UsuarioService {
 
    @Autowired
    private UsuarioRepository usuarioRepository;
 
    @Autowired
    private JwtService jwtService;
 
    @Autowired
    private AuthenticationManager authenticationManager;
 
    // Cadastrar usuário
    public Optional<Usuario> cadastrarUsuario(Usuario usuario) {
 
        if (usuarioRepository.findByUsuario(usuario.getUsuario()).isPresent()) {
            return Optional.empty();
        }
 
        usuario.setSenha(criptografarSenha(usuario.getSenha()));
 
        return Optional.ofNullable(usuarioRepository.save(usuario));
    }
 
    // Atualizar usuário
    public Optional<Usuario> atualizarUsuario(Usuario usuario) {
 
        if (usuarioRepository.findById(usuario.getId()).isPresent()) {
            usuario.setSenha(criptografarSenha(usuario.getSenha()));
            return Optional.ofNullable(usuarioRepository.save(usuario));
        }
 
        return Optional.empty();
    }
 
    // Autenticar usuário
    public Optional<UsuarioLogin> autenticarUsuario(Optional<UsuarioLogin> usuarioLogin) {
 
        var credenciais = new UsernamePasswordAuthenticationToken(
                usuarioLogin.get().getUsuario(),
                usuarioLogin.get().getSenha()
        );
 
        Authentication authentication = authenticationManager.authenticate(credenciais);
 
        if (authentication.isAuthenticated()) {
 
            Optional<Usuario> usuario = usuarioRepository.findByUsuario(usuarioLogin.get().getUsuario());
 
            if (usuario.isPresent()) {
 
                usuarioLogin.get().setId(usuario.get().getId());
                usuarioLogin.get().setNome(usuario.get().getNome());
                usuarioLogin.get().setFoto(usuario.get().getFoto());
                usuarioLogin.get().setSenha("");
                usuarioLogin.get().setToken(gerarToken(usuarioLogin.get().getUsuario()));
 
                return usuarioLogin;
            }
        }
 
        return Optional.empty();
    }
 
    // Gerar token
    private String gerarToken(String usuario) {
        return "Bearer " + jwtService.generateToken(usuario);
    }
 
    // Criptografar senha
    private String criptografarSenha(String senha) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(senha);
    }
    
 // Listar todos os usuários
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    // Buscar usuário por ID
    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

 
}
 