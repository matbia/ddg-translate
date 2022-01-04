package io.github.matbia.translate;

import org.junit.Assert;
import org.junit.Test;

public class TranslatorTest {
    /**
     * Test the usual translation attempts right after the creation of TranslationService.
     */
    @Test
    public void testTranslate() {
        var t = new Translator(Language.ENGLISH);
        Assert.assertEquals(t.translate("Jan ma kota."), "John has a cat.");
        t = new Translator(Language.ENGLISH, Language.CHINESE_SIMPLIFIED);
        Assert.assertEquals(t.translate("I am happy"), "我很高兴");
        t = new Translator("auto", "en");
        Assert.assertEquals(t.translate("Jan ma kota."), "John has a cat.");
    }

    /**
     * Test passing invalid language codes to the constructor.
     */
    @Test
    public void testConstructor() {
        Assert.assertThrows(IllegalArgumentException.class, () -> {
            var t = new Translator("gaa", "de"); // First argument is invalid
        });
        Assert.assertThrows(IllegalArgumentException.class, () -> {
            var t = new Translator("de", "pll"); // Second argument is invalid
        });
        Assert.assertThrows(IllegalArgumentException.class, () -> {
            var t = new Translator("auto"); // Second argument is invalid
        });
        var t = new Translator("auto", "ga"); // Both arguments are correct
    }

    /**
     * Uses reflection API to simulate an expired or incorrect vqd token.
     * @throws NoSuchFieldException if there's no field named vqdToken inside TranslationService
     * @throws IllegalAccessException should really never happen
     */
    @Test
    public void testTranslateInvalidToken() throws NoSuchFieldException, IllegalAccessException {
        final var t = new Translator(Language.POLISH, Language.ENGLISH);
        final var vqdField = TranslatorService.getInstance().getClass().getDeclaredField("vqdToken");
        vqdField.setAccessible(true);
        vqdField.set(TranslatorService.getInstance(), "AnExpiredOrSomehowIncorrectToken");
        Assert.assertEquals(t.translate("Jan ma kota."), "John has a cat.");
    }
}