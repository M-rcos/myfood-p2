package br.ufal.ic.myfood.models;

import br.ufal.ic.myfood.exceptions.MyFoodException;
import java.util.*;

public class EmpresaManager {

    private final String PATH = "data/empresas.json";
    private UsuarioManager usuarioManager;

    public EmpresaManager(UsuarioManager usuarioManager) {
        this.usuarioManager = usuarioManager;
    }

    private List<Empresa> carregar() {
        List<Empresa> lista = new ArrayList<>();
        for (Map<String, String> m : JsonUtil.carregarLista(PATH))
            lista.add(Empresa.fromMap(m));
        return lista;
    }

    private void salvar(List<Empresa> lista) {
        List<String> jsons = new ArrayList<>();
        for (Empresa e : lista) jsons.add(e.toJson());
        JsonUtil.salvarLista(PATH, jsons);
    }

    public String criarEmpresa(String tipoEmpresa, String idDono, String nome,
                               String endereco, String tipoCozinha) {
        buscarDonoOuErro(idDono);
        validarNome(nome);
        List<Empresa> lista = carregar();
        validarDuplicata(lista, idDono, nome, endereco);
        Empresa nova = new Empresa(String.valueOf(lista.size() + 1), nome, endereco, tipoCozinha, idDono);
        lista.add(nova);
        salvar(lista);
        return nova.getId();
    }

    public String criarEmpresa(String tipoEmpresa, String idDono, String nome,
                               String endereco, String abre, String fecha, String tipoMercado) {
        if (tipoEmpresa == null || tipoEmpresa.trim().isEmpty())
            throw new MyFoodException("Tipo de empresa invalido");
        buscarDonoOuErro(idDono);
        validarNome(nome);
        validarEndereco(endereco);
        validarHorario(abre, fecha);
        if (tipoMercado == null || tipoMercado.trim().isEmpty())
            throw new MyFoodException("Tipo de mercado invalido");
        List<Empresa> lista = carregar();
        validarDuplicata(lista, idDono, nome, endereco);
        Empresa nova = new Empresa(String.valueOf(lista.size() + 1), nome, endereco,
                idDono, tipoEmpresa, abre, fecha, tipoMercado);
        lista.add(nova);
        salvar(lista);
        return nova.getId();
    }

    public String criarEmpresa(String tipoEmpresa, String idDono, String nome,
                               String endereco, boolean aberto24Horas, int numeroFuncionarios) {
        if (tipoEmpresa == null || tipoEmpresa.trim().isEmpty())
            throw new MyFoodException("Tipo de empresa invalido");
        buscarDonoOuErro(idDono);
        validarNome(nome);
        validarEndereco(endereco);
        List<Empresa> lista = carregar();
        validarDuplicata(lista, idDono, nome, endereco);
        Empresa nova = new Empresa(String.valueOf(lista.size() + 1), nome, endereco,
                idDono, tipoEmpresa, aberto24Horas, numeroFuncionarios);
        lista.add(nova);
        salvar(lista);
        return nova.getId();
    }

    public void alterarFuncionamento(String idMercado, String abre, String fecha) {
        List<Empresa> lista = carregar();
        Empresa e = buscarPorIdNaLista(lista, idMercado);
        if (!"mercado".equals(e.getTipoEmpresa()))
            throw new MyFoodException("Nao e um mercado valido");
        validarHorario(abre, fecha);
        e.setAbre(abre);
        e.setFecha(fecha);
        salvar(lista);
    }

    public String getAtributoEmpresa(String idEmpresa, String atributo) {
        Empresa e = buscarPorId(idEmpresa);
        if (atributo == null || atributo.trim().isEmpty())
            throw new MyFoodException("Atributo invalido");
        switch (atributo) {
            case "nome":               return e.getNome();
            case "endereco":           return e.getEndereco();
            case "tipoCozinha":        return e.getTipoCozinha();
            case "abre":               return e.getAbre();
            case "fecha":              return e.getFecha();
            case "tipoMercado":        return e.getTipoMercado();
            case "aberto24Horas":      return String.valueOf(e.isAberto24Horas());
            case "numeroFuncionarios": return String.valueOf(e.getNumeroFuncionarios());
            case "dono":
                try { return usuarioManager.buscarPorId(e.getIdDono()).getNome(); }
                catch (Exception ex) { throw new MyFoodException("Usuario nao cadastrado"); }
            default: throw new MyFoodException("Atributo invalido");
        }
    }

