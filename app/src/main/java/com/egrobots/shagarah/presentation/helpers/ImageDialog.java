package com.egrobots.shagarah.presentation.helpers;

import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.egrobots.shagarah.R;
import com.egrobots.shagarah.data.models.Image;
import com.jsibbold.zoomage.ZoomageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ImageDialog extends AppCompatDialogFragment {

    private ImageDialogCallback imageDialogCallback;
    private String imageUri;
    @BindView(R.id.img_view)
    ZoomageView imgView;

    public ImageDialog(String imageUri, ImageDialogCallback imageDialogCallback) {
        this.imageUri = imageUri;
        this.imageDialogCallback = imageDialogCallback;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.image_item_layout);
        dialog.setCanceledOnTouchOutside(true);
        ButterKnife.bind(this, dialog);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;

        dialog.getWindow().setAttributes(lp);
        Glide.with(this)
                .load(imageUri)
                .into(imgView);
        imgView.setScaleType(ImageView.ScaleType.FIT_XY);
        return dialog;
    }

    public interface ImageDialogCallback {
        void setImage();
    }
}
