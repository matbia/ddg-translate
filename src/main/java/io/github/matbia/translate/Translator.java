package io.github.matbia.translate;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Immutable translator class provides the means of translating text accordingly to the construction parameters.
 * It ensures the validity of received arguments before they're used for calling the translation service.
 */
public final class Translator {
    private final String langCodeFrom, langCodeTo;

    /**
     * Constructor using the helper enum class as parameters for specifying the input and output languages of the translator.
     * @param from input language
     * @param to output language
     */
    public Translator(Language from, Language to) {
        this(from.toString(), to.toString());
    }

    /**
     * Constructor using string language codes as parameters for specifying the input and output languages of the translator.
     * @param langCodeFrom input language code
     * @param langCodeTo output language code
     */
    public Translator(String langCodeFrom, String langCodeTo) {
        this.langCodeFrom = langCodeFrom;
        this.langCodeTo = langCodeTo;
        validate();
    }

    /**
     * Automatic input language detection constructor that takes only one enum parameter defining the output language of the translator.
     * @param to language specifying the desired translator output
     */
    public Translator(Language to) {
        this(to.toString());
    }

    /**
     * Automatic input language detection constructor that takes only one string parameter defining the output language of the translator.
     * @param langCodeTo language code specifying the desired translator output
     */
    public Translator(String langCodeTo) {
        langCodeFrom = Language.AUTO.toString();
        this.langCodeTo = langCodeTo;
        validate();
    }

    /**
     * Calls the translation service using the current state as parameters and the given string as input.
     * @param text input string to be translated
     * @return translated text
     */
    public String translate(String text) {
         return TranslatorService.getInstance().translate(langCodeFrom, langCodeTo, text);
    }

    /**
     * Ensures that the current state of the object is valid by throwing IllegalArgumentException if it's not.
     * Validity is determined by both language codes correlating to one of the enum values and the output language code not being set to auto.
     * It's meant to ensure that the class has been constructed with proper arguments that can be used as translation service parameters.
     * @throws IllegalArgumentException if langCodeFrom or langCodeTo is invalid or langCodeTo is set to auto
     * @see Language
     */
    private void validate() {
        if (langCodeTo.equals(Language.AUTO.toString())) throw new IllegalArgumentException("Output language cannot be set to auto");
        Set<String> validLangCodes = Arrays.stream(Language.values()).map(Objects::toString).collect(Collectors.toUnmodifiableSet());
        if (!validLangCodes.contains(langCodeFrom)) throw new IllegalArgumentException("Not a valid language code: " + langCodeFrom);
        if (!validLangCodes.contains(langCodeTo)) throw new IllegalArgumentException("Not a valid language code: " + langCodeTo);
    }
}
