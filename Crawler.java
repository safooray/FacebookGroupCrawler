package facebookcrawl;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.CookieManager;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 *
 * @author Safoora Yousefi
 */
class Crawler 
{
    public static final boolean DEBUG = false;
    public static final boolean DEBUG_2 = false;
    public static final boolean LINGANAL = false;
    private HtmlPage loginPage;
    private HtmlPage groupPage;
    private HtmlPage postPage;
    public WebClient webClient;
    private WebClient webClientStndrd;
    private BrowserVersion bvTemp;
    private BrowserVersion bv;
    int postCount = 0;
    public final String facebookGraphUrl = "http://graph.facebook.com/";
    public final String facebookMobileUrl = "https://m.facebook.com/";
    CookieManager cm;
    String[] accountInfo;
    int userIndex;
    Crawler() throws IOException
    {
        userIndex = 1;
        accountInfo = new String[2];
        accountInfo[0] = "mary.torv@gmail.com";
        accountInfo[1] = "Mary@1192";
//        accountInfo[0] = "tom.shaban1192@gmail.com";
//        accountInfo[1] = "Tom@1192";
//        accountInfo[0] = "safoora.yousefi1192@gmail.com";
//        accountInfo[1] = "safoora1192";    
        bvTemp = BrowserVersion.FIREFOX_17;
        bv = new BrowserVersion(
        		bvTemp.getApplicationName(), 
        		bvTemp.getApplicationVersion(), 
        		"Opera/9.80 (S60; Opera Mobi/2.1; U; en) Presto/2 Version/2.1", 
        		bvTemp.getBrowserVersionNumeric());
        webClient = new WebClient(bv);
        webClientStndrd = new WebClient(bvTemp);
    }
    public void renewWC(int i)
    {
        if (i == 3)
        {
            accountInfo[0] = "mary.torv@gmail.com";
            accountInfo[1] = "Mary@1192";
            userIndex = 1;
        }
        else if (i == 1)
        {
            accountInfo[0] = "tom.shaban1192@gmail.com";
            accountInfo[1] = "Tom@1192";
            userIndex = 2;
        }
        else if (i == 2)
        {
            accountInfo[0] = "safoora.yousefi1192@gmail.com";
            accountInfo[1] = "safoora1192";   
            userIndex = 3;
        }
        webClient = new WebClient(bv);
        webClientStndrd = new WebClient(bvTemp);      
    }
    public HtmlPage signIn(String[] accountInfo) throws IOException, Exception
    {
        loginPage = webClient.getPage(facebookMobileUrl);
        if(DEBUG)
        {
            System.out.println("Title:" + loginPage.getTitleText());
        }
        String email = accountInfo[0];
        String password = accountInfo[1];
        if (DEBUG)
            System.out.println(accountInfo[0] + " " + accountInfo[1]);
       // final HtmlForm loginForm = loginPage.getFirstByXPath("//form[@action='https://m.facebook.com/login.php?refsrc=https%3A%2F%2Fm.facebook.com%2F&refid=8']");
        final HtmlInput signInButton = loginPage.getElementByName("login");
        final HtmlTextInput emailField = loginPage.getElementByName("email");
        final HtmlPasswordInput passField = loginPage.getElementByName("pass");

        // Change the value of the text field
        emailField.setValueAttribute(email);
        passField.setValueAttribute(password); 

        // Now submit the form by clicking the button
        HtmlPage signInResultPage = signInButton.click();
        //WebWindow window = page.getEnclosingWindow();
        if (signInResultPage.asText().contains("try again later"))
        {
            System.err.println("Sign in failed...");
            Exception tooOften = new Exception();
            renewWC(userIndex);
            signIn(accountInfo);
            throw tooOften;
        }
        signInButton.click();
        //signInResultPage = (HtmlPage) window.getEnclosedPage();
        //System.out.println("Profile Page : "+ signInResultPage.asText());
        this.cm = webClient.getCookieManager();
        webClient.closeAllWindows();
        return signInResultPage;
    }
    public int getPostComments(Post post) throws IOException, InterruptedException, Exception
    {

        String timeStrMilsOld = post.time;
        HtmlPage testPage = this.webClient.getPage("https://m.facebook.com/groups/" + post.group.id);
        if(testPage.asText().contains("Log In"))
        {
            try
            {
                this.signIn(accountInfo);
            }
            catch (IOException e)
            {
                System.out.println("Sign in webclient IOException");
                return getPostComments(post);
            }
            catch(Exception e)
            {
                return -1;
            }
        }
            //webClient.setCookieManager(cm);
        //post.id = "539781356090933";
        String url = "https://m.facebook.com/groups/" + post.group.id + "?view=permalink&id=" + post.id;
        List <HtmlElement> commentContainers = new ArrayList<HtmlElement>();
        postPage = webClient.getPage(url);
        HtmlElement moreComments;
        List <HtmlElement> cmnts;
        if ((cmnts = (List <HtmlElement>)postPage.getByXPath("//div[@class='row ufiComment aclb apl']")) != null)
        {
            commentContainers.addAll(cmnts);
        }
        while ((moreComments = (HtmlElement) postPage.getElementById("see_prev_" + post.id)) != null)
        {
            postPage = webClient.getPage(facebookMobileUrl + moreComments.getElementsByTagName("a").get(0).getAttribute("href"));
            commentContainers.addAll((List <HtmlElement>)postPage.getByXPath("//div[@class='row ufiComment aclb apl']"));
        }
//        if ((cmnts = (List <HtmlElement>)postPage.getByXPath("//div[@class='row ufiComment aclb apl']")) != null)
//        {
//            commentContainers.addAll(cmnts);
//        }

        HtmlAnchor cCommenter;
        System.out.println("Post By: " + post.getPoster().getId());
        for (int i = 0; i < commentContainers.size(); i++)
        {
//            if ()
//            {
//                System.err.println("Test...");
//            }
            String timestr = null;
            String content = null;
            String cid = commentContainers.get(i).getAttribute("id");
            try
            {
                String JSonComment = webClient.getPage(facebookGraphUrl + cid).getWebResponse().getContentAsString();
                timestr = JSonComment.substring(JSonComment.indexOf("created_time") + "created_time".length() + 4,
                        JSonComment.indexOf("created_time") + "created_time".length() + 4 + "2012-11-21T18:07:29+0000".length());
                if (LINGANAL)
                if (!JSonComment.contains("\"message_tags\""))
                    content = JSonComment.substring(JSonComment.indexOf("message") + "message".length() + 4, JSonComment.indexOf("can_remove") - 7);
                else
                    content = JSonComment.substring(JSonComment.indexOf("message") + "message".length() + 4, JSonComment.indexOf("message_tags") - 7);
            }
            catch(com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException e)
            {
                
                //Thread.sleep(5000);
                
                if (e.getMessage().contains("403 Forbidden for http://graph.facebook.com/"))
                {
                    i--;
                    renewWC(userIndex);
                    continue;
                }
                
                else
                {
                    HtmlPage testPsge = webClient.getPage(url);
                    if (testPsge.asText().contains("You must log in"))
                    {
                        this.signIn(accountInfo);
                        i--;
                        continue;
                    }
                    else if (timestr == null)
                    {
                        timestr = "-1";
                    }
                    else
                    {
                        System.err.println("FailingHttpStatusCodeException- Comment Skipped.");
                        System.err.println(post.id + " " + cid);
                    }
                }       
            }
            String comUrl = null;
            try
            {
                cCommenter = (HtmlAnchor) commentContainers.get(i).getOneHtmlElementByAttribute("a", "class", "actor-link");//FirstByXPath("//a[@class='actor-link']");
                comUrl = cCommenter.getHrefAttribute();
                Member commenter = new Member(comUrl.substring(1, comUrl.length() - "?refid=18".length()));
                //time = commentContainers.get(i).getElementsByTagName("abbr").get(0).asText();
                Comment comment;
                if (timestr.equals("-1"))
                {
                    comment = new Comment(commenter, null);
                    if (commentContainers.size() <= 1 || i == commentContainers.size() - 1)
                        comment.timeInMil = Long.valueOf(timeStrMilsOld);
                }
                else
                {
                    comment = new Comment(commenter, Utils.stringToDate(timestr));
                    comment.timeInMil = Utils.msecToSec(Utils.dateToLong(Utils.stringToDate(timestr)));
//                    int j = i - 1;
//                    while (j >= 0 && post.comments.get(j).time == null)
//                    {
//                        
//                    }
                    if ( i > 0 && post.comments.get(i - 1).time == null)
                    {
                        Comment modifCom = post.comments.get(i - 1);
                        Long timeMils = Utils.msecToSec(Utils.dateToLong(Utils.stringToDate(timestr)));
                        Long modifTimeMils = (Long.valueOf(timeStrMilsOld) + timeMils) / 2;
                        
                        modifCom.timeInMil = modifTimeMils;
                        post.comments.set(i - 1, comment);
                    }
                    timeStrMilsOld = String.valueOf(Utils.msecToSec(Utils.dateToLong(Utils.stringToDate(timestr))));
                }
                comment.id = cid;
                comment.post = post;
                comment.content = content;
                post.addComment(comment);
                if (DEBUG)
                {
                    System.out.println(i + " = " + commenter.getId() + " comment at: " + comment.time);
                }
            }
            catch (ElementNotFoundException e)
            {
                System.out.println("No actor found for comment " + cid);
            }
            catch (Exception e)
            {
                i--;
            }
        }
        if (post.comments.size() < post.numOfComments)
        {
            System.err.println("CONFLICT:Number of comments:Post " + post.id + " " + post.comments.size() + " " + post.numOfComments);
        }
        postCount ++;
        return postCount;
    }
    public void getGroupPosts(Group group) throws IOException, Exception
    {
        String urlP1 = "/groups/" + group.id + "?view=permalink&id=";
        int postIdLen = "400646220071050".length();
        List <HtmlElement> storyContainers = new ArrayList<>();
        File file = new File("/Users/Ayine/Desktop/Research/GT/Faraj/" + group.id + "_Posts");
        if (!file.exists())
        {
                file.createNewFile();
        }
        FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
        BufferedWriter bw = new BufferedWriter(fw);
        HtmlElement morePosts;
        File nonPostFile = new File("/Users/Ayine/Desktop/Research/GT/Faraj/" + group.id + "_nonPosts");
        if (!nonPostFile.exists())
        {
                nonPostFile.createNewFile();
        }
        FileWriter nonPostFilefw = new FileWriter(nonPostFile.getAbsoluteFile(), true);
        BufferedWriter nonPostFilebw = new BufferedWriter(nonPostFilefw);
        
        //int postCount = 0;
        //int i = 0;
        String url = "https://m.facebook.com/groups/" + group.id;
        try
        {
            groupPage = webClient.getPage(url);
//            groupPage.save(new File("lastPage.html"));
        }
        catch (java.net.SocketTimeoutException e)
        {
            System.err.println(" ");
            groupPage = webClient.getPage(url);
        }
        boolean lastPage = false;
        morePosts = (HtmlElement) groupPage.getElementById("m_more_item");
        while (morePosts != null || lastPage)
        {
            System.err.println("URL: " + groupPage.getUrl());
            storyContainers.clear();
            //Read a page of posts
            storyContainers.addAll((List <HtmlElement>)groupPage.getByXPath("//div[@class='_55wo _56bf _5rgl']"));
            if(false)
            {
                System.out.println(morePosts.getElementsByTagName("a").get(0).getAttribute("href"));
                System.out.println("Group Page : "+ groupPage.asText());
            }
            
            for (int i = 0; i < storyContainers.size(); i++)
            {
                HtmlElement cPoster = null;
                String time = null;
                String purl = null;
                int numCom = 0;
                HtmlElement comcount = null;
                List<HtmlElement> anchorEls = storyContainers.get(i).getElementsByAttribute("div", "class", "mfss fcg").get(1)
                        .getElementsByTagName("a");
                String postId = anchorEls.get(anchorEls.size() - 1)
                        .getAttribute("href").substring(urlP1.length(), urlP1.length() + postIdLen);
                try
                {
                    //_52ja
                    cPoster = storyContainers.get(i).getOneHtmlElementByAttribute("div", "class", "_52ja").getElementsByTagName("a").get(0);//FirstByXPath("//a[@class='actor-link']");
                }
                catch (ElementNotFoundException e)
                {
                    System.out.println(postId + " is a post without actor-link.");
                    nonPostFilebw.write(postId);
                    nonPostFilebw.newLine();
                    nonPostFilebw.flush();
                }
                if (cPoster != null)
                {
                    purl = cPoster.getAttribute("href");
                    Member member = new Member(purl.substring(1, purl.length() - "?refid=18&_ft_".length()));
                    webClientStndrd.getOptions().setJavaScriptEnabled(false);
                    webClientStndrd.setCookieManager(webClient.getCookieManager());
                    HtmlPage fbpp = null;
                    try
                    {
                        fbpp = webClientStndrd.getPage("https://www.facebook.com/groups/" + group.id + "/permalink/" + postId);
                    }
                    catch (java.net.SocketTimeoutException e)
                    {
                        System.err.println(" ");
                        i--;
                        continue;
                    }
                    try
                    {
                        DomElement timespan = fbpp.getElementsByTagName("abbr").get(0);
                        time = timespan.getAttribute("data-utime");
                    }
                    catch(IndexOutOfBoundsException e)
                    {
                        i--;
                        continue;
                    }
//                    try
//                    {
//                        comcount = (HtmlElement)storyContainers.get(i).getOneHtmlElementByAttribute("a", "class", "sec nowrap nowrap");
//                    }
//                    catch (ElementNotFoundException e)
//                    {
//                        System.out.println(postId + " is a post without nowrap elements.");
//                    }
                    if (comcount != null && !comcount.asText().contains("Comment"))
                        System.out.println("No comments found for " + postId);
                    else if (comcount != null && comcount.asText().length() == 9)
                    {
                        numCom = 1;
                        System.out.println("ONE comment found for " + postId);
                    }
                    else if (comcount != null)
                    {
                        String modifNum = comcount.asText();
                        while (modifNum.contains(","))
                            modifNum = modifNum.substring(0, modifNum.indexOf(",")) + modifNum.substring(modifNum.indexOf(",") + 1);
                            
                        numCom = Integer.valueOf(modifNum.substring(0, modifNum.length() - " Comments".length()));
                    }

                    Post post = new Post(member, time, postId);
                    post.group = group;
                    post.numOfComments = numCom;
                    group.addPost(post);
                    member.addPost(post);

                    bw.write(post.id + " " + member.getId() + " " + post.time);// + " " + post.numOfComments);
                    bw.newLine();
                    bw.flush();

                    if (true)
                    {
                        System.out.println(postId + " = " + member.getId() + " post at: " + post.time);
                    }
                }
                if (false)
                {
                    System.out.println(i + ": " + storyContainers.get(i).getId());
                    System.out.println(i + ": " + time);
    //                System.out.println(purl.substring(0, purl.length() - "?refid=18&_ft_".length() - 1));
                }
            }
            morePosts = null;
            morePosts = (HtmlElement) groupPage.getElementById("m_more_item");
            if (morePosts == null && lastPage == true)
                break;
            if (morePosts == null)
            {
                lastPage = true;
                System.err.println("LAST PAGE OF POSTS");
            }
            if (lastPage == false)
            {
                groupPage = webClient.getPage(facebookMobileUrl + morePosts.getElementsByTagName("a").get(0).getAttribute("href"));
            }
            //postCount += storyContainers.size();
            postCount++;
        }
        bw.close();
        //webClient.getCookieManager().clearCookies();
    }
    public void getPostContent(Post post) throws IOException, Exception
    {
        HtmlPage testPage = webClient.getPage("https://m.facebook.com/groups/" + post.group.id);
        if(testPage.asText().contains("Log In"))
        {
            try
            {
                this.signIn(accountInfo);
            }
            catch (IOException e)
            {
                System.out.println("Sign in webclient IOException");
            }
        }
        
        //String groupTitle = "Dystopia Rising";
        HtmlPage postPage = webClient.getPage("https://m.facebook.com/groups/" + post.group.id + "?view=permalink&id=" + post.id);
        HtmlElement contentEl;
        String content;
        if (postPage.getTitleText().contains("Content Not Found"))
            content = "NA";
        else
        {
            try
                {
                    contentEl = postPage.getFirstByXPath("//div[@class='msg']");
                    if (contentEl == null)
                    {
                        System.out.print("Taajjob");
                    }
                    content = contentEl.getTextContent();
                    String useless = post.getPoster().getId() + " > " + post.group.title;
                    content = content.substring(useless.length());
                }
                catch (ElementNotFoundException e)
                {
                    System.out.println(post.id + " is a post without text content");
                    content = "NoText";
                }
                catch (StringIndexOutOfBoundsException e)
                {
                    System.out.println(post.id + " is a post without text content");
                    content = "NoText";
                }
        }
        post.Content = content;
    }
    public void importPosts(Group group) throws FileNotFoundException, IOException, Exception
    {
              FileReader fr = new FileReader("/Users/Ayine/Desktop/Research/GT/Faraj/" + group.id +"_posts");
              BufferedReader br = new BufferedReader(fr);
              int lineCount = 1;
              String line;

              while ((line = br.readLine()) != null)
              {
                StringTokenizer st = new StringTokenizer(line, " ");
                String postId = st.nextToken();
                String poster = st.nextToken();
                String postTime = st.nextToken();
//                String numComments = st.nextToken();
                Post post = new Post(new Member(poster), postTime, postId);
//                post.numOfComments = Integer.valueOf(numComments);
                group.addPost(post);
                post.group = group;
              }            
    }
    public Map<String, Integer> importMap(Map<String, Integer> map, Group group) throws FileNotFoundException, IOException, Exception
    {
              FileReader fr = new FileReader("/Users/Ayine/Desktop/Research/GT/Faraj/" + group.id +"_People");
              BufferedReader br = new BufferedReader(fr);
              String line;

              while ((line = br.readLine()) != null)
              {
                StringTokenizer st = new StringTokenizer(line, " ");
                String userId = st.nextToken();
                String userName = st.nextToken();
                map.put(userName, Integer.valueOf(userId));
              }    
              return map;
    }
    public void getPublicInfo(Member member)
    {
        HtmlPage userPage;
        String url = facebookMobileUrl + member.getId();
        try
        {
            userPage = webClient.getPage(url);
            List<?> fields = userPage.getByXPath("//a[@class='sec']");
        }
        catch(IOException e)
        {
            getPublicInfo(member);
            return;
        }
    }
}

