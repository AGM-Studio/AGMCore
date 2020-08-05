package me.ashenguard.api;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class WebReader {
    private String url;

    public WebReader(String url) {
        this.url = url;
    }

    @NotNull
    public List<String> readLines() {
        List<String> result = new ArrayList<>();

        try {
            URLConnection con = new URL(url).openConnection();
            BufferedReader buffer = new BufferedReader(new InputStreamReader(con.getInputStream()));

            String line;
            while ((line = buffer.readLine()) != null)
                result.add(line);

        } catch (IOException ignored) {}

        return result;
    }

    @NotNull
    public String read() {
        List<String> lines = readLines();
        StringBuilder result = new StringBuilder();
        for (String line: lines) {
            result.append(line.replace("\n", "")).append("\n");
        }

        return result.toString();
    }
}
