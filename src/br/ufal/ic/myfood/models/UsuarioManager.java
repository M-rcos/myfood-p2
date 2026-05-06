package br.ufal.ic.myfood.models;

import com.google.gson.reflect.TypeToken;
import java.util.*;
import java.io.*;

public class UsuarioManager {

    private List<Usuario> usuarios;
    private int proximoId;
    private final String PATH = "data/usuarios.json";

    public UsuarioManager() {
        this.usuarios  = carregar();
        this.proximoId = usuarios.size() + 1;
    }

    // criarUsuario — cliente (sem CPF)

    public void criarUsuario(String nome, String email, String senha, String endereco) {
        validarCamposBasicos(nome, email, senha, endereco);
        checarEmailDuplicado(email);
        usuarios.add(new Usuario(String.valueOf(proximoId++), nome, email, senha, endereco, (String) null));
        salvar();
    }

    // criarUsuario — dono de empresa (com CPF)

    public void criarUsuario(String nome, String email, String senha, String endereco, String cpf) {
        validarCamposBasicos(nome, email, senha, endereco);
        if (cpf == null || cpf.trim().isEmpty() || cpf.length() != 14) {
            throw new IllegalArgumentException("CPF invalido");
        }
        checarEmailDuplicado(email);
        usuarios.add(new Usuario(String.valueOf(proximoId++), nome, email, senha, endereco, cpf));
        salvar();
    }

    // criarUsuario — entregador (com veiculo e placa)

    public void criarUsuario(String nome, String email, String senha,
                             String endereco, String veiculo, String placa) {
        validarCamposBasicos(nome, email, senha, endereco);

        if (veiculo == null || veiculo.trim().isEmpty()) {
            throw new IllegalArgumentException("Veiculo invalido");
        }
        if (placa == null || placa.trim().isEmpty()) {
            throw new IllegalArgumentException("Placa invalido");
        }

        // Placa deve ser única
        for (Usuario u : usuarios) {
            if (placa.equals(u.getPlaca())) {
                throw new IllegalArgumentException("Placa invalido");
            }
        }

        checarEmailDuplicado(email);
        usuarios.add(new Usuario(String.valueOf(proximoId++), nome, email, senha, endereco, veiculo, placa));
        salvar();
    }

    // getAtributoUsuario

    public String getAtributoUsuario(String id, String atributo) {
        Usuario u = buscarPorId(id);
        if (atributo == null || atributo.trim().isEmpty()) {
            throw new IllegalArgumentException("Atributo invalido");
        }
        switch (atributo) {
            case "nome":     return u.getNome();
            case "email":    return u.getEmail();
            case "senha":    return u.getSenha();
            case "endereco": return u.getEndereco();
            case "cpf":      return u.getCpf();
            case "veiculo":  return u.getVeiculo();
            case "placa":    return u.getPlaca();
            default:         throw new IllegalArgumentException("Atributo invalido");
        }
    }

    // login

    public String login(String email, String senha) {
        if (email == null || email.isEmpty() || senha == null || senha.isEmpty()) {
            throw new IllegalArgumentException("Login ou senha invalidos");
        }
        for (Usuario u : usuarios) {
            if (u.getEmail().equals(email) && u.getSenha().equals(senha)) {
                return u.getId();
            }
        }
        throw new IllegalArgumentException("Login ou senha invalidos");
    }

    // Auxiliares públicos

    public Usuario buscarPorId(String id) {
        for (Usuario u : usuarios) {
            if (u.getId().equals(id)) return u;
        }
        throw new IllegalArgumentException("Usuario nao cadastrado.");
    }

    // Validações privadas

    private void checarEmailDuplicado(String email) {
        for (Usuario u : usuarios) {
            if (u.getEmail().equals(email)) {
                throw new IllegalArgumentException("Conta com esse email ja existe");
            }
        }
    }

    private void validarCamposBasicos(String nome, String email, String senha, String endereco) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome invalido");
        }
        if (email == null || email.trim().isEmpty() || !email.contains("@")) {
            throw new IllegalArgumentException("Email invalido");
        }
        if (senha == null || senha.trim().isEmpty()) {
            throw new IllegalArgumentException("Senha invalido");
        }
        if (endereco == null || endereco.trim().isEmpty()) {
            throw new IllegalArgumentException("Endereco invalido");
        }
    }

    // JSON

    private List<Usuario> carregar() {
        try {
            File f = new File(PATH);
            if (!f.exists()) return new ArrayList<>();
            return JsonUtil.fromJsonFile(PATH, new TypeToken<List<Usuario>>(){}.getType());
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private void salvar() {
        JsonUtil.toJsonFile(PATH, usuarios);
    }
}
