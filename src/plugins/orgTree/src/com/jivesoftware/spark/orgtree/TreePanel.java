package com.jivesoftware.spark.orgtree;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.jivesoftware.Spark;
import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.RosterPacket;
import org.jivesoftware.smackx.LastActivityManager;
import org.jivesoftware.smackx.packet.LastActivity;
import org.jivesoftware.spark.ChatManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.ui.ContactItem;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.sparkimpl.profile.VCardManager;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;


public class TreePanel extends JPanel {
	
	 private JPanel mainTreePanel = new JPanel();
	 private JScrollPane treeScrollPane;
	 private JScrollPane treeScroller;
	 private JPanel panel;
	 private ExpandTreeNode selectedNode;
	 private ExpandTreeNode newNode;
	 private TreeNode[] nodes;
	 private TreePath path;
     JTree tree;
     
     private HttpURLConnection connect;
     private URL url;
     private InputStream is;
     private BufferedReader br;
     private String strLine;
     private String result;
 	// 上面JTree对象对应的model
 	DefaultTreeModel model;
    //  定义几个初始节点
	ExpandTreeNode root = new ExpandTreeNode("宝信软件","00000000","1"); 
	

   
	ChatManager chatManager;
	String serviceName;
	String xmlNodes;
	String xmlChildrenNodes;
	TreeXMLParse parse;
	List<UserNode> userList;
	List<OrgNode> orgList;
	UserNode uNode;
	OrgNode oNode;
	ExpandTreeNode oTreeNode;
	

	 String urlStr;
	 String name;
	 String password;
	 LocalPreferences localPreferences ;
	 String webUrl ;
	 String webPath;
	 ConfigurationUtil config;
	 
