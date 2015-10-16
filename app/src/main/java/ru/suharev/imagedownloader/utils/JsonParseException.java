package ru.suharev.imagedownloader.utils;

import org.json.JSONException;

/**
 * Вызывается, чтобы обозначить появление проблемы во время парсинга JSON-объекта
 * Created by pasha on 02.10.2015.
 */
public class JsonParseException extends JSONException {

    public JsonParseException(String s) {
        super(s);
    }
}
