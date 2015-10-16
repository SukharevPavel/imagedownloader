package ru.suharev.imagedownloader.ui;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import ru.suharev.imagedownloader.R;
import ru.suharev.imagedownloader.provider.ImageProvider;
import ru.suharev.imagedownloader.utils.JsonParseException;
import ru.suharev.imagedownloader.utils.JsonParser;

/**
 * Фрагмент, отображающий лист с заголовками, полученными из JSON-файла
 */
public class ListActivityFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_ID = 1;
    private final static String DOWNLOAD_URI = "http://private-db05-jsontest111.apiary-mock.com/androids";
    private final static String DIALOG_TAG = "database dialog";
    private final static String ASYNC_DIALOG_TAG = "async_dialog";
    private SimpleCursorAdapter mAdapter;
    private LoadDataDialogFragment mLoadDataDialogFragment;
    private LoadDataDialogFragment mAsyncTaskDialogFragment;
    private AdapterView.OnItemClickListener mListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Cursor cursor = ((SimpleCursorAdapter) getListAdapter()).getCursor();
            cursor.moveToPosition(position);
            Intent intent = new Intent(getContext(), ImageActivity.class);
            intent.putExtra(ViewPagerFragment.EXTRA_IMAGE_URI,
                    cursor.getString(cursor.getColumnIndex(ImageProvider.Columns.IMAGE_URI)));
            intent.putExtra(ViewPagerFragment.EXTRA_TITLE,
                    cursor.getString(cursor.getColumnIndex(ImageProvider.Columns.TITLE)));
            intent.putExtra(ViewPagerFragment.EXTRA_POSITION,
                    position);
            startActivity(intent);
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLoadDataDialogFragment = new LoadDataDialogFragment();
        mAsyncTaskDialogFragment = new LoadDataDialogFragment();
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getLoaderManager().initLoader(LOADER_ID, null, this);
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (mAdapter == null) {
            mAdapter = new SimpleCursorAdapter(getContext(),
                    android.R.layout.simple_list_item_1,
                    null,
                    new String[]{ImageProvider.Columns.TITLE},
                    new int[]{android.R.id.text1},
                    0);
        }
        setListAdapter(mAdapter);

        getListView().setOnItemClickListener(mListener);

        if (mAdapter.getCursor() == null
                && ! getLoaderManager().getLoader(LOADER_ID).isStarted()) {
            forceLoad();
        }
    }


    public void forceLoad() {
        Bundle bundle = new Bundle();
        bundle.putString(LoadDataDialogFragment.EXTRA_MESSAGE,
                getString(R.string.fragment_dialog_load_db));
        if (mLoadDataDialogFragment != null && !mLoadDataDialogFragment.isAdded()) {
            mLoadDataDialogFragment.setArguments(bundle);
            mLoadDataDialogFragment.show(getFragmentManager(), DIALOG_TAG);
        }
        getLoaderManager().getLoader(LOADER_ID).forceLoad();
    }


    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getContext(),
                ImageProvider.Uris.URI_IMAGE,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        mAdapter.changeCursor(data);
        mLoadDataDialogFragment = (LoadDataDialogFragment) getFragmentManager().
                findFragmentByTag(DIALOG_TAG);
        if (mLoadDataDialogFragment != null && mLoadDataDialogFragment.isAdded())
            mLoadDataDialogFragment.hideDialog();
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
        mAdapter.changeCursor(null);
    }


    public void loadAndParseData() {
        new AsyncGetListTask().execute(DOWNLOAD_URI);
    }

    public static class LoadDataDialogFragment extends DialogFragment {

        public final static String EXTRA_MESSAGE = "message";

        private String mMessage;
        private ProgressDialog mDialog;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setCancelable(false);
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Bundle bundle = getArguments();
            mMessage = bundle.getString(EXTRA_MESSAGE);
            mDialog = new ProgressDialog(getActivity(), getTheme());
            mDialog.setMessage(mMessage);
            mDialog.setIndeterminate(true);
            mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            return mDialog;
        }

        public void hideDialog() {
            mDialog.hide();
            mDialog.dismiss();
        }

    }


    private class AsyncGetListTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Bundle bundle = new Bundle();
            bundle.putString(LoadDataDialogFragment.EXTRA_MESSAGE,
                    getString(R.string.dialog_load_data));
            if (mAsyncTaskDialogFragment != null && ! mAsyncTaskDialogFragment.isAdded()) {
                mAsyncTaskDialogFragment.setArguments(bundle);
                mAsyncTaskDialogFragment.show(getFragmentManager(), ASYNC_DIALOG_TAG);
            }

        }

        @Override
        protected String doInBackground(String... params) {
            StringBuilder builder = new StringBuilder();
            URL url = null;
            HttpURLConnection urlConnection = null;
            BufferedReader in = null;
            try {
                url = new URL(params[0]);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            if (url != null) {
                try {
                    urlConnection = (HttpURLConnection) url.openConnection();
                    in = new BufferedReader(
                            new InputStreamReader(urlConnection.getInputStream()));
                    String buffer;
                    while ((buffer = in.readLine()) != null) {
                        builder.append(buffer);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                    if (in != null) try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return builder.toString();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            mAsyncTaskDialogFragment = (LoadDataDialogFragment) getFragmentManager().
                    findFragmentByTag(ASYNC_DIALOG_TAG);
            if (mAsyncTaskDialogFragment != null && mAsyncTaskDialogFragment.isAdded())
                mAsyncTaskDialogFragment.hideDialog();
            try {
                if (result == null) {
                    Toast.makeText(getContext(), getString(R.string.toast_download_error), Toast.LENGTH_LONG).show();
                    return;
                }
                JsonParser parser = new JsonParser();
                parser.parseJson(getContext(), result);
            } catch (JsonParseException e) {
                Toast.makeText(getContext(),
                        getString(R.string.toast_parse_error),
                        Toast.LENGTH_LONG)
                        .show();
                return;
            }
            forceLoad();
        }

    }

}
