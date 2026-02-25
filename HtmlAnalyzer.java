import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Stack;

public class HtmlAnalyzer {

    public static void main(String[] args) {
        if (args.length != 1) return;

        try {
            URL url = new URL(args[0]);
            URLConnection connection = url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            analyzeHtml(reader);
            reader.close();
        } catch (IOException e) {
            System.out.println("URL connection error");
        }
    }

    private static void analyzeHtml(BufferedReader reader) throws IOException {
        Stack<String> stack = new Stack<>();
        String deepestText = null;
        int maxDepth = -1;
        String line;

        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty()) continue;

            if (isOpeningTag(line)) {
                stack.push(getTagContent(line));
            } else if (isClosingTag(line)) {
                if (stack.isEmpty() || !stack.pop().equals(getTagContent(line))) {
                    System.out.println("malformed HTML");
                    return;
                }
            } else {
                int currentDepth = stack.size();
                if (currentDepth > maxDepth) {
                    maxDepth = currentDepth;
                    deepestText = line;
                }
            }
        }

        if (!stack.isEmpty()) {
            System.out.println("malformed HTML");
        } else if (deepestText != null) {
            System.out.println(deepestText);
        }
    }

    private static boolean isOpeningTag(String line) {
        return line.startsWith("<") && !line.startsWith("</") && line.endsWith(">");
    }

    private static boolean isClosingTag(String line) {
        return line.startsWith("</") && line.endsWith(">");
    }

    private static String getTagContent(String tag) {
        return tag.replace("<", "").replace(">", "").replace("/", "");
    }
}