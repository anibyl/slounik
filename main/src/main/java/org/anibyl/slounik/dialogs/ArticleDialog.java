package org.anibyl.slounik.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import org.anibyl.slounik.Article;
import org.anibyl.slounik.R;
import org.anibyl.slounik.network.ArticlesCallback;
import org.anibyl.slounik.network.ArticlesInfo;
import org.anibyl.slounik.network.SlounikOrg;

/**
 * Dialog for the text of an article.
 * <p>
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

        if (isLoadable) {
            final Button loadButton = (Button) findViewById(R.id.load);
            final SmoothProgressBar progressBar = (SmoothProgressBar) findViewById(R.id.article_progress);
            progressBar.setVisibility(View.INVISIBLE);
            loadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadButton.setEnabled(false);

                    if (article.getFullDescription() == null) {
                        progressBar.setVisibility(View.VISIBLE);
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