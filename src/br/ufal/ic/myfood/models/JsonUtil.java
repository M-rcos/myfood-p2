package br.ufal.ic.myfood.models;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.*;
import java.lang.reflect.Type;

public class JsonUtil {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static <T> T fromJsonFile(String path, Type type) {
        try (Reader reader = new FileReader(path)) {
            T data = gson.fromJson(reader, type);
            if (data == null) {
                return (T) new java.util.ArrayList<>();
            }
            return data;
        } catch (Exception e) {
            return (T) new java.util.ArrayList<>();
        }
    }

    public static void deleteFile(String path) {
        File file = new File(path);
        if (file.exists()) {
        file.delete();
        }
    }

    public static void toJsonFile(String path, Object data) {
        try {
            File file = new File(path);
            file.getParentFile().mkdirs();

            try (Writer writer = new FileWriter(file)) {
                gson.toJson(data, writer);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
