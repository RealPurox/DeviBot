package net.devibot.provider.utils;

import net.devibot.core.Core;
import net.devibot.core.entities.Language;
import net.devibot.core.entities.Translation;
import net.devibot.core.utils.DiscordWebhook;
import net.devibot.provider.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public class Translator {

    private static Logger logger = LoggerFactory.getLogger(Translator.class);

    private static HashMap<Language, HashMap<Integer, String>> translationsOLD = new HashMap<>();
    private static HashMap<Language, HashMap<String, String>> translations = new HashMap<>();

    public static void initialize() {
        // TODO: 05/02/2019 start: remove
        for (Language language : Language.values()) {
            Provider.getInstance().getMainframeManager().getTranslationsForLanguageOLD(language.getRegistry(), retrieved -> {
                translationsOLD.put(language, retrieved);
            });
        }
        // TODO: 05/02/2019 end

        Provider.getInstance().getMainframeManager().getAllTranslations(translationsList -> {
            if (translationsList.isEmpty())
                //throw new UnsupportedOperationException("translation list empty");
                return;
            //get language
            Language language = Language.getLanguage(translationsList.get(0).getLang());

            if (language == null)
                throw new UnsupportedOperationException("Invalid language received! " + translationsList.get(0).getLang());

            translations.put(language, new HashMap<>());

            for (Translation receivedTranslation : translationsList) {
                if (receivedTranslation.getText() == null || receivedTranslation.getText().equals("none")) continue;
                translations.get(language).put(receivedTranslation.getKey(), receivedTranslation.getText());
            }
        });
    }

    @Deprecated
    public static boolean hasTranslationOLD(Language language, int id) {
        return translationsOLD.get(language).containsKey(id);
    }

    public static String getTranslation(Language language, String key, Object ... args) {
        String translation = getTranslation(language, key);
        for (int i = 0; i < args.length; i++) {
            translation = translation.replace("{" + i + "}", String.valueOf(args[i]));
        }
        return translation;
    }

    public static String getTranslation(Language language, String key) {
        String translation = translations.get(language).get(key);
        if (translation == null) {
            if (language == Language.ENGLISH) {
                sendWebhook(language, key);
                logger.error("Translation for key=" + key + " and language=" + language.getName() + " not found.");
                return "Failed to lookup translation for key=`" + key + "`. This issue has been reported to our developers and will be fixed as soon as they see it.";
            }
            return getTranslation(Language.ENGLISH, key);
        }
        return translation;
    }

    @Deprecated
    public static HashMap<Language, HashMap<Integer, String>> getTranslationsOLD() {
        return translationsOLD;
    }

    public static HashMap<Language, HashMap<String, String>> getTranslations() {
        return translations;
    }

    private static void sendWebhook(Language language, String key) {
        StringBuilder fixedDescription = new StringBuilder();

        fixedDescription.append(!Core.CONFIG.isDevMode() ? "@everyone " : "").append("__**Translation not found!**__\n\n");

        fixedDescription.append("```");
        fixedDescription.append("Translation for key=\"").append(key).append("\" and language=\"").append(language.getName()).append("\" could not found.\n\nPlease register the translation ASAP.");
        fixedDescription.append("```");

        DiscordWebhook webhook = new DiscordWebhook(Core.CONFIG.getErrorWebhook());
        webhook.setContent(fixedDescription.toString());

        webhook.execute();
    }

}
