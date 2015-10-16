package ru.suharev.imagedownloader.ui;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import ru.suharev.imagedownloader.R;

/**
 * Fragment, который содержит изображение, загружаемое из сети Интернет
 */
public class ImageFragment extends Fragment {

    public final static String IMAGE_URI = "image_uri";


    private ImageLoader mImageLoader;

    private String mImagePath;
    private ImageView mImageView;
    private ProgressBar mProgressBar;

    public ImageFragment() {
    }

    public static ImageFragment getFragment(String img) {
        ImageFragment fragment = new ImageFragment();
        Bundle args = new Bundle();
        args.putString(IMAGE_URI, img);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        setRetainInstance(true);
        mImagePath = args.getString(IMAGE_URI);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_image, container, false);
        mImageView = (ImageView) v.findViewById(R.id.image_view);
        mProgressBar = (ProgressBar) v.findViewById(R.id.image_progress_bar);
        imageLoad();
        return v;
    }

    public void imageLoad() {
        if (mImageLoader == null)
            mImageLoader = ImageLoader.getInstance();
        if (!mImageLoader.isInited()) {
            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getContext())
                    .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                    .build();
            ImageLoader.getInstance().init(config);
        }
        mImageLoader.displayImage(mImagePath, mImageView, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                mProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                if (isAdded()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        mImageView.setImageDrawable(getResources().
                                getDrawable(R.drawable.dl_error, null));
                    } else
                        mImageView.setImageDrawable(getResources().
                                getDrawable(R.drawable.dl_error));
                    mProgressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                mProgressBar.setVisibility(View.GONE);

            }
        });
    }


}
