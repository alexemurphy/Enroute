package utils;

import main.db.DBHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ResourceUtils {

	public static List<String> getLines(String resourceFile) throws IOException {
		List<String> lines = new ArrayList<>(4);
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(
				DBHelper.class.getResourceAsStream("/" + resourceFile)))) {
			while (true) {
				String line = reader.readLine();
				if (line == null) break;
				lines.add(line);
			}
		}
		return lines;
	}
}
