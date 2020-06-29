package ch.software_atelier.simpleflex;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * This Class holds some static Utility-methods
 */
public class Utils {

    private static HashMap<String,String> customMimeTypes = new HashMap<>();

    public static String getMimeFromFilePath(String path) {
        StringTokenizer t = new StringTokenizer(path, ".");
        String extension = "default";
        while (t.hasMoreTokens())
            extension = t.nextToken().toLowerCase();
        if (customMimeTypes.containsKey(extension))
            return customMimeTypes.get(extension);

        JSONObject o = new JSONObject(getResource("mime.json"));
        if (o.has(extension)) {
            return o.getString(extension);
        }
        return o.getString("default");
    }

    public static void registerMime(String suffix, String mime){
        customMimeTypes.put(suffix.toLowerCase(),mime);
    }

    public static String getResource(String name) {

        StringBuilder result = new StringBuilder();
        ClassLoader classLoader = Utils.class.getClassLoader();
        try (Scanner scanner = new Scanner(classLoader.getResourceAsStream(name))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                result.append(line).append("\n");
            }
        }
        return result.toString();

    }

    public static void main(String[] args) {
        registerMime("HeLLo","custom/hello");
        System.out.println(getMimeFromFilePath("file.JPG"));
        System.out.println(getMimeFromFilePath("file.HELLO"));
    }

    /**
     * Reads all bytes of a BuferedInputStream untill a NewLine-Char and returns the readed data as a String. This
     * Method blocks, if no data is aviable and only stops on a newline- or EOF-char!
     *
     * @param is The InputStream
     * @return The readed Data or null on EOF.
     * @throws java.io.IOException
     */
    public static byte[] readBytesUntilNewLine(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        int charAsInt = is.read();
        boolean eof = false;
        while ((charAsInt != -1) && (charAsInt != (int) '\n')) {
            baos.write(charAsInt);
            if (is.available() >= 1)
                charAsInt = is.read();
            if (charAsInt == -1)
                eof = true;
        }
        if ((baos.toByteArray().length == 0) && (eof))
            return null;
        baos.write((int) ('\n'));
        byte[] bytes = baos.toByteArray();

        return bytes;
    }

    /**
     * Reads all bytes of a BuferedInputStream untill a NewLine-Char and returns the readed data as a String. This
     * Method blocks, if no data is aviable and only stops on a newline- or EOF-char!
     *
     * @param is   The InputStream
     * @param trim trims the String
     * @return The readed Data or null on EOF.
     * @throws java.io.IOException
     */
    public static String readUntilNewLine(BufferedInputStream is, boolean trim) throws IOException {
        StringBuilder sb = new StringBuilder();

        int charAsInt = is.read();
        boolean eof = false;
        while ((charAsInt != -1) && (charAsInt != (int) '\n')) {
            sb.append((char) ((byte) charAsInt));
            if (is.available() >= 1)
                charAsInt = is.read();
            if (charAsInt == -1)
                eof = true;
        }
        if ((sb.length() == 0) && (eof))
            return null;
        sb.append("\n");
        String line = sb.toString();
        if (trim) {
            line = line.trim();
        }
        return line;
    }

    private static String getCharter(String charAsHex) {
        Integer charAsInteger = Integer.valueOf(charAsHex, 16);
        int charAsInt = charAsInteger.intValue();
        return (char) charAsInt + "";
    }

    public static String[] splitHeader(String header) {
        StringTokenizer headerTokenizer = new StringTokenizer(header, ":");
        String key = null;
        String value = null;
        if (headerTokenizer.hasMoreTokens()) {
            key = headerTokenizer.nextToken();
        }
        if (headerTokenizer.hasMoreTokens()) {
            value = headerTokenizer.nextToken();
        }

        if ((key != null) && (value != null)) {
            return new String[]{key, value};
        } else
            return null;
    }

    public static String getFileExtension(String fileName) {
        StringTokenizer st = new StringTokenizer(fileName, ".");

        String extension = "";
        while (st.hasMoreTokens())
            extension = st.nextToken();
        System.out.println("Extension: " + extension);
        return extension;
    }

    public static String getHTTPDateHeaderValue(Date d) {

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormat.format(calendar.getTime());

    }

    public static byte[] readFile(File f) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             BufferedInputStream is = new BufferedInputStream(new FileInputStream(f));) {

            byte[] buffer = new byte[512];
            int len = 0;
            while ((len = is.read(buffer)) != -1)
                bos.write(buffer, 0, len);
            bos.flush();
            byte[] data2return = bos.toByteArray();
            is.close();
            bos.close();
            return data2return;

        } catch (Exception e) {
            return new byte[0];
        }
    }

    public static List<String> tokenize(String src, String separator) {
        List<String> list = new ArrayList();
        StringTokenizer st = new StringTokenizer(src, separator);
        while (st.hasMoreTokens()) {
            list.add(st.nextToken());
        }
        return list;
    }

    public static long parseLong(String s) {
        StringBuilder result = new StringBuilder();
        char[] chars = s.toCharArray();

        if (chars.length > 0) {
            if (chars[0] == '-')
                result.append('-');
        }
        for (int i = 0; i < chars.length; i++) {
            if ((chars[i] == '.') | (chars[i] == ',')) {
                break;
            }
            if (Character.isDigit(chars[i]))
                result.append(chars[i]);
        }
        String resStr = result.toString();
        if (resStr.length() == 0)
            return 0;
        else
            return Long.parseLong(resStr);
    }

    public static JSONObject file2JSON(File f) throws IOException, JSONException {
        String content = new String(readFile(f));
        return new JSONObject(content);
    }

    public static List<String> tokenizeByIgnoringEnclosure(String src, char separator, char enclosure) {
        List<String> list = new ArrayList();

        boolean enclosed = false;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < src.length(); i++) {
            String c = src.substring(i, i + 1);

            if (enclosed && c.equals(enclosure + "")) {
                sb.append(c);
                enclosed = false;
            } else if (c.equals(enclosure + "")) {
                sb.append(c);
                enclosed = true;
            } else if (enclosed) {
                sb.append(c);
            } else if (c.equals(separator + "")) {
                list.add(sb.toString());
                sb = new StringBuilder();
            } else {
                sb.append(c);
            }
        }

        list.add(sb.toString());
        return list;
    }

    public static String removeNonEscaped(String src, char remove, char escape) {
        boolean escaped = false;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < src.length(); i++) {
            String c = src.substring(i, i + 1);
            if (escaped) {
                sb.append(c);
                escaped = false;
            } else if (c.equals(escape + "")) {
                escaped = true; // Add next Character, no mather what!
            } else if (c.equals(remove + "")) {
                // Do nothing
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
