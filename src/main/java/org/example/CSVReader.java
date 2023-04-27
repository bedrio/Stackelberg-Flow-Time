package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class CSVReader {
    String path = "src/main/java/org/example/";

    public ArrayList<String> readSingleColumnCSV(String fileName) {
        ArrayList<String> data = new ArrayList<>();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(this.path + fileName));
            String line;
            while ((line = br.readLine()) != null) {
                StringTokenizer st = new StringTokenizer(line, ",");
                String[] row = new String[st.countTokens()];
                for (int i = 0; i < row.length; i++) {
                    row[i] = st.nextToken();
                }
                data.add(Arrays.toString(row));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        data.remove(0); //Deletes column headers
        return data;
    }

    public ArrayList<Map<String, String>> readCSV(String fileName) {
        String line = "";
        String cvsSplitBy = ",";
        ArrayList<Map<String, String>> data = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(this.path + fileName))) {
            String[] headers = br.readLine().split(cvsSplitBy);

            while ((line = br.readLine()) != null) {
                String[] values = line.split(cvsSplitBy);
                Map<String, String> row = new HashMap<>();

                for (int i = 0; i < headers.length; i++) {
                    row.put(headers[i], values[i]);
                }

                data.add(row);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;
    }
}
