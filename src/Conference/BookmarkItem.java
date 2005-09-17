/*
 * BookmarkItem.java
 *
 * Created on 17 Сентябрь 2005 г., 23:21
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package Conference;
import Client.StaticData;
import com.alsutton.jabber.JabberDataBlock;
import ui.*;

/**
 *
 * @author EvgS
 */
public class BookmarkItem extends IconTextElement{
    
    String name;
    String jid;
    String nick;
    String password;
    boolean autojoin;
    boolean isUrl;
    
    public int getImageIndex(){ return (isUrl)? ImageList.ICON_PRIVACY_ACTIVE: ImageList.ICON_GCJOIN_INDEX; }
    public String toString(){ return jid+'/'+nick; }
    public int getColor(){ return 0;}
    
    /** Creates a new instance of BookmarkItem */
    public BookmarkItem() {
        super(StaticData.getInstance().rosterIcons);
    }
    
    public BookmarkItem(JabberDataBlock data) {
        this();
        isUrl=!data.getTagName().equals("conference");
        name=data.getAttribute("name");
        try {
            autojoin=data.getAttribute("autojoin").equals("true");
        } catch (Exception e) {}
        jid=data.getAttribute((isUrl)?"url":"jid");
        nick=data.getChildBlockText("nick");
        password=data.getChildBlockText("password");
    }
    
    public JabberDataBlock constructBlock() {
        JabberDataBlock data=new JabberDataBlock((isUrl)?"url":"conference", null, null);
        data.setAttribute("name", name);
        data.setAttribute((isUrl)?"jid":"url", jid);
        if (autojoin) data.setAttribute("autojoin", "true");
        if (nick.length()>0) data.addChild("nick",nick);
        if (password.length()>0) data.addChild("password",password);
        
        return data;
    }
}
