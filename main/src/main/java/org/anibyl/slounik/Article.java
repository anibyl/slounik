package org.anibyl.slounik;

import android.text.Html;
import android.text.Spanned;
import org.anibyl.slounik.network.DictionarySiteCommunicator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Article.
 *
 * Created by Usievaład Čorny on 26.02.2015 14:06.
 */
public class Article {
    private final DictionarySiteCommunicator communicator;
    private String title;
    private Spanned description;
    private String dictionary;
    private String linkToFullDescription;
    private Spanned fullDescription;

    public Article(DictionarySiteCommunicator communicator, Element element) {
        this.communicator = communicator;

        if (element != null) {
            Elements elements = element.select("a.tsb");
            if (elements != null && elements.size() != 0) {
                Element link = elements.first();
                title = link.html();
                linkToFullDescription = link.attr("href");

                if (title != null) {
                    elements = element.select("a.ts");
                    if (elements != null && elements.size() != 0) {
                        description = Html.fromHtml(elements.first().html());
                    }
                }
            }

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

    public String getLinkToFullDescription() {
        return linkToFullDescription;
    }

    public void setLinkToFullDescription(String linkToFullDescription) {
        this.linkToFullDescription = linkToFullDescription;
    }

    public Spanned getFullDescription() {
        return fullDescription;
    }

    public void setFullDescription(Spanned fullDescription) {
        this.fullDescription = fullDescription;
    }

    public DictionarySiteCommunicator getCommunicator() {
        return communicator;
    }
}
