package facebookcrawl;

import java.util.Date;
import java.util.Vector;

/**
 *
 * @author Safoora Yousefi
 */
public class Post 
{
    private
            Member poster;
            String id;
            String time;
            Vector<Comment> comments;
            int likeCount;
            Member[] likers;
            Group group;
            int numOfComments;
            String Content;
    Post(Member poster, String time, String postId)
    {
        this.comments = new Vector<>();
        this.poster = poster;
        this.time = time;
        this.id = postId;
    }
    public void addComment(Comment comment)
    {
        this.comments.add(comment);
    }
    public Member getPoster()
    {
        return this.poster;
    }       
}
