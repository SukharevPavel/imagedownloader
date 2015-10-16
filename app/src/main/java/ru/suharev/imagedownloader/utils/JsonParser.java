package ru.suharev.imagedownloader.utils;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ru.suharev.imagedownloader.provider.ImageProvider;

/**
 * Парсит информацию в виде JSON-объекта, которую мы получаем в
 * AsyncGetListTask, после чего записывает её в базу
 * данных
 * Created by pasha on 02.10.2015.
 */
public class JsonParser {

    private static final String PARSE_ERROR= "JSON parsing raises exception: ";

    public void parseJson(Context ctx, String source) throws JsonParseException {
        JSONArray jsonArray;
        try {
            jsonArray = new JSONArray(source);
        } catch (JSONException e) {
            throw new JsonParseException(PARSE_ERROR + e.getMessage());

        }
        ctx.getContentResolver().delete(ImageProvider.Uris.URI_IMAGE,null,null);
        for (int i = 0; i < jsonArray.length() ; i ++ ) {
            try {
                parseJsonObjectForEntry(ctx, jsonArray.getJSONObject(i));
            } catch (JSONException e) {
                throw new JsonParseException(PARSE_ERROR + e.getMessage());
            }
        }
    }

    private void parseJsonObjectForEntry(Context ctx, JSONObject json) {
        String id, image, title;
        try{
            id = json.getString(Fields.ID);
            image = json.getString(Fields.IMAGE);
            title = json.getString(Fields.TITLE);
        }catch (JSONException e) {
            return;
        }
        addEntry(ctx,id,image,title);
    }

    private void addEntry(Context ctx, String id, String image, String title) {
        ContentValues cv = new ContentValues();
        cv.put(ImageProvider.Columns.ID, id);
        cv.put(ImageProvider.Columns.IMAGE_URI, image);
        cv.put(ImageProvider.Columns.TITLE, title);
        ctx.getContentResolver().insert(ImageProvider.Uris.URI_IMAGE, cv);
    }


    public static class Fields{

        public static final String ID = "id";
        public static final String IMAGE = "img";
        public static final String TITLE = "title";

    }

}
