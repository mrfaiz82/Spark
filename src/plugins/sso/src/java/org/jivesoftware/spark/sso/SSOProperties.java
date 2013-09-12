package org.jivesoftware.spark.sso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.jivesoftware.Spark;

public class SSOProperties {
	
    private Properties props;
    private File configFile;

    public static final String REMOTEURL = "remoteUrl";
    public static final String REMOTENAME = "remoteName";
    
    private static final Object LOCK = new Object();  
    
    private static SSOProperties ins = null;
    
    public static SSOProperties getInstance() {
    	synchronized (LOCK) {
    	    if (ins == null) {
    		ins = new SSOProperties();
    	    }
    	    return ins;
    	}
    }
    
    private SSOProperties() {
    	this.props = new Properties();

    	try {
    	    props.load(new FileInputStream(getConfigFile()));
    	} catch (IOException e) {
    	    // Can't load ConfigFile
    	}

    }
    
    private File getConfigFile() {
    	if (configFile == null)
    	    configFile = new File(Spark.getSparkUserHome(), "sso.properties");
    	return configFile;
    }
    
    public void save() {
    	try {
    	    props.store(new FileOutputStream(getConfigFile()),
    		    "Storing sso properties");
    	} catch (Exception e) {
    	    e.printStackTrace();
    	}
    }
    
    public String getRemoteUrl()
    {
    	return props.getProperty(REMOTEURL,"");
    }
    
    public void setRemoteUrl(String url)
    {
    	props.setProperty(REMOTEURL, url);
    }
    
    public String getRemoteName()
    {
    	return props.getProperty(REMOTENAME, "");
    }
    
    public void setRemoteName(String name)
    {
    	props.setProperty(REMOTENAME, name);
    }

}
