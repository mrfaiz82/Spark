package org.jivesoftware.spark.sso;

import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.plugin.Plugin;


/**
 * provider a method to simply achieve SSO
 * @author luye66
 *
 */
public class SSOPlugin implements Plugin{

	@Override
	public void initialize() {
		//draw panel on login advanced panel
		SSOPreference pref = new SSOPreference();
		SparkManager.getPreferenceManager().addPreference(pref);
	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean canShutDown() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void uninstall() {
		
	}

	
}
