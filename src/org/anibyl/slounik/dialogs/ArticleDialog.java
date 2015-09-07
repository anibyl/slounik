package org.anibyl.slounik.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import org.anibyl.slounik.Article;
import org.anibyl.slounik.R;
import org.anibyl.slounik.network.ArticlesCallback;
import org.anibyl.slounik.network.ArticlesInfo;
import org.anibyl.slounik.network.SlounikOrg;

/**
 * Dialog for the text of an article.
 *
 * Created by Usievaład Čorny on 01.03.2015 10:54.
 */
public class ArticleDialog extends Dialog {
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
        final TextView description = (TextView) findViewById(R.id.decription);

        dictionary.setText(article.getDictionary());
        description.setText(article.getDescription());

        description.setMovementMethod(new ScrollingMovementMethod());

        if (isLoadable) {
            final Button loadButton = (Button) findViewById(R.id.load);
            final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
            loadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadButton.setEnabled(false);

                    if (article.getFullDescription() == null) {
                        progressBar.setVisibility(View.VISIBLE);
                        SlounikOrg.loadArticleDescription(article, context, new ArticlesCallback() {
                            @Override
                            public void invoke(ArticlesInfo info) {
                                switch (info.getStatus()) {
                                    case SUCCESS:
                                        description.setText(article.getFullDescription());
                                        progressBar.setVisibility(View.INVISIBLE);
                                        break;

                                    default:
                                        loadButton.setEnabled(true);
                                        progressBar.setVisibility(View.INVISIBLE);
                                        break;
                                }
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
