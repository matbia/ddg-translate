package io.github.matbia.translate;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A lazy loading singleton class responsible for providing access to DuckDuckGo's translation service.
 * Fetches the vqd token at the time of construction and updates it after detecting that the initial is no longer valid.
 */
final class TranslatorService {
    private static TranslatorService instance;
    private static final Logger LOGGER = Logger.getLogger(TranslatorService.class.getName());
    private static final HttpClient httpClient = HttpClient.newBuilder().build();
    private static final String BASE_URL = "https://duckduckgo.com",
            USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:90.0) Gecko/20100101 Firefox/90.0";
    /**
     * A token found in a script tag inside the head element of every search results HTML page.
     * It's required as a parameter for accessing the /translation.js endpoint.
     */
    private String vqdToken;

    /**
     * Initial vqd token is fetched when the class is instantiated.
     */
    private TranslatorService() {
        try {
            vqdToken = getVqdToken();
        } catch (IOException | InterruptedException e) {
            LOGGER.log(Level.SEVERE, "Unable to fetch the initial vqd token");
            e.printStackTrace();
        }
    }

    /**
     * Lazily initializes and gives access to the only instance of TranslationService.
     * @return the only instance of this class
     */
    static TranslatorService getInstance() {
        if(instance == null) {
            instance = new TranslatorService();
        }
        return instance;
    }

    /**
     * Uses the HTML response for query 'translate' to find the vqd token via Regex.
     * @return vqd token or null if none was found
     * @throws NoSuchElementException if no vqd token was found in the response
     * @throws IOException if a connection error has occurred
     */
    private String getVqdToken() throws IOException, InterruptedException {
        var request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(BASE_URL + "/?q=translate"))
                .setHeader("User-Agent", USER_AGENT)
                .build();

        String res = httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body();
        Matcher m = Pattern.compile("vqd='.+';").matcher(res);
        if(m.find()) {
            String match = m.group(0);
            return match.substring(5, match.length() - 2);
        } else throw new NoSuchElementException("No vqd found in response: " + res);
    }

    /**
     * Translates the given text to a specified language.
     * @param fromLangCode language code according to input
     * @param toLangCode language code specifying output
     * @param text translator input
     * @return translated text or 'TRANSLATOR ERROR' fallback message
     * @see Language
     */
    public String translate(String fromLangCode, String toLangCode, String text) {
        // Build the request target url with parameters
        final String url = String.format(BASE_URL + "/translation.js?query=translate&vqd=%s&to=%s%s", vqdToken, toLangCode, fromLangCode.equals("auto") ? "" : "&from=" + fromLangCode);
        // Default fallback result
        String result = "TRANSLATOR ERROR";
        var request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .setHeader("User-Agent", USER_AGENT)
                .POST(HttpRequest.BodyPublishers.ofString(text))
                .build();

        try {
            HttpResponse<String> res = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if(res.statusCode() == 403) {
                LOGGER.log(Level.INFO, "403 status code received; updating expired vqd token and retrying translation.");
                vqdToken = getVqdToken();
                return translate(fromLangCode, toLangCode, text);
            } else {
                // Response is simple and predictable enough not to warrant using a JSON parser
                result = Unescaper.unescapeJava(res.body().substring(40, res.body().length() - 2));
            }
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}

