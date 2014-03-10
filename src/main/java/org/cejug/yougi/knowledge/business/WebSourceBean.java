/* Yougi is a web application conceived to manage user groups or
 * communities focused on a certain domain of knowledge, whose members are
 * constantly sharing information and participating in social and educational
 * events. Copyright (C) 2011 Hildeberto Mendonça.
 *
 * This application is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation; either version 2.1 of the License, or (at your
 * option) any later version.
 *
 * This application is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * There is a full copy of the GNU Lesser General Public License along with
 * this library. Look for the file license.txt at the root level. If you do not
 * find it, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA.
 * */
package org.cejug.yougi.knowledge.business;

import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import org.cejug.yougi.business.AbstractBean;
import org.cejug.yougi.entity.UserAccount;
import org.cejug.yougi.knowledge.entity.WebSource;
import org.cejug.yougi.knowledge.entity.Article;
import org.cejug.yougi.util.UrlUtils;

/**
 * Business logic dealing with web sources entities.
 *
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Stateless
public class WebSourceBean extends AbstractBean<WebSource> {

    private static final Logger LOGGER = Logger.getLogger(WebSourceBean.class.getName());

    @PersistenceContext
    private EntityManager em;

    @EJB
    private ArticleBean articleBean;

    public WebSourceBean() {
        super(WebSource.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public WebSource findWebSourceByProvider(UserAccount provider) {
        try {
            return (WebSource) em.createQuery("select ws from WebSource ws where ws.provider = :provider order by ws.title asc")
                                 .setParameter("provider", provider)
                                 .getSingleResult();
        }
        catch(NoResultException nre) {
            return null;
        }
    }

    public List<WebSource> findWebResources() {
    	return em.createQuery("select ws from WebSource ws order by ws.title asc", WebSource.class).getResultList();
    }

    public List<UserAccount> findNonReferencedProviders() {
        return em.createQuery("select ua from UserAccount ua " +
                "where ua.deactivated = false and " +
                    "ua.confirmationCode is null and " +
                    "(ua.website is not null and ua.website <> '') and " +
                    "ua not in (select distinct ws.provider from WebSource ws) " +
                "order by ua.firstName", UserAccount.class)
                .getResultList();
    }

    public List<Article> loadUnpublishedArticles(WebSource webSource) {
        List<Article> unpublishedArticles = loadArticles(webSource);

        // Remove from the list of unpublished articles the ones that are already published.
        List<Article> publishedArticles = articleBean.findPublishedArticles(webSource);
        for(Article publishedArticle: publishedArticles) {
            unpublishedArticles.remove(publishedArticle);
        }

        return unpublishedArticles;
    }

    public List<Article> loadArticles(WebSource webSource) {
        List<Article> feedArticles = new ArrayList<>();

        try {
            URL url  = new URL(webSource.getFeed());
            XmlReader reader = new XmlReader(url);
            SyndFeed feed = new SyndFeedInput().build(reader);
            Article article;
            for (Iterator i = feed.getEntries().iterator(); i.hasNext();) {
                SyndEntry entry = (SyndEntry) i.next();

                article = new Article();
                article.setTitle(entry.getTitle());
                article.setPermanentLink(entry.getLink());
                if(entry.getAuthor() != null) {
                    article.setAuthor(entry.getAuthor());
                }
                else {
                    article.setAuthor(webSource.getProvider().getFullName());
                }
                if(entry.getUpdatedDate() != null) {
                    article.setPublication(entry.getUpdatedDate());
                }
                else {
                    article.setPublication(entry.getPublishedDate());
                }
                article.setWebSource(webSource);
                if(entry.getDescription() != null) {
                    article.setSummary(entry.getDescription().getValue());
                }
                SyndContent syndContent;
                StringBuilder content = new StringBuilder();
                for(int j = 0;j < entry.getContents().size();j++) {
                    syndContent = (SyndContent) entry.getContents().get(j);
                    content.append(syndContent.getValue());
                }
                article.setContent(content.toString());

                feedArticles.add(article);
            }
        } catch (IllegalArgumentException | FeedException | IOException iae) {
            LOGGER.log(Level.WARNING, iae.getMessage(), iae);
        }

        return feedArticles;
    }

    /**
     * @param webSource a web source that contains details about a user website.
     * @return the webSource loaded with feed values.
     * */
    public WebSource loadWebSource(WebSource webSource) {
        String id = webSource.getId();
        UserAccount provider = webSource.getProvider();
        WebSource newWebSource = loadWebSource(provider.getWebsite());
        if(newWebSource != null) {
            newWebSource.setId(id);
            newWebSource.setProvider(provider);
            return newWebSource;
        }
        else {
            return webSource;
        }
    }

    /**
     * @param websiteUrl a website url where we can find a feed url.
     * @return a new webSource filled with feed url and title only. Null if a feed url is not found in the content of
     * the informed website url.
     * */
    public WebSource loadWebSource(String websiteUrl) {
        WebSource webSource = null;
        String feedUrl = findWebsiteFeedURL(websiteUrl);
        LOGGER.log(Level.INFO, "feedUrl: {0}", feedUrl);
        if(feedUrl != null) {
            try {
                URL url  = new URL(feedUrl);
                XmlReader reader = new XmlReader(url);
                SyndFeed feed = new SyndFeedInput().build(reader);
                webSource = new WebSource(feed.getTitle(), feedUrl);
            } catch (IllegalArgumentException | FeedException | IOException iae) {
                LOGGER.log(Level.SEVERE, iae.getMessage(), iae);
            }
        }
        return webSource;
    }

    /**
     * @param urlWebsite url used to find the web content where there is probably a feed to be consumed.
     * @return if a feed url is found in the web content, it is returned. Otherwise, the method returns null.
     * */
    public String findWebsiteFeedURL(String urlWebsite) {
        String feedUrl = null;
        String websiteContent = retrieveWebsiteContent(urlWebsite);
        LOGGER.log(Level.INFO, "urlWebsite: {0}", urlWebsite);
        if(websiteContent == null) {
            return null;
        }

        Pattern hrefPattern = Pattern.compile("href=\\W([^(\"|\')])+\\W");
        Matcher matcher = hrefPattern.matcher(websiteContent);

        while (matcher.find()) {
            feedUrl = matcher.group();
            if(feedUrl.contains("\"")) {
                feedUrl = feedUrl.substring(feedUrl.indexOf("\"") + 1, feedUrl.lastIndexOf("\""));
            }
            else if(feedUrl.contains("\'")) {
                feedUrl = feedUrl.substring(feedUrl.indexOf("\'") + 1, feedUrl.lastIndexOf("\'"));
            }
            else {
                continue;
            }
            if(isFeedURL(feedUrl)) {
                if(UrlUtils.INSTANCE.isRelative(feedUrl)) {
                    urlWebsite = UrlUtils.INSTANCE.setProtocol(urlWebsite);
                    feedUrl = UrlUtils.INSTANCE.concatUrlFragment(urlWebsite, feedUrl);
                }
                break;
            }
        }
        return feedUrl;
    }

    /**
     * @param url candidate to be a feed url.
     * @return true if the informed url is actually a feed, false otherwise.
     * */
    private boolean isFeedURL(String url) {
        String lowerCaseUrl = url.toLowerCase();
        if(lowerCaseUrl.contains("feed") || lowerCaseUrl.contains("rss") || lowerCaseUrl.contains("atom")) {
            return true;
        }
        return false;
    }

    /**
     * @param url used to find the web content where there is probably a feed to be consumed.
     * @return the entire content, which or without url feed.
     * */
    private String retrieveWebsiteContent(String url) {
        StringBuilder content = null;
        String fullUrl = UrlUtils.INSTANCE.setProtocol(url);

        if(fullUrl != null) {
            try {
                URL theUrl = new URL(fullUrl);
                BufferedReader br = new BufferedReader(new InputStreamReader(theUrl.openStream()));
                String line = "";
                content = new StringBuilder();
                while(null != (line = br.readLine())) {
                    content.append(line);
                }
            }
            catch (IOException ex) {
                LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
        return content != null ? content.toString() : null;
    }
}