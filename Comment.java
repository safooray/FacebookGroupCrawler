package facebookcrawl;

import java.util.Date;
import java.util.GregorianCalendar;

/**
 *
 * @author Safoora Yousefi
 */
public class Comment 
{
    private
            Member commenter;
            GregorianCalendar time;
            Long timeInMil;
            String id;
            Post post;
            String content;
    Comment(Member commenter, GregorianCalendar time)
    {
        this.commenter = commenter;
        this.time = time;
    }
    public Member getCommenter()
    {
        return commenter;
    }
}