	   JPopupMenu popup = new JPopupMenu();
	  final PopupMenu p = new PopupMenu();
	  final MenuItem m1;
    public TreePanel(){
    	 
		// System.out.println("!@#$%^:"+ SparkRes.getString("ORG"));
		//组织机构树根节点                     j
		  File file = new File(Spark.getSparkUserHome());
	        if (!file.exists()) {
	            file.mkdirs();
	        }
	       new File(file, "spark.properties");
	       try{
	      file = new File(Spark.getSparkUserHome(), "/spark.properties");
	      config = new ConfigurationUtil(file.getAbsolutePath());
          }catch(Exception ex){ex.printStackTrace();}
//	      webUrl = config.getValue("weburl");
//	      webPath = SparkRes.getString("ORG");
        webUrl="http://localhost:8080/contactsweb/";
        webPath="";


        //	 String pathString = "http://localhost:8080/efmpx/EFMPX/IM/IM02.jsp?serviceName=JQueryOrgTree&top=top";
    	 String pathString =webUrl+webPath ;
    	
    	 
    	 String authenType = "CodedPwd";
         ORGPasswordMD5 oPasswordMD5 = ORGPasswordMD5.getInstance();
         name = oPasswordMD5.getAdminName();
         password = oPasswordMD5.passwordMD5;
         
         SimpleDateFormat CREDENTIAL_FORMAT = new SimpleDateFormat("yyyyMMddHHmm");
         Calendar now = Calendar.getInstance();
         String minuteStr = CREDENTIAL_FORMAT.format(now.getTime());
         
         String cre = ORGPasswordMD5.md5((minuteStr + password));
         
         urlStr = pathString + "&p_username=" + name + "&p_password=" + cre + "&p_authen=" + authenType;

         System.out.println("URL:"+urlStr);
		 xmlNodes = getTreeChildren(urlStr);
		 xmlNodes = xmlNodes.replaceAll("&", "&amp;");
    	
    	
	
		      parse = new TreeXMLParse();
		    
		      orgList = parse.xmlOrg(xmlNodes);
		
		      if(orgList!=null)
		      {
		    	  for(int i=0;i<orgList.size();i++)
		    	  {
		    		  oNode =   orgList.get(i);
		    		  oTreeNode = new ExpandTreeNode(oNode.getGroupName(),oNode.getGroupLabel(),"1");
		    	      root.add(oTreeNode);
		    	  }
		      }
		      tree = new JTree(root);
			  model = (DefaultTreeModel)tree.getModel();
			  tree.setRootVisible(false);
			  tree.setCellRenderer(new IconNodeRenderer());  
			  tree.setToggleClickCount(1);
			  tree.putClientProperty("JTree.lineStyle" , "None");
	          tree.setBackground(new Color(240,243,253));
	          UIManager.getDefaults().put("Tree.lineTypeDashed", true);
			  tree.setUI(new ORGTreeUI());
			  tree.setRowHeight(20);
		      
			  m1 = new MenuItem("cai");
			  p.add(m1);
			  tree.add(p);
			  tree.addMouseListener(new MouseAdapter(){
				public void mouseReleased(MouseEvent e) {
					  if(e.isPopupTrigger())
				 		{
				 		//	p.show(tree, e.getX(), e.getY());
						    TreePath path = tree.getPathForLocation(e.getX(), e.getY());  
					        if (path == null) {
					            return;
					        }
					        tree.setSelectionPath(path);
						  
						    selectedNode = (ExpandTreeNode) tree.getLastSelectedPathComponent();
							if (selectedNode == null) return;		
							ChatManager chatManager = SparkManager.getChatManager();//对话控制器
					 		serviceName=SparkManager.getConnection().getServiceName();//域名
					 	
				 			ContactItem item = new ContactItem(selectedNode.toString(),selectedNode.getText(),selectedNode.getText()+"@"+serviceName);
				 			if(selectedNode.getType()!=null&&selectedNode.getType().equals("2"))
				 			{
				 			  showPopup(tree,e,item);
				 			}
				 		
				 		}
				}
			  });
	     
	     MouseListener ml = new MouseAdapter() 
		 {
			 	public void mouseClicked(MouseEvent e)
				{
			 		if(e.getClickCount()==1) return;
//			 		
			 		//获取选中节点
					selectedNode = (ExpandTreeNode) tree.getLastSelectedPathComponent();
					//如果节点为空，直接返回
					if (selectedNode == null) return;
				    System.out.println("selectedNode的Label:"+selectedNode.getText());
			 		
			 	    if(selectedNode.getType()!=null&&selectedNode.getType().equals("2"))
			 	    {
			 	       
			 		 //聊天窗口弹出,要进行判断是组织机构还是人员
				 		ChatManager chatManager = SparkManager.getChatManager();//对话控制器
				 		serviceName=SparkManager.getConnection().getServiceName();//域名
				 		chatManager.activateChat(selectedNode.getText()+"@"+serviceName, selectedNode.toString());//弹出对话框
			 	        //如果是人员则返回
				 		return;
			 	    }
			 	    
			 	    
			    //	机构,先清除节点的子节点 
			 	    if(!selectedNode.isLeaf())
			 	       deleteChildNode(selectedNode);
			 	    
			 	//重新查询子节点	
			 	//     String pathString = "http://localhost:8080/efmpx/EFMPX/IM/IM02.jsp?serviceName=JQueryOrgTree&labelParent="+selectedNode.getText();
			 	    File file = new File(Spark.getSparkUserHome());
			          if (!file.exists()) {
			            file.mkdirs();
			         }
			        new File(file, "spark.properties");
			        try{
			    	   	file = new File(Spark.getSparkUserHome(), "/spark.properties");
			    	   	config = new ConfigurationUtil(file.getAbsolutePath());
			       		}catch(Exception ex){ex.printStackTrace();}
			         webUrl = config.getValue("weburl");
			      	 webPath = SparkRes.getString("ORG_LEAF");
			 	     String pathString =webUrl+webPath+selectedNode.getText() ;
			 	     String authenType = "CodedPwd";
			         ORGPasswordMD5 oPasswordMD5 = ORGPasswordMD5.getInstance();
			         name = oPasswordMD5.getAdminName();
			         password = oPasswordMD5.passwordMD5;
			         
			         SimpleDateFormat CREDENTIAL_FORMAT = new SimpleDateFormat("yyyyMMddHHmm");
			         Calendar now = Calendar.getInstance();
			         String minuteStr = CREDENTIAL_FORMAT.format(now.getTime());
			         
			         String cre = ORGPasswordMD5.md5((minuteStr + password));
			         
			         urlStr = pathString + "&p_username=" + name + "&p_password=" + cre + "&p_authen=" + authenType;

					 xmlNodes = getTreeChildren(urlStr);
					 xmlNodes = xmlNodes.replaceAll("&", "&amp;");
			 
					
					  userList =  parse.xmlUser(xmlNodes);
					  //System.out.println(xmlChildrenNodes);
				      orgList = parse.xmlOrg(xmlNodes);
				      if(userList!=null)
				      {
				    	  for(int i=0;i<userList.size();i++)
				    	  {
				    		  uNode =   userList.get(i);
				    		  oTreeNode = new ExpandTreeNode(uNode.getUserName(),uNode.getUserLabel(),"2");
				    
				    		  selectedNode.add(oTreeNode);
				    		  nodes = model.getPathToRoot(oTreeNode);
							  path = new TreePath(nodes);
							  tree.scrollPathToVisible(path);
							  tree.updateUI();
				    	  }
				      }
				      if(orgList!=null)
				      {
				    	  for(int i=0;i<orgList.size();i++)
				    	  {
				    		  oNode =   orgList.get(i);
				    		  oTreeNode = new ExpandTreeNode(oNode.getGroupName(),oNode.getGroupLabel(),"1");
				    	
				    		  selectedNode.add(oTreeNode);
				    		  nodes = model.getPathToRoot(oTreeNode);
							  path = new TreePath(nodes);
							  tree.scrollPathToVisible(path);
							  tree.updateUI();
				    	  }
				      }
					
					
			 		
			 		
				}
			};
		   tree.addMouseListener(ml);
	   
	      
	      
	        setLayout(new BorderLayout());
	        panel = new JPanel();
	        panel.setLayout(new GridBagLayout());
	        panel.setBackground(new Color(240,243,253));
	        treeScroller = new JScrollPane(tree);// 滚动条
	        treeScroller.setBorder(BorderFactory.createEmptyBorder());
	        panel.add(treeScroller, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0,
	                GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5,
	                        5, 5, 5), 0, 0));// 设置边界
	        add(panel, BorderLayout.CENTER);
     }
    
    
    
    public void showPopup(Component component, MouseEvent e, final ContactItem item) {
        if (item.getJID() == null) {
            return;
        }

       
      
        popup = new JPopupMenu();
        JMenuItem chatMenu;
        chatMenu = new JMenuItem(Res.getString("menuitem.start.a.chat"), SparkRes.getImageIcon(SparkRes.SMALL_MESSAGE_IMAGE));
       
        
        ActionListener listener = new ActionListener(){
        	public void actionPerformed(ActionEvent et) {
        		ChatManager chatManager = SparkManager.getChatManager();//对话控制器
		 		serviceName=SparkManager.getConnection().getServiceName();//域名
		 		chatManager.activateChat(item.getJID(), item.getAlias());//弹出对话框
        		
        	}
        };
        chatMenu.addActionListener(listener);
        // Add Start Chat Menu
        popup.add(chatMenu);

        // Add Send File Action
        Action sendAction = new AbstractAction() {
			private static final long serialVersionUID = -7519717310558205566L;

			public void actionPerformed(ActionEvent actionEvent) {
                SparkManager.getTransferManager().sendFileTo(item);
            }
        };

        sendAction.putValue(Action.SMALL_ICON, SparkRes.getImageIcon(SparkRes.DOCUMENT_16x16));
        sendAction.putValue(Action.NAME, Res.getString("menuitem.send.a.file"));

        if (item.getPresence() != null) {
            popup.add(sendAction);
        }

        popup.addSeparator();


        Action viewProfile = new AbstractAction() {
			private static final long serialVersionUID = -2562731455090634805L;

			public void actionPerformed(ActionEvent e) {
                VCardManager vcardSupport = SparkManager.getVCardManager();
                String jid = item.getJID();
                vcardSupport.viewProfile(jid, SparkManager.getWorkspace());
            }
        };
        viewProfile.putValue(Action.SMALL_ICON, SparkRes.getImageIcon(SparkRes.PROFILE_IMAGE_16x16));
        viewProfile.putValue(Action.NAME, Res.getString("menuitem.view.profile"));

        popup.add(viewProfile);


        popup.addSeparator();

        Action lastActivityAction = new AbstractAction() {
			private static final long serialVersionUID = -4884230635430933060L;

			public void actionPerformed(ActionEvent actionEvent) {
	            try {
					String client = "";
					if (item.getPresence().getType() != Presence.Type.unavailable) {
						client = item.getPresence().getFrom();
						if ((client != null) && (client.lastIndexOf("/") != -1)) {
							client = client.substring(client.lastIndexOf("/"));
						} else client = "/";
					}
	
	                LastActivity activity = LastActivityManager.getLastActivity(SparkManager.getConnection(), item.getJID()+client);
                    long idleTime = (activity.getIdleTime() * 1000);
                    String time = ModelUtil.getTimeFromLong(idleTime);
                    JOptionPane.showMessageDialog(null, Res.getString("message.idle.for", time), Res.getString("title.last.activity"), JOptionPane.INFORMATION_MESSAGE);
                }
                catch (Exception e1) {
                    JOptionPane.showMessageDialog(null, Res.getString("message.unable.to.retrieve.last.activity", item.getJID()), Res.getString("title.error"), JOptionPane.ERROR_MESSAGE);
                }

            }
        };

        lastActivityAction.putValue(Action.NAME, Res.getString("menuitem.view.last.activity"));
        lastActivityAction.putValue(Action.SMALL_ICON, SparkRes.getImageIcon(SparkRes.SMALL_USER1_STOPWATCH));

      

        Action subscribeAction = new AbstractAction() {
			private static final long serialVersionUID = -7754905015338902300L;

			public void actionPerformed(ActionEvent e) {
                String jid = item.getJID();
                Presence response = new Presence(Presence.Type.subscribe);
                response.setTo(jid);

                SparkManager.getConnection().sendPacket(response);
            }
        };

        subscribeAction.putValue(Action.SMALL_ICON, SparkRes.getImageIcon(SparkRes.SMALL_USER1_INFORMATION));
        subscribeAction.putValue(Action.NAME, Res.getString("menuitem.subscribe.to"));

        Roster roster = SparkManager.getConnection().getRoster();
        RosterEntry entry = roster.getEntry(item.getJID());
        if (entry != null && entry.getType() == RosterPacket.ItemType.from) {
            popup.add(subscribeAction);
        }       
        else if( entry!=null && entry.getType() != RosterPacket.ItemType.both && entry.getStatus() == RosterPacket.ItemStatus.SUBSCRIPTION_PENDING)
        {
            popup.add(subscribeAction);
        }

        // Fire Context Menu Listener
       // fireContextMenuListenerPopup(popup, item);

       // ContactGroup group = getContactGroup(item.getGroupName());
	
          System.out.println("右键菜单");
		  popup.show(component, e.getX(), e.getY());
	
    }//////
    
     
     public void deleteChildNode(ExpandTreeNode selectedNode)
     {
    	 if(selectedNode!=null)
    	 {
    	    selectedNode.removeAllChildren();
    	    
//    	    nodes = model.getPathToRoot(selectedNode);
//			path = new TreePath(nodes);
//			tree.scrollPathToVisible(path);
//			tree.updateUI();
    	 }
     }
       
     
     public String getTreeChildren(String path)
     {
    	 
	     String detail = "";
    	 try
	 		{
    		 URL url = new URL(path);

				HttpURLConnection connect = (HttpURLConnection) url
						.openConnection();
				connect.setDoOutput(true);
				connect.setRequestMethod("GET");
				connect.setRequestProperty("Content-Type",
						"application/x-www-form-urlencoded");
				connect.connect();

				InputStream is = connect.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(
						is, "utf-8"));
				String strLine = null;
				
				while ((strLine = br.readLine()) != null ) {
					detail += strLine;
				}
				br.close();
				is.close();
				connect.disconnect();
	 		}
	 		catch (Exception ex) {
				ex.printStackTrace();
			}
	 		System.out.println(path+"   "+detail);
            return detail;
	 		
     }
   
     
     
}

