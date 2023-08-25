package com.blueshift.reads;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class LiveContentDialog extends DialogFragment {

    private static final String CONTENT_HTML = "content_html";
    private static final String CONTENT_POSITION = "content_position";

    public LiveContentDialog() {
    }

    public static LiveContentDialog getInstance(
            String htmlContent, Position position) {
        Bundle bundle = new Bundle();
        bundle.putString(CONTENT_HTML, htmlContent);
        bundle.putSerializable(CONTENT_POSITION, position);

        LiveContentDialog dialog = new LiveContentDialog();
        dialog.setArguments(bundle);

        return dialog;
    }

    private int getDisplayLayoutId(Position position) {
        if (position != null && position == Position.Full) {
            return R.layout.activity_web_view_full_screen;
        }

        return R.layout.activity_web_view;
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());

        View root;
        if (getArguments() != null) {
            Position position = (Position) getArguments().getSerializable(CONTENT_POSITION);
            if (position == Position.TopAnimated) {
                builder = new AlertDialog.Builder(getActivity(), R.style.CustomDialog);
            } else if (position == Position.BottomAnimated) {
                builder = new AlertDialog.Builder(getActivity(), R.style.CustomDialog);
            }

            root = inflater.inflate(getDisplayLayoutId(position), null, false);
        } else {
            root = inflater.inflate(R.layout.activity_web_view, null, false);
        }

        View closeBtn = root.findViewById(R.id.dlg_close_btn);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        WebView webView = root.findViewById(R.id.live_content_html);
        if (webView != null) {
            String html = getArguments() != null ? getArguments().getString(CONTENT_HTML) : null;
            if (!TextUtils.isEmpty(html)) {
                webView.setWebViewClient(new WebViewClient());
                webView.loadData(html, "text/html; charset=UTF-8", null);
            }
        }

        builder.setView(root);

        return builder.create();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null && getArguments() != null) {
                Position position =
                        (Position) getArguments().getSerializable(CONTENT_POSITION);

                if (position != null && position != Position.Full) {
                    WindowManager.LayoutParams lp = window.getAttributes();
                    lp.gravity = position.getGravity();
                    lp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                    window.setAttributes(lp);
                }
            }
        }

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public enum Position {
        Top,
        Center,
        Bottom,
        Full,
        TopAnimated,
        BottomAnimated;

        public static Position fromString(String position) {
            if (position != null) {
                switch (position) {
                    case "top":
                        return Top;
                    case "center":
                        return Center;
                    case "bottom":
                        return Bottom;
                    case "full":
                        return Full;
                    case "top_animated":
                        return TopAnimated;
                    case "bottom_animated":
                        return BottomAnimated;
                    default:
                        return Center;
                }
            } else return Center;
        }

        int getGravity() {
            switch (this) {
                case Top:
                    return Gravity.TOP;
                case TopAnimated:
                    return Gravity.TOP;
                case Center:
                    return Gravity.CENTER;
                case Bottom:
                    return Gravity.BOTTOM;
                case BottomAnimated:
                    return Gravity.BOTTOM;
                default:
                    return Gravity.CENTER;
            }
        }
    }
}
