package sysu.project.lee.sportslife.News.UI;

import org.litepal.crud.DataSupport;

import java.io.Serializable;
import java.util.ArrayList;

import sysu.project.lee.sportslife.User.UserEntity;


/**
 * 1.title
 * 2.description
 * 3.link
 * 4.pubdate
 * 
 * */

@SuppressWarnings("serial")
public class FeedItem extends DataSupport implements Serializable
{
    private UserEntity user;

    private String title;
    private String content;
    private String link;
    private String pubdate;
    private String category;
    private String firstImageUrl;
    private boolean readed = false;
    private boolean favorite = false;
    private ArrayList<String> imageUrls = new ArrayList<String>();
    private int Id;

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }


	public String getFirstImageUrl()
	{
		return firstImageUrl;
	}
    public void setFirstImageUrl(String imageUrl)
	{
		this.firstImageUrl = imageUrl;
	}
	
	public String getTitle() 
	{
		return title;
	}
	public void setTitle(String title) 
	{
		this.title = title;
	}
	public String getLink() 
	{
		return link;
	}
	public void setLink(String link)
	{
		this.link = link;
	}
	public String getPubdate() 
	{
		return pubdate;
	}
	public void setPubdate(String pubdate)
	{
		this.pubdate = pubdate;
	}
	public String getCategory() 
	{
		return category;
	}
	public void setCategory(String category)
	{
		this.category = category;
	}
	public ArrayList<String> getImageUrls()
	{
		return imageUrls;
	}
	public void setImageUrls(ArrayList<String> imageUrls)
	{
		this.imageUrls = imageUrls;
	}
	public boolean isReaded()
	{
		return readed;
	}
	public void setReaded(boolean readed)
	{
		this.readed = readed;
	}
	public boolean isFavorite()
	{
		return favorite;
	}
	public void setFavorite(boolean favorite)
	{
		this.favorite = favorite;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}

    public int getId() {
        return Id;
    }

    @Override
    public String toString() {
        return "FeedItem{" +
                "Id=" + Id +
                ", title='" + title + '\'' +
                ", user=" + user +
                ", favorite=" + favorite +
                '}';
    }
}
