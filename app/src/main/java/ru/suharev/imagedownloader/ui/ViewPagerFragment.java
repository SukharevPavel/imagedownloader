package ru.suharev.imagedownloader.ui;


import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.suharev.imagedownloader.R;
import ru.suharev.imagedownloader.provider.ImageProvider;

/**
 * Fragment с ViewPager для перелистывания ImageFragment-ов
 **/
public class ViewPagerFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String EXTRA_IMAGE_URI = "extra_image";
    public static final String EXTRA_TITLE = "extra_title";
    public static final String EXTRA_POSITION = "extra_position";

    private static final int LOADER_ID = 2;

    private int INVALID_POSITION = -1;

    private ViewPager mViewPager;
    private ImageFragmentPagerAdapter mAdapter;
    private int mPosition = INVALID_POSITION;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_view_pager, container, false);
        if (savedInstanceState != null && savedInstanceState.containsKey(EXTRA_POSITION))
            mPosition = savedInstanceState.getInt(EXTRA_POSITION);
        mAdapter = new ImageFragmentPagerAdapter(getActivity().getSupportFragmentManager());
        if (getLoaderManager().getLoader(LOADER_ID) == null)
            getLoaderManager().initLoader(LOADER_ID, null, this);
        mViewPager = (ViewPager) v.findViewById(R.id.pager);
        return v;
    }

    public void setPosition(int position) {
        if (mPosition == INVALID_POSITION) mPosition = position;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(mPosition);
        if (mAdapter.isCursorValid() && !getLoaderManager().getLoader(LOADER_ID).isStarted())
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
        if (mAdapter.swapCursor(data) == null)
            mViewPager.setCurrentItem(mPosition);
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(EXTRA_POSITION, mViewPager.getCurrentItem());
    }

    public class ImageFragmentPagerAdapter extends FragmentStatePagerAdapter {

        private Cursor mCursor;

        public ImageFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (mCursor != null && mCursor.moveToPosition(position)) {
                String uri = mCursor.getString(mCursor.getColumnIndex(ImageProvider.Columns.IMAGE_URI));
                return ImageFragment.getFragment(uri);
            }
            return null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (mCursor != null && mCursor.moveToPosition(position))
                return mCursor.getString(mCursor.getColumnIndex(ImageProvider.Columns.TITLE));
            return null;
        }

        public Cursor swapCursor(Cursor cursor) {
            Cursor oldCursor = mCursor;
            mCursor = cursor;
            notifyDataSetChanged();
            return oldCursor;
        }


        public boolean isCursorValid() {
            return (mCursor != null);
        }

        @Override
        public int getCount() {
            if (mCursor != null) return mCursor.getCount();
            return 0;
        }
    }


}
