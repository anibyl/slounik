package org.anibyl.slounik.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;
import org.anibyl.slounik.Article;
import org.anibyl.slounik.R;

/**
 * Dialog for the text of an article.
 *
 * Created by Usievaład Čorny on 01.03.2015 10:54.
 */
public class ArticleDialog extends Dialog {
    private final Article article;

    public ArticleDialog(Context context, Article article) {
        super(context);

        this.article = article;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.article);

        setTitle(article.getTitle());

        TextView dictionary = (TextView) findViewById(R.id.dictionary);
        TextView description = (TextView) findViewById(R.id.decription);

        dictionary.setText(article.getDictionary());
        description.setText(article.getDescription());

        description.setMovementMethod(new ScrollingMovementMethod());

        setCancelable(true);
    }
}
