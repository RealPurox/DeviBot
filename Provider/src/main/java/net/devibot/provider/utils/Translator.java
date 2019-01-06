package net.devibot.provider.utils;

import net.devibot.provider.Provider;
import net.devibot.provider.entities.Language;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public class Translator {

    private static Logger logger = LoggerFactory.getLogger(Translator.class);

    private static HashMap<Language, HashMap<Integer, String>> translations = new HashMap<>();

    public static void initialize() {
        for (Language language : Language.values()) {
            Provider.getInstance().getMainframeManager().getTranslationsForLanguage(language.getRegistry(), retrieved -> {
                translations.put(language, retrieved);
            });
        }
    }

    public static String getTranslation(Language language, int id, Object ... args) {
        String translation = getTranslation(language, id);
        for (int i = 0; i < args.length; i++) {
            translation = translation.replace("{" + i  + "}", String.valueOf(args[i]));
        }
        return translation;
    }

    public static String getTranslation(Language language, int id) {
        String translation = translations.get(language).get(id);
        if (translation == null) {
            if (language == Language.ENGLISH) {
                logger.error("Translation for id=" + id + " and language=" + language.getName() + " not found.");
                return "Failed to lookup the the translation for id `" + id + "`. This issue has been reported to our developers and will be fixed as soon as they see it.";
            }
            return translations.get(Language.ENGLISH).get(id);
        }
        return translation;
    }

    public static HashMap<Language, HashMap<Integer, String>> getTranslations() {
        return translations;
    }
}
