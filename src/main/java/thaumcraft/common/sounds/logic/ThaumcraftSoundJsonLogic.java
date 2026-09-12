package thaumcraft.common.sounds.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ThaumcraftSoundJsonLogic {

    private static final Pattern VALID_SOUND_EVENT_NAME = Pattern.compile("^[a-z0-9_\\-\\./]+$");
    private static final Pattern VALID_SUBTITLE_KEY = Pattern.compile("^[a-z0-9_\\-\\.]+$");
    private static final Pattern VALID_FILE_PATH = Pattern.compile("^[a-z0-9_\\-\\./:]+$");

    public static boolean isValidSoundEventName(String eventName) {
        if (eventName == null || eventName.isEmpty()) return false;
        return VALID_SOUND_EVENT_NAME.matcher(eventName).matches();
    }

    public static boolean isValidSubtitleKey(String subtitle) {
        if (subtitle == null || subtitle.isEmpty()) return false;
        return VALID_SUBTITLE_KEY.matcher(subtitle).matches();
    }

    public static boolean isValidFilePath(String path) {
        if (path == null || path.isEmpty()) return false;
        return VALID_FILE_PATH.matcher(path).matches();
    }

    public static List<String> validateSoundDefinition(String eventName, String subtitle, List<String> paths) {
        List<String> errors = new ArrayList<>();

        if (!isValidSoundEventName(eventName)) {
            errors.add("Invalid sound event name: " + eventName);
        }

        if (subtitle != null && !isValidSubtitleKey(subtitle)) {
            errors.add("Invalid subtitle key: " + subtitle);
        }

        if (paths == null || paths.isEmpty()) {
            errors.add("Sound definition must contain at least one file path.");
        } else {
            for (String path : paths) {
                if (!isValidFilePath(path)) {
                    errors.add("Invalid file path: " + path);
                }
            }
        }

        return errors;
    }
}