    public String getEmpresasDoUsuario(String idDono) {
        buscarDonoOuErro(idDono);
        List<String> lista = new ArrayList<>();
        for (Empresa e : carregar())
            if (e.getIdDono().equals(idDono))
                lista.add("[" + e.getNome() + ", " + e.getEndereco() + "]");
        return "{" + lista.toString() + "}";
    }

    public String getIdEmpresa(String idDono, String nome, int indice) {
        if (nome == null || nome.trim().isEmpty())
            throw new MyFoodException("Nome invalido");
        if (indice < 0)
            throw new MyFoodException("Indice invalido");
        List<Empresa> encontradas = new ArrayList<>();
        for (Empresa e : carregar())
            if (e.getIdDono().equals(idDono) && e.getNome().equals(nome)) encontradas.add(e);
        if (encontradas.isEmpty()) throw new MyFoodException("Nao existe empresa com esse nome");
        if (indice >= encontradas.size()) throw new MyFoodException("Indice maior que o esperado");
        return encontradas.get(indice).getId();
    }

    public boolean isDonoDeEmpresa(String idUsuario) {
        if (idUsuario == null) return false;
        for (Empresa e : carregar())
            if (e.getIdDono().equals(idUsuario)) return true;
        return false;
    }

    public Empresa buscarPorId(String id) {
        for (Empresa e : carregar())
            if (e.getId().equals(id)) return e;
        throw new MyFoodException("Empresa nao cadastrada");
    }

    public List<Empresa> getTodas() { return carregar(); }

    private Empresa buscarPorIdNaLista(List<Empresa> lista, String id) {
        for (Empresa e : lista)
            if (e.getId().equals(id)) return e;
        throw new MyFoodException("Empresa nao cadastrada");
    }

    private Usuario buscarDonoOuErro(String idDono) {
        if (idDono == null || idDono.trim().isEmpty())
            throw new MyFoodException("Usuario nao pode criar uma empresa");
        Usuario dono;
        try { dono = usuarioManager.buscarPorId(idDono); }
        catch (Exception e) { throw new MyFoodException("Usuario nao cadastrado"); }
        if (dono.getCpf() == null || dono.getCpf().trim().isEmpty())
            throw new MyFoodException("Usuario nao pode criar uma empresa");
        return dono;
    }

    private void validarNome(String nome) {
        if (nome == null || nome.trim().isEmpty())
            throw new MyFoodException("Nome invalido");
    }

    private void validarEndereco(String endereco) {
        if (endereco == null || endereco.trim().isEmpty())
            throw new MyFoodException("Endereco da empresa invalido");
    }

    private void validarDuplicata(List<Empresa> lista, String idDono, String nome, String endereco) {
        for (Empresa e : lista) {
            if (e.getNome().equals(nome) && !e.getIdDono().equals(idDono))
                throw new MyFoodException("Empresa com esse nome ja existe");
            if (e.getNome().equals(nome) && e.getEndereco().equals(endereco) && e.getIdDono().equals(idDono))
                throw new MyFoodException("Proibido cadastrar duas empresas com o mesmo nome e local");
        }
    }

    private void validarHorario(String abre, String fecha) {
        if (abre == null) throw new MyFoodException("Horario invalido");
        if (fecha == null) throw new MyFoodException("Horario invalido");
        validarFormato(abre);
        validarFormato(fecha);
        int[] a = parseHora(abre), f = parseHora(fecha);
        if (a[0] > 23 || a[1] > 59 || f[0] > 23 || f[1] > 59)
            throw new MyFoodException("Horario invalido");
        if (f[0] * 60 + f[1] <= a[0] * 60 + a[1])
            throw new MyFoodException("Horario invalido");
    }

    private void validarFormato(String hora) {
        if (hora.isEmpty() || !hora.matches("\\d{2}:\\d{2}"))
            throw new MyFoodException("Formato de hora invalido");
    }

    private int[] parseHora(String hora) {
        String[] p = hora.split(":");
        return new int[]{ Integer.parseInt(p[0]), Integer.parseInt(p[1]) };
    }
}
