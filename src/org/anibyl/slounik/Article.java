package org.anibyl.slounik;

import android.text.Html;
import android.text.Spanned;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Article.
 *
 * Created by Usievaład Čorny on 26.02.2015 14:06.
 */
public class Article {
    private String title;
    private Spanned description;
    private String dictionary;

    public Article() {
    }

    public Article(Element element) {
        if (element != null) {
            Elements elements = element.select("a.tsb");
            if (elements != null && elements.size() != 0) {
                title = elements.first().html();

                if (title != null) {
                    elements = element.select("a.ts");
                    if (elements != null && elements.size() != 0) {
                        description = Html.fromHtml(elements.first().html());
                    }
                }
            }

//            if (title == null) {
//                final String html = element.outerHtml();
//                if (html != null) {
//                    title = html.substring(html.indexOf("<b>"), html.indexOf("<br>"));
//                }
//            }

            if (title == null) {
                elements = element.select("b");
                if (elements != null && elements.size() != 0) {
                    title = elements.first().html();

                    if (title != null) {
                        description = Html.fromHtml(element.html());
                    }
                }
            }

            if (title != null) {
                title = title.replaceAll("<u>", "");
                title = title.replaceAll("</u>", "́");

                // Escape all other HTML tags, e.g. second <b>.
                title = Jsoup.parse(title).text();
            }

            elements = element.select("a.la1");
            if (elements != null && elements.size() != 0) {
                dictionary = elements.first().html();
            }
        }
        // TODO
    }

    public Article setTitle(String title) {
        this.title = title;
        return this;
    }

    public Article setDescription(Spanned description) {
        this.description = description;
        return this;
    }

    public Article setDictionary(String dictionary) {
        this.dictionary = dictionary;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Spanned getDescription() {
        return description;
    }

    public String getDictionary() {
        return dictionary;
    }
}
