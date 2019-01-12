package net.devibot.provider.utils;

import net.devibot.provider.Provider;
import net.devibot.provider.entities.Language;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;

public class Translator {

    private static Logger logger = LoggerFactory.getLogger(Translator.class);

    private static HashMap<Language, HashMap<Integer, String>> translations = new HashMap<>();

    private static JSONArray usedTranslationIds;

    public static void initialize() {
        usedTranslationIds = loadUsedTranslationIds();
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
        //todo remove this after development
        if (!hasArrayValue(usedTranslationIds, id)) {
            usedTranslationIds.put(id);
            saveUsedTranslationIds(usedTranslationIds);
        }

        String translation = translations.get(language).get(id);
        if (translation == null) {
            if (language == Language.ENGLISH) {
                //todo report
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

    private static JSONArray loadUsedTranslationIds() {
        File file = new File("translations.json");
        JSONArray jsonArray = new JSONArray();

        if (!file.exists()) {
            try {
                FileWriter writer = new FileWriter(file);

                writer.write(jsonArray.toString());
                writer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            FileReader fileReader = new FileReader(file);
            StringBuilder stringBuffer = new StringBuilder();
            int numCharsRead;
            char[] charArray = new char[1024];
            while ((numCharsRead = fileReader.read(charArray)) > 0) {
                stringBuffer.append(charArray, 0, numCharsRead);
            }
            fileReader.close();
            jsonArray = new JSONArray(stringBuffer.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonArray;
    }

    private static void saveUsedTranslationIds(JSONArray jsonArray) {
        try {

            FileWriter writer = new FileWriter(new File("translations.json"));

            writer.write(jsonArray.toString());
            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static boolean hasArrayValue(JSONArray array, int value) {
        for (int i = 0; i < array.length(); i++) {
            if (array.getInt(i) == value)
                return true;
        }
        return false;
    }
}
