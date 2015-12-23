package org.anibyl.slounik.network;

import android.content.Context;

import java.util.ArrayList;

/**
 * Batch loader of the dictionary site communicators.
 * <p/>
 * Created by Usievaład Kimajeŭ on 23.12.15.
 */
public class BatchArticlesLoader implements ArticlesLoader {
    private final DictionarySiteCommunicator[] communicators;

    public BatchArticlesLoader(DictionarySiteCommunicator... communicators) {
        this.communicators = communicators;
    }

    @Override
    public void loadArticles(String wordToSearch, Context context, final ArticlesCallback communicatorCallBack) {
        int activeCommunicators = 0;

        for (DictionarySiteCommunicator communicator : communicators) {
            if (communicator.enabled()) {
                activeCommunicators++;
                ArticlesCallback callback = new ArticlesCallback() {
                    @Override
                    public void invoke(ArticlesInfo info) {
                        ((BatchArticlesCallback) communicatorCallBack).invoke(this, info);
                    }
                };
                ((BatchArticlesCallback) communicatorCallBack).addCallback(callback);
                communicator.loadArticles(wordToSearch, context, callback);
            }
        }

        if (activeCommunicators == 0) {
            communicatorCallBack.invoke(new ArticlesInfo(ArticlesInfo.Status.FAILURE));
        }
    }

    public static abstract class BatchArticlesCallback extends ArticlesCallback {
        ArrayList<ArticlesCallback> callbacks = new ArrayList<>();

        void invoke(ArticlesCallback callback, ArticlesInfo info) {
            if (!callbacks.contains(callback)) {
                throw new RuntimeException("No such callback in batch callback.");
            }

            switch (info.getStatus()) {
                case SUCCESS:
                case FAILURE:
                    callbacks.remove(callback);

                    if (callbacks.size() != 0) {
                        info.setStatus(ArticlesInfo.Status.IN_PROCESS);
                    }

                case IN_PROCESS:
                    invoke(info);
                    break;
            }
        }

        public void addCallback(ArticlesCallback callback) {
            callbacks.add(callback);
        }
    }
}