package gaia.cu9.ari.gaiaorbit.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.I18NBundle;
import gaia.cu9.ari.gaiaorbit.util.Logger.Log;

import java.util.Locale;
import java.util.MissingResourceException;

/**
 * Manages the i18n system.
 * @author Toni Sagrista
 *
 */
public class I18n {
    private static final Log logger = Logger.getLogger(I18n.class);

    public static I18NBundle bundle;
    public static Locale locale;

    /**
     * Initialises the i18n system.
     */
    public static void initialize() {
        if (bundle == null) {
            forceInit(Gdx.files.internal("i18n/gsbundle"));
        }
    }

    public static void initialize(FileHandle fh) {
        forceInit(fh);
    }

    public static boolean forceInit(FileHandle baseFileHandle) {
        if (GlobalConf.program == null || GlobalConf.program.LOCALE.isEmpty()) {
            // Use system default
            locale = Locale.getDefault();
        } else {
            locale = forLanguageTag(GlobalConf.program.LOCALE);
            // Set as default locale
            Locale.setDefault(locale);
        }
        try {
            bundle = I18NBundle.createBundle(baseFileHandle, locale);
            return true;
        } catch (MissingResourceException e) {
            logger.info(e.getLocalizedMessage());
            // Use default locale - en_GB
            locale = new Locale("en", "GB");
            try {
                bundle = I18NBundle.createBundle(baseFileHandle, locale);
            } catch (Exception e2) {
                logger.error(e);
            }
            return false;
        }

    }

    private static Locale forLanguageTag(String languageTag) {
        String[] tags = languageTag.split("-");
        if (tags.length > 1) {
            return new Locale(tags[0], tags[1]);
        } else {
            return new Locale(languageTag);
        }
    }

}
