package org.anibyl.slounik.dialogs;

import android.app.Dialog;
import android.content.ClipData;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import org.anibyl.slounik.Notifier;
import org.anibyl.slounik.R;
import org.anibyl.slounik.SlounikActivity;
import org.anibyl.slounik.network.Article;
import org.anibyl.slounik.network.ArticlesCallback;
import org.anibyl.slounik.network.ArticlesInfo;
import org.anibyl.slounik.ui.ProgressBar;

/**
 * Dialog for the text of an article.
 * <p/>
 * Created by Usievaład Čorny on 01.03.2015 10:54.
 */
public class ArticleDialog extends DialogFragment {
    private Context context;
    private Article article;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getActivity();
        article = ((SlounikActivity) getActivity()).getCurrentArticle();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.article, container, false);

        final boolean isLoadable = article.getLinkToFullDescription() != null;

        final TextView dictionary = (TextView) view.findViewById(R.id.dictionary);
        final TextView description = (TextView) view.findViewById(R.id.list_item_description);
        final Button closeButton = (Button) view.findViewById(R.id.article_button_close);
        final Button loadButton = (Button) view.findViewById(R.id.article_button_load);
        final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.article_progress);

        dictionary.setText(article.getDictionary());
        description.setText(article.getDescription());

        description.setMovementMethod(new ScrollingMovementMethod());

        description.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                CharSequence text = description.getText();
                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
                    android.text.ClipboardManager clipboard =
                            (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    clipboard.setText(text);
                } else {
                    android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context
                            .getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText(article.getDescription(), text);
                    clipboard.setPrimaryClip(clip);
                }
                Notifier.toast(context, R.string.toast_text_copied);
                return true;
            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        if (isLoadable) {
            loadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadButton.setEnabled(false);

                    if (article.getFullDescription() == null) {
                        progressBar.progressiveStart();
                        article.getCommunicator().loadArticleDescription(article, context, new ArticlesCallback() {
                            @Override
                            public void invoke(ArticlesInfo info) {
                                switch (info.getStatus()) {
                                    case SUCCESS:
                                        description.setText(article.getFullDescription());
                                        break;

                                    default:
                                        loadButton.setEnabled(true);
                                        break;
                                }
                                progressBar.progressiveStop();
                            }
                        });
                    } else {
                        description.setText(article.getFullDescription());
                    }
                }
            });
        } else {
            loadButton.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
        }

        setCancelable(true);

        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        return dialog;
    }
}