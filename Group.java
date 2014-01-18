/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package facebookcrawl;

import com.gargoylesoftware.htmlunit.html.HtmlPage;
import java.io.IOException;
import java.util.Vector;

/**
 *
 * @author Ayine
 */
public class Group 
{
    //String Title;
    private Member[] members;
    String id;
    private Vector<Post> posts;
    String title;
    Group(String groupId, Crawler crlr) throws IOException, Exception
    {
        this.id = groupId;
        posts = new Vector<>();
        HtmlPage testPage = crlr.webClient.getPage("https://m.facebook.com/groups/" + groupId);
        if(!testPage.getByXPath("//input[@value='Log In']").isEmpty())
        {
            try
                {
                    crlr.signIn(crlr.accountInfo);      
                }
                catch (IOException e)
                {
                    System.out.println("Sign in webclient IOException");
                }
        }
        HtmlPage gPage = crlr.webClient.getPage("https://m.facebook.com/groups/" + groupId);
        title = gPage.getTitleText();
    }
    public Member getMemberById(String id)
    {
        for (Member member : members)
        {
            if (member.getId().equals(id)) 
            {
                return member;
            }
        }
        return null;
    }
    public void addPost(Post post)
    {
        posts.add(post);
    }
    public Post getPost(int i)
    {
        return posts.get(i);
    }
    public Vector<Post> getPosts()
    {
        return posts;
    }
}
