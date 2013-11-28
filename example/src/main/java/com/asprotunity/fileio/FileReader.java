package com.asprotunity.fileio;

import java.io.*;

public class FileReader {
    public static String readFile(String fileName) throws IOException {
        InputStream sqlStream = new FileInputStream(fileName);
        BufferedReader reader = new BufferedReader(new InputStreamReader(sqlStream));

        try {
            StringBuilder stringBuilder = new StringBuilder();

            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }
                stringBuilder.append(line);
            }

            return stringBuilder.toString();
        } finally {
            reader.close();
        }
    }
}
