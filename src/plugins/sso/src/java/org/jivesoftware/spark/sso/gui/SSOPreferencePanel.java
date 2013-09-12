package org.jivesoftware.spark.sso.gui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jivesoftware.spark.component.VerticalFlowLayout;
import org.jivesoftware.spark.sso.SSOResources;


public class SSOPreferencePanel extends JPanel {

	private static final long serialVersionUID = -4217756610718587907L;
	private JTextField _url;
	private JTextField _name;
	
	public SSOPreferencePanel() {
		JPanel contents = new JPanel();
		contents.setLayout(new GridBagLayout());
		contents.setBackground(new Color(0,0,0,0));
		this.setLayout(new VerticalFlowLayout());
		contents.setBorder(BorderFactory.createTitledBorder(SSOResources.getString("sso.settings")));
		
		add(contents);
		
		_url = new JTextField();
		_name = new JTextField();
		
		Insets in = new Insets(5,5,5,5);
		
		contents.add(new JLabel(SSOResources.getString("sso.url")), new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, in, 0, 0));
		contents.add(_url, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, in, 0, 0));

		contents.add(new JLabel(SSOResources.getString("sso.name")), new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, in, 0, 0));
		contents.add(_name, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, in, 0, 0));
	}
	
	public String getRemoteUrl()
	{
		return _url.getText();
	}
	
	public void setRemoteUrl(String url)
	{
		_url.setText(""+url);
	}
	
	public String getRemoteName()
	{
		return _name.getText();
	}
	
	public void setRemoteName(String name)
	{
		_name.setText(name);
	}
}
