
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import java.util.ArrayList;
import java.util.List;
import twitter4j.Paging;
import rita.RiMarkov;
import twitter4j.StatusUpdate;
import twitter4j.conf.ConfigurationBuilder;

public class TrumpBot {

    private final Twitter twitter;
    private final ArrayList<Status> tweets;

    public TrumpBot() {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey("ElLFOou7bIv0WqZlGv1prURwJ")
                .setOAuthConsumerSecret("7CZmKGkuX6ZzP5J8ftp1Pt9FxSt5smoV0b8AdQbQV69BC9t2aU")
                .setOAuthAccessToken("931757389929426944-iEOc7Whdg8p6wEXgWssqGyekIimu0qP")
                .setOAuthAccessTokenSecret("0xu2CXVM9ceFMyqKWuJOzgfjrhjuCEmE6Je7729HOzQeC");
        TwitterFactory tf = new TwitterFactory(cb.build());
        twitter = tf.getInstance();
        tweets = this.getTrumpTweets();
    }

    public static void main(String... args) throws TwitterException {
        TrumpBot tb = new TrumpBot();
        tb.sendMarkovTweet();
    }

    public void sendMarkovTweet() {
        String tweetText = "";
        while (tweetText.isEmpty() || tweetText.length() > 140) {
            tweetText = this.generateMarkovSentence();
        }

        try {
            Status status = twitter.updateStatus((new StatusUpdate(tweetText)));
            //System.out.println(twitter.getScreenName() + ": " + tweetText + "\nSent");
            System.out.println(tweetText +"\nSent.");
        } catch (TwitterException e) {
            System.out.println("Blocked from tweeting.");
        }
    }

    private String generateMarkovSentence() {
        RiMarkov rm = new RiMarkov(3, true, false);
        rm.loadText(this.getTrumpTweetText());
        return rm.generateSentence();
    }

    private String getTrumpTweetText() {
        String statusText = "";
        for (Status tweet : tweets) {
            statusText += tweet.getText() + " ";
        }
        return statusText;
    }

    private ArrayList<Status> getTrumpTweets() {
        List statuses = new ArrayList<>();
        String user = "realDonaldTrump";
        int pageno = 1;
        int size = -1;

        do {
            try {
                size = statuses.size();
                Paging page = new Paging(pageno++, 200);
                statuses.addAll(this.twitter.getUserTimeline(user, page));
            } catch (TwitterException e) {
                e.printStackTrace();
            }
        } while (!(statuses.size() == size));
        return this.processTweets(statuses);
    }

    private ArrayList<Status> processTweets(List<Status> statuses) {
        ArrayList<Status> filtered = new ArrayList<>();
        for (Status status : statuses) {
            if (status.getURLEntities().length == 0
                    && status.getMediaEntities().length == 0) {
                filtered.add(status);
            }
        }
        return filtered;
    }
}
