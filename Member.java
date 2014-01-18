package facebookcrawl;

import java.util.Vector;

/**
 *
 * @author Safoora Yousefi
 */
public class Member
{
    private String id;
   // private String url;
    private String[] interestUrls;
    private Vector <Post> posts;
    Member(String id)
    {
        this.id = id;
        this.posts = new Vector<>();
    }
    public String getId()
    {
        return this.id;
    }
    public void addPost(Post post)
    {
        this.posts.add(post);
    }
}
