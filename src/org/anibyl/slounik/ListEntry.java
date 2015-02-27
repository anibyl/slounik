package org.anibyl.slounik;

import android.text.Spanned;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Entry of the article list.
 *
 * Created by Usievaład Čorny on 26.2.15 14.06.
 */
public class ListEntry {
    private String title;
    private Spanned description;
    private String dictionary;

    public ListEntry() {
    }

    public ListEntry(Element element) {
        if (element != null) {
            Elements elements = element.select("a.tsb");
            if (elements != null && elements.size() != 0) {
                title = elements.first().html();
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
                }
            }

            if (title != null) {
                title = title.replaceAll("<u>", "");
                title = title.replaceAll("</u>", "́");
            }
        }
        // TODO
    }

    public ListEntry setTitle(String title) {
        this.title = title;
        return this;
    }

    public ListEntry setDescription(Spanned description) {
        this.description = description;
        return this;
    }

    public ListEntry setDictionary(String dictionary) {
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
