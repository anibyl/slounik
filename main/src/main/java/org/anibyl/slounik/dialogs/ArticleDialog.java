package org.anibyl.slounik.dialogs;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import org.anibyl.slounik.Article;
import org.anibyl.slounik.Notifier;
import org.anibyl.slounik.R;
import org.anibyl.slounik.network.ArticlesCallback;
import org.anibyl.slounik.network.ArticlesInfo;
import org.anibyl.slounik.network.SlounikOrg;
import org.anibyl.slounik.ui.ProgressBar;

/**
 * Dialog for the text of an article.
 * <p/>
 * Created by Usievaład Čorny on 01.03.2015 10:54.
 */
public class ArticleDialog extends AlertDialog {
    private final Context context;
    private final Article article;

    public ArticleDialog(Context context, Article article) {
        super(context);

        this.context = context;
        this.article = article;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final boolean isLoadable = article.getLinkToFullDescription() != null;

        setContentView(isLoadable ? R.layout.article_with_loading : R.layout.article);

        setTitle(article.getTitle());

        TextView dictionary = (TextView) findViewById(R.id.dictionary);
        final TextView description = (TextView) findViewById(R.id.description);

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

        if (isLoadable) {
            final Button loadButton = (Button) findViewById(R.id.load);
            final ProgressBar progressBar = (ProgressBar) findViewById(R.id.article_progress);
            loadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadButton.setEnabled(false);

                    if (article.getFullDescription() == null) {
                        progressBar.progressiveStart();
                        SlounikOrg.loadArticleDescription(article, context, new ArticlesCallback() {
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
        }

        setCancelable(true);
    }
}