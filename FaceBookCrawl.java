package facebookcrawl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Safoora Yousefi
 */
public class FaceBookCrawl {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, Exception
    {      
        Crawler crlr = new Crawler();
        /**** Get posts ***/
        ///121784701311648 254490381374412 Dog owners
        ///20444826822 10152095789086823 Cesar Millan
        ///343934742322479 585991338116817 Secular Scotland
        ///151761649081 10152089079114082 Dystopia Rising
        ///1389972874566356 1433632960200347 DEBATE OF RELIGIONS
        //383886871680383 556600784408990 Not Necessarily Bishop Stopford Debate Society
        //244137892388551 386126304856375 Paul's Icmeler Lovers
        
        final boolean LINGANAL = false;
        String[] accountInfo = new String[2];
        accountInfo[0] = "mary.torv@gmail.com";
        accountInfo[1] = "Mary@1192";
//        accountInfo[0] = "tom.shaban1192@gmail.com";
//        accountInfo[1] = "Tom@1192";
//        accountInfo[0] = "safoora.yousefi1192@gmail.com";
//        accountInfo[1] = "safoora1192";

        try
        {
            crlr.signIn(accountInfo);      
        }
        catch (IOException e)
        {
            System.out.println("Sign in webclient IOException");
        }
        String gId = "244137892388551";
        Group group =  new Group(gId, crlr);
 //       crlr.getGroupPosts(group);
        crlr.importPosts(group);
        File file2 = new File("/Users/Ayine/Desktop/Research/GT/Faraj/" + gId + "_content");
        FileWriter fw = new FileWriter(file2.getAbsoluteFile(), true);
        BufferedWriter bw = new BufferedWriter(fw);
        if (!file2.exists())
        {
                file2.createNewFile();
        }
        
        File peopleFile2 = new File("/Users/Ayine/Desktop/Research/GT/Faraj/" + gId + "_People");
        FileWriter peoplefw = new FileWriter(peopleFile2.getAbsoluteFile(), true);
        BufferedWriter peoplebw = new BufferedWriter(peoplefw);
        if (!peopleFile2.exists())
        {
                peopleFile2.createNewFile();
        }
        
        Map<String, Integer> map = new HashMap<String, Integer>();
        //map = crlr.importMap(map, group);
        int idCount = map.size() + 1;
        long ctime;
        Post post;
        int postCount = 0;
        for (int i = postCount; i < group.getPosts().size(); i++)
        {
            System.err.println(i);
            post = group.getPost(i);
            //crlr.getPostContent(post);
            try
            {
                
                postCount = crlr.getPostComments(post);
                if (postCount == -1)
                {
                    i--;
                    continue;
                }
                if(!map.containsKey(post.getPoster().getId()))
                {        
                    map.put(post.getPoster().getId(), idCount);
                    peoplebw.write(idCount + " " + post.getPoster().getId());
                    peoplebw.newLine();
                    peoplebw.flush();
                    idCount++;
                }
                if (LINGANAL)
                {
                    bw.write(map.get(post.getPoster().getId()) + "\t" + post.time + "\t" + post.Content);
                    bw.newLine();
                    bw.write( "" + post.comments.size());
                    bw.newLine();
                }
                else
                    bw.write(map.get(post.getPoster().getId()) + "\t" + post.time);
                bw.flush();
                
                for (Comment comment : post.comments)
                {
                    if (comment.time != null)
                        ctime = Utils.msecToSec(Utils.dateToLong(comment.time));
                    else
                        ctime = comment.timeInMil;
                    if(!map.containsKey(comment.getCommenter().getId()))
                    {
                        map.put(comment.getCommenter().getId(), idCount);
                        peoplebw.write(idCount + " " + comment.getCommenter().getId());
                        peoplebw.newLine();
                        peoplebw.flush();
                        idCount++;
                    }
                    if (LINGANAL)
                    {
                        bw.write(map.get(comment.getCommenter().getId()) + "\t" + ctime + "\t" + comment.content);
                        bw.newLine();
                    }
                    else 
                        bw.write(map.get(comment.getCommenter().getId()) + "\t" + ctime + "\t");
                    
                    bw.flush();
                   // System.out.print(map.get(comment.getCommenter().getId()) + " " + ctime + " ");
                }
                bw.newLine();
                post.comments.clear();
                //if (!post.comments.isEmpty())
                //bw.newLine();
                bw.flush();
                //System.out.println();
            }
            catch (NullPointerException e)
            {
                System.err.println(i + ": Post Skipped");
                bw.write("SKIPPED");
                bw.newLine();
                bw.newLine();
                bw.flush();
                continue;
            }
            catch(Exception e)
            {
                e.printStackTrace();
                post.comments.clear();
                i--;
            }
        }
        bw.write(group.getPosts().size() + " " + idCount);
        bw.newLine();
        //crlr.getPostComments(group.getPost(9));
         //bw.write(content);
        bw.close();  
        peoplebw.close();
    }   
}
