package io.github.skulltah.gifpaper.gifbrowser;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.greysonparrelli.permiso.Permiso;

import io.github.skulltah.gifpaper.R;
import tr.xip.errorview.ErrorView;

/**
 * A simple {@link Fragment} subclass.
 */
public class GifBrowserFragment extends Fragment {
    View view;
    RecyclerView recyclerView;
    ImageAdapter adapter;
    ErrorView errorView;

    public GifBrowserFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_gif_browser, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        errorView = (ErrorView) view.findViewById(R.id.error_view);

        requestPermissions();

        return view;
    }

    private void requestPermissions() {
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            permissionsGranted();
            return;
        }

        Permiso.getInstance().requestPermissions(new Permiso.IOnPermissionResult() {
            @Override
            public void onPermissionResult(Permiso.ResultSet resultSet) {
                if (resultSet.areAllPermissionsGranted()) {
                    // Permission granted!
                    permissionsGranted();
                } else {
                    // Permission denied.
                    errorView.setTitle("Permission denied");
                    errorView.setSubtitle("GifPaper needs access to your files in order for it to find your GIFs.");
                    errorView.setRetryButtonText("Allow access");
                    errorView.setOnRetryListener(new ErrorView.RetryListener() {
                        @Override
                        public void onRetry() {
                            requestPermissions();
                        }
                    });
                    errorView.showRetryButton(true);
                    errorView.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onRationaleRequested(Permiso.IOnRationaleProvided callback, String... permissions) {
                Permiso.getInstance().showRationaleInDialog(
                        "Access to your GIFs",
                        "GifPaper needs access to your files in order for it to find your GIFs.",
                        null, callback);
            }
        }, Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    private void permissionsGranted() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;

        adapter = new ImageAdapter(getActivity());

        errorView.setTitle("No GIFs found");
        errorView.setSubtitle("Come back once you've downloaded some GIFs.");
        errorView.setRetryButtonText("Rescan");
        errorView.setOnRetryListener(new ErrorView.RetryListener() {
            @Override
            public void onRetry() {
                permissionsGranted();
            }
        });
        errorView.showRetryButton(true);

        errorView.setVisibility(adapter.getItemCount() > 0 ? View.GONE : View.VISIBLE);
        recyclerView.setVisibility(adapter.getItemCount() > 0 ? View.VISIBLE : View.GONE);

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), (int) dpWidth / 130);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Permiso.getInstance().setActivity(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        Permiso.getInstance().setActivity(getActivity());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Permiso.getInstance().onRequestPermissionResult(requestCode, permissions, grantResults);
    }
}
