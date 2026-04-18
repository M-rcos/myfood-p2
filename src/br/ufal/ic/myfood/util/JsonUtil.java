package br.ufal.ic.myfood.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;

public class JsonUtil {

    private static final Gson gson = new Gson();

    public static <T> void salvar(String path, T dados) {
        try (Writer writer = new FileWriter(path)) {
            gson.toJson(dados, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static <T> T carregar(String path, Type tipo) {
        try (Reader reader = new FileReader(path)) {
            return gson.fromJson(reader, tipo);
        } catch (IOException e) {
            return null;
        }
    }
}